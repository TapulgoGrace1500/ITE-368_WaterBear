package com.example.waterbear2021;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Seller_Orders extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller__orders);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_bar);

        bottomNav.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new OrderPendingFragment()).commit();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @SuppressLint("NonConstantResourceId")
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment fragment = null;

                    switch (item.getItemId()) {
                        case R.id.seller_pending:
                            fragment = new OrderPendingFragment();
                            Toast.makeText(Seller_Orders.this, "Pending", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.seller_processing:
                            fragment = new OrderProcessingFragment();
                            Toast.makeText(Seller_Orders.this, "Processing", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.seller_ToBeDeliver:
                            fragment = new OrderToBeDeliverFragment();
                            Toast.makeText(Seller_Orders.this, "To be deliver", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.seller_delivered:
                            fragment = new OrderDeliveredFragment();
                            Toast.makeText(Seller_Orders.this, "Delivered", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.seller_history:
                            fragment = new OrderHistoryFragment();
                            Toast.makeText(Seller_Orders.this, "History", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    assert fragment != null;
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                    return true;
                }
            };

}