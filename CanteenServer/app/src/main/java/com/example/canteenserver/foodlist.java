package com.example.canteenserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.canteenserver.Common.Common;
import com.example.canteenserver.Interface.ItemClickListener;
import com.example.canteenserver.Model.Category;
import com.example.canteenserver.Model.Food;
import com.example.canteenserver.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class foodlist extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    //Firebase
    FirebaseDatabase db;
    DatabaseReference foodList;
    FirebaseStorage storage;
    StorageReference storageReference;

    CoordinatorLayout rootLayout;

    String categoryId = "";
    FloatingActionButton fab;

    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    //Add New Food
    MaterialEditText edtName,edtDescription,edtPrice,edtDiscount;
    Button btnSelect,btnUpload;

    Food newFood;

    Uri saveUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodlist);

        //Firebase
        db = FirebaseDatabase.getInstance("https://canteen-b207c-default-rtdb.firebaseio.com");
        foodList = db.getReference("Foods");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //Init
        recyclerView = (RecyclerView)findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        rootLayout = (CoordinatorLayout) findViewById(R.id.rootLayout);

        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddFoodDialog();
            }
        });
        if(getIntent() != null)
            categoryId = getIntent().getStringExtra("CategoryId");
        if(!categoryId.isEmpty())
            loadListFood(categoryId);
    }

    private void showAddFoodDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder( foodlist.this );
        alertDialog.setTitle( "ADD NEW FOOD" );
        alertDialog.setMessage( "Please fill full information" );

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate( R.layout.add_new_food_layout,null );

        edtName = add_menu_layout.findViewById(R.id.edtName) ;
        edtDescription = add_menu_layout.findViewById(R.id.edtDescription) ;
        edtPrice = add_menu_layout.findViewById(R.id.edtPrice) ;
        edtDiscount = add_menu_layout.findViewById(R.id.edtDiscount) ;

        btnSelect = add_menu_layout.findViewById( R.id.btnSelect );
        btnUpload = add_menu_layout.findViewById( R.id.btnUpload );

        //Event for button
        btnSelect.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        } );

        btnUpload.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        } );

        alertDialog.setView( add_menu_layout );
        alertDialog.setIcon( R.drawable.ic_shopping_cart_black_24dp );

        //Set Button
        alertDialog.setPositiveButton( "YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                if(newFood != null){
                    foodList.push().setValue( newFood );
                    Snackbar.make(rootLayout,"New Category "+newFood.getName()+ " was added",Snackbar.LENGTH_SHORT).show();
                }
            }
        } );

        alertDialog.setNegativeButton( "NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        } );
        alertDialog.show();

    }

    private void uploadImage() {
        if(saveUri != null){
            final ProgressDialog mDialog = new ProgressDialog( this );
            mDialog.setMessage( "Uploading.." );
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            Toast.makeText( foodlist.this, imageName, Toast.LENGTH_SHORT ).show();
            final StorageReference imageFolder = storageReference.child( "images/"+imageName );
            imageFolder.putFile( saveUri )
                    .addOnSuccessListener( this,new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText( foodlist.this,"Uploaded!!" ,Toast.LENGTH_LONG).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newFood = new Food();
                                    newFood.setName(edtName.getText().toString());
                                    newFood.setDescription(edtDescription.getText().toString());
                                    newFood.setPrice(edtPrice.getText().toString());
                                    newFood.setDiscount(edtDiscount.getText().toString());
                                    newFood.setMenuId(categoryId);
                                    newFood.setImage(uri.toString());
                                }
                            } );
                        }
                    } )
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText( foodlist.this,""+e.getMessage() ,Toast.LENGTH_LONG).show();
                        }
                    } )
                    .addOnProgressListener( this,new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage( "Uploaded " + progress + "%");
                        }
                    } );
        }
    }

    private void chooseImage() {
//        Intent i = new Intent( Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
//        startActivityForResult( i,PICK_IMAGE_REQUEST );

        Intent intent = new Intent(  );
        intent.setType( "image/*" );
        intent.setAction( Intent.ACTION_GET_CONTENT );
        startActivityForResult( Intent.createChooser( intent,"Select Picture" ), Common.PICK_IMAGE_REQUEST );

    }

    private void loadListFood(String categoryId) {
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
                Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodList.orderByChild("menuId").equalTo(categoryId)
        ) {
            @Override
            protected void populateViewHolder(FoodViewHolder foodViewHolder, Food food, int i) {
                foodViewHolder.food_name.setText(food.getName());
                Picasso.with(getBaseContext())
                        .load(food.getImage())
                        .into(foodViewHolder.food_image);

                foodViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Code late
                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data!=null && data.getData() != null)
        {
            saveUri = data.getData();
            btnSelect.setText( "Image Selected" );
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if(item.getTitle().equals(Common.UPDATE))
        {
            showUpdateFoodDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        else if(item.getTitle().equals(Common.DELETE))
        {
            deleteFood(adapter.getRef(item.getOrder()).getKey());
        }
        return super.onContextItemSelected(item);
    }

    private void deleteFood(String key) {
        foodList.child(key).removeValue();
    }

    private void showUpdateFoodDialog(final String key, final Food item) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder( foodlist.this );
        alertDialog.setTitle( "EDIT FOOD" );
        alertDialog.setMessage( "Please fill full information" );

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate( R.layout.add_new_food_layout,null );

        edtName = add_menu_layout.findViewById(R.id.edtName) ;
        edtDescription = add_menu_layout.findViewById(R.id.edtDescription) ;
        edtPrice = add_menu_layout.findViewById(R.id.edtPrice) ;
        edtDiscount = add_menu_layout.findViewById(R.id.edtDiscount) ;

        //Set default value for view
        edtName.setText(item.getName());
        edtDescription.setText(item.getDescription());
        edtPrice.setText(item.getPrice());
        edtDiscount.setText(item.getDiscount());

        btnSelect = add_menu_layout.findViewById( R.id.btnSelect );
        btnUpload = add_menu_layout.findViewById( R.id.btnUpload );

        //Event for button
        btnSelect.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        } );

        btnUpload.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImage(item);
            }
        } );

        alertDialog.setView( add_menu_layout );
        alertDialog.setIcon( R.drawable.ic_shopping_cart_black_24dp );

        //Set Button
        alertDialog.setPositiveButton( "YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                //Update Information
                item.setName(edtName.getText().toString());
                item.setPrice(edtPrice.getText().toString());
                item.setDiscount(edtDiscount.getText().toString());
                item.setDescription(edtDescription.getText().toString());

                foodList.child(key).setValue(item);

                Snackbar.make(rootLayout,"Food "+ item.getName()+ " was edited",Snackbar.LENGTH_SHORT).show();
            }
        } );

        alertDialog.setNegativeButton( "NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        } );
        alertDialog.show();
    }

    private void changeImage(final Food item) {
        if(saveUri != null){
            final ProgressDialog mDialog = new ProgressDialog( this );
            mDialog.setMessage( "Uploading.." );
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            Toast.makeText( foodlist.this, imageName, Toast.LENGTH_SHORT ).show();
            final StorageReference imageFolder = storageReference.child( "images/"+imageName );
            imageFolder.putFile( saveUri )
                    .addOnSuccessListener( this,new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText( foodlist.this,"Uploaded!!" ,Toast.LENGTH_LONG).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    item.setImage(uri.toString());
                                }
                            } );
                        }
                    } )
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText( foodlist.this,""+e.getMessage() ,Toast.LENGTH_LONG).show();
                        }
                    } )
                    .addOnProgressListener( this,new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage( "Uploaded " + progress + "%");
                        }
                    } );
        }

    }
}
