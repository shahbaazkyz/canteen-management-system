package com.example.canteen;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.canteen.Common.Common;
import com.example.canteen.Interface.ItemClickListener;
import com.example.canteen.Model.Category;
import com.example.canteen.Service.ListenOrder;
import com.example.canteen.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
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

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private AppBarConfiguration mAppBarConfiguration;
    FirebaseDatabase database;
    DatabaseReference category;
    DrawerLayout drawer;
    TextView txtFullName;
    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_home );
        Toolbar toolbar = findViewById( R.id.toolbar );
        toolbar.setTitle( "Menu" );
        setSupportActionBar( toolbar );

        //Init firebase
        database = FirebaseDatabase.getInstance("https://canteen-b207c-default-rtdb.firebaseio.com");
        category = database.getReference().child( "Category" );

        FloatingActionButton fab = findViewById( R.id.fab );
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent = new Intent(Home.this,Cart.class);
                startActivity(cartIntent);
            }
        } );
        drawer = findViewById( R.id.drawer_layout );
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener(this);

        //Set Name for User
        View headerView = navigationView.getHeaderView( 0 );
        txtFullName = (TextView) headerView.findViewById( R.id.txtFullName );
        txtFullName.setText( Common.currentUser.getName() );

        // Load Menu
        recycler_menu = (RecyclerView) findViewById( R.id.recycler_menu );
        recycler_menu.setHasFixedSize( false );
        layoutManager = new LinearLayoutManager( this );
        recycler_menu.setLayoutManager( layoutManager );
        loadMenu();
        Intent service=new Intent(Home.this, ListenOrder.class);
        startService(service);


    }

    private void loadMenu() {

         adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>( Category.class, R.layout.menu_item, MenuViewHolder.class, category ) {
            @Override
            protected void populateViewHolder(MenuViewHolder menuViewHolder, Category category, final int i) {
                menuViewHolder.txtMenuName.setText( category.getName() );
                Picasso.with( getBaseContext() ).load( category.getImage() )
                        .into( menuViewHolder.imageView );
                final Category clickItem = category;
                menuViewHolder.setItemClickListener( new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText( Home.this, "" + clickItem.getName(), Toast.LENGTH_LONG ).show();
                        Intent foodList=new Intent(Home.this,FoodList.class);
                        foodList.putExtra("CategoryId",adapter.getRef(position).getKey());
                        startActivity(foodList);
                    }
                } );
            }
        };
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
        else if(id == R.id.nav_cart)
        {
            Intent cartintent = new Intent(Home.this,Cart.class);
            startActivity(cartintent);
        }
        else if(id == R.id.nav_orders)
        {
            Intent orderintent = new Intent(Home.this,Orderstatus.class);
            startActivity(orderintent);
        }
        else if(id == R.id.nav_log_out)
        {
            Intent signIn = new Intent(Home.this, SignIn.class);
            signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signIn);
        }
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }
}
