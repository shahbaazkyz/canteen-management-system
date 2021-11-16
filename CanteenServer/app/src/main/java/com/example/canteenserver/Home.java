package com.example.canteenserver;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.canteenserver.Common.Common;
import com.example.canteenserver.Interface.ItemClickListener;
import com.example.canteenserver.Model.Category;
import com.example.canteenserver.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileInputStream;
import java.util.Locale;
import java.util.UUID;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    TextView txtFullName;
    DrawerLayout drawer;

    //Firebase
    FirebaseDatabase database;
    DatabaseReference categories;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;

    //View
    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;

    //Add new menu layout
    MaterialEditText edtName;
    Button btnUpload,btnSelect;

    Category newCategory;


    Uri saveUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_home );
        Toolbar toolbar = findViewById( R.id.toolbar );
        toolbar.setTitle( "Menu Management" );
        setSupportActionBar( toolbar );


        //Init Firebase

        database = FirebaseDatabase.getInstance("https://canteen-b207c-default-rtdb.firebaseio.com");
        categories = database.getReference("Category");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        FloatingActionButton fab = findViewById( R.id.fab );
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        } );

        drawer = findViewById( R.id.drawer_layout );
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener(this);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        mAppBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.nav_menu, R.id.nav_cart, R.id.nav_orders,R.id.nav_sign_out )
//                .setDrawerLayout( drawer )
//                .build();
//        NavController navController = Navigation.findNavController( this, R.id.nav_host_fragment );
//        NavigationUI.setupActionBarWithNavController( this, navController, mAppBarConfiguration );
//        NavigationUI.setupWithNavController( navigationView, navController );


        //Set Name for user
        View headView = navigationView.getHeaderView( 0 );
        txtFullName = (TextView)headView.findViewById( R.id.txtFullName);
        txtFullName.setText( Common.currentUser.getName() );

        //Init View
        recycler_menu = (RecyclerView)findViewById( R.id.recycler_menu );
        recycler_menu.setHasFixedSize( true);
        layoutManager = new LinearLayoutManager( this );
        recycler_menu.setLayoutManager( layoutManager );

        loadMenu();
    }

    private void showDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder( Home.this );
        alertDialog.setTitle( "ADD NEW CATEGORY" );
        alertDialog.setMessage( "Please fill full information" );

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate( R.layout.add_new_menu,null );

        edtName = add_menu_layout.findViewById(R.id.edtName) ;
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

                if(newCategory != null){
                    categories.push().setValue( newCategory );

                    Snackbar.make( drawer,"New Category "+newCategory.getName()+ " was added",Snackbar.LENGTH_SHORT).show();
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
            Toast.makeText( Home.this, imageName, Toast.LENGTH_SHORT ).show();
            final StorageReference imageFolder = storageReference.child( "images/"+imageName );
            imageFolder.putFile( saveUri )
                    .addOnSuccessListener( this,new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText( Home.this,"Uploaded!!" ,Toast.LENGTH_LONG).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newCategory = new Category( edtName.getText().toString(),uri.toString());
                                }
                            } );
                        }
                    } )
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText( Home.this,""+e.getMessage() ,Toast.LENGTH_LONG).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        if(requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data!=null && data.getData() != null)
        {
            saveUri = data.getData();
            btnSelect.setText( "Image Selected" );
        }
    }

    private void chooseImage() {
//        Intent i = new Intent( Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
//        startActivityForResult( i,PICK_IMAGE_REQUEST );

        Intent intent = new Intent(  );
        intent.setType( "image/*" );
        intent.setAction( Intent.ACTION_GET_CONTENT );
        startActivityForResult( Intent.createChooser( intent,"Select Picture" ),Common.PICK_IMAGE_REQUEST );

    }

    private void loadMenu() {

        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(
                Category.class, R.layout.menu_item, MenuViewHolder.class, categories ) {
            @Override
            protected void populateViewHolder(MenuViewHolder menuViewHolder, Category category,  int position) {
                menuViewHolder.txtMenuName.setText( category.getName() );
                Picasso.with(getBaseContext()).load( category.getImage() )
                        .into( menuViewHolder.imageView );
                menuViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Send Category Id and start new Activity
                        Intent Foodlist = new Intent(Home.this,foodlist.class);
                        Foodlist.putExtra("CategoryId",adapter.getRef(position).getKey());
                        startActivity(Foodlist);
                    }
                });
            }
        };
        adapter.notifyDataSetChanged(); //Refresh data if data has changed
        recycler_menu.setAdapter( adapter );
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        if (drawer.isDrawerOpen( GravityCompat.START )) {
            drawer.closeDrawer( GravityCompat.START );
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.home, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id == R.id.nav_menu)
        {

        }
        else if(id == R.id.nav_orders)
        {
            Toast.makeText( Home.this ,"Orders",Toast.LENGTH_LONG).show();
            Intent orders=new Intent(Home.this,OrderStatus.class);
            startActivity(orders);
        }
        else if(id == R.id.nav_sign_out)
        {
            Intent signIn = new Intent(Home.this, SignIn.class);
            signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signIn);
        }
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if(item.getTitle().equals(Common.UPDATE)){
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        else if(item.getTitle().equals((Common.DELETE)))
        {
            deleteCategory(adapter.getRef(item.getOrder()).getKey());
        }
        return super.onContextItemSelected(item);
    }

    private void deleteCategory(String key) {

        categories.child(key).removeValue();
    }

    private void showUpdateDialog(final String key, final Category item) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder( Home.this );
        alertDialog.setTitle( "UPDATE CATEGORY" );
        alertDialog.setMessage( "Please fill full information" );

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate( R.layout.add_new_menu,null );

        edtName = add_menu_layout.findViewById(R.id.edtName) ;
        btnSelect = add_menu_layout.findViewById( R.id.btnSelect );
        btnUpload = add_menu_layout.findViewById( R.id.btnUpload );

        edtName.setText(item.getName());

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

                item.setName(edtName.getText().toString());
                // now newname appears
                categories.child(key).setValue(item);
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

    private void changeImage(final Category item) {
        if(saveUri != null){
            final ProgressDialog mDialog = new ProgressDialog( this );
            mDialog.setMessage( "Uploading.." );
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            Toast.makeText( Home.this, imageName, Toast.LENGTH_SHORT ).show();
            final StorageReference imageFolder = storageReference.child( "images/"+imageName );
            imageFolder.putFile( saveUri )
                    .addOnSuccessListener( this,new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            Toast.makeText( Home.this,"Uploaded!!" ,Toast.LENGTH_LONG).show();
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
                            Toast.makeText( Home.this,""+e.getMessage() ,Toast.LENGTH_LONG).show();
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


    //    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController( this, R.id.nav_host_fragment );
//        return NavigationUI.navigateUp( navController, mAppBarConfiguration )
//                || super.onSupportNavigateUp();
//    }
}
