package com.example.waterbear2021;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

public class StoreProductsDashboard extends AppCompatActivity {

    private ImageView imageView;
    private TextView StoreName, StoreDesc;
    private RecyclerView recyclerView;
    private FloatingActionButton CartButton;
    private ProductsRecyclerAdapter productsRecyclerAdapter;
    private CollectionReference pRef = FirebaseFirestore.getInstance().collection("Merchants");
    private CollectionReference cartRef = FirebaseFirestore.getInstance().collection("Orders");
    private String id, name, cID, sID;
    private FirebaseAuth mAuth;
    private String prodID;
    private DatabaseHelper databaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_products_dashboard);

        mAuth = FirebaseAuth.getInstance();

        imageView = findViewById(R.id.imgStore);
        StoreName = findViewById(R.id.storeName);
        StoreDesc = findViewById(R.id.storeDesc);
        recyclerView = findViewById(R.id.recyclerStoreProduct);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseHelper = new DatabaseHelper(this);

        CartButton = findViewById(R.id.cartFab);

         id = getIntent().getStringExtra("Store ID");
         name = getIntent().getStringExtra("Store Name");

        //CartButton.hide();

        CartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StoreProductsDashboard.this, Cart.class);
                intent.putExtra("Store ID",id);
                intent.putExtra("Store Name",name);
                startActivity(intent);

            }
        });

        try {
            Cursor cursor = databaseHelper.getData();

            String storeIdFromCart = cursor.getString(5);

            if (!storeIdFromCart.equals(id)){
                if (DatabaseHelper.isCartAvailable(this)){
                    DatabaseHelper.deleteData(this);
                }
            }
        }
        catch (Exception e){
            Log.e("TAG",e.getMessage());
            e.printStackTrace();
        }

        ShowStoreFragment();

    }

    @Override
    protected void onStart() {
        super.onStart();
        productsRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        productsRecyclerAdapter.stopListening();
    }

    private void ShowStoreFragment() {

        String store = getIntent().getStringExtra("Store Image");
        String id = getIntent().getStringExtra("Store ID");

        Picasso.get().load(store).into(imageView);
        StoreName.setText(getIntent().getStringExtra("Store Name"));
        StoreDesc.setText(getIntent().getStringExtra("Store Description"));

        FirestoreRecyclerOptions<ProductList> options = new FirestoreRecyclerOptions.Builder<ProductList>()
                        .setQuery(pRef.document(id).collection("Products").orderBy("productPrice"),ProductList.class)
                        .build();

        productsRecyclerAdapter = new ProductsRecyclerAdapter(getApplicationContext(),options);
        recyclerView.setAdapter(productsRecyclerAdapter);

    }
}