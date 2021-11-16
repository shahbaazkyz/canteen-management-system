package com.example.canteen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.canteen.Common.Common;
import com.example.canteen.Interface.ItemClickListener;
import com.example.canteen.Model.Order;
import com.example.canteen.Model.Request;
import com.example.canteen.Model.User;
import com.example.canteen.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Orderstatus extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference requests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderstatus);

        //Firebase
        database = FirebaseDatabase.getInstance("https://canteen-b207c-default-rtdb.firebaseio.com");
        requests = database.getReference("Requests");

        recyclerView = (RecyclerView)findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        if(getIntent().getExtras()==null)
            loadOrders(Common.currentUser.getPhone());
        else
            loadOrders(getIntent().getStringExtra("userPhone"));
    }

    private void loadOrders(final String phone) {
        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                requests.orderByChild("phone")
                    .equalTo(phone)
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder orderViewHolder, final Request request, final int i) {
                orderViewHolder.txtOrderId.setText(adapter.getRef(i).getKey());
                orderViewHolder.txtOrderPhone.setText(request.getPhone());  // Error
                orderViewHolder.txtOrderStatus.setText(Common.convertCodeToStatus(request.getStatus()));
                orderViewHolder.txtOrderSRN.setText(request.getSRN());
                orderViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent orderDetail = new Intent(Orderstatus.this,Orderdetails.class);
                        Common.currentRequest = request;
                        orderDetail.putExtra("OrderId",adapter.getRef(position).getKey());
                        startActivity(orderDetail);
                    }
                });
                orderViewHolder.btn_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(adapter.getItem(i).getStatus().equals("0"))
                            deleteOrder(adapter.getRef(i).getKey());
                        else
                            Toast.makeText(Orderstatus.this,"Order cannot be deleted at this stage !!",Toast.LENGTH_LONG).show();
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
    }

    private void deleteOrder(final String key) {
        requests.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(Orderstatus.this,new StringBuilder("Order ").append(key).append("  has been deleted!").toString(),Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Orderstatus.this,e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }


}
