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
import androidx.fragment.app.Fragment;

public class SellerHomeFragment extends Fragment {

    private ImageButton productsButton, ordersButton, deliveryButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_seller_dashboard,container,false);

        productsButton = v.findViewById(R.id.seller_products);
        ordersButton = v.findViewById(R.id.seller_orders);
        deliveryButton = v.findViewById(R.id.seller_delivery);

        productsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(),SellerDashboard.class);
                startActivity(intent);
                Toast.makeText(getActivity(), "Welcome to Products List!", Toast.LENGTH_SHORT).show();

            }
        });

        ordersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(),Seller_Orders.class);
                startActivity(intent);
                Toast.makeText(getActivity(), "Welcome to Orders Field!", Toast.LENGTH_SHORT).show();

            }
        });

        deliveryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(),DeliveryListProfile.class);
                startActivity(intent);
                Toast.makeText(getActivity(), "Welcome to Delivery Field!", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }
}
