package com.example.waterbear2021;

import androidx.annotation.NonNull;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class DeliveryListProfile extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton addFab;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private FirebaseFirestore fStore;

    FirestoreRecyclerAdapter<DeliveryProfile, DeliveryViewHolder> adapter;

    private Context mycontext = DeliveryListProfile.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_list_profile);

        addFab = findViewById(R.id.fabDelivery);

        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeliveryListProfile.this, AddDelivery.class);
                startActivity(intent);


            }
        });

        recyclerView = findViewById(R.id.recyclerDelivery);

        fStore = FirebaseFirestore.getInstance();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        showDeliveryList();
    }

    private void showDeliveryList() {

        String user = mAuth.getCurrentUser().getUid();

        Query query = fStore.collection("Merchants").document(user).collection("DeliveryMan");

        FirestoreRecyclerOptions<DeliveryProfile> options = new FirestoreRecyclerOptions.Builder<DeliveryProfile>()
                .setQuery(query, DeliveryProfile.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<DeliveryProfile, DeliveryViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull DeliveryViewHolder holder, int position, @NonNull DeliveryProfile model) {
                holder.fullname.setText(model.getFirstName()+" "+model.getLastName());
                holder.contactNum.setText(model.getContactNumber());

            }

            @NonNull
            @Override
            public DeliveryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.delivery_profile_row, parent, false);
                return new DeliveryViewHolder(v);
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
            showUpdateDialog(adapter.getItem(item.getOrder()).getOwnerId(),
                    adapter.getItem(item.getOrder()).getDeliveryId(),
                    adapter.getItem(item.getOrder()));
        }
        else if(item.getTitle().equals("Delete")){
            deleteDelivery(adapter.getItem(item.getOrder()).getOwnerId(),
                    adapter.getItem(item.getOrder()).getDeliveryId());
        }
        return super.onContextItemSelected(item);
    }

    private void showUpdateDialog(String ownerId, String deliveryId, DeliveryProfile item) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update");
        builder.setMessage("Please Update the Fields");

        View updateLayout = LayoutInflater.from(this).inflate(R.layout.custom_delivery_layout, null);
        TextView textView = updateLayout.findViewById(R.id.edit_update_OwnerId);
        TextView textView1 = updateLayout.findViewById(R.id.edit_update_DeliveryId);
        EditText editText1 = updateLayout.findViewById(R.id.edit_update_firstname);
        EditText editText2 = updateLayout.findViewById(R.id.edit_update_lastname);
        EditText editText3 = updateLayout.findViewById(R.id.edit_update_contactnumber);

        textView.setText(item.getOwnerId());
        textView1.setText(item.getDeliveryId());
        editText1.setText(item.getFirstName());
        editText2.setText(item.getLastName());
        editText3.setText(item.getContactNumber());

        builder.setView(updateLayout);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String ownerID = textView.getText().toString();
                String deliveryID = textView1.getText().toString();
                String fname = editText1.getText().toString();
                String lname = editText2.getText().toString();
                String cNumber = editText3.getText().toString();

                DeliveryProfile updateDeliveryProfile = new DeliveryProfile(deliveryID,ownerID,fname,lname,cNumber);

                fStore.collection("Merchants").document(ownerId)
                        .collection("DeliveryMan").document(deliveryId).set(updateDeliveryProfile);

                Toast.makeText(DeliveryListProfile.this, "Delivery Profile is Updated!", Toast.LENGTH_SHORT).show();
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

    private void deleteDelivery(String ownerId, String deliveryId) {
        fStore.collection("Merchants").document(ownerId)
                .collection("DeliveryMan").document(deliveryId).delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            Toast.makeText(mycontext, "Successfully deleted a Delivery Man!", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("TAG","Error in Delete Delivery Man!"+"\n"+e.getMessage());
            }
        });
    }
}