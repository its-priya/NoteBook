package com.example.mynotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.example.mynotes.Adapter.RecyclerViewAdapter;
import com.example.mynotes.Adapter.RecyclerViewAdapter.ViewHolder;
import com.example.mynotes.Model.Note;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener{
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    SearchView searchView;
    ToggleButton toggleView;
    RecyclerView recyclerView;
    FirebaseFirestore firestore;
    FirestoreRecyclerAdapter<Note, ViewHolder> recyclerViewAdapter;
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
        toggleView= findViewById(R.id.toggleView);

        setSupportActionBar(toolbar);
        toggle= new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        firestore= FirebaseFirestore.getInstance();
        Query query= firestore.collection("notes");
        FirestoreRecyclerOptions<Note> allNotes= new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();
        recyclerViewAdapter= new RecyclerViewAdapter(allNotes);

        FloatingActionButton addNoteFab = findViewById(R.id.addNoteFab);
        addNoteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                startActivity(new Intent(MainActivity.this, AddNote.class));
            }
        });

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        toggleView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked)
                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                else
                    recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            }
        });
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        recyclerViewAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(recyclerViewAdapter!=null){
            recyclerViewAdapter.stopListening();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.addNote:
                startActivity(new Intent(MainActivity.this, AddNote.class));
        }
        return true;
    }
}