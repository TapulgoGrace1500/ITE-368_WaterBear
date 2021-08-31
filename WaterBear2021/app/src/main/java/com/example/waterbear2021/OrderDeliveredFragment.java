package com.example.waterbear2021;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class OrderDeliveredFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference orderRef = db.collection("Orders");
    private String buyerID;

    private RecyclerView ordersDeliveredRecyclerView;
    private ArrayList<OrderDetail> options;
    private OrderDeliveredAdapter orderDeliveredAdapter;

    public OrderDeliveredFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_order_delivered,container,false);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        ordersDeliveredRecyclerView = view.findViewById(R.id.orders_delivered);

        options = new ArrayList<>();
        ordersDeliveredRecyclerView.setHasFixedSize(true);
        ordersDeliveredRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        SetUpOrderPendingRecyclerView();
    }
    private void SetUpOrderPendingRecyclerView() {
        try {

            orderRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot documentSnapshots) {
                    for (DocumentSnapshot snapshot: documentSnapshots){
                        try {
                            buyerID = snapshot.getString("Customer ID");


                            try {
                                orderRef.document(buyerID)
                                        .collection("Store").document(mAuth.getCurrentUser().getUid())
                                        .collection("Customer").document(buyerID)
                                        .collection("Cart").whereEqualTo("Order Status", "Delivered").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                        if (value != null) {
                                            for (DocumentSnapshot snapshot : value) {

                                                OrderDetail orderDetail = new OrderDetail();

                                                try {

                                                    orderDetail.setOrderId(snapshot.getString("Order ID"));
                                                    orderDetail.setCartId(snapshot.getString("Cart ID"));
                                                    orderDetail.setOrderStoreId(snapshot.getString("Store ID"));
                                                    orderDetail.setOrderStoreName(snapshot.getString("Store Name"));
                                                    orderDetail.setOrderBuyerId(snapshot.getString("Customer ID"));
                                                    orderDetail.setOrderBuyerName(snapshot.getString("Customer Name"));
                                                    orderDetail.setOrderBuyerAddress(snapshot.getString("Customer Address"));
                                                    orderDetail.setOrderBuyerContactNumber(snapshot.getString("Contact Number"));
                                                    orderDetail.setOrderDeliveryFee(snapshot.getDouble("Delivery Fee"));
                                                    orderDetail.setOrderTotal(snapshot.getDouble("Order Total"));
                                                    orderDetail.setOrderStatus(snapshot.getString("Order Status"));
                                                    orderDetail.setDeliveredBy(snapshot.getString("Delivered By"));

                                                    options.add(orderDetail);
                                                } catch (Exception e) {
                                                    Log.e("TAG", "Error to display cart item! " + e.getMessage());
                                                    e.printStackTrace();
                                                }

                                            }
                                        }

                                        orderDeliveredAdapter = new OrderDeliveredAdapter(getContext(), options);
                                        ordersDeliveredRecyclerView.setAdapter(orderDeliveredAdapter);
                                        orderDeliveredAdapter.notifyDataSetChanged();
                                    }

                                });
                            }
                            catch (Exception e){
                                Log.e("TAG","Error in getting storeID"+"\n"+e.getMessage());
                                e.printStackTrace();
                            }

                        }
                        catch (Exception e){
                            Log.e("TAG","Failed to get store id");
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        catch (Exception e){
            Log.e("TAG","Failed to execute path! "+e.getMessage());
            e.printStackTrace();
        }

    }
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        if (item.getTitle().equals("Payment Received")){
            confirmPaymentReceived(orderDeliveredAdapter.getItem(item.getOrder()).getOrderId()
                    ,orderDeliveredAdapter.getItem(item.getOrder()).getOrderBuyerId()
                    ,orderDeliveredAdapter.getItem(item.getOrder()).getOrderStatus()
                    ,orderDeliveredAdapter.getItem(item.getOrder()));
        }

        return super.onContextItemSelected(item);
    }

    private void confirmPaymentReceived(String orderId, String orderBuyerId, String orderStatus, OrderDetail item) {
        try {

            if (orderStatus.equals("Delivered")){

                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Transaction");
                builder.setMessage("Are you sure that payment has been received?");

                View cancelLayout = LayoutInflater.from(getContext()).inflate(R.layout.cancel_order_layout, null);
                builder.setView(cancelLayout);
                builder.setNegativeButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        orderRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                if (task.isSuccessful()){

                                    orderRef.document(orderBuyerId).collection("Store")
                                            .document(mAuth.getCurrentUser().getUid()).collection("Customer")
                                            .document(orderBuyerId).collection("Cart")
                                            .document(orderId).update("Order Status","Transaction Complete");

                                    getActivity().finish();
                                    Toast.makeText(getContext(), "Transaction Complete!", Toast.LENGTH_SHORT).show();

                                }

                            }
                        });

                        dialog.dismiss();
                    }
                });
                builder.show();
            }

        }
        catch (Exception e){
            Log.e("TAG","Error to cancel order"+e.getMessage());
            e.printStackTrace();
        }
    }
}
