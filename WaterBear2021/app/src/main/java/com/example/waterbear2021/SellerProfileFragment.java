package com.example.waterbear2021;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SellerProfileFragment extends Fragment {

    private TextView storeName, email, fullName, contactNumber;
    private ImageView storeImage;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userId, StoreName, StoreImage, FirstName, LastName, Email, ContactNumber;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_seller_profile,container,false);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        storeName = view.findViewById(R.id.tv_seller_Fname);
        storeImage = view.findViewById(R.id.seller_profile_imageview);
        fullName = view.findViewById(R.id.sellerProfile_name_textview);
        email = view.findViewById(R.id.seller_email_textview);
        contactNumber = view.findViewById(R.id.seller_phone_textview);

        SetUpSellerProfile();
    }

    private void SetUpSellerProfile() {

        db.collection("Merchants").document(userId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){

                    StoreName = task.getResult().getString("Store Name");
                    StoreImage = task.getResult().getString("Store Image");
                    FirstName = task.getResult().getString("First Name");
                    LastName = task.getResult().getString("Last Name");
                    Email = task.getResult().getString("Seller Email");
                    ContactNumber = task.getResult().getString("Contact Number");

                    storeName.setText(StoreName);
                    Picasso.get().load(StoreImage).into(storeImage);
                    fullName.setText(FirstName+" "+LastName);
                    email.setText(Email);
                    contactNumber.setText(ContactNumber);

                }
            }
        });

    }
}
