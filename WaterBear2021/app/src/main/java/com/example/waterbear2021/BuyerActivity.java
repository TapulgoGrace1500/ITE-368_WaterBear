package com.example.waterbear2021;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Objects;

public class BuyerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView nav_view;

    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private StorageReference storageReference;
    private ArrayList<StoresView>storesViews;
    private ArrayList<ProductList>productLists;
    private RecyclerAdapter recyclerAdapter;
    private Context mycontext = BuyerActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer);

        Toolbar toolbar = findViewById(R.id.toolbars);
        setSupportActionBar(toolbar);

        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        progressDialog = new ProgressDialog(this);
        productLists = new ArrayList<>();
        storesViews = new ArrayList<>();

        drawerLayout = findViewById(R.id.drawer);
        nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        recyclerView = findViewById(R.id.dashboard_recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        GetDataFromFirebase();


    }

    private void GetDataFromFirebase() {

        fStore.collection("Merchants").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    fStore.collection("Merchants").whereEqualTo("Approved","1").orderBy("Store Name")
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@androidx.annotation.Nullable QuerySnapshot value, @androidx.annotation.Nullable FirebaseFirestoreException error) {
                            if (value != null) {
                                for (DocumentSnapshot snapshot: value){

                                    StoresView storesView = new StoresView();
                                    String id = snapshot.getId();

                                    // Maybe in here the condition will change to "Result of Calculated latitude and longitude"
                                    // if(result.equals(2km||3km)) then execute the try catch to display the stores based of calculated results

                                    if (snapshot.getId().equals(id)) {

                                        storesView.setStoreID(snapshot.getId());
                                        storesView.setImage(snapshot.getString("Store Image"));
                                        storesView.setTitle(snapshot.getString("Store Name"));
                                        storesView.setDescript(snapshot.getString("Store Description"));

                                        storesViews.add(storesView);
                                    }

                                }
                            }

                            recyclerAdapter = new RecyclerAdapter(mycontext,storesViews);
                            recyclerView.setAdapter(recyclerAdapter);
                            recyclerAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.logout){
            FirebaseAuth.getInstance().signOut();//logout
            startActivity(new Intent(BuyerActivity.this,MainActivity.class));
            finish();
        }
        if (item.getItemId() == R.id.Usermail){
            startActivity(new Intent(getApplicationContext(),UserInformation.class));
            Toast.makeText(this, "User Profile Information", Toast.LENGTH_SHORT).show();
        }
        if (item.getItemId() == R.id.location){

//            startActivity(new Intent(getApplicationContext(),Location.class));
            Toast.makeText(this, "Location Address", Toast.LENGTH_SHORT).show();
        }
        if (item.getItemId() == R.id.CustomerOrder){
            Toast.makeText(this, "Orders", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getApplicationContext(),OrderSummary.class);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.terms){
            Toast.makeText(this, "You've Click Terms and Conditions", Toast.LENGTH_SHORT).show();
        }
        if (item.getItemId() == R.id.privacy){
            Toast.makeText(this, "You've Click Privacy", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

}