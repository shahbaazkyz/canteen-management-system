package com.example.canteenserver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.canteenserver.Common.Common;
import com.example.canteenserver.Interface.ItemClickListener;
import com.example.canteenserver.Model.Request;
import com.example.canteenserver.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaredrummler.materialspinner.MaterialSpinner;

public class OrderStatus extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;
    FirebaseDatabase db;
    DatabaseReference requests;
    MaterialSpinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        db= FirebaseDatabase.getInstance("https://canteen-b207c-default-rtdb.firebaseio.com");
        requests=db.getReference("Requests");
        //inititalize
        recyclerView=findViewById((R.id.listOrders));
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        loadOrders();
    }

    private void loadOrders() {

        adapter=new FirebaseRecyclerAdapter<Request, OrderViewHolder>(Request.class,R.layout.order_layout,OrderViewHolder.class,requests) {
            @Override
            protected void populateViewHolder(OrderViewHolder orderViewHolder, final Request request, int i) {
                orderViewHolder.txtOrderId.setText(adapter.getRef(i).getKey());
                orderViewHolder.txtOrderStatus.setText(Common.convertCodeToStatus(request.getStatus()));
                orderViewHolder.txtOrderSRN.setText(request.getSRN());
                orderViewHolder.txtOrderPhone.setText(request.getPhone());
                orderViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent orderDetail = new Intent(OrderStatus.this,Orderdetails.class);
                        Common.currentRequest = request;
                        orderDetail.putExtra("OrderId",adapter.getRef(position).getKey());
                        startActivity(orderDetail);
                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if(item.getTitle().equals(Common.UPDATE))
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        else if(item.getTitle().equals(Common.DELETE))
            deleteOrder(adapter.getRef(item.getOrder()).getKey());


        return super.onContextItemSelected(item);
    }

    private void deleteOrder(String key) {
        requests.child(key).removeValue();
    }

    private void showUpdateDialog(String key, final Request item) {
            final AlertDialog.Builder alertDialog=new AlertDialog.Builder(OrderStatus.this);
            alertDialog.setTitle("Update Order");
            alertDialog.setMessage("Please choose status");
            LayoutInflater inflater= this.getLayoutInflater();
            final View view=inflater.inflate(R.layout.update_order_layout,null);
            spinner= (MaterialSpinner)view.findViewById(R.id.statusSpinner);
            spinner.setItems("Placed","Being Cooked","Order Ready");
            alertDialog.setView(view);
            final String localKey=key;
            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    item.setStatus(String.valueOf(spinner.getSelectedIndex()));
                    requests.child(localKey).setValue(item);

                }
            });
            alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialog.show();

    }
}
