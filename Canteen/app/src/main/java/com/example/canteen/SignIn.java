package com.example.canteen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.canteen.Common.Common;
import com.example.canteen.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class SignIn extends AppCompatActivity {

    EditText edtPhone,edtPassword;
    Button btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_sign_in );

        edtPhone = (EditText)findViewById( R.id.edtPhone );
        edtPassword = (EditText)findViewById( R.id.edtPassword );
        btnSignIn = (Button) findViewById( R.id.btnSignIn );

        //Init Firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance("https://canteen-b207c-default-rtdb.firebaseio.com");
        final DatabaseReference table_user = database.getReference("User");

        btnSignIn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                final ProgressDialog mDialog = new ProgressDialog( SignIn.this );
                mDialog.setMessage( "Please Wait ..." );
                mDialog.show();

                table_user.addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        //Check if user does not exist in database
                        if(dataSnapshot.child( edtPhone.getText().toString() ).exists()){
                            //Get User Info
                            mDialog.dismiss();
                            User user = dataSnapshot.child( edtPhone.getText().toString() ).getValue( User.class );
                            if(user.getPassword()!=null && user.getPassword().equals(edtPassword.getText().toString()))
                            {
                                Common.currentUser = user;
                                Toast.makeText(SignIn.this, "Sign In Successfully", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent( SignIn.this,Home.class);
                                startActivity( i );
                                finish();
                            }
                            else
                            {
                                Toast.makeText(SignIn.this, "Wrong Password!!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        else{
                            mDialog.dismiss();
                            Toast.makeText( SignIn.this,"User Does not exist!",Toast.LENGTH_LONG ).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                } );
            }
        } );


    }
}
