package com.example.waterbear2021;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class SellerActivity extends AppCompatActivity {

    private ImageView ImageStore, ImageBilling;
    private EditText StoreName, StoreDesc;
    private Button btnLogOut, btnAttachProofOfPayment;
    private TextView header, tvBillStatement;
    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    private String userID, proofOfPayImage;
    private FirebaseUser user;
    private StorageReference StoreImageRef, ProofOfPaymentImageRef;
    private ProgressDialog progressDialog;
    private StorageReference storageReference;
    private String fName,lName,address,contactNum,pWord,time;
    private AlertDialog alertDialog;
    private AlertDialog.Builder builderDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller);

        ImageStore = findViewById(R.id.StoreImage);

        ImageBilling = findViewById(R.id.billingStatement);
        Picasso.get().load(R.drawable.billing).into(ImageBilling);
        tvBillStatement = findViewById(R.id.tv_billStatement);

        btnAttachProofOfPayment = findViewById(R.id.btn_AttachProofOfPayment);

        btnAttachProofOfPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog(R.layout.attach_proof_of_payment);
            }
        });

        ImageBilling.setVisibility(View.INVISIBLE);
        btnAttachProofOfPayment.setVisibility(View.INVISIBLE);
        tvBillStatement.setVisibility(View.INVISIBLE);

        btnLogOut = findViewById(R.id.btn_VerifySignOut);
        header = findViewById(R.id.tv_header);
        progressDialog = new ProgressDialog(this);

        StoreName = findViewById(R.id.editText_StoreName);
        StoreDesc = findViewById(R.id.editText_StoreDescription);

        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference();
        StoreImageRef = storageReference.child("Users/"+checkUserLevel(mAuth.getCurrentUser().getUid())+"/StoreImageProfile.jpg");

        userID = mAuth.getCurrentUser().getUid();
        user = mAuth.getCurrentUser();

         fStore.collection("Merchants").document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
             @Override
             public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                 if (value != null){
                     fName = value.getString("First Name").trim();
                     lName = value.getString("Last Name").trim();
                     address = value.getString("Address").trim();
                     contactNum = value.getString("Contact Number").trim();
                     pWord = value.getString("Password").trim();
                     time = value.getString("Time Register").trim();
                 }

             }
         });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();//logout
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        });

        if (StoreName.getText().toString() != null && StoreDesc.getText().toString() != null) {

            ImageStore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(openGallery, 1000);
                }
            });
        }
        else {
            Toast.makeText(this, "Please Complete The Entry!", Toast.LENGTH_SHORT).show();
        }

    }

    private void showAlertDialog(int attach_proof_of_payment) {
        builderDialog = new AlertDialog.Builder(this);
        View layoutView = getLayoutInflater().inflate(attach_proof_of_payment,null);

        ImageView imgProofPayment = layoutView.findViewById(R.id.ImageProofOfPayment);
        Button chooseImg = layoutView.findViewById(R.id.btnChooseFile);

        builderDialog.setView(layoutView);
        builderDialog.setTitle("Attach Proof of Payment");

        alertDialog = builderDialog.create();
        alertDialog.show();

        ProofOfPaymentImageRef = storageReference.child("Payments/"+checkUserLevelAccess(mAuth.getCurrentUser().getUid())+"/ProofOfPayment.jpg");

        ProofOfPaymentImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(imgProofPayment);
            }
        });

        chooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openImageGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openImageGallery,2000);
                alertDialog.dismiss();
            }
        });
    }

    private String checkUserLevelAccess(String uid) {

        DocumentReference paymentRef = fStore.collection("Merchants").document(uid);
        paymentRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.exists()){
                    ProofOfPaymentImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //Picasso.get().load(uri).into(imgProofPayment);
                        }
                    });
                }

            }
        });
        return uid;
    }

    private String checkUserLevel(String uid) {

        DocumentReference df = fStore.collection("Merchants").document(uid);
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("TAG","onSuccess" + documentSnapshot.getData());

                if (documentSnapshot.exists()){

                    StoreImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(ImageStore);

            }
        });

                }
            }
        });
        return uid;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000){
            if (resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();

                    uploadImageToFirebase(imageUri);
            }
        }
        else {
            if (requestCode == 2000){
                if (resultCode == Activity.RESULT_OK){
                    Uri uriImage = data.getData();
                    
                    uploadProofOfPaymentImage(uriImage);
                }
            }
        }
    }

    private void uploadProofOfPaymentImage(Uri uriImage) {

        StorageReference paymentImgRef = storageReference.child("Payments/"+checkUserLevelAccess(mAuth.getCurrentUser().getUid())+"/ProofOfPayment.jpg");
        progressDialog.setMessage("Please wait for a minute....");
        progressDialog.show();

        paymentImgRef.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                if (taskSnapshot != null){
                    paymentImgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Toast.makeText(SellerActivity.this, "Successfully Sent!", Toast.LENGTH_SHORT).show();

                            if (uri != null) {
                                DocumentReference PaymentDf = fStore.collection("Payments").document(userID);

                                String storeName = StoreName.getText().toString().trim();
                                String image = uri.toString();

                                Map<String, Object> paymentInfo = new HashMap<>();
                                paymentInfo.put("First Name", fName);
                                paymentInfo.put("Last Name", lName);
                                paymentInfo.put("Time Submitted", time);
                                paymentInfo.put("Seller ID", userID);
                                paymentInfo.put("Store Name", storeName);
                                paymentInfo.put("Attached Image",image);

                                PaymentDf.set(paymentInfo);

                              fStore.collection("Merchants")
                                        .document(userID).update("ProofOfPayment","1");
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            }
        });
    }

    private void uploadImageToFirebase(Uri imageUri) {

        StorageReference fileREF = storageReference.child("Users/"+checkUserLevel(mAuth.getCurrentUser().getUid())+"/StoreImageProfile.jpg");

        progressDialog.setMessage("Please wait for a minute....");
        progressDialog.show();

        fileREF.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                if (taskSnapshot != null) {

                    fileREF.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            Toast.makeText(SellerActivity.this, "Successfully Sent Request!", Toast.LENGTH_SHORT).show();
                            header.setText("Please Contact Admin for Approval of your Account!");
                            StoreName.setEnabled(false);
                            StoreDesc.setEnabled(false);
                            ImageStore.setEnabled(false);
                            ImageBilling.setVisibility(View.VISIBLE);
                            btnAttachProofOfPayment.setVisibility(View.VISIBLE);
                            tvBillStatement.setVisibility(View.VISIBLE);

                            if (uri != null) {

                                DocumentReference df = fStore.collection("Merchants").document(userID);

                                String storeN = StoreName.getText().toString().trim();
                                String storeD = StoreDesc.getText().toString().trim();
                                String owner = mAuth.getCurrentUser().getEmail();
                                String img = uri.toString();

                                if (storeN != null && storeD != null && img != null){

                                    Map<String, Object> storeInfo = new HashMap<>();
                                    storeInfo.put("First Name",fName);
                                    storeInfo.put("Last Name",lName);
                                    storeInfo.put("Address",address);
                                    storeInfo.put("Contact Number",contactNum);
                                    storeInfo.put("Password",pWord);
                                    storeInfo.put("isSeller","1");
                                    storeInfo.put("Time Register",time);
                                    storeInfo.put("Seller ID",userID);
                                    storeInfo.put("Seller Email",owner);
                                    storeInfo.put("Store ID", userID);
                                    storeInfo.put("Store Name", storeN);
                                    storeInfo.put("Store Description", storeD);
                                    storeInfo.put("Store Image",img);
                                    storeInfo.put("Approved",null);
                                    storeInfo.put("Pending","1");
                                    storeInfo.put("ProofOfPayment",null);

                                    df.set(storeInfo);

                                    progressDialog.dismiss();

                                    Picasso.get().load(uri).into(ImageStore);
                                }
                                else {
                                    StoreName.setError("Required!");
                                    StoreDesc.setError("Required!");
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(SellerActivity.this, "Please input complete requirement!", Toast.LENGTH_SHORT).show();
                            StoreName.setError("required");
                            StoreDesc.setError("required");
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressDialog.dismiss();
                Toast.makeText(SellerActivity.this, "Failed to Upload Image!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}