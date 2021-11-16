package com.example.canteen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.example.canteen.Common.Common;
import com.example.canteen.ViewHolder.OrderDetailsAdapter;

public class Orderdetails extends AppCompatActivity {

    TextView order_id,order_phone,order_srn,order_total;
    String order_id_value="";
    RecyclerView firstFoods;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderdetails);

        order_id = (TextView)findViewById(R.id.order_id);
        order_phone = (TextView)findViewById(R.id.order_phone);
        order_srn = (TextView)findViewById(R.id.order_SRN);
        order_total = (TextView)findViewById(R.id.order_total);

        firstFoods = (RecyclerView)findViewById(R.id.firstFoods);
        firstFoods.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        firstFoods.setLayoutManager(layoutManager);

        if(getIntent() != null)
            order_id_value = getIntent().getStringExtra("OrderId");

        //Set Value
        order_id.setText(order_id_value);
        order_phone.setText(Common.currentRequest.getPhone());
        order_total.setText(Common.currentRequest.getTotal());
        order_srn.setText(Common.currentRequest.getSRN());

        OrderDetailsAdapter adapter = new OrderDetailsAdapter(Common.currentRequest.getFoods());
        adapter.notifyDataSetChanged();
        firstFoods.setAdapter(adapter);
    }
}
