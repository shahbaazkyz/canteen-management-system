package com.example.canteen.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.view.textclassifier.TextLinks;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.canteen.Common.Common;
import com.example.canteen.Model.Request;
import com.example.canteen.Orderstatus;
import com.example.canteen.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ListenOrder extends Service implements ChildEventListener {
    FirebaseDatabase db;
    DatabaseReference requests;

    public ListenOrder() {
    }

    @Override
    public IBinder onBind(Intent intent) {
     return null;

    }

    @Override
    public void onCreate() {
        super.onCreate();
        db=FirebaseDatabase.getInstance("https://canteen-b207c-default-rtdb.firebaseio.com");
        requests=db.getReference("Requests");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        requests.addChildEventListener(this);

        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        //Trigger due to order update or delete
        Request request=dataSnapshot.getValue(Request.class);
        showNotification(dataSnapshot.getKey(),request);

    }

    private void showNotification(String key, Request request) {
        //code for notification
        Intent intent=new Intent(getBaseContext(), Orderstatus.class);
        intent.putExtra("userPhone",request.getPhone());
        PendingIntent contentIntent=PendingIntent.getActivity(getBaseContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(getBaseContext());
        builder.setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL).setWhen(System.currentTimeMillis()).setTicker("Canteen Owner").setContentInfo("Your order was Updated").
                setContentText("Order #"+key+"  was updated to status  "+ Common.convertCodeToStatus(request.getStatus())).setContentIntent(contentIntent).
                setContentInfo("Info").setSmallIcon(R.mipmap.ic_launcher);
        NotificationManager notificationManager=(NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1,builder.build());
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
