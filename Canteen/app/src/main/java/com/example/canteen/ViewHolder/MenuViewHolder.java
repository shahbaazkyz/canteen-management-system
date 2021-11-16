package com.example.canteen.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canteen.Interface.ItemClickListener;
import com.example.canteen.R;

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtMenuName;
    public ImageView imageView;
    private ItemClickListener itemClickListener;

    public MenuViewHolder(@NonNull View itemView) {
        super( itemView );

        txtMenuName = (TextView)itemView.findViewById( R.id.menu_name );
        imageView = (ImageView)itemView.findViewById( R.id.menu_image );

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
}
