package com.example.waterbeardeliveryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Dashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_bar);

        bottomNav.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new OrderToBeDeliverFragment()).commit();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @SuppressLint("NonConstantResourceId")
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment fragment = null;

                    switch (item.getItemId()) {
                        case R.id.seller_ToBeDeliver:
                            fragment = new OrderToBeDeliverFragment();
                            Toast.makeText(Dashboard.this, "To be deliver", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.seller_delivered:
                            fragment = new OrderDeliveredFragment();
                            Toast.makeText(Dashboard.this, "Delivered", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.seller_history:
                            fragment = new OrderHistoryFragment();
                            Toast.makeText(Dashboard.this, "History", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    assert fragment != null;
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                    return true;
                }
            };
}