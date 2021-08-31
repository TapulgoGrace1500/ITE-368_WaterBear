package com.example.waterbear2021;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.UUID;

public class AddDelivery extends AppCompatActivity {

    private Button btnSaveDeliveryMan;
    private EditText firstName, lastName, contactNumber;
    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private final UUID id = UUID.randomUUID();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_delivery);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        firstName = findViewById(R.id.deliveryFirstName);
        lastName = findViewById(R.id.deliveryLastName);
        contactNumber = findViewById(R.id.deliveryContactNumber);

        btnSaveDeliveryMan = findViewById(R.id.btn_add_delivery);

        btnSaveDeliveryMan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    fStore.collection("Merchants").document(mAuth.getCurrentUser().getUid());

                    String DeliveryId = String.valueOf(id);
                    String OwnerId = mAuth.getCurrentUser().getUid();
                    String deliveryFname = firstName.getText().toString().trim();
                    String deliveryLname = lastName.getText().toString().trim();
                    String deliveryCnumber = contactNumber.getText().toString().trim();

                    if (!TextUtils.isEmpty(DeliveryId) && !TextUtils.isEmpty(OwnerId) && !TextUtils.isEmpty(deliveryFname)
                            && !TextUtils.isEmpty(deliveryLname) && !TextUtils.isEmpty(deliveryCnumber)){

                        DeliveryProfile deliveryProfile = new DeliveryProfile(DeliveryId,OwnerId,deliveryFname,deliveryLname,deliveryCnumber);

                        fStore.collection("Merchants").document(OwnerId)
                                .collection("DeliveryMan").document(DeliveryId)
                                .set(deliveryProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Intent intent = new Intent(AddDelivery.this, DeliveryListProfile.class);
                                    startActivity(intent);
                                    finish();
                                    Toast.makeText(AddDelivery.this, "Delivery Man Successfully Added!", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Log.d("TAG","task is not successful onn added delivery man!");
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddDelivery.this, "onFailure to add Delivery", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                    else {
                        firstName.setError("Required");
                        lastName.setError("Required");
                        contactNumber.setError("Required");
                        Toast.makeText(AddDelivery.this, "Please Fill Empty Fields!", Toast.LENGTH_SHORT).show();
                    }

                }
                catch (Exception e){
                    Log.e("TAG",e.getMessage());
                    e.printStackTrace();
                }

            }
        });

    }
}