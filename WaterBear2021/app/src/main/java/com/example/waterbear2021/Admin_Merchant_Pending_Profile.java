package com.example.waterbear2021;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Admin_Merchant_Pending_Profile extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference merchantRef = db.collection("Merchants");
    private ArrayList<StoresView> options;
    private RecyclerView recyclerView;

    private Admin_Merchant_Pending_Adapter merchant_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin__merchant__pending__profile);

        options = new ArrayList<>();

        recyclerView = findViewById(R.id.merchant_recyclerView_pending);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        merchantRecyclerView();
    }
    private void merchantRecyclerView() {

        merchantRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    merchantRef.whereEqualTo("Approved",null).orderBy("Store Name").addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (value != null) {
                                for (DocumentSnapshot snapshot: value){

                                    StoresView storesView = new StoresView();
                                    String id = snapshot.getId();

                                    if (snapshot.getId().equals(id)) {

                                        storesView.setFirstName(snapshot.getString("First Name"));
                                        storesView.setLastName(snapshot.getString("Last Name"));
                                        storesView.setEmail(snapshot.getString("Seller Email"));
                                        storesView.setPassword(snapshot.getString("Password"));
                                        storesView.setContactNumber(snapshot.getString("Contact Number"));
                                        storesView.setAddress(snapshot.getString("Address"));

                                        storesView.setStoreID(snapshot.getId());
                                        storesView.setImage(snapshot.getString("Store Image"));
                                        storesView.setTitle(snapshot.getString("Store Name"));
                                        storesView.setDescript(snapshot.getString("Store Description"));

                                        options.add(storesView);
                                    }

                                }
                            }

                            merchant_adapter = new Admin_Merchant_Pending_Adapter(getApplicationContext(),options);
                            recyclerView.setAdapter(merchant_adapter);
                            merchant_adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });

    }

}