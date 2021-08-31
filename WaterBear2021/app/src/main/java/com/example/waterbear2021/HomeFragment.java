package com.example.waterbear2021;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    private ImageButton storeButton, customerButton, pendingButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home_admin,container,false);

        storeButton = v.findViewById(R.id.Merchant_admin_button);
        customerButton = v.findViewById(R.id.Customer_admin_button);
        pendingButton = v.findViewById(R.id.Merchant_admin_button_pending);

        storeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(),Admin_Merchant_Profile.class);
                startActivity(intent);
                Toast.makeText(getActivity(), "Welcome to Merchant's Approved Profile!", Toast.LENGTH_SHORT).show();

            }
        });

        pendingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(),Admin_Merchant_Pending_Profile.class);
                startActivity(intent);
                Toast.makeText(getActivity(), "Welcome to Merchant's Pending Profile!", Toast.LENGTH_SHORT).show();

            }
        });

        customerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),Admin_Customer_Profile.class);
                startActivity(intent);
                Toast.makeText(getActivity(), "Welcome to Customer's Profile!", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }
}
