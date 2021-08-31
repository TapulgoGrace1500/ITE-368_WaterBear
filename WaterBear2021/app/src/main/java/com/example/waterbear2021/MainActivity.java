package com.example.waterbear2021;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.NavigationUI;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Document;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private EditText Username, Password;
    private CheckBox showPassword;
    private Button SignIn;
    private TextView tvRegisterUser;
    private ProgressDialog progressDialog;
    boolean valid = true;
    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private StorageReference storageReference;
    private String fName,lName,address,conNumbner,pWord,time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Username = findViewById(R.id.editText_Username);
        Password = findViewById(R.id.editText_Password);
        showPassword = findViewById(R.id.checkboxShowPword);

        SignIn = findViewById(R.id.btn_SignIn);
        tvRegisterUser = findViewById(R.id.tv_RegisterAccount);
        storageReference = FirebaseStorage.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);

         fName = getIntent().getStringExtra("First Name");
         lName = getIntent().getStringExtra("Last Name");
         address = getIntent().getStringExtra("Address");
         conNumbner = getIntent().getStringExtra("Contact Number");
         pWord = getIntent().getStringExtra("Password");
         time = getIntent().getStringExtra("Time Register");

         showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
             @Override
             public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                 if (isChecked){
                     Password.setTransformationMethod(null);
                 }
                 else {
                     Password.setTransformationMethod(new PasswordTransformationMethod());
                 }
             }
         });

        tvRegisterUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, UserRegistration.class));
                Username.setText("");
                Password.setText("");

            }
        });

        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkField(Username);
                checkField(Password);

                if (valid) {
                    progressDialog.setMessage("Please wait for a minute to LogIn....");
                    progressDialog.show();

                    mAuth.signInWithEmailAndPassword(Username.getText().toString().trim(), Password.getText().toString().trim())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {

                                    if (authResult != null){

                                        FirebaseUser user = mAuth.getCurrentUser();

                                        if (user.isEmailVerified()){
                                            Username.setText("");
                                            Password.setText("");
                                            progressDialog.dismiss();

                                            Toast.makeText(MainActivity.this, "Login Successfully!", Toast.LENGTH_SHORT).show();


                                            checkUserAccessLevel(authResult.getUser().getUid());
                                        }
                                        else {
                                            Username.setText("");
                                            Password.setText("");
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

        DocumentReference df = fStore.collection("Customers").document(uid);
        DocumentReference CheckSellerRequest = fStore.collection("Merchants").document(uid);
        DocumentReference admin = fStore.collection("Admin").document(uid);

        CheckSellerRequest.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override

            public void onSuccess(DocumentSnapshot documentSnapshot) {

                try {

                    if (documentSnapshot.getString("isSeller")!=null) {

                        try {
                            if (documentSnapshot.getString("Approved")!=null) {
                                Intent intent = new Intent(getApplicationContext(),Seller_Home.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(MainActivity.this, "Your Account is Approved!", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                if (documentSnapshot.getString("Pending")!=null && documentSnapshot.getString("ProofOfPayment")!=null){
                                    openCompleteRequirementDialog();
                                    FirebaseAuth.getInstance().signOut();
                                }
                                else if (documentSnapshot.getString("Pending")!=null &&
                                        documentSnapshot.getString("ProofOfPayment")==null){
                                    openDialog();
                                }
                                else {

                                    Intent intent = new Intent(getApplicationContext(),SellerActivity.class);
                                    intent.putExtra("First Name",fName);
                                    intent.putExtra("Last Name",lName);
                                    intent.putExtra("Address",address);
                                    intent.putExtra("Contact Number",conNumbner);
                                    intent.putExtra("Email",mAuth.getCurrentUser().getEmail());
                                    intent.putExtra("Password",pWord);
                                    intent.putExtra("isSeller","1");
                                    intent.putExtra("Time Register",time);
                                    startActivity(intent);
                                    finish();

                                }
                            }
                        }
                        catch (Exception error){

                            Log.d("TAG","Error in identifying user access");
                            error.printStackTrace();

                        }
                    }

                    else{
                        if (df != null){

                            df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Log.d("TAG", "onSuccess" + documentSnapshot.getData());

                                    if (!Objects.equals(documentSnapshot.getString("isBuyer"), "Spam")) {
                                        startActivity(new Intent(getApplicationContext(), BuyerActivity.class));
                                        finish();
                                    }
                                    else {
                                        Toast.makeText(MainActivity.this, "Account has been reported as Spam!", Toast.LENGTH_SHORT).show();
                                        FirebaseAuth.getInstance().signOut();
                                    }
                                }
                            });

                        }
                        if (admin != null){
                            admin.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.getString("isAdmin") != null){

                                        Intent intent = new Intent(getApplicationContext(),Admin.class);
                                        startActivity(intent);
                                        finish();

                                    }
                                }
                            });
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

    private void openCompleteRequirementDialog() {
        AlertDialogForCompleteRequirement alertDialogForCompleteRequirement = new AlertDialogForCompleteRequirement();
        alertDialogForCompleteRequirement.show(getSupportFragmentManager(),"complete requirement dialog");
    }

    private void openDialog() {
        ExampleAlertDialog exampleAlertDialog = new ExampleAlertDialog();
        exampleAlertDialog.show(getSupportFragmentManager(),"example dialog");
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

                    DocumentReference dfBuyer = FirebaseFirestore.getInstance().collection("Customers")
                            .document(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    DocumentReference dfSellerRequestCheck = FirebaseFirestore.getInstance().collection("Merchants")
                            .document(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    DocumentReference admin = FirebaseFirestore.getInstance().collection("Admin")
                            .document(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    dfBuyer.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            try {

                                if (documentSnapshot.getString("isBuyer") != null){

                                    if (!documentSnapshot.getString("isBuyer").equals("Spam")) {

                                        Toast.makeText(MainActivity.this, "Account Logging In!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), BuyerActivity.class));
                                        finish();
                                    }
                                    else{
                                        if (documentSnapshot.getString("isBuyer").equals("Spam")){
                                            Toast.makeText(MainActivity.this, "Account has been reported as Spam!", Toast.LENGTH_SHORT).show();
                                            FirebaseAuth.getInstance().signOut();

                                        }
                                    }

                                }
                                else if (dfSellerRequestCheck != null){

                                    dfSellerRequestCheck.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                                            if (documentSnapshot.exists()) {

                                                try {
                                                    if (documentSnapshot.getString("Approved") != null) {
                                                        Intent intent = new Intent(getApplicationContext(),Seller_Home.class);
                                                        startActivity(intent);
                                                        finish();
                                                        Toast.makeText(MainActivity.this, "Your Account is Approved!", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else {
                                                        if (documentSnapshot.getString("Pending") != null &&
                                                                documentSnapshot.getString("ProofOfPayment") != null){

                                                            openCompleteRequirementDialog();
                                                            FirebaseAuth.getInstance().signOut();
                                                        }
                                                        else if (documentSnapshot.getString("Pending")!=null &&
                                                                documentSnapshot.getString("ProofOfPayment")==null){
                                                            FirebaseAuth.getInstance().signOut();
                                                            Toast.makeText(MainActivity.this, "Please Login to submit proof of payment!", Toast.LENGTH_SHORT).show();
                                                        }
                                                        else {
                                                            startActivity(new Intent(getApplicationContext(), SellerActivity.class));
                                                            finish();                                                    }
                                                    }
                                                }
                                                catch (Exception error){

                                                    Log.d("TAG","Error in identifying user access");
                                                    error.printStackTrace();

                                                }
                                            }

                                        }
                                    });

                                }
                                else{
                                    admin.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (documentSnapshot.getString("isAdmin") != null){

                                                Toast.makeText(MainActivity.this, "Admin Logging In", Toast.LENGTH_SHORT).show();

                                                Intent intent = new Intent(getApplicationContext(),Admin.class);
                                                startActivity(intent);
                                                finish();

                                            }
                                        }
                                    });

                                }

                            }
                            catch (Exception e){

                                Log.e("TAG","Error found in OnStart!");
                                e.printStackTrace();

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
        else {
            Toast.makeText(this, "Login/Register an Account!", Toast.LENGTH_SHORT).show();
        }

    }

}