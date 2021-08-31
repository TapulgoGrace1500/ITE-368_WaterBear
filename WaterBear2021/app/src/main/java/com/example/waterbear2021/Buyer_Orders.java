package com.example.waterbear2021;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Buyer_Orders extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference orderRef = db.collection("Orders");
    private CollectionReference storeRef = db.collection("Merchants");
    private String storeId, cID, status, storeName;
    private String cartIdFromIntent, orderIdFromIntent;

    private RecyclerView orderRecyclerView;
    private ArrayList<CartPreference> options;

    private OrderAdapter orderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer__orders);

        mAuth = FirebaseAuth.getInstance();

        orderRecyclerView = findViewById(R.id.orders_recyclerView);

        options = new ArrayList<>();
        orderRecyclerView.setHasFixedSize(true);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        storeRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {

                try {
                    for (DocumentSnapshot snapshot: documentSnapshots){
                        if (snapshot.exists()){
                            storeId = snapshot.getId();
                            SetUpOrderRecyclerView(storeId);
                            //SetUpNotification(storeId);
                        }
                    }

                }
                catch (Exception e){
                    Log.e("TAG","Error to execute getting ID "+e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        orderIdFromIntent = getIntent().getStringExtra("Order ID");
        //cartIdFromIntent = getIntent().getStringExtra("Cart ID");
    }

//    private void SetUpNotification(String storeId) {
//
//        orderRef.document(mAuth.getCurrentUser().getUid())
//                .collection("Store").document(storeId)
//                .collection("Customer").document(mAuth.getCurrentUser().getUid())
//                .collection("Products").addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//
//                if (value != null){
//                    for (DocumentSnapshot snapshot: value){
//                        status = snapshot.getString("status");
//                        storeName = snapshot.getString("storeName");
//                    }
//                    notification(storeName,status);
//                }
//
//            }
//        });
//    }
//
//    private void notification(String storeName, String status) {
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//
//            NotificationChannel channel = new NotificationChannel(
//                    "n","n",NotificationManager.IMPORTANCE_DEFAULT);
//
//            NotificationManager manager = getSystemService(NotificationManager.class);
//            manager.createNotificationChannel(channel);
//        }
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"n")
//                .setContentText("WaterBear")
//                .setSmallIcon(R.drawable.ic_notifications)
//                .setAutoCancel(true)
//                .setContentTitle("Your Order from "+storeName)
//                .setContentText("Has been "+status);
//
//        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
//        managerCompat.notify(0,builder.build());
//    }

    private void SetUpOrderRecyclerView(String storeId) {
        try {

            orderRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){


                        orderRef.document(mAuth.getCurrentUser().getUid())
                                .collection("Store").document(storeId)
                                .collection("Customer").document(mAuth.getCurrentUser().getUid())
                                .collection("Cart").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot documentSnapshots) {

                                try {
                                    for (DocumentSnapshot snapshot: documentSnapshots){
                                        if (snapshot.exists()){
                                            cID = snapshot.getString("Order ID");

                                            if (orderIdFromIntent.equals(cID)){
                                                try {
                                                    orderRef.document(mAuth.getCurrentUser().getUid())
                                                            .collection("Store").document(storeId)
                                                            .collection("Customer").document(mAuth.getCurrentUser().getUid())
                                                            .collection("Cart").document(cID)
                                                            .collection("Products").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                                            if (value != null) {
                                                                for (DocumentSnapshot snapshot : value) {

                                                                    CartPreference cartPreference = new CartPreference();

                                                                    try {

                                                                        cartPreference.setCartID(snapshot.getString("cartID"));
                                                                        cartPreference.setBuyerID(snapshot.getString("buyerID"));
                                                                        cartPreference.setBuyerName(snapshot.getString("buyerName"));
                                                                        cartPreference.setStoreID(snapshot.getString("storeID"));
                                                                        cartPreference.setStoreName(snapshot.getString("storeName"));
                                                                        cartPreference.setProductID(snapshot.getString("productID"));
                                                                        cartPreference.setProductType(snapshot.getString("productType"));
                                                                        cartPreference.setProductName(snapshot.getString("productName"));
                                                                        cartPreference.setProductPrice(snapshot.getDouble("productPrice"));
                                                                        cartPreference.setQuantity(snapshot.getDouble("quantity"));
                                                                        cartPreference.setTotalPrice(snapshot.getDouble("totalPrice"));
                                                                        cartPreference.setStatus(snapshot.getString("status"));

                                                                        options.add(cartPreference);


                                                                    } catch (Exception e) {
                                                                        Log.e("TAG", "Error to display cart item! " + e.getMessage());
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            }

                                                            orderAdapter = new OrderAdapter(getApplicationContext(), options);
                                                            orderRecyclerView.setAdapter(orderAdapter);
                                                            orderAdapter.notifyDataSetChanged();

                                                        }

                                                    });
                                                }
                                                catch (Exception e){
                                                    Log.e("TAG","Error in getting storeID"+"\n"+e.getMessage());
                                                    e.printStackTrace();
                                                }
                                            }

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
                }
            });
        }
        catch (Exception e){
            Log.e("TAG","Failed to execute path! "+e.getMessage());
            e.printStackTrace();
        }
    }

//    @Override
//    public boolean onContextItemSelected(@NonNull MenuItem item) {
//
//        if (item.getTitle().equals("Cancel Order")){
//            cancelOrder(orderAdapter.getItem(item.getOrder()).getCartID(),
//                    orderAdapter.getItem(item.getOrder()).getBuyerID(),
//                    orderAdapter.getItem(item.getOrder()).getStoreID(),
//                    orderAdapter.getItem(item.getOrder()).getStatus());
//        }
//
//        return super.onContextItemSelected(item);
//    }
//
//    private void cancelOrder(String cartID, String buyerId, String storeID, String status) {
//
//        try {
//
//            if (status.equals("Pending") || status.equals("Cancelled")){
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setTitle("Cancel Order");
//                builder.setMessage("Are you sure to cancel this order?");
//
//                View cancelLayout = LayoutInflater.from(this).inflate(R.layout.cancel_order_layout, null);
//                builder.setView(cancelLayout);
//                builder.setNegativeButton("Confirm", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        orderRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//
//                                if (task.isSuccessful()){
//
//                                    orderRef.document(buyerId)
//                                            .collection("Store").document(storeID)
//                                            .collection("Customer").document(buyerId)
//                                            .collection("Products").document(cartID).delete();
//
//                                    Toast.makeText(Buyer_Orders.this, "Successfully Cancelled an Order!", Toast.LENGTH_SHORT).show();
//
//                                }
//
//                            }
//                        });
//
//                        dialog.dismiss();
//                    }
//                });
//                builder.show();
//            }
//            else {
//                Toast.makeText(this, "Unable to Cancel Order!", Toast.LENGTH_SHORT).show();
//            }
//
//        }
//        catch (Exception e){
//            Log.e("TAG","Error to cancel order"+e.getMessage());
//            e.printStackTrace();
//        }
//    }

}