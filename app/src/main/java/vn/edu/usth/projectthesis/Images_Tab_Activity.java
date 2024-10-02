package vn.edu.usth.projectthesis;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class Images_Tab_Activity extends AppCompatActivity {
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_tab);
        toolbar = findViewById(R.id.toolbars_images_tab);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerLayout = findViewById(R.id.drawer_layout_images_tab);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.nav_open,R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view_images_tab);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if(id == R.id.tab_files){
                Intent intent = new Intent(Images_Tab_Activity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START);
            }else if (id == R.id.tab_images){

            }else if (id == R.id.tab_statistic){
                Intent intent = new Intent(Images_Tab_Activity.this, Statistic_Tab_Activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START);
            }else if(id == R.id.tab_about){
                Intent intent = new Intent(Images_Tab_Activity.this, About_Tab_Activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START);
            } else if (id == R.id.tab_logout) {
                Intent intent = new Intent(Images_Tab_Activity.this, LogInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            return true;
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerview);

        List<Item> items = new ArrayList<Item>();
        items.add(new Item("Image", "hyper_20220913_3cm", R.drawable.hyper_20220913_3cm));
        items.add(new Item("Image", "hyper_20220913_2cm", R.drawable.hyper_20220913_2cm));
        items.add(new Item("Image", "hyper_20220923_3cm", R.drawable.hyper_20220923_3cm));
        items.add(new Item("Image", "hyper_20220923_2cm", R.drawable.hyper_20220923_2cm));
        items.add(new Item("Image", "hyper_20220519_3cm", R.drawable.hyper_20220519_3cm));


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MyAdapter(getApplicationContext(),items));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}