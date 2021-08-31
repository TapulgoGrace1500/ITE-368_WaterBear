package com.example.waterbear2021;

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
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UserRegistration extends AppCompatActivity{

    private EditText FirstName, LastName, Email, Pword, ConfirmPword, ContactNumber, RegAddress;
    private Button btnRegisterAccount,GPS;
    private ProgressDialog progressDialog;
    boolean valid = true;
    private CheckBox showpassword, isSellerBox, isBuyerBox;
    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private String currentDate;

//    private LocationManager locationManager;
//    protected double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        Calendar calendar = Calendar.getInstance();
        currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());

        isSellerBox = findViewById(R.id.isSeller);
        isBuyerBox = findViewById(R.id.isBuyer);
        showpassword = findViewById(R.id.RegshowPassword);

        showpassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Pword.setTransformationMethod(null);
                    ConfirmPword.setTransformationMethod(null);
                }
                else {
                    Pword.setTransformationMethod(new PasswordTransformationMethod());
                    ConfirmPword.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        FirstName = findViewById(R.id.editText_RegFirstname);
        LastName = findViewById(R.id.editText_RegLastname);
        Email = findViewById(R.id.editText_RegEmail);
        Pword = findViewById(R.id.editText_RegPassword);
        ConfirmPword = findViewById(R.id.editText_RegReTypePassword);
        ContactNumber = findViewById(R.id.editText_RegContact);
        RegAddress = findViewById(R.id.editText_RegAddress);

//        GPS = findViewById(R.id.Gps_Location);
        btnRegisterAccount = findViewById(R.id.btn_Register);
        progressDialog = new ProgressDialog(this);

        btnRegisterAccount.setEnabled(false);

//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//
//        if (ActivityCompat.checkSelfPermission(UserRegistration.this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED){
//
//            ActivityCompat.requestPermissions(UserRegistration.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},100);
//
//        }
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
//
//        GPS.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Log.d("TAG","Gps is clicked!");
//
//            }
//        });

        isSellerBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    isBuyerBox.setChecked(false);
                    btnRegisterAccount.setEnabled(true);

                } else {
                    btnRegisterAccount.setEnabled(false);
                }
            }
        });

        isBuyerBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    isSellerBox.setChecked(false);
                    btnRegisterAccount.setEnabled(true);

                } else {
                    btnRegisterAccount.setEnabled(false);
                }
            }
        });

        if (isSellerBox.isChecked() || isBuyerBox.isChecked()) {
            Toast.makeText(UserRegistration.this, "Select Type Account!", Toast.LENGTH_SHORT).show();
        }

        btnRegisterAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkField(FirstName);
                checkField(LastName);
                checkField(RegAddress);
                checkField(ContactNumber);
                checkField(Email);
                checkField(Pword);
                checkField(ConfirmPword);

                if (valid) {

                    try {
                        if (Pword.getText().toString().trim().equals(ConfirmPword.getText().toString().trim())){

                            progressDialog.setMessage("Please wait for a minute to Register....");
                            progressDialog.show();

                            mAuth.createUserWithEmailAndPassword(Email.getText().toString().trim(), Pword.getText().toString().trim())
                                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                        @Override
                                        public void onSuccess(AuthResult authResult) {

                                            String Ruser = mAuth.getCurrentUser().getUid();

                                            if (isBuyerBox.isChecked()) {

                                                        DocumentReference df = fStore.collection("Customers").document(Ruser);

                                                        Map<String, Object> userInfo = new HashMap<>();
                                                        userInfo.put("Buyer ID", Ruser);
                                                        userInfo.put("First Name", FirstName.getText().toString().trim());
                                                        userInfo.put("Last Name", LastName.getText().toString().trim());
                                                        userInfo.put("Address", RegAddress.getText().toString().trim());
                                                        userInfo.put("Contact Number", ContactNumber.getText().toString().trim());
                                                        userInfo.put("Email", Email.getText().toString().trim());
                                                        userInfo.put("Password", Pword.getText().toString().trim());
                                                        userInfo.put("isBuyer", "1");
                                                        userInfo.put("Time Register",currentDate);
                                                        df.set(userInfo);

                                                        progressDialog.dismiss();
                                                        Toast.makeText(UserRegistration.this, "You've Successfully Created an Account!", Toast.LENGTH_SHORT).show();

                                                        BuyerProceedToLogIn();
                                            }
                                            if (isSellerBox.isChecked()) {

                                                        DocumentReference df = fStore.collection("Merchants").document(Ruser);

                                                        Map<String, Object> userInfo = new HashMap<>();
                                                        userInfo.put("Seller ID", Ruser);
                                                        userInfo.put("First Name", FirstName.getText().toString().trim());
                                                        userInfo.put("Last Name", LastName.getText().toString().trim());
                                                        userInfo.put("Address", RegAddress.getText().toString().trim());
                                                        userInfo.put("Contact Number", ContactNumber.getText().toString().trim());
                                                        userInfo.put("Email", Email.getText().toString().trim());
                                                        userInfo.put("Password", Pword.getText().toString().trim());
                                                        userInfo.put("isSeller", "1");
                                                        userInfo.put("Time Register",currentDate);
                                                        df.set(userInfo);

                                                        progressDialog.dismiss();
                                                        Toast.makeText(UserRegistration.this, "You've Successfully Created an Account!", Toast.LENGTH_SHORT).show();

                                                        SellerProceedToLogIn();
                                            }
                                        }

                                        private void SellerProceedToLogIn() {

                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            intent.putExtra("First Name", FirstName.getText().toString().trim());
                                            intent.putExtra("Last Name", LastName.getText().toString().trim());
                                            intent.putExtra("Address", RegAddress.getText().toString().trim());
                                            intent.putExtra("Contact Number", ContactNumber.getText().toString().trim());
                                            intent.putExtra("Email", Email.getText().toString().trim());
                                            intent.putExtra("Password", Pword.getText().toString().trim());
                                            intent.putExtra("isSeller", "1");
                                            intent.putExtra("Time Register",currentDate);
                                            startActivity(intent);
                                            finish();
                                        }

                                        private void BuyerProceedToLogIn() {
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }

                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    progressDialog.dismiss();
                                    Toast.makeText(UserRegistration.this, "Account Failed to Register!", Toast.LENGTH_SHORT).show();
                                    Toast.makeText(UserRegistration.this, "Please Check Register as Seller or Buyer!", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                        else {
                            Pword.setError("Incorrect");
                            ConfirmPword.setError("Incorrect");
                        }
                    }
                    catch (Exception e){
                        Log.e("TAG","error to register user"+e.getMessage());
                        e.printStackTrace();
                        progressDialog.dismiss();
                        Toast.makeText(UserRegistration.this, "Please Fill Empty Fields!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
    public void checkField(EditText textField) {

        if (textField.getText().toString().trim().isEmpty()) {
            textField.setError("Required");
            valid = false;
        } else {
            valid = true;

        }
    }

//    @Override
//    public void onLocationChanged(@NonNull Location location) {
//
//        latitude = location.getLatitude();
//        longitude = location.getLongitude();
//        Log.d("TAG","Latitude : "+latitude);
//        Log.d("TAG","Latitude : "+longitude);
//
//        locationManager.removeUpdates(this);
//    }
//
//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras) {
//
//    }
//
//    @Override
//    public void onProviderEnabled(@NonNull String provider) {
//
//    }
//
//    @Override
//    public void onProviderDisabled(@NonNull String provider) {
//
//    }
}