package com.example.waterbeardeliveryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity {

    private EditText email, password;
    private Button SignIn;
    private CheckBox showPassword;

    private ProgressDialog progressDialog;
    boolean valid = true;
    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = findViewById(R.id.editText_Username);
        password = findViewById(R.id.editText_Password);
        SignIn = findViewById(R.id.btn_SignIn);
        showPassword = findViewById(R.id.checkboxShowPword);

        storageReference = FirebaseStorage.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);

        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    password.setTransformationMethod(null);
                }
                else {
                    password.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkField(email);
                checkField(password);

                if (valid){
                    
                    progressDialog.setMessage("Please wait for a minute to LogIn....");
                    progressDialog.show();

                    mAuth.signInWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {

                                    if (authResult != null){

                                        FirebaseUser user = mAuth.getCurrentUser();

                                        if (user.isEmailVerified()){
                                            email.setText("");
                                            password.setText("");
                                            email.setEnabled(false);
                                            password.setEnabled(false);
                                            progressDialog.dismiss();

                                            Toast.makeText(MainActivity.this, "Login Successfully!", Toast.LENGTH_SHORT).show();


                                            checkUserAccessLevel(authResult.getUser().getUid());
                                        }
                                        else {
                                            email.setText("");
                                            password.setText("");
                                            progressDialog.dismiss();
                                            user.sendEmailVerification();
                                            Toast.makeText(MainActivity.this, "Please Check your Email to Verify Account and LogIn!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Login Failed!", Toast.LENGTH_SHORT).show();

                        }
                    });
                    
                }
            }
        });
    }

    private void checkUserAccessLevel(String uid) {

        DocumentReference CheckSellerRequest = fStore.collection("Merchants").document(uid);

        CheckSellerRequest.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override

            public void onSuccess(DocumentSnapshot documentSnapshot) {

                try {

                    if (documentSnapshot.getString("isSeller")!=null) {

                        try {
                            if (documentSnapshot.getString("Approved")!=null) {
                                Intent intent = new Intent(getApplicationContext(),DeliveryManCredentials.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                        catch (Exception error){

                            Log.d("TAG","Error in identifying user access");
                            error.printStackTrace();

                        }
                    }
                }
                catch (Exception e){

                    Log.e("TAG","Error in checkUserLevelAccess!");
                    e.printStackTrace();

                }

            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.d("TAG", "Account Does not Exist!");
            }
        });
    }

    public boolean checkField(EditText textField){

        if (textField.getText().toString().isEmpty()){
            textField.setError("Required");
            valid = false;
        }
        else {
            valid = true;

        }
        return valid;
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser onUser = mAuth.getCurrentUser();

        if (onUser != null) {

            if (onUser.isEmailVerified()) {

                if (FirebaseAuth.getInstance().getCurrentUser() != null) {

                    DocumentReference dfSellerRequestCheck = FirebaseFirestore.getInstance().collection("Merchants")
                            .document(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    dfSellerRequestCheck.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            if (documentSnapshot.exists()) {

                                try {
                                    if (documentSnapshot.getString("Approved") != null) {
                                        Intent intent = new Intent(getApplicationContext(),DeliveryManCredentials.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                                catch (Exception error){

                                    Log.d("TAG","Error in identifying user access");
                                    error.printStackTrace();

                                }
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(MainActivity.this, "Please Login and/or Register Account!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            } else {
                onUser.sendEmailVerification();
                Toast.makeText(this, "Check Email to Verify!", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
            }
        }
    }
}