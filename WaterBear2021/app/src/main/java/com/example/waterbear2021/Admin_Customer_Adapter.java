package com.example.waterbear2021;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;

public class Admin_Customer_Adapter extends RecyclerView.Adapter<Admin_Customer_Adapter.MerchantHolder> {

    private Context mContext;
    private ArrayList<UserProfile> options;

    public Admin_Customer_Adapter(Context mContext, ArrayList<UserProfile> options) {
        this.mContext = mContext;
        this.options = options;
    }

    @Override
    public void onBindViewHolder(@NonNull MerchantHolder holder, int position) {
        options.get(position);

        holder.Name.setText(options.get(position).getFirstName()+" "+options.get(position).getLastName());
        holder.ContactNumber.setText(options.get(position).getContactNumber());
        holder.Address.setText(options.get(position).getAddress());
        holder.Email.setText(options.get(position).getEmail());

        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                final UserProfile temp = options.get(position);
//
//                Intent intent = new Intent(mContext,Admin_Merchant_Details.class);
//                intent.putExtra("First Name",temp.getFirstName());
//                intent.putExtra("Last Name",temp.getLastName());
//                intent.putExtra("Email",temp.getEmail());
//                intent.putExtra("Contact Number",temp.getContactNumber());
//                intent.putExtra("Address",temp.getAddress());
//
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//                Toast.makeText(mContext, "Welcome to Customer Details!", Toast.LENGTH_SHORT).show();
//                mContext.startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return options.size();
    }

    @NonNull
    @Override
    public MerchantHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_customer_item, parent, false);
        return new MerchantHolder(v);
    }

    class MerchantHolder extends RecyclerView.ViewHolder{

        public View v;

        TextView Name, ContactNumber, Address, Email;

        public MerchantHolder(@NonNull View itemView) {
            super(itemView);

            Name = itemView.findViewById(R.id.admin_customerName);
            ContactNumber = itemView.findViewById(R.id.admin_customerContact);
            Address = itemView.findViewById(R.id.admin_customerAddress);
            Email = itemView.findViewById(R.id.admin_customerEmail);
            v = itemView;
        }
    }
}
