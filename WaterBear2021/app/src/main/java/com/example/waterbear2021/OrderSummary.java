package com.example.waterbear2021;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class OrderSummary extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference orderRef = db.collection("Orders");
    private CollectionReference storeRef = db.collection("Merchants");
    private String storeId, cID, status, storeName, pID;

    private RecyclerView orderSummaryRecyclerView;
    private ArrayList<OrderDetail> options;

    private OrderSummaryAdapter orderSummaryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);

        mAuth = FirebaseAuth.getInstance();

        orderSummaryRecyclerView = findViewById(R.id.orderSummary_recyclerView);

        options = new ArrayList<>();
        orderSummaryRecyclerView.setHasFixedSize(true);
        orderSummaryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        storeRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {

                try {
                    for (DocumentSnapshot snapshot: documentSnapshots){
                        if (snapshot.exists()){
                            storeId = snapshot.getId();
                            SetUpOrderRecyclerView(storeId);
                            SetUpNotification(storeId);
                        }
                    }

                }
                catch (Exception e){
                    Log.e("TAG","Error to execute getting ID "+e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    private void SetUpNotification(String storeId) {

        orderRef.document(mAuth.getCurrentUser().getUid())
                .collection("Store").document(storeId)
                .collection("Customer").document(mAuth.getCurrentUser().getUid())
                .collection("Cart").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (value != null){
                    for (DocumentSnapshot snapshot: value){
                        status = snapshot.getString("Order Status");
                        storeName = snapshot.getString("Store Name");
                    }
                    notification(storeName,status);
                }

            }
        });
    }

    private void notification(String storeName, String status) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel channel = new NotificationChannel(
                    "n","n", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"n")
                .setContentText("WaterBear")
                .setSmallIcon(R.drawable.ic_notifications)
                .setAutoCancel(true)
                .setContentTitle("Your Order from "+storeName)
                .setContentText("Has been "+status);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(0,builder.build());
    }

    private void SetUpOrderRecyclerView(String storeId) {

        try {

            orderRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){

                        try {
                            orderRef.document(mAuth.getCurrentUser().getUid())
                                    .collection("Store").document(storeId)
                                    .collection("Customer").document(mAuth.getCurrentUser().getUid())
                                    .collection("Cart").addSnapshotListener(new EventListener<QuerySnapshot>() {
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

                                    orderSummaryAdapter = new OrderSummaryAdapter(getApplicationContext(), options);
                                    orderSummaryRecyclerView.setAdapter(orderSummaryAdapter);
                                    orderSummaryAdapter.notifyDataSetChanged();

                                }

                            });
                        }
                        catch (Exception e){
                            Log.e("TAG","Error in getting storeID"+"\n"+e.getMessage());
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

        if (item.getTitle().equals("Cancel Order")){
            cancelOrder(orderSummaryAdapter.getItem(item.getOrder()).getOrderId(),
                    orderSummaryAdapter.getItem(item.getOrder()).getOrderBuyerId(),
                    orderSummaryAdapter.getItem(item.getOrder()).getOrderStoreId(),
                    orderSummaryAdapter.getItem(item.getOrder()).getOrderStatus());
        }

        return super.onContextItemSelected(item);
    }

    private void cancelOrder(String orderID, String buyerId, String storeID, String status) {

        try {

            if (status.equals("Pending") || status.equals("Cancelled")){

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Cancel Order");
                builder.setMessage("Are you sure to cancel this order?");

                View cancelLayout = LayoutInflater.from(this).inflate(R.layout.cancel_order_layout, null);
                builder.setView(cancelLayout);
                builder.setNegativeButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        orderRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                if (task.isSuccessful()){

                                    orderRef.document(buyerId)
                                            .collection("Store").document(storeID)
                                            .collection("Customer").document(buyerId)
                                            .collection("Cart").document(orderID).delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    orderRef.document(buyerId)
                                                            .collection("Store").document(storeID)
                                                            .collection("Customer").document(buyerId)
                                                            .collection("Cart").document(orderID).delete();
                                                    Toast.makeText(OrderSummary.this, "Successfully Cancelled an Order!", Toast.LENGTH_SHORT).show();

                                                }
                                            });
                                }

                            }
                        });

                        dialog.dismiss();
                    }
                });
                builder.show();
            }
            else {
                Toast.makeText(this, "Unable to Cancel Order!", Toast.LENGTH_SHORT).show();
            }

        }
        catch (Exception e){
            Log.e("TAG","Error to cancel order"+e.getMessage());
            e.printStackTrace();
        }
    }
}