package com.example.mynotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.mynotes.Adapter.RecyclerViewAdapter;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener{
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    SearchView searchView;
    RecyclerView recyclerView;
    List<String> noteTitles;
    List<String> noteContents;
    RecyclerViewAdapter recyclerViewAdapter;
    //ToggleButton toggleView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Initializing Resources
        drawerLayout= findViewById(R.id.drawerLayout);
        navigationView= findViewById(R.id.navigationView);
        toolbar= findViewById(R.id.toolbar);
        searchView= findViewById(R.id.searchView);
        recyclerView= findViewById(R.id.recyclerView);
        //toggleView= findViewById(R.id.toggleView);
        noteTitles= new ArrayList<>();
        noteContents= new ArrayList<>();

        setSupportActionBar(toolbar);
        toggle= new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        /*noteTitles.add("First Title");
        noteContents.add("First Content");
        noteTitles.add("Second");
        noteContents.add("This is a note.This is a note.This is a note.");
        noteTitles.add("Third");
        noteContents.add("This is third note content.");*/

        recyclerViewAdapter= new RecyclerViewAdapter(noteTitles, noteContents);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            default:
                Toast.makeText(this, "Default", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}