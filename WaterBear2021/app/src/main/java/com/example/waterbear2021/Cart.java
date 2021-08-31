package com.example.waterbear2021;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Cart extends AppCompatActivity{

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference cartRef = db.collection("Orders");
    private String storeId;
    private final UUID orderID = UUID.randomUUID();
    private TextView TotalSumm, tvDeliveryFee, TotalCost;
    private TextView tvSum, tvDevFee, tvTotalFee;

    private RecyclerView cartRecyclerView;
    private ArrayList<CartPreferenceSQL> options;

    DatabaseHelper databaseHelper;

    private CartAdapter cartAdapter;

    private Button placeOrder;
    private String id, cID, checkStatus, status, OrderID;
    private String devFee, TotalCostFee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        mAuth = FirebaseAuth.getInstance();
        databaseHelper = new DatabaseHelper(this);
        options = new ArrayList<>();

        placeOrder = findViewById(R.id.PlaceOrderButton);
        tvDeliveryFee = findViewById(R.id.DeliveryFee);
        TotalCost = findViewById(R.id.Total);
        TotalSumm = findViewById(R.id.SumTotal);

        tvSum = findViewById(R.id.tvasad);
        tvDevFee = findViewById(R.id.tvTotal);
        tvTotalFee = findViewById(R.id.tvDFee);

        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.collection("Customers").document(mAuth.getCurrentUser().getUid())
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.getResult().exists()){

                            String contactResult = task.getResult().getString("Contact Number");

                            PlaceOrder(contactResult);
                        }
                    }
                });
            }
        });

        cartRecyclerView = findViewById(R.id.cart_recyclerView);

        cartRecyclerView.setHasFixedSize(true);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        SetUpCartRecyclerView();

        storeId = getIntent().getStringExtra("Store ID");

        Log.d("TAG",storeId);

        try {
            cartRef.whereEqualTo("status","Confirmation")
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot documentSnapshots) {

                            if (documentSnapshots != null){
                                for (DocumentSnapshot snapshot: documentSnapshots){
                                    cID = snapshot.getId();
                                    status = snapshot.getString("status");
                                }
                            }

                        }
                    });
        }
        catch (Exception e){
            Log.e("TAG",e.getMessage());
            e.printStackTrace();
        }

        calculatedTotalPay();

    }

    private void calculatedTotalPay() {

        try {
            Cursor cursor = databaseHelper.getData();
            int i;

            for (i = 0; i <= cursor.getCount(); i++){

                overAllTotal = allTotalPriceWaterContainer + allTotalPrice500ml + allTotalPrice1L + allTotalPrice4L + allTotalPrice5L + allTotalPrice6L;
            overAllTotalSumm = waterContainerCost + bottledWater500ml + bottledWater1L + bottledWater4L + bottledWater5L + bottledWater6L;
            overAllTotalDevFee = TotalDevFeeWaterContainer + TotalDevFee500ml + TotalDevFee1L + TotalDevFee4L + TotalDevFee5L + TotalDevFee6L;

                if (cursor != null){
                    cursor.moveToFirst();
                }
                do {
                    String productName = cursor.getString(9);
                    double price = cursor.getDouble(11);

                    if (cursor.getInt(13) <= 5){

                        switch (productName) {
                            case "Water Container":
                                TotalDevFeeWaterContainer = deliveryFeeContainer + 30.00;
                                waterContainerCost = price * cursor.getInt(13);
                                allTotalPriceWaterContainer = TotalDevFeeWaterContainer + waterContainerCost;
                                break;
                            case "Bottled Water(500ml)":
                                TotalDevFee500ml = deliveryFee500ml + 10.00;
                                bottledWater500ml = price * cursor.getInt(13);
                                allTotalPrice500ml = TotalDevFee500ml + bottledWater500ml;
                                break;
                            case "Bottled Water(1Liter)":
                                TotalDevFee1L = deliveryFee1L + 15.00;
                                bottledWater1L = price * cursor.getInt(13);
                                allTotalPrice1L = TotalDevFee1L + bottledWater1L;
                                break;
                            case "Bottled Water(4Liters)":
                                TotalDevFee4L = deliveryFee4L + 20.00;
                                bottledWater4L = price * cursor.getInt(13);
                                allTotalPrice4L = TotalDevFee4L + bottledWater4L;
                                break;
                            case "Bottled Water(5Liters)":
                                TotalDevFee5L = deliveryFee5L + 25.00;
                                bottledWater5L = price * cursor.getInt(13);
                                allTotalPrice5L = TotalDevFee5L + bottledWater5L;
                                break;
                            case "Bottled Water(6Liters)":
                                TotalDevFee6L = deliveryFee6L + 27.50;
                                bottledWater6L = price * cursor.getInt(13);
                                allTotalPrice6L = TotalDevFee6L + bottledWater6L;
                                break;
                        }
                    }
                    else {

                        overAllTotal = allTotalPriceWaterContainer + allTotalPrice500ml + allTotalPrice1L + allTotalPrice4L + allTotalPrice5L + allTotalPrice6L;
                        overAllTotalSumm = waterContainerCost + bottledWater500ml + bottledWater1L + bottledWater4L + bottledWater5L + bottledWater6L;
                        overAllTotalDevFee = TotalDevFeeWaterContainer + TotalDevFee500ml + TotalDevFee1L + TotalDevFee4L + TotalDevFee5L + TotalDevFee6L;


                        switch (productName) {
                            case "Water Container":
                                deliveryFeeContainer = cursor.getInt(13) * 6.00;
                                TotalDevFeeWaterContainer = deliveryFeeContainer;
                                waterContainerCost = price * cursor.getInt(13);
                                allTotalPriceWaterContainer = TotalDevFeeWaterContainer + waterContainerCost;
                                break;
                            case "Bottled Water(500ml)":
                                deliveryFee500ml = cursor.getInt(13) * 2.00;
                                TotalDevFee500ml = deliveryFee500ml;
                                bottledWater500ml = price * cursor.getInt(13);
                                allTotalPrice500ml = TotalDevFee500ml + bottledWater500ml;
                                break;
                            case "Bottled Water(1Liter)":
                                deliveryFee1L = cursor.getInt(13) * 3.00;
                                TotalDevFee1L = deliveryFee1L;
                                bottledWater1L = price * cursor.getInt(13);
                                allTotalPrice1L = TotalDevFee1L + bottledWater1L;
                                break;
                            case "Bottled Water(4Liters)":
                                deliveryFee4L = cursor.getInt(13) * 4.00;
                                TotalDevFee4L = deliveryFee4L;
                                bottledWater4L = price * cursor.getInt(13);
                                allTotalPrice4L = TotalDevFee4L + bottledWater4L;
                                break;
                            case "Bottled Water(5Liters)":
                                deliveryFee5L = cursor.getInt(13) * 5.00;
                                TotalDevFee5L = deliveryFee5L;
                                bottledWater5L = price * cursor.getInt(13);
                                allTotalPrice5L = TotalDevFee5L + bottledWater5L;
                                break;
                            case "Bottled Water(6Liters)":
                                deliveryFee6L = cursor.getInt(13) * 5.50;
                                TotalDevFee6L = deliveryFee6L;
                                bottledWater6L = price * cursor.getInt(13);
                                allTotalPrice6L = TotalDevFee6L + bottledWater6L;
                                break;
                        }
                    }
                }
                while (cursor.moveToNext());
            }

            TotalCostFee = String.format("%.2f",overAllTotal);
            TotalCost.setText(TotalCostFee);
            tvDeliveryFee.setText(String.format("%.2f",overAllTotalDevFee));
            TotalSumm.setText(String.format("%.2f",overAllTotalSumm));
        }
        catch (Exception e){
            Log.e("TAG",e.getMessage());
            e.printStackTrace();
        }

    }

    public double allTotalPriceWaterContainer = 0.00, allTotalPrice500ml = 0.00, allTotalPrice1L = 0.00, allTotalPrice4L = 0.00, allTotalPrice5L = 0.00, allTotalPrice6L = 0.00;
    public double deliveryFeeContainer = 0.00, deliveryFee500ml = 0.00, deliveryFee1L = 0.00, deliveryFee4L = 0.00, deliveryFee5L = 0.00, deliveryFee6L = 0.00;
    public double TotalDevFeeWaterContainer = 0.00, TotalDevFee500ml= 0.00,TotalDevFee1L= 0.00, TotalDevFee4L= 0.00, TotalDevFee5L= 0.00, TotalDevFee6L= 0.00;
    public double waterContainerCost = 0.00, bottledWater500ml = 0.00, bottledWater1L = 0.00, bottledWater4L = 0.00, bottledWater5L = 0.00, bottledWater6L = 0.00;
    public double overAllTotal = 0.00;
    public double overAllTotalSumm = 0.00;
    public double overAllTotalDevFee = 0.00;

    private void PlaceOrder(String contactResult){

        OrderID = String.valueOf(orderID);

        try {

            Cursor cursor = databaseHelper.getData();

            if (cursor.getCount() == 0){
                Toast.makeText(this, "Cart is Empty, Select Product to Place an Order!", Toast.LENGTH_SHORT).show();
            }
            else {

                while (cursor.moveToNext()){
                    CartPreferenceSQL obj = new CartPreferenceSQL(cursor.getString(1),cursor.getString(2),
                            cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),
                            cursor.getString(7),cursor.getString(8),cursor.getString(9),
                            cursor.getString(10),cursor.getInt(13),cursor.getDouble(11),
                            cursor.getDouble(12));

                    try {

                        cartRef.document(mAuth.getCurrentUser().getUid()).collection("Store")
                                .document(cursor.getString(5)).collection("Customer")
                                .document(mAuth.getCurrentUser().getUid()).collection("Cart")
                                .document(OrderID).collection("Products")
                                .document(cursor.getString(1)).set(obj);

                        DocumentReference df = db.collection("Orders").document(mAuth.getCurrentUser().getUid())
                                .collection("Store").document(cursor.getString(5))
                                .collection("Customer").document(mAuth.getCurrentUser().getUid())
                                .collection("Cart").document(OrderID);

                        Map<String, Object> storeInfo = new HashMap<>();

                        storeInfo.put("Order ID",OrderID);
                        storeInfo.put("Cart ID",cursor.getString(1));
                        storeInfo.put("Store ID",cursor.getString(5));
                        storeInfo.put("Store Name",cursor.getString(6));
                        storeInfo.put("Customer ID",mAuth.getCurrentUser().getUid());
                        storeInfo.put("Customer Name",cursor.getString(3));
                        storeInfo.put("Customer Address",cursor.getString(4));
                        storeInfo.put("Order Status",cursor.getString(10));
                        storeInfo.put("Delivery Fee",Double.parseDouble(String.format("%.2f",overAllTotalDevFee)));
                        storeInfo.put("Order Total",Double.parseDouble(String.format("%.2f",overAllTotal)));
                        storeInfo.put("Contact Number",contactResult);
                        storeInfo.put("Delivered By",null);

                        df.set(storeInfo);

                        DocumentReference df2 = db.collection("Orders").document(mAuth.getCurrentUser().getUid());


                        Map<String, Object> storeInfo2 = new HashMap<>();

                        storeInfo2.put("Customer ID",mAuth.getCurrentUser().getUid());
                        storeInfo2.put("Contact Number",contactResult);

                        df2.set(storeInfo2);


                        if (DatabaseHelper.isCartAvailable(getApplicationContext())){
                            DatabaseHelper.deleteData(getApplicationContext());
                        }
                        startActivity(new Intent(getApplicationContext(),BuyerActivity.class));
                        finish();
                        Toast.makeText(Cart.this, "Successfully Place an Order!", Toast.LENGTH_SHORT).show();

                    }
                    catch (Exception e){
                        Log.e("TAG",e.getMessage());
                        e.printStackTrace();
                    }

                }
            }
        }
        catch (Exception e){

            Log.e("TAG","Failed to execute"+e.getMessage());
            e.printStackTrace();

        }
    }

    private void SetUpCartRecyclerView() {

        try {

            Cursor cursor = databaseHelper.getData();

            if (cursor.getCount() == 0){
                Toast.makeText(this, "Cart is Empty!", Toast.LENGTH_SHORT).show();
                TotalSumm.setVisibility(View.INVISIBLE);
                tvDeliveryFee.setVisibility(View.INVISIBLE);
                TotalCost.setVisibility(View.INVISIBLE);
                placeOrder.setVisibility(View.INVISIBLE);

                tvSum.setVisibility(View.INVISIBLE);
                tvDevFee.setVisibility(View.INVISIBLE);
                tvTotalFee.setVisibility(View.INVISIBLE);
            }
            else {
                TotalSumm.setVisibility(View.VISIBLE);
                tvDeliveryFee.setVisibility(View.VISIBLE);
                TotalCost.setVisibility(View.VISIBLE);
                placeOrder.setVisibility(View.VISIBLE);

                tvSum.setVisibility(View.VISIBLE);
                tvDevFee.setVisibility(View.VISIBLE);
                tvTotalFee.setVisibility(View.VISIBLE);

                while (cursor.moveToNext()){
                    CartPreferenceSQL obj = new CartPreferenceSQL(cursor.getString(1),cursor.getString(2),
                            cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),
                            cursor.getString(7),cursor.getString(8),cursor.getString(9),
                            cursor.getString(10),cursor.getInt(13),cursor.getDouble(11),
                            cursor.getDouble(12));
                    options.add(obj);
                }

                cartAdapter = new CartAdapter(getApplicationContext(),options);
                cartRecyclerView.setAdapter(cartAdapter);
                cartAdapter.notifyDataSetChanged();

            }

        }
        catch (Exception e){
            Log.e("TAG","Failed to execute path! "+e.getMessage());
            e.printStackTrace();
        }

    }

}