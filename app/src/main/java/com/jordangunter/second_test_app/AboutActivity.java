package com.jordangunter.second_test_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.about_toolbar);
        toolbar.setTitle("About");
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.about_menu, menu);
        /*
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(searchItem);

        // Configure the search info and add any event listeners...
        MenuItem.OnActionExpandListener expandListener = new MenuItem.OnActionExpandListener(){
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item){
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item){
                return true;
            }
        };
        */
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_search:
                super.onOptionsItemSelected(item);
                return true;
            case R.id.action_home:
                Intent homeIntent = new Intent(this,HomeActivity.class);
                startActivity(homeIntent);
                return true;
            case R.id.action_speak:
                Intent speakIntent = new Intent(this,SpeakActivity.class);
                startActivity(speakIntent);
                return true;
            case R.id.action_rep:
                Intent repIntent = new Intent(this,RepresentativeActivity.class);
                startActivity(repIntent);
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
