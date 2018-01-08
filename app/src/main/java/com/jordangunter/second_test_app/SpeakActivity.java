package com.jordangunter.second_test_app;

import android.app.SearchManager;
import android.content.Context;
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
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import org.votesmart.classes.*;
import org.votesmart.api.*;
import org.votesmart.data.*;

public class SpeakActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speak);
        Toolbar toolbar = findViewById(R.id.speak_toolbar);
        toolbar.setTitle("Speak");
        setSupportActionBar(toolbar);

        addListenerToButton();
        //initializeBill();
    }

    //sets up polling feature
    private boolean addListenerToButton(){
        final RadioGroup radioGroup = findViewById(R.id.radioGroup);
        Button submit = findViewById(R.id.submitButton);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(radioGroup.getCheckedRadioButtonId() != -1) {
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    RadioButton choice = findViewById(selectedId);

                    Toast.makeText(SpeakActivity.this,
                            choice.getText(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return true;
    }

    //sets up menu bar navigation
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.speak_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));


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
            case R.id.action_rep:
                Intent repIntent = new Intent(this,RepresentativeActivity.class);
                startActivity(repIntent);
                return true;
            case R.id.action_about:
                Intent aboutIntent = new Intent(this,AboutActivity.class);
                startActivity(aboutIntent);
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private boolean initializeBill() {
        //VotesClass votesClass = new VotesClass();
        return true;
    }

}
