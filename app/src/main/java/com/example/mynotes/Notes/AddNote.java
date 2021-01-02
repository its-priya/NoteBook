package com.example.mynotes.Notes;

import android.os.Bundle;

import com.example.mynotes.MainActivity;
import com.example.mynotes.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class AddNote extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private EditText addNoteTitle, addNoteContent;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        firestore= FirebaseFirestore.getInstance();

        addNoteTitle= findViewById(R.id.addNoteTitle);
        addNoteContent= findViewById(R.id.addNoteContent);
        progressBar= findViewById(R.id.progressAddNote);

        FloatingActionButton saveNoteFab = findViewById(R.id.saveNoteFab);
        saveNoteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String titleVal= addNoteTitle.getText().toString();
                String contentVal= addNoteContent.getText().toString();
                if(titleVal.trim().isEmpty() && contentVal.trim().isEmpty()) {
                    onBackPressed();
                }

                progressBar.setVisibility(View.VISIBLE);

                DocumentReference docRef= firestore.collection("notes")
                        .document(MainActivity.fUser.getUid())
                        .collection("myNotes").document();
                Map<String, Object> note= new HashMap<>();
                note.put("title", titleVal);
                note.put("content", contentVal);

                docRef.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        onBackPressed();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(AddNote.this, "Error! Try again.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}