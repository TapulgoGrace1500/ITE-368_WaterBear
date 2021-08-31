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

public class OrderProcessingFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference orderRef = db.collection("Orders");
    private String buyerID;

    private RecyclerView ordersProcessingRecyclerView;
    private ArrayList<OrderDetail> options;
    private OrderProcessingAdapter orderProcessingAdapter;

    public OrderProcessingFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_order_processing,container,false);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        ordersProcessingRecyclerView = view.findViewById(R.id.orders_processing);

        options = new ArrayList<>();
        ordersProcessingRecyclerView.setHasFixedSize(true);
        ordersProcessingRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

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
                                        .collection("Cart").whereEqualTo("Order Status", "Processing").addSnapshotListener(new EventListener<QuerySnapshot>() {
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

                                                    options.add(orderDetail);
                                                } catch (Exception e) {
                                                    Log.e("TAG", "Error to display cart item! " + e.getMessage());
                                                    e.printStackTrace();
                                                }

                                            }
                                        }

                                        orderProcessingAdapter = new OrderProcessingAdapter(getContext(), options);
                                        ordersProcessingRecyclerView.setAdapter(orderProcessingAdapter);
                                        orderProcessingAdapter.notifyDataSetChanged();
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

        if (item.getTitle().equals("Prepare to deliver order")){
            confirmOrder(orderProcessingAdapter.getItem(item.getOrder()).getOrderId()
                    ,orderProcessingAdapter.getItem(item.getOrder()).getOrderBuyerId()
                    ,orderProcessingAdapter.getItem(item.getOrder()).getOrderStatus());
        }
        return super.onContextItemSelected(item);
    }

    private void confirmOrder(String cartID, String buyerID, String status) {

        try {

            if (status.equals("Processing")){

                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Preparing to deliver Order");
                builder.setMessage("Are you sure to prepare this order to deliver?");

                View cancelLayout = LayoutInflater.from(getContext()).inflate(R.layout.cancel_order_layout, null);
                builder.setView(cancelLayout);
                builder.setNegativeButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        orderRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                if (task.isSuccessful()){

                                    orderRef.document(buyerID).collection("Store")
                                            .document(mAuth.getCurrentUser().getUid()).collection("Customer")
                                            .document(buyerID).collection("Cart")
                                            .document(cartID).update("Order Status","ToBeDeliver");

                                    getActivity().finish();
                                    Toast.makeText(getContext(), "Prepare to deliver an Order!", Toast.LENGTH_SHORT).show();

                                }

                            }
                        });

                        dialog.dismiss();
                    }
                });
                builder.show();
            }
            else {
                Toast.makeText(getContext(), "Unable to Cancel Order!", Toast.LENGTH_SHORT).show();
            }

        }
        catch (Exception e){
            Log.e("TAG","Error to cancel order"+e.getMessage());
            e.printStackTrace();
        }

    }

}
