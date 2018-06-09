package com.greylabs.greylabs.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.greylabs.greylabs.DatabaseHandler;
import com.greylabs.greylabs.R;
import com.greylabs.greylabs.adapter.LocationAdapter;
import com.greylabs.greylabs.model.LocationObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Button addNew;
    ArrayList<LocationObject> locationObjectArrayList = new ArrayList<LocationObject>();
    LocationAdapter locationAdapter;
    public static DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        readDB();
        init();
        onClicks();

    }

    public void initViews(){
        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        addNew = (Button)findViewById(R.id.add_new);
    }

    public void readDB(){
        db = new DatabaseHandler(this);
        locationObjectArrayList = db.getAllLocations();
        for (LocationObject lo : locationObjectArrayList) {
            String log = "Id: " + lo.getId() + " ,Address: " + lo.getAddress();
            // Writing Contacts to log
            Log.d("Name: ", log);
//            contacts.add(cn);
        }
        init();
    }

    public void init(){


        locationAdapter = new LocationAdapter(this, locationObjectArrayList);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(locationAdapter);



        db.getAllLocations();
    }

    public void onClicks(){
        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }



    @Override
    protected void onRestart() {
        super.onRestart();
        locationObjectArrayList = db.getAllLocations();
        locationAdapter.notifyDataSetChanged();
    }
}
