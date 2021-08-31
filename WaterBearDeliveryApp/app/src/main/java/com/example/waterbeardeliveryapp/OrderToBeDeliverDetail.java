package com.example.waterbeardeliveryapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

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

public class OrderToBeDeliverDetail extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference orderRef = db.collection("Orders");
    private String buyerId, cID;
    private String orderIdFromIntent;

    private RecyclerView orderRecyclerView;
    private ArrayList<CartPreference> options;

    private OrderAdapter orderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_to_be_deliver_detail);

        mAuth = FirebaseAuth.getInstance();

        orderRecyclerView = findViewById(R.id.ordersToBeDeliverDetail_recyclerView);

        options = new ArrayList<>();
        orderRecyclerView.setHasFixedSize(true);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderIdFromIntent = getIntent().getStringExtra("Order ID");
        buyerId = getIntent().getStringExtra("Customer ID");

        SetUpOrderRecyclerView();
    }

    private void SetUpOrderRecyclerView() {


        try {

            orderRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){


                        orderRef.document(buyerId)
                                .collection("Store").document(mAuth.getCurrentUser().getUid())
                                .collection("Customer").document(buyerId)
                                .collection("Cart").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot documentSnapshots) {

                                try {
                                    for (DocumentSnapshot snapshot: documentSnapshots){
                                        if (snapshot.exists()){
                                            cID = snapshot.getString("Order ID");

                                            if (orderIdFromIntent.equals(cID)){
                                                try {
                                                    orderRef.document(buyerId)
                                                            .collection("Store").document(mAuth.getCurrentUser().getUid())
                                                            .collection("Customer").document(buyerId)
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
}