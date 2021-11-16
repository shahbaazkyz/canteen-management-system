package com.example.canteenserver.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canteenserver.Common.Common;
import com.example.canteenserver.Interface.ItemClickListener;
import com.example.canteenserver.R;

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener{

    public TextView food_name;
    public ImageView food_image;
    private ItemClickListener itemClickListener;

    public FoodViewHolder(@NonNull View itemView) {
        super( itemView );

        food_name = (TextView)itemView.findViewById( R.id.food_name );
        food_image = (ImageView)itemView.findViewById( R.id.food_image );
        itemView.setOnCreateContextMenuListener(this);
        itemView.setOnClickListener( this );
    }

    public FoodViewHolder setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
        return this;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick( v,getAdapterPosition(),false );
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select The Action");
        menu.add(0,0,getAdapterPosition(), Common.UPDATE);
        menu.add(0,1,getAdapterPosition(), Common.DELETE);

    }

}
