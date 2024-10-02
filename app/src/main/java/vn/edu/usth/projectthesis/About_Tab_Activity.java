package vn.edu.usth.projectthesis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class About_Tab_Activity extends AppCompatActivity {
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_tab);
        Toolbar toolbar = findViewById(R.id.toolbars_about_tab);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        drawerLayout = findViewById(R.id.drawer_layout_about_tab);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,R.string.nav_open,R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view_about_tab);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if(id == R.id.tab_files){
                Intent intent = new Intent(About_Tab_Activity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START);
            }else if (id == R.id.tab_images){
                Intent intent = new Intent(About_Tab_Activity.this, Images_Tab_Activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START);
            }else if (id == R.id.tab_statistic){
                Intent intent = new Intent(About_Tab_Activity.this, Statistic_Tab_Activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START);
            }else if(id == R.id.tab_about){

            } else if (id == R.id.tab_logout) {
                Intent intent = new Intent(About_Tab_Activity.this, LogInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            return true;
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}