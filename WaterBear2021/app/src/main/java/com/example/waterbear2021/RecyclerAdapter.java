package com.example.waterbear2021;


import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.internal.$Gson$Preconditions;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private static final String Tag = "RecyclerView";
    private Context mContext;
    private ArrayList<StoresView> storesViews;

    public RecyclerAdapter(Context mContext, ArrayList<StoresView> storesViews) {
        this.mContext = mContext;
        this.storesViews = storesViews;
    }

    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.store_item,parent,false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        storesViews.get(position);

            holder.tvTitle.setText(storesViews.get(position).getTitle());
            holder.tvDesc.setText(storesViews.get(position).getDescript());

            Glide.with(mContext)
                    .load(storesViews.get(position).getImage())
                    .into(holder.imageView);

            holder.v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                final StoresView temp = storesViews.get(position);

                    Intent intent = new Intent(mContext,StoreProductsDashboard.class);
                    intent.putExtra("Store ID",temp.getStoreID());
                    intent.putExtra("Store Image",temp.getImage());
                    intent.putExtra("Store Name",temp.getTitle());
                    intent.putExtra("Store Description",temp.getDescript());

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    Toast.makeText(mContext, "Welcome to our Store!", Toast.LENGTH_SHORT).show();
                    mContext.startActivity(intent);
                }
            });

    }

    @Override
    public int getItemCount() {
        return storesViews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView tvTitle, tvDesc;
        View v;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imgView);
            tvTitle = itemView.findViewById(R.id.imgTitle);
            tvDesc = itemView.findViewById(R.id.imgDescript);
            v = itemView;


        }
    }

}
