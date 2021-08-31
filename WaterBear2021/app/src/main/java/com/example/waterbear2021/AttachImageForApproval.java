package com.example.waterbear2021;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class AttachImageForApproval extends AppCompatActivity {

    private Button attachPayment;
    private StorageReference storageReference,ProofOfPaymentImageRef;
    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private String fName,lName,time;
    private ProgressDialog progressDialog;
    private AlertDialog alertDialog;
    private AlertDialog.Builder builderDialog;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attach_image_for_approval);

        attachPayment = findViewById(R.id.btn_attach);
        imageView = findViewById(R.id.attached_billingStatement);
        Picasso.get().load(R.drawable.billing).into(imageView);

        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(this);

        fStore.collection("Merchants").document(mAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if (value != null){
                    fName = value.getString("First Name").trim();
                    lName = value.getString("Last Name").trim();
                    time = value.getString("Time Register").trim();
                }

            }
        });

        attachPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlerDialog(R.layout.attach_proof_of_payment);
            }
        });
    }

    private void showAlerDialog(int attach_proof_of_payment) {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2000){
            if (resultCode == Activity.RESULT_OK){
                Uri uriImage = data.getData();

                uploadProofOfPaymentImage(uriImage);
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

                            if (uri != null) {
                                DocumentReference PaymentDf = fStore.collection("Payments").document(mAuth.getCurrentUser().getUid());

                                String image = uri.toString();

                                Map<String, Object> paymentInfo = new HashMap<>();
                                paymentInfo.put("First Name", fName);
                                paymentInfo.put("Last Name", lName);
                                paymentInfo.put("Time Submitted", time);
                                paymentInfo.put("Seller ID", mAuth.getCurrentUser().getUid());
                                paymentInfo.put("Attached Image",image);

                                PaymentDf.set(paymentInfo);
                                fStore.collection("Merchants")
                                        .document(mAuth.getCurrentUser().getUid()).update("ProofOfPayment","1");
                                progressDialog.dismiss();
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                finish();
                            }
                        }
                    });
                }
            }
        });
    }
}