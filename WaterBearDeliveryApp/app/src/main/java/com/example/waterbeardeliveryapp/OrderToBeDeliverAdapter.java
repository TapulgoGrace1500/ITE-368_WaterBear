package com.example.waterbeardeliveryapp;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class OrderToBeDeliverAdapter extends RecyclerView.Adapter<OrderToBeDeliverAdapter.CartHolder>{

    private Context mContext;
    private ArrayList<OrderDetail> options;

    public OrderToBeDeliverAdapter(Context mContext, ArrayList<OrderDetail> options) {
        this.mContext = mContext;
        this.options = options;
    }

    @Override
    public void onBindViewHolder(@NonNull OrderToBeDeliverAdapter.CartHolder holder, int position) {
        options.get(position);

        holder.prodName.setText(options.get(position).getOrderBuyerName());
        holder.prodPrice.setText(String.format("%.2f",options.get(position).getOrderTotal()));
        holder.prodDeliveryFee.setText(String.format("%.2f",options.get(position).getOrderDeliveryFee()));
        holder.prodStatus.setText(options.get(position).getOrderStatus());

        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final OrderDetail temp = options.get(position);

                Intent intent = new Intent(mContext,OrderToBeDeliverDetail.class);
                intent.putExtra("Order ID",temp.getOrderId());
                intent.putExtra("Cart ID",temp.getCartId());
                intent.putExtra("Store ID",temp.getOrderStoreId());
                intent.putExtra("Store Name",temp.getOrderStoreName());
                intent.putExtra("Customer ID",temp.getOrderBuyerId());
                intent.putExtra("Customer Name",temp.getOrderBuyerName());
                intent.putExtra("Customer Address",temp.getOrderBuyerAddress());
                intent.putExtra("Contact Number",temp.getOrderBuyerContactNumber());
                intent.putExtra("Delivery Fee",temp.getOrderDeliveryFee());
                intent.putExtra("Order Total",temp.getOrderTotal());
                intent.putExtra("Order Status",temp.getOrderStatus());

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                Toast.makeText(mContext, "Order Details!", Toast.LENGTH_SHORT).show();
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
    public OrderToBeDeliverAdapter.CartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_detail_row, parent, false);
        return new OrderToBeDeliverAdapter.CartHolder(v);
    }

    public OrderDetail getItem(int order) {
        return options.get(order);
    }

    static class CartHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        public View v;
        TextView prodType, prodName, prodPrice, prodStatus, prodDeliveryFee;

        public CartHolder(@NonNull View itemView) {
            super(itemView);

            prodName = itemView.findViewById(R.id.tv_prodName);
            prodPrice = itemView.findViewById(R.id.tv_price);
            prodDeliveryFee = itemView.findViewById(R.id.tv_deliveryFee);
            prodStatus = itemView.findViewById(R.id.tv_status);
            itemView.setOnCreateContextMenuListener(this);
            v = itemView;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(0,0,getAdapterPosition(),"Successfully Delivered");
            menu.add(0,0,getAdapterPosition(),"Report Buyer as Spam");
        }
    }
}
