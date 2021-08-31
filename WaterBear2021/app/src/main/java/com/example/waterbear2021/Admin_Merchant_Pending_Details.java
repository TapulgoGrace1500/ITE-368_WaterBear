package com.example.waterbear2021;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class Admin_Merchant_Pending_Details extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference mRef = db.collection("Merchants");

    private TextView fName, lName, Email, Password, cNumber, address, sID, sName, sDescript;
    private ImageView sImage, ProofImage;

    private String firstname, lastname, email, pword, conNum, adds, id, sname, sdesc, sImg, proofImg;
    private Button approved;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin__merchant__pending__details);

        fName = findViewById(R.id.mFname);
        lName = findViewById(R.id.mLName);
        Email = findViewById(R.id.mEmail);
        Password = findViewById(R.id.mPword);
        cNumber = findViewById(R.id.mContact);
        address = findViewById(R.id.mAddress);
        sID = findViewById(R.id.mID);
        sName = findViewById(R.id.mStoreName);
        sDescript = findViewById(R.id.mDescription);
        sImage = findViewById(R.id.mStoreImage);
        approved = findViewById(R.id.ApprovedMerchant);
        ProofImage = findViewById(R.id.proofOfPayImageView);

        firstname = getIntent().getStringExtra("First Name");
        lastname = getIntent().getStringExtra("Last Name");
        email = getIntent().getStringExtra("Seller Email");
        pword = getIntent().getStringExtra("Password");
        conNum = getIntent().getStringExtra("Contact Number");
        adds = getIntent().getStringExtra("Address");
        id = getIntent().getStringExtra("Store ID");
        sname = getIntent().getStringExtra("Store Name");
        sdesc = getIntent().getStringExtra("Store Description");
        sImg = getIntent().getStringExtra("Store Image");

        fName.setText(firstname);
        lName.setText(lastname);
        Email.setText(email);
        Password.setText(String.valueOf(pword.hashCode()));
        cNumber.setText(conNum);
        address.setText(adds);
        sID.setText(id);
        sName.setText(sname);
        sDescript.setText(sdesc);
        Picasso.get().load(sImg).into(sImage);

        db.collection("Payments").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                proofImg = documentSnapshot.getString("Attached Image");
                Picasso.get().load(proofImg).into(ProofImage);

            }
        });

        approved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApprovedMerchant();
            }
        });


    }
    private void ApprovedMerchant() {
        mRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    mRef.whereEqualTo("Approved",null).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            mRef.document(id).update("Approved","1");
                            Toast.makeText(Admin_Merchant_Pending_Details.this, "Approved Merchant Successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Admin_Merchant_Pending_Details.this,Admin_Merchant_Pending_Profile.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }
        });
    }
}