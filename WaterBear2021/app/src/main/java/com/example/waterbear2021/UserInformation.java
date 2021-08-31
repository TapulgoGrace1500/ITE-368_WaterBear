package com.example.waterbear2021;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserInformation extends AppCompatActivity {

    private TextView name, email, contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);

        name = findViewById(R.id.tv_ProfName);
        email = findViewById(R.id.tv_ProfEmail);
        contact = findViewById(R.id.tv_ProfNumber);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentId = user.getUid();
        DocumentReference reference;
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        reference = firebaseFirestore.collection("Customers").document(currentId);

        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.getResult().exists()){

                    String nameResult = task.getResult().getString("First Name") +"\t"+ task.getResult().getString("Last Name");
                    String emailResult = task.getResult().getString("Email");
                    String contactResult = task.getResult().getString("Contact Number");

                    name.setText(nameResult);
                    email.setText(emailResult);
                    contact.setText(contactResult);

                }
            }
        });
    }
}