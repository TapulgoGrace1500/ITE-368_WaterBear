package com.example.waterbear2021;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

public class OrderPendingFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference orderRef = db.collection("Orders");
    private String buyerID, contactNum;

    private RecyclerView ordersPendingRecyclerView;
    private ArrayList<OrderDetail> options;
    private OrderPendingAdapter orderPendingAdapter;

    public OrderPendingFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_order_pending,container,false);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        ordersPendingRecyclerView = view.findViewById(R.id.orders_pending);

        options = new ArrayList<>();
        ordersPendingRecyclerView.setHasFixedSize(true);
        ordersPendingRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

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
                            contactNum = snapshot.getString("Contact Number");


                            try {
                                orderRef.document(buyerID)
                                        .collection("Store").document(mAuth.getCurrentUser().getUid())
                                        .collection("Customer").document(buyerID)
                                        .collection("Cart").whereEqualTo("Order Status", "Pending").addSnapshotListener(new EventListener<QuerySnapshot>() {
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

                                        orderPendingAdapter = new OrderPendingAdapter(getContext(), options);
                                        ordersPendingRecyclerView.setAdapter(orderPendingAdapter);
                                        orderPendingAdapter.notifyDataSetChanged();
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

        if (item.getTitle().equals("Process order")){
            confirmOrder(orderPendingAdapter.getItem(item.getOrder()).getOrderId()
                    ,orderPendingAdapter.getItem(item.getOrder()).getOrderBuyerId()
                    ,orderPendingAdapter.getItem(item.getOrder()).getOrderStatus()
                    ,orderPendingAdapter.getItem(item.getOrder()));
        }
        else if(item.getTitle().equals("Cancel order")){
            cancelOrder(orderPendingAdapter.getItem(item.getOrder()).getOrderId()
                    ,orderPendingAdapter.getItem(item.getOrder()).getOrderBuyerId()
                        ,orderPendingAdapter.getItem(item.getOrder()).getOrderStatus()
                        ,orderPendingAdapter.getItem(item.getOrder()));
        }

        return super.onContextItemSelected(item);
    }

    private void confirmOrder(String orderID, String buyerID, String status, OrderDetail item) {

        try {

            if (status.equals("Pending")){

                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Process Order");
                builder.setMessage("Are you sure to process this order?");

                View confirm = LayoutInflater.from(getContext()).inflate(R.layout.confirm_order_layout, null);

                EditText buyer = confirm.findViewById(R.id.bName);
                EditText address = confirm.findViewById(R.id.bAddress);
                EditText contact = confirm.findViewById(R.id.bContact);
                EditText prodTotalPrice = confirm.findViewById(R.id.pTotalPrice);
                Button confirmOrder = confirm.findViewById(R.id.btnConfirm);

                buyer.setText(item.getOrderBuyerName());
                address.setText(item.getOrderBuyerAddress());
                contact.setText(item.getOrderBuyerContactNumber());
                prodTotalPrice.setText(String.format("%.2f",item.getOrderTotal()));

                buyer.setEnabled(false);
                address.setEnabled(false);
                contact.setEnabled(false);
                prodTotalPrice.setEnabled(false);

                builder.setView(confirm);
                confirmOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        orderRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                if (task.isSuccessful()){

                                    orderRef.document(buyerID).collection("Store")
                                            .document(mAuth.getCurrentUser().getUid()).collection("Customer")
                                            .document(buyerID).collection("Cart")
                                            .document(orderID).update("Order Status","Processing");

                                    getActivity().finish();
                                    Toast.makeText(getContext(), "Process an Order!", Toast.LENGTH_SHORT).show();

                                }

                            }
                        });

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

    private void cancelOrder(String OrderID, String buyerID, String status, OrderDetail item) {

        try {

            if (status.equals("Pending")){

                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Cancel Order");
                builder.setMessage("Are you sure to cancel this order?");

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
                                            .document(OrderID).update("Order Status","Cancelled");

                                    Toast.makeText(getContext(), "Cancelled an Order!", Toast.LENGTH_SHORT).show();

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
