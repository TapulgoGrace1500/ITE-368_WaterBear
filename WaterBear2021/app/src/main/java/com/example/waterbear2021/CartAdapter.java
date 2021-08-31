package com.example.waterbear2021;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartHolder>{

    private Context mContext;
    private ArrayList<CartPreferenceSQL> options;

    public CartAdapter(Context mContext, ArrayList<CartPreferenceSQL> options) {
        this.mContext = mContext;
        this.options = options;
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.CartHolder holder, int position) {
        options.get(position);

        holder.prodType.setText(options.get(position).getProductType());
        holder.prodName.setText(options.get(position).getProductName());
        holder.prodQty.setText(String.valueOf(options.get(position).getQuantity()));
        holder.prodPriceEach.setText(String.format("%.2f",options.get(position).getProductPrice()));
        holder.prodPrice.setText(String.format("%.2f",options.get(position).getTotalPrice()));

        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                final CartPreference temp = options.get(position);
//
//                Intent intent = new Intent(mContext,Admin_Merchant_Pending_Details.class);
//                intent.putExtra("First Name",temp.getFirstName());
//                intent.putExtra("Last Name",temp.getLastName());
//                intent.putExtra("Seller Email",temp.getEmail());
//                intent.putExtra("Password",temp.getPassword());
//                intent.putExtra("Contact Number",temp.getContactNumber());
//                intent.putExtra("Address",temp.getAddress());
//                intent.putExtra("Store ID",temp.getStoreID());
//                intent.putExtra("Store Image",temp.getImage());
//                intent.putExtra("Store Name",temp.getTitle());
//                intent.putExtra("Store Description",temp.getDescript());
//
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//                Toast.makeText(mContext, "Welcome to Merchant Details!", Toast.LENGTH_SHORT).show();
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
    public CartAdapter.CartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_row, parent, false);
        return new CartAdapter.CartHolder(v);
    }

    class CartHolder extends RecyclerView.ViewHolder{

        public View v;
        TextView prodType, prodName, prodQty, prodPrice, prodPriceEach;

        public CartHolder(@NonNull View itemView) {
            super(itemView);

            prodType = itemView.findViewById(R.id.tv_prodType);
            prodName = itemView.findViewById(R.id.tv_prodName);
            prodQty = itemView.findViewById(R.id.tv_qty);
            prodPriceEach = itemView.findViewById(R.id.tv_DproductPrice);
            prodPrice = itemView.findViewById(R.id.tv_price);
            v = itemView;
        }
    }

}
