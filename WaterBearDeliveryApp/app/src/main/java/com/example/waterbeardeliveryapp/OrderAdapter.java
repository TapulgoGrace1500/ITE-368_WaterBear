package com.example.waterbeardeliveryapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.CartHolder>{

    private Context mContext;
    private ArrayList<CartPreference> options;

    public OrderAdapter(Context mContext, ArrayList<CartPreference> options) {
        this.mContext = mContext;
        this.options = options;
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.CartHolder holder, int position) {
        options.get(position);

        holder.prodType.setText(options.get(position).getProductType());
        holder.prodName.setText(options.get(position).getProductName());
        holder.prodTotal.setText(String.format("%.2f",options.get(position).getTotalPrice()));
        holder.prodQty.setText(String.valueOf(options.get(position).getQuantity()));
        holder.prodPrice.setText(String.format("%.2f",options.get(position).getProductPrice()));


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
    public OrderAdapter.CartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.pending_order_row, parent, false);
        return new OrderAdapter.CartHolder(v);
    }

    public CartPreference getItem(int order) {
        return options.get(order);
    }

    class CartHolder extends RecyclerView.ViewHolder{

        public View v;
        TextView prodType, prodName, prodQty, prodPrice, prodTotal;

        public CartHolder(@NonNull View itemView) {
            super(itemView);

            prodType = itemView.findViewById(R.id.tv_prodType);
            prodName = itemView.findViewById(R.id.tv_prodName);
            prodTotal = itemView.findViewById(R.id.tv_price);
            prodPrice = itemView.findViewById(R.id.tv_eachPrice);
            prodQty = itemView.findViewById(R.id.tv_status);
            v = itemView;

        }

    }
}