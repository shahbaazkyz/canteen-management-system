package com.example.canteenserver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.canteenserver.Common.Common;
import com.example.canteenserver.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignIn extends AppCompatActivity {

    EditText edtPhone,edtPassword;
    Button btnSignIn;
    FirebaseDatabase db;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_sign_in );

        edtPassword = (EditText)findViewById( R.id.edtPassword );
        edtPhone = (EditText)findViewById( R.id.edtPhone );
        btnSignIn = (Button)findViewById( R.id.btnSignIn );

        //Init Firebase
        db = FirebaseDatabase.getInstance();
        users = db.getReference( "User" );

        btnSignIn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUser(edtPhone.getText().toString(),edtPassword.getText().toString());
            }
        } );
    }

    private void signInUser(final String phone, String password) {
        final ProgressDialog mDialog = new ProgressDialog( SignIn.this );
        mDialog.setMessage( "Please Wait ..." );
        mDialog.show();

        final String localPhone = phone;
        final String localPassword = password;
        users.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child( localPhone ).exists()){
                    mDialog.dismiss();
                    User user = dataSnapshot.child( localPhone ).getValue(User.class);
                    user.setPhone( localPhone );
                    if(Boolean.parseBoolean( user.getIsStaff() )){ //If isStaff == true
                        if(user.getPassword().equals( localPassword )){
                            Toast.makeText( SignIn.this ,"Staff Sign In Successful!!",Toast.LENGTH_LONG).show();
                            Intent i = new Intent( SignIn.this,Home.class );
                            Common.currentUser = user;
                            startActivity( i );
                            finish();
                        }
                        else {
                            mDialog.dismiss();
                            Toast.makeText( SignIn.this ,"Wrong Password",Toast.LENGTH_LONG).show();
                        }
                    }

                    else{
                        mDialog.dismiss();
                        Toast.makeText( SignIn.this ,"Please Login with Staff Account",Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    mDialog.dismiss();
                    Toast.makeText( SignIn.this ,"User does not Exists",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }
}
