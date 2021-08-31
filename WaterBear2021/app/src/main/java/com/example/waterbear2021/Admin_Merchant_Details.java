package com.example.waterbear2021;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class Admin_Merchant_Details extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference mRef = db.collection("Merchants");
    private StorageReference storageReference;

    private EditText fName, lName, Email, Password, cNumber, address, sID, sName, sDescript;
    private ImageView sImage;
    private Button delete;

    private String firstname, lastname, email, pword, conNum, adds, id, sname, sdesc, sImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin__merchant__details);

        fName = findViewById(R.id.PmFname);
        lName = findViewById(R.id.PmLName);
        Email = findViewById(R.id.PmEmail);
        Password = findViewById(R.id.PmPword);
        cNumber = findViewById(R.id.PmContact);
        address = findViewById(R.id.PmAddress);
        sID = findViewById(R.id.PmID);
        sName = findViewById(R.id.PmStoreName);
        sDescript = findViewById(R.id.PmDescription);
        sImage = findViewById(R.id.PmStoreImage);
        delete = findViewById(R.id.deleteMerchant);

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
        Password.setText((pword));
        cNumber.setText(conNum);
        address.setText(adds);
        sID.setText(id);
        sName.setText(sname);
        sDescript.setText(sdesc);
        Picasso.get().load(sImg).into(sImage);

        FieldSetEnabled();

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(Admin_Merchant_Details.this);
                dialog.setTitle("Are you sure?");
                dialog.setMessage("Deleting this Merchant Account will result completely removing the account from system!");
                dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mRef.document(id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){

                                    storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(sImg);
                                    storageReference.delete();

                                    Toast.makeText(Admin_Merchant_Details.this, "Successfully Deleted Merchant Account!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Admin_Merchant_Details.this,Admin_Merchant_Profile.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else {
                                    Toast.makeText(Admin_Merchant_Details.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.e("TAG","Deleting Merchant Data Failed!");
                                }
                            }
                        });
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
            }
        });


    }

    private void FieldSetEnabled() {

        fName.setEnabled(false);
        lName.setEnabled(false);
        Email.setEnabled(false);
        Password.setEnabled(false);
        cNumber.setEnabled(false);
        address.setEnabled(false);
        sID.setEnabled(false);
        sName.setEnabled(false);
        sDescript.setEnabled(false);
        sImage.setEnabled(false);
    }

}