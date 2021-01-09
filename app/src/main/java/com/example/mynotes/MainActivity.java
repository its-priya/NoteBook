package com.example.mynotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.mynotes.Adapter.RecyclerViewAdapter;
import com.example.mynotes.Adapter.RecyclerViewAdapter.ViewHolder;
import com.example.mynotes.Auth.Login;
import com.example.mynotes.Auth.SignUp;
import com.example.mynotes.Model.Note;
import com.example.mynotes.Notes.AddNote;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    SearchView searchView;
    ToggleButton toggleView;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    public static boolean isAccountActive;
    FirebaseFirestore firestore;
    FirebaseAuth fAuth;
    public static FirebaseUser fUser;
    public static FirestoreRecyclerAdapter<Note, ViewHolder> recyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Initializing Resources
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerView);
        toggleView = findViewById(R.id.toggleView);
        progressBar = findViewById(R.id.progressLogout);
        progressBar.setVisibility(View.GONE);
        isAccountActive= true;
        //Navigation Drawer
        setSupportActionBar(toolbar);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //Firebase
        firestore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
        Query query = firestore.collection("notes").document(fUser.getUid()).collection("myNotes");   //Set Query as per User.
        FirestoreRecyclerOptions<Note> allNotes = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();
        recyclerViewAdapter = new RecyclerViewAdapter(allNotes);

        // Navigation Header Display
        View navHeaderView = navigationView.getHeaderView(0);
        TextView displayName = navHeaderView.findViewById(R.id.userDisplayName);
        TextView displayEmail = navHeaderView.findViewById(R.id.userDisplayEmail);
        if (fUser.isAnonymous()) {
            displayName.setText(R.string.guest);
            displayEmail.setVisibility(View.INVISIBLE);
        } else {
            displayName.setVisibility(View.VISIBLE);
            displayName.setText(fUser.getDisplayName());
            displayEmail.setText(fUser.getEmail());
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        FloatingActionButton addNoteFab = findViewById(R.id.addNoteFab);
        addNoteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddNote.class));
            }
        });

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        toggleView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked)
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
        if (recyclerViewAdapter != null) {
            recyclerViewAdapter.stopListening();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        switch (item.getItemId()) {
            case R.id.addNote:
                startActivity(new Intent(MainActivity.this, AddNote.class));
                break;
            case R.id.sync:
                if (fUser.isAnonymous()) {
                    displayAlertOnNewLogin();
                } else {
                    Toast.makeText(getApplicationContext(), "Your notes are Synced.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.logout:
                checkUser();
                break;
        }
        return true;
    }

    private void displayAlertOnNewLogin() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.alertTitle)
                .setMessage(R.string.alertMsgOnSync)
                .setPositiveButton(R.string.syncNote, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(getApplicationContext(), SignUp.class));
                        finish();
                    }
                })
                .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {
                        progressBar.setVisibility(View.VISIBLE);
                        // Delete Anonymous user's Account with data.
                        deleteUserAccount();
                    }
                })
                .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void checkUser() {
        if (fUser.isAnonymous()) {
            displayAlert();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.GONE);
                    fAuth.signOut();
                    isAccountActive= false;
                    startActivity(new Intent(getApplicationContext(), Login.class));
                    finish();
                }
            }, 500);
        }
    }

    private void displayAlert() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.alertTitle)
                .setMessage(R.string.alertMsg)
                .setPositiveButton(R.string.syncNote, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        isAccountActive= false;
                        startActivity(new Intent(getApplicationContext(), Login.class));
                        finish();
                    }
                })
                .setNegativeButton(R.string.logout, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {
                        progressBar.setVisibility(View.VISIBLE);
                        // Delete Anonymous user's Account with its data.
                        deleteUserAccount();
                    }
                })
                .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void deleteUserAccount() {
        // Delete Anonymous User's data.
        firestore.collection("notes").document(fUser.getUid())
                .collection("myNotes").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                WriteBatch batch = firestore.batch();
                List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot snapshot : snapshotList)
                    batch.delete(snapshot.getReference());
                batch.commit()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                fUser.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        isAccountActive= false;
                                        startActivity(new Intent(getApplicationContext(), Login.class));
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        onFailureDisplay(e);
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        onFailureDisplay(e);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onFailureDisplay(e);
            }
        });
    }
    private void onFailureDisplay(Exception e){
        progressBar.setVisibility(View.GONE);
        Toast.makeText(MainActivity.this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
}