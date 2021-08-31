package com.example.waterbear2021;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.net.IDN;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class AddProducts extends AppCompatActivity {

    private Button btnSave;
    private EditText ProdPrice;
    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private final UUID id = UUID.randomUUID();
    private String store_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_products);

//        btnLogOut = findViewById(R.id.btnAddLogout);
        btnSave = findViewById(R.id.btn_Save);
        ProdPrice = findViewById(R.id.et_prodPrice);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        List<String> typesOfWater = Arrays.asList("Purified Drinking Water","Distilled Drinking Water","Alkaline",
                "Spring Water","ElectroLyte Water","Tap Water","Mineral Water");
        List<String> products = Arrays.asList("Water Container","Bottled Water(500ml)","Bottled Water(1Liter)",
                "Bottled Water(4Liters)","Bottled Water(5Liters)","Bottled Water(6Liters)");

        final Spinner type = findViewById(R.id.types);
        final Spinner Pname = findViewById(R.id.name);

        ArrayAdapter TypeAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item,typesOfWater);
        ArrayAdapter ProductAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item,products);


        TypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ProductAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        type.setAdapter(TypeAdapter);
        Pname.setAdapter(ProductAdapter);

        fStore.collection("Merchants").document(mAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if (value != null){
                    store_name = value.getString("Store Name");
                }

            }
        });


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fStore.collection("Merchants").document(mAuth.getCurrentUser().getUid());


                String PId = String.valueOf(id);
                String owner = mAuth.getCurrentUser().getUid();
                String owner_name = store_name;
                String WaterType = type.getSelectedItem().toString();
                String name = Pname.getSelectedItem().toString();
                String price = ProdPrice.getText().toString();

                if (!TextUtils.isEmpty(WaterType) && !TextUtils.isEmpty(name) && !TextUtils.isEmpty(price)){

                    //OBJECT TO ADD THE PRODUCTS TO DATABASE//
                    ProductList productList = new ProductList(owner,owner_name,PId,WaterType,name,Double.parseDouble(price));

                    //IN THIS OBJECT IT MUST BE VALIDATE SO THAT THE PRODUCT TYPE THAT IS ALREADY EXIST MUST NOT BE ADDED//
                    //ALSO IF THE SELLER WILL LOGOUT TO ITS ACCOUNT IT MUST NOT BE REPLACE BY ITS ID, MUST HAVE UNIQUE ID//
                    fStore.collection("Merchants").document(owner).collection("Products")
                            .document(PId).set(productList).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                Intent intent = new Intent(AddProducts.this, SellerDashboard.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(AddProducts.this, "Product Successfully Added!", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.e("TAG","FAILED TO ADD PRODUCTS : " + e);

                        }
                    });

                }
                else {
                    ClearTextField();
                    Toast.makeText(AddProducts.this, "Please fill empty fields!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void ClearTextField() {

        ProdPrice.setError("Required");
    }

}