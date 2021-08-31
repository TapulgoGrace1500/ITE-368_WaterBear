package com.example.waterbear2021;

import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CartViewHolder  extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

    public TextView type, textView1, textView2;
    public CartViewHolder(@NonNull View itemView) {
        super(itemView);

        type = itemView.findViewById(R.id.tv_prodType);
        textView1 = itemView.findViewById(R.id.tv_prodName);
        textView2 = itemView.findViewById(R.id.tv_price);
        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        menu.setHeaderTitle("Select Menu");
        menu.add(0,1,getAdapterPosition(),"Remove");

    }

}
