package com.example.waterbear2021;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.ArrayList;


public class ProductsRecyclerAdapter extends FirestoreRecyclerAdapter<ProductList,ProductsRecyclerAdapter.myViewHolder> {

    private Context mContext;
    private FirestoreRecyclerOptions<ProductList> options;

    public ProductsRecyclerAdapter(Context mContext, FirestoreRecyclerOptions<ProductList> options) {
        super(options);
        this.mContext = mContext;
        this.options = options;
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull ProductList model) {

        options.getSnapshots().get(position);

        holder.type.setText(model.getProductType());
        holder.textView1.setText(model.getProductName());
        holder.textView2.setText(String.format("%.2f",model.getProductPrice()));

        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ProductList temp = options.getSnapshots().get(position);

                Intent intent = new Intent(mContext,AddToCart.class);
                intent.putExtra("Seller ID",temp.getSellerID());
                intent.putExtra("Store Name",temp.getStoreName());
                intent.putExtra("Product ID",temp.getProductID());
                intent.putExtra("Product Type",temp.getProductType());
                intent.putExtra("Product Name",temp.getProductName());
                intent.putExtra("Product Price",String.valueOf(temp.getProductPrice()));

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);

            }
        });

    }
    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_row,parent,false);
        return new myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder
    {
        public View v;
        public TextView type, textView1, textView2;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            type = itemView.findViewById(R.id.tv_prodType);
            textView1 = itemView.findViewById(R.id.tv_prodName);
            textView2 = itemView.findViewById(R.id.tv_price);
            v = itemView;
        }
    }


}
