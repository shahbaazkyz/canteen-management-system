package com.example.canteen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.canteen.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignUp extends AppCompatActivity {

    MaterialEditText edtPhone,edtName,edtPassword;
    Button btnSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_sign_up );

        edtName = (MaterialEditText)findViewById( R.id.edtName );
        edtPhone = (MaterialEditText)findViewById( R.id.edtPhone );
        edtPassword = (MaterialEditText)findViewById( R.id.edtPassword );
        btnSignUp = (Button)findViewById( R.id.btnSignUp );

        //Init Firebase


        final FirebaseDatabase database = FirebaseDatabase.getInstance("https://canteen-b207c-default-rtdb.firebaseio.com");
        final DatabaseReference table_user = database.getReference("User");

        btnSignUp.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog mDialog = new ProgressDialog( SignUp.this );
                mDialog.setMessage( "Please Wait ..." );
                mDialog.show();

                table_user.addListenerForSingleValueEvent( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //Check if the user already exists
                        if(dataSnapshot.child( edtPhone.getText().toString() ).exists()){
                            mDialog.dismiss();
                            Toast.makeText( SignUp.this,edtPhone.getText().toString()+"Phone Number Exists !",Toast.LENGTH_LONG ).show();
                        }

                        else{
                            mDialog.dismiss();
                            User user = new User( edtPhone.getText().toString(), edtName.getText().toString(),edtPassword.getText().toString() );
                            table_user.child( edtPhone.getText().toString() ).setValue( user );
                            Toast.makeText( SignUp.this,"Sign Up Successful",Toast.LENGTH_LONG ).show();
                            finish();
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

