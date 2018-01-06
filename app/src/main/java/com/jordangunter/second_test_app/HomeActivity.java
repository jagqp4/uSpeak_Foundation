package com.jordangunter.second_test_app;

import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Initialize toolbar
        Toolbar homeToolbar = (Toolbar) findViewById(R.id.home_toolbar);
        homeToolbar.setTitle("uSpeak");
        setSupportActionBar(homeToolbar);

        //Setup button listeners
        final Button speakButton = findViewById(R.id.speak_button);
        speakButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                switchToSpeakPage();
            }
        });

        final Button repButton = findViewById(R.id.rep_button);
        repButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                switchToRepPage();
            }
        });

        final Button aboutButton = findViewById(R.id.about_button);
        aboutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                switchToAboutPage();
            }
        });


    }

    //Initializes the navigation menu which is a child of the AppBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.home_menu, menu);
    /*
    MenuItem searchItem = menu.findItem(R.id.action_search);
    SearchView searchView =
            (SearchView) MenuItemCompat.getActionView(searchItem);

    // Configure the search info and add any event listeners...
    OnActionExpandListener expandListener = new OnActionExpandListener(){
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

    //Handles the logic for when a user selects an option from the Navigation menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_search:
                super.onOptionsItemSelected(item);
                return true;
            case R.id.action_speak:
                switchToSpeakPage();
                return true;
            case R.id.action_rep:
                switchToRepPage();
                return true;
            case R.id.action_about:
                switchToAboutPage();
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean switchToSpeakPage(){
        Intent speakIntent = new Intent(this,SpeakActivity.class);
        startActivity(speakIntent);
        return true;
    }

    private boolean switchToRepPage(){
        Intent repIntent = new Intent(this,RepresentativeActivity.class);
        startActivity(repIntent);
        return true;
    }

    private boolean switchToAboutPage(){
        Intent aboutIntent = new Intent(this,AboutActivity.class);
        startActivity(aboutIntent);
        return true;
    }
}
