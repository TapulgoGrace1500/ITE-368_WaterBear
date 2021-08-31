package com.example.waterbeardeliveryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class DeliveryManCredentials extends AppCompatActivity {

    private EditText firstname, lastname;
    private Button btnContinue;

    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_man_credentials);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);
        databaseHelper = new DatabaseHelper(this);

        firstname = findViewById(R.id.deliveryFirstName);
        lastname = findViewById(R.id.deliveryLastName);

        btnContinue = findViewById(R.id.btn_continue);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyInputtedCredential();
            }
        });

        if (DatabaseHelper.isDeliveryManAvailable(this)){
            DatabaseHelper.deleteData(this);
        }
    }

    private void verifyInputtedCredential() {

        try {

            if (!TextUtils.isEmpty(firstname.getText().toString().trim()) && !TextUtils.isEmpty(lastname.getText().toString().trim())){

                try {
                    fStore.collection("Merchants").document(mAuth.getCurrentUser().getUid())
                            .collection("DeliveryMan").get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot documentSnapshots) {

                                    if (documentSnapshots != null){
                                        for (DocumentSnapshot snapshot: documentSnapshots){

                                            try {
                                                if (snapshot.exists()){

                                                    String dataFirstName = snapshot.getString("firstName");
                                                    String dataLastName = snapshot.getString("lastName");

                                                    progressDialog.setMessage("Checking Access Credentials, Please wait....");
                                                    progressDialog.show();

                                                    if (firstname.getText().toString().equalsIgnoreCase(dataFirstName)
                                                            && lastname.getText().toString().equalsIgnoreCase(dataLastName)){

                                                        databaseHelper.insertData(snapshot.getId(),firstname.getText().toString(),lastname.getText().toString());

                                                        Intent intent = new Intent(getApplicationContext(),Dashboard.class);
                                                        intent.putExtra("DeliveryID",snapshot.getId());
                                                        startActivity(intent);

                                                        progressDialog.dismiss();
                                                        Toast.makeText(DeliveryManCredentials.this, "Access Granted!", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }
                                                    else {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(DeliveryManCredentials.this, "Access Denied!", Toast.LENGTH_SHORT).show();
                                                    }

                                                }
                                                else {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(DeliveryManCredentials.this, "Credential doesn't exist!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            catch (Exception e){
                                                progressDialog.dismiss();
                                                Log.e("TAG","onSuccess but failed!"+e.getMessage());
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(DeliveryManCredentials.this, "Access Failed!", Toast.LENGTH_SHORT).show();
                                    Log.e("TAG",e.getMessage());
                                    e.printStackTrace();
                                }
                            });
                }
                catch (Exception e){
                    Log.e("TAG",e.getMessage());
                    e.printStackTrace();
                }

            }
            else {
                firstname.setError("Required");
                lastname.setError("Required");
                Toast.makeText(this, "Please Fill Empty Fields!", Toast.LENGTH_SHORT).show();
            }

        }
        catch (Exception e){
            Log.e("TAG","first try catch error"+e.getMessage());
            e.printStackTrace();
        }

    }
}