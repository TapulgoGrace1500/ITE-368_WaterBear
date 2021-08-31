package com.example.waterbear2021;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class AddToCart extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private TextView productType, productName, productPrice;
    private Button AddToCart;
    private ElegantNumberButton ButtonQty;
    private String store_Id, storeName, buyer_Id, buyerName,buyerAddress, product_Id, type, name, price, qty, total, status;
    private String nameResult, addressResult ,contactResult;
    private final UUID cart_Id = UUID.randomUUID();

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_cart);

        productType = findViewById(R.id.type);
        productName = findViewById(R.id.name);
        productPrice = findViewById(R.id.price);
        ButtonQty = findViewById(R.id.qty);

        databaseHelper = new DatabaseHelper(this);

        mAuth = FirebaseAuth.getInstance();

        AddToCart = findViewById(R.id.addToCart);

        AddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddedToCart();
            }
        });

        String currentId = mAuth.getCurrentUser().getUid();
        DocumentReference reference;
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        reference = firebaseFirestore.collection("Customers").document(currentId);

        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.getResult().exists()){

                    nameResult = task.getResult().getString("First Name") +"\t"+ task.getResult().getString("Last Name");
                    addressResult = task.getResult().getString("Address");
                    contactResult = task.getResult().getString("Contact Number");

                }
            }
        });

        ShowProductDetail();
    }

    private void AddedToCart() {

        try {

            store_Id = getIntent().getStringExtra("Seller ID");
            storeName = getIntent().getStringExtra("Store Name");
            buyer_Id = mAuth.getCurrentUser().getUid();
            buyerName = nameResult;
            buyerAddress = addressResult;
//            buyerContact = contactResult;
            product_Id = getIntent().getStringExtra("Product ID");
            type = getIntent().getStringExtra("Product Type");
            name = getIntent().getStringExtra("Product Name");
            price = getIntent().getStringExtra("Product Price");
            qty = String.valueOf(ButtonQty.getNumber());
            total = String.format("%.2f",Double.parseDouble(qty) * Double.parseDouble(price));
            status = "Pending";

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = LayoutInflater.from(this).inflate(R.layout.confirm_order,null);

            TextView conf_name = (TextView)view.findViewById(R.id.confirm_name);
            TextView conf_type = (TextView)view.findViewById(R.id.confirm_type);
            TextView conf_price = (TextView)view.findViewById(R.id.confirm_price);
            TextView conf_qty = (TextView)view.findViewById(R.id.confirm_qty);
            TextView conf_total = (TextView)view.findViewById(R.id.confirm_Total);

            conf_type.setText(type);
            conf_name.setText(name);
            conf_price.setText(price);
            conf_qty.setText(qty);
            conf_total.setText(total);

            builder.setView(view);

            builder.setNegativeButton("CONFIRM", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    try {
                        ConfirmAddedToCart();
                    }
                    catch (Exception e){
                        Log.e("TAG",e.getMessage());
                        e.printStackTrace();
                    }

                }
            });
            builder.show();
        }
        catch (Exception e){
            Log.e("TAG","Error to execute add to cart"+"\n"+e.getMessage());
            e.printStackTrace();
        }


    }
    private void ConfirmAddedToCart() {

        try {

            databaseHelper.insertData(String.valueOf(cart_Id),buyer_Id,buyerName,buyerAddress,
                    store_Id,storeName,type,product_Id,name,status,Double.parseDouble(price),
                    Double.parseDouble(total),Integer.parseInt(qty));

            Toast.makeText(this, "Added To Cart!", Toast.LENGTH_SHORT).show();


        }
        catch (Exception e){
            Log.e("TAG","Error to confirm order"+"\n"+e.getMessage());
            e.printStackTrace();
        }

    }

    private void ShowProductDetail() {

        String id = getIntent().getStringExtra("Product ID");

        productType.setText(getIntent().getStringExtra("Product Type"));
        productName.setText(getIntent().getStringExtra("Product Name"));
        productPrice.setText(String.valueOf(getIntent().getStringExtra("Product Price")));
    }

}