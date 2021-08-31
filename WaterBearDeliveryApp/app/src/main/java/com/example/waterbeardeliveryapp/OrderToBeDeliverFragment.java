package com.example.waterbeardeliveryapp;

import android.content.DialogInterface;
import android.database.Cursor;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class OrderToBeDeliverFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference orderRef = db.collection("Orders");
    private CollectionReference userRef = db.collection("Customers");
    private String buyerID,contactNum;

    private RecyclerView orderTobeDeliverRecyclerView;
    private ArrayList<OrderDetail> options;
    private OrderToBeDeliverAdapter orderToBeDeliverAdapter;

    DatabaseHelper databaseHelper;

    public OrderToBeDeliverFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_order_tobedeliver,container,false);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        orderTobeDeliverRecyclerView = view.findViewById(R.id.orders_tobedeliver);

        options = new ArrayList<>();
        databaseHelper = new DatabaseHelper(getContext());

        orderTobeDeliverRecyclerView.setHasFixedSize(true);
        orderTobeDeliverRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

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
                                        .collection("Cart").whereEqualTo("Order Status", "ToBeDeliver").addSnapshotListener(new EventListener<QuerySnapshot>() {
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

                                        orderToBeDeliverAdapter = new OrderToBeDeliverAdapter(getContext(), options);
                                        orderTobeDeliverRecyclerView.setAdapter(orderToBeDeliverAdapter);
                                        orderToBeDeliverAdapter.notifyDataSetChanged();
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

        if (item.getTitle().equals("Successfully Delivered")){
            confirmPaymentReceived(orderToBeDeliverAdapter.getItem(item.getOrder()).getOrderId()
                    ,orderToBeDeliverAdapter.getItem(item.getOrder()).getOrderBuyerId()
                    ,orderToBeDeliverAdapter.getItem(item.getOrder()).getOrderStatus()
                    ,orderToBeDeliverAdapter.getItem(item.getOrder()));
        }
        else if (item.getTitle().equals("Report Buyer as Spam")){
            confirmMarkedSpam(orderToBeDeliverAdapter.getItem(item.getOrder()).getOrderId()
                    ,orderToBeDeliverAdapter.getItem(item.getOrder()).getOrderBuyerId()
                    ,orderToBeDeliverAdapter.getItem(item.getOrder()).getOrderStatus()
                    ,orderToBeDeliverAdapter.getItem(item.getOrder()));
        }

        return super.onContextItemSelected(item);
    }

    private void confirmMarkedSpam(String orderId, String orderBuyerId, String orderStatus, OrderDetail item) {
        try {

            if (orderStatus.equals("ToBeDeliver")){

                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Marked as Spam");
                builder.setMessage("Are you sure to Mark Buyer as Spam?");

                View confirm = LayoutInflater.from(getContext()).inflate(R.layout.confirm_spam_layout, null);

                EditText buyer = confirm.findViewById(R.id.bName);
                EditText address = confirm.findViewById(R.id.bAddress);
                EditText contact = confirm.findViewById(R.id.bContact);
                Button confirmOrder = confirm.findViewById(R.id.btnConfirm);

                buyer.setText(item.getOrderBuyerName());
                address.setText(item.getOrderBuyerAddress());
                contact.setText(item.getOrderBuyerContactNumber());

                buyer.setEnabled(false);
                address.setEnabled(false);
                contact.setEnabled(false);

                builder.setView(confirm);
                confirmOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        orderRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                if (task.isSuccessful()){

                                    userRef.document(orderBuyerId).update("isBuyer","Spam");
                                    Toast.makeText(getContext(), "Successfully marked as Spam!", Toast.LENGTH_SHORT).show();

                                }

                            }
                        });

                    }
                });
                builder.show();
            }
            else {
                Toast.makeText(getContext(), "Unable to Marked as Spam!", Toast.LENGTH_SHORT).show();
            }

        }
        catch (Exception e){
            Log.e("TAG","Error to marked spam"+e.getMessage());
            e.printStackTrace();
        }
    }

    private void confirmPaymentReceived(String orderId, String orderBuyerId, String orderStatus, OrderDetail item) {
        try {

            if (orderStatus.equals("ToBeDeliver")){

                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Transaction");
                builder.setMessage("Are you sure that order is delivered?");

                View cancelLayout = LayoutInflater.from(getContext()).inflate(R.layout.confirm_order_delivered_layout, null);

                EditText buyer = cancelLayout.findViewById(R.id.bName);
                EditText address = cancelLayout.findViewById(R.id.bAddress);
                EditText contact = cancelLayout.findViewById(R.id.bContact);

                buyer.setText(item.getOrderBuyerName());
                address.setText(item.getOrderBuyerAddress());
                contact.setText(item.getOrderBuyerContactNumber());

                buyer.setEnabled(false);
                address.setEnabled(false);
                contact.setEnabled(false);

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
                                            .document(orderId).update("Order Status","Delivered").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){

                                                Cursor cursor = databaseHelper.getData();
                                                if (cursor.getCount() != 0){
                                                    while (cursor.moveToNext()){
                                                        String firstname = cursor.getString(2);
                                                        String lastname = cursor.getString(3);

                                                        orderRef.document(orderBuyerId).collection("Store")
                                                                .document(mAuth.getCurrentUser().getUid()).collection("Customer")
                                                                .document(orderBuyerId).collection("Cart")
                                                                .document(orderId).update("Delivered By",firstname+" "+lastname);
                                                    }
                                                }
                                            }
                                        }
                                    });

                                    Toast.makeText(getContext(), "Order has been Delivered!", Toast.LENGTH_SHORT).show();

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
