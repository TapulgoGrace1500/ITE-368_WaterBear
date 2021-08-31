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

import java.util.ArrayList;

public class Admin_Merchant_Pending_Adapter extends RecyclerView.Adapter<Admin_Merchant_Pending_Adapter.MerchantHolder>{

    private Context mContext;
    private ArrayList<StoresView> options;

    public Admin_Merchant_Pending_Adapter(Context mContext, ArrayList<StoresView> options) {
        this.mContext = mContext;
        this.options = options;
    }

    @Override
    public void onBindViewHolder(@NonNull Admin_Merchant_Pending_Adapter.MerchantHolder holder, int position) {
        options.get(position);


        holder.storeName.setText(options.get(position).getTitle());
        holder.storeDescription.setText(options.get(position).getDescript());

        Glide.with(mContext)
                .load(options.get(position).getImage())
                .into(holder.merchantImage);

        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final StoresView temp = options.get(position);

                Intent intent = new Intent(mContext,Admin_Merchant_Pending_Details.class);
                intent.putExtra("First Name",temp.getFirstName());
                intent.putExtra("Last Name",temp.getLastName());
                intent.putExtra("Seller Email",temp.getEmail());
                intent.putExtra("Password",temp.getPassword());
                intent.putExtra("Contact Number",temp.getContactNumber());
                intent.putExtra("Address",temp.getAddress());
                intent.putExtra("Store ID",temp.getStoreID());
                intent.putExtra("Store Image",temp.getImage());
                intent.putExtra("Store Name",temp.getTitle());
                intent.putExtra("Store Description",temp.getDescript());

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Toast.makeText(mContext, "Welcome to Merchant Details!", Toast.LENGTH_SHORT).show();
                mContext.startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return options.size();
    }

    @NonNull
    @Override
    public Admin_Merchant_Pending_Adapter.MerchantHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_merchant_item, parent, false);
        return new Admin_Merchant_Pending_Adapter.MerchantHolder(v);
    }

    class MerchantHolder extends RecyclerView.ViewHolder{

        public View v;
        ImageView merchantImage;
        TextView storeName, storeDescription;

        public MerchantHolder(@NonNull View itemView) {
            super(itemView);

            merchantImage = itemView.findViewById(R.id.admin_imgMerchant);
            storeName = itemView.findViewById(R.id.admin_merchantName);
            storeDescription = itemView.findViewById(R.id.admin_merchantDesc);
            v = itemView;
        }
    }
}
