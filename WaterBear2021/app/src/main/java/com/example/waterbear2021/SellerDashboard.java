package com.example.waterbear2021;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellerDashboard extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private FirebaseFirestore fStore;

    FirestoreRecyclerAdapter<ProductList, ProductViewHolder> adapter;

    private Context mycontext = SellerDashboard.this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_dashboard);

        fab = findViewById(R.id.fab1);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SellerDashboard.this, AddProducts.class);
                startActivity(intent);


            }
        });

        recyclerView = findViewById(R.id.recyclerSeller);

        fStore = FirebaseFirestore.getInstance();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        showTask();

    }

    private void showTask() {

        String user = mAuth.getCurrentUser().getUid();

        Query query = fStore.collection("Merchants").document(user).collection("Products");

        FirestoreRecyclerOptions<ProductList> options = new FirestoreRecyclerOptions.Builder<ProductList>()
                .setQuery(query, ProductList.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<ProductList, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull ProductList model) {
                holder.type.setText(model.getProductType());
                holder.textView1.setText(model.getProductName());
                holder.textView2.setText(String.valueOf(model.getProductPrice()));

            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_row, parent, false);
                return new ProductViewHolder(v);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        if (item.getTitle().equals("Update")){
            showUpdateDialog(adapter.getItem(item.getOrder()).getProductID(),adapter.getItem(item.getOrder()));
        }
        else if(item.getTitle().equals("Delete")){
            deleteTask(adapter.getItem(item.getOrder()).getProductID());
        }

        return super.onContextItemSelected(item);
    }

    private void deleteTask(String productID) {

        fStore.collection("Merchants").document(mAuth.getCurrentUser().getUid())
                .collection("Products").document(productID).delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    Toast.makeText(mycontext, "Successfully deleted a product!", Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("TAG","Error in Delete Product!"+"\n"+e.getMessage());
            }
        });

    }

    private void showUpdateDialog(String productID, ProductList item) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update");
        builder.setMessage("Please Update the Fields");

        List<String> typesOfWater = Arrays.asList("Purified Drinking Water","Distilled Drinking Water","Alkaline",
                "Spring Water","ElectroLyte Water","Tap Water","Mineral Water");
        List<String> products = Arrays.asList("Water Container","Bottled Water(500ml)","Bottled Water(1Liter)",
                "Bottled Water(4Liters)","Bottled Water(5Liters)","Bottled Water(6Liters)");

        View updateLayout = LayoutInflater.from(this).inflate(R.layout.custom_layout, null);
        TextView textView = updateLayout.findViewById(R.id.edit_update_id);
        TextView textView1 = updateLayout.findViewById(R.id.edit_update_name);
        final Spinner spinnerType = updateLayout.findViewById(R.id.Update_types);
        final Spinner spinnerName = updateLayout.findViewById(R.id.Update_name);


        ArrayAdapter TypeAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item,typesOfWater);
        ArrayAdapter ProductAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item,products);

        TypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ProductAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerType.setAdapter(TypeAdapter);
        spinnerName.setAdapter(ProductAdapter);

        EditText editText1 = updateLayout.findViewById(R.id.edit_update_price);

        textView.setText(item.getProductID());
        textView1.setText(item.getStoreName());
        spinnerType.setTag(item.getProductType());
        spinnerName.setTag(item.getProductName());
        editText1.setText(String.valueOf(item.getProductPrice()));

        builder.setView(updateLayout);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String owner = mAuth.getCurrentUser().getUid();
                String owner_name = textView1.getText().toString();
                String prodID = textView.getText().toString();
                String WaterType = spinnerType.getSelectedItem().toString();
                String name = spinnerName.getSelectedItem().toString();

                String price = editText1.getText().toString();

                ProductList todo = new ProductList(owner,owner_name,prodID,WaterType,name,Double.parseDouble(price));
                fStore.collection("Merchants").document(owner).collection("Products").document(productID).set(todo);
                Toast.makeText(SellerDashboard.this, "Product is Updated!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();

    }

}