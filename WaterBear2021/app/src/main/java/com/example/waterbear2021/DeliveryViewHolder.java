package com.example.waterbear2021;

import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class DeliveryViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

    public TextView fullname, contactNum;
    public DeliveryViewHolder(@NonNull View itemView) {
        super(itemView);

        fullname = itemView.findViewById(R.id.tv_deliveryFullName);
        contactNum = itemView.findViewById(R.id.tv_deliveryCNumber);
        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        menu.setHeaderTitle("Select Menu");
        menu.add(0,0,getAdapterPosition(),"Update");
        menu.add(0,1,getAdapterPosition(),"Delete");

    }

}
