package com.jordangunter.second_test_app;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class RepresentativeActivity extends AppCompatActivity {
    private FusedLocationProviderClient mFusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_representative);

        Toolbar rep_toolbar = findViewById(R.id.rep_toolbar);
        rep_toolbar.setTitle("Your Representative");
        setSupportActionBar(rep_toolbar);

        ImageView image = findViewById(R.id.rep_pic);
        image.setImageResource(R.drawable.bobby_rush_photo);

        ImageView vote1Graphic = findViewById(R.id.vote_1_graphic);
        vote1Graphic.setImageResource(R.drawable.green_check);

        ImageView vote2Graphic = findViewById(R.id.vote_2_graphic);
        vote2Graphic.setImageResource(R.drawable.red_x);

        ImageView vote3Graphic = findViewById(R.id.vote_3_graphic);
        vote3Graphic.setImageResource(R.drawable.red_x);


        addListenerToButton();
    }

    private boolean addListenerToButton(){
        final RadioGroup radioGroup = findViewById(R.id.radioGroup);
        Button submit = findViewById(R.id.submitButton);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(radioGroup.getCheckedRadioButtonId() != -1) {

                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    RadioButton choice = findViewById(selectedId);

                    Toast.makeText(RepresentativeActivity.this,
                            choice.getText(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.representative_menu, menu);
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
            case R.id.action_speak:
                Intent speakIntent = new Intent(this,SpeakActivity.class);
                startActivity(speakIntent);
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
}
