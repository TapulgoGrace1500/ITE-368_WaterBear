package com.example.waterbear2021;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Admin_Customer_Profile extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference merchantRef = db.collection("Customers");
    private ArrayList<UserProfile> options;
    private RecyclerView recyclerView;

    private Admin_Customer_Adapter customer_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin__customer__profile);

        options = new ArrayList<>();

        recyclerView = findViewById(R.id.customer_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        customerRecyclerView();
    }

    private void customerRecyclerView() {

        merchantRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    merchantRef.whereEqualTo("isBuyer","1").orderBy("Buyer ID").addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (value != null) {
                                for (DocumentSnapshot snapshot: value){

                                    UserProfile userProfile = new UserProfile();
                                    String id = snapshot.getId();

                                    if (snapshot.getId().equals(id)) {

                                        userProfile.setFirstName(snapshot.getString("First Name"));
                                        userProfile.setLastName(snapshot.getString("Last Name"));
                                        userProfile.setEmail(snapshot.getString("Email"));
                                        userProfile.setContactNumber(snapshot.getString("Contact Number"));
                                        userProfile.setAddress(snapshot.getString("Address"));

                                        options.add(userProfile);
                                    }

                                }
                            }

                            customer_adapter = new Admin_Customer_Adapter(getApplicationContext(),options);
                            recyclerView.setAdapter(customer_adapter);
                            customer_adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });

    }
}