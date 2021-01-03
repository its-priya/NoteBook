package com.example.mynotes.Notes;

import android.content.Intent;
import android.os.Bundle;

import com.example.mynotes.MainActivity;
import com.example.mynotes.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class NoteDetails extends AppCompatActivity {
    TextView noteDetailsContent, noteDetailsTitle;
    FirebaseFirestore fStore;
    public static DocumentReference docRef;
    String curNoteId, curTitle, curContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        noteDetailsTitle= findViewById(R.id.noteDetailsTitle);
        noteDetailsContent= findViewById(R.id.noteDetailsContent);
        noteDetailsContent.setMovementMethod(new ScrollingMovementMethod());

        fStore= FirebaseFirestore.getInstance();

        final Intent noteData= getIntent();
        curTitle= noteData.getStringExtra("title");
        curContent= noteData.getStringExtra("content");
        curNoteId= noteData.getStringExtra("noteId");
        noteDetailsTitle.setText(curTitle);
        noteDetailsContent.setText(curContent);
        docRef = fStore.collection("notes")
                .document(MainActivity.fUser.getUid())
                .collection("myNotes")
                .document(curNoteId);
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(NoteDetails.this, MainActivity.class));
        finish();
    }
    protected void deleteNote() {
        docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                onBackPressed();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error! Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    protected void updateNoteFun(){
        String newTitle= noteDetailsTitle.getText().toString();
        String newContent= noteDetailsContent.getText().toString();

        if(curTitle.equals(newTitle)
                && curContent.equals(newContent))
            onBackPressed();
        else if(newTitle.trim().isEmpty() && newContent.trim().isEmpty()){
            //delete the note.
            deleteNote();
        }
        else {
            Map<String, Object> note= new HashMap<>();
            note.put("title",newTitle);
            note.put("content", newContent);
            docRef.update(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    onBackPressed();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Error! Try again.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notes_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.deleteBtn:
                deleteNote();
                break;
            case R.id.updateNote:
                updateNoteFun();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}