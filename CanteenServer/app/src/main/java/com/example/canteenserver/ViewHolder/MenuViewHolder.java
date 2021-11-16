package com.example.canteenserver.ViewHolder;

import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canteenserver.Common.Common;
import com.example.canteenserver.R;
import com.example.canteenserver.Interface.ItemClickListener;

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener{

    public TextView txtMenuName;
    public ImageView imageView;
    private ItemClickListener itemClickListener;

    public MenuViewHolder(@NonNull View itemView) {
        super( itemView );

        txtMenuName = (TextView)itemView.findViewById( R.id.menu_name );
        imageView = (ImageView)itemView.findViewById( R.id.menu_image );
        itemView.setOnCreateContextMenuListener(this);
        itemView.setOnClickListener( this );
    }

    public MenuViewHolder setItemClickListener(ItemClickListener itemClickListener) {
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
