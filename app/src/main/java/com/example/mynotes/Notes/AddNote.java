package com.example.mynotes.Notes;

import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class AddNote extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private EditText addNoteTitle, addNoteContent;
    private ProgressBar progressBar;
    private ImageButton saveNote;
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
        saveNote= findViewById(R.id.saveNote);
        progressBar= findViewById(R.id.progressAddNote);
        progressBar.setVisibility(View.GONE);

        saveNote.setOnClickListener(new View.OnClickListener() {
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
    public void onBackPressed() {
        startActivity(new Intent(AddNote.this, MainActivity.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}