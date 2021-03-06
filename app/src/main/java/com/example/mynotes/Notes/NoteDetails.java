package com.example.mynotes.Notes;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.example.mynotes.MainActivity;
import com.example.mynotes.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.method.ScrollingMovementMethod;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class NoteDetails extends AppCompatActivity {
    EditText noteDetailsContent, noteDetailsTitle;
    TextView noteDetailsLink;
    FirebaseFirestore fStore;
    public static DocumentReference docRef;
    String curNoteId, curTitle, curContent, curLink, curBkgColor, newBkgColor;
    LinearLayout noteDetailsLayout;
    ImageView imageColor0, imageColor1, imageColor2, imageColor3, imageColor4, imageColor5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        noteDetailsLayout= findViewById(R.id.noteDetailsLayout);
        noteDetailsTitle= findViewById(R.id.noteDetailsTitle);
        noteDetailsLink= findViewById(R.id.noteDetailsLink);
        noteDetailsContent= findViewById(R.id.noteDetailsContent);
        noteDetailsContent.setMovementMethod(new ScrollingMovementMethod());

        fStore= FirebaseFirestore.getInstance();

        final Intent noteData= getIntent();
        curTitle= noteData.getStringExtra("title");
        curContent= noteData.getStringExtra("content");
        curBkgColor= noteData.getStringExtra("bkgColor");
        curNoteId= noteData.getStringExtra("noteId");
        curLink= noteData.getStringExtra("link");
        newBkgColor= curBkgColor;

        noteDetailsTitle.setText(curTitle);
        noteDetailsContent.setText(curContent);
        noteDetailsLink.setText(curLink);
        if(noteDetailsLink.getText().length()==0){
            noteDetailsLink.setVisibility(View.GONE);
        }else{
            noteDetailsLink.setVisibility(View.VISIBLE);
        }
        noteDetailsLayout.setBackgroundColor(Color.parseColor(curBkgColor));
        docRef = fStore.collection("notes")
                .document(MainActivity.fUser.getUid())
                .collection("myNotes")
                .document(curNoteId);

        initQuickOptions();
        setNoteBkgColor(curBkgColor);
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
        String newLink= noteDetailsLink.getText().toString();

        if(curTitle.equals(newTitle)
                && curContent.equals(newContent) && curBkgColor.equals(newBkgColor)
                && noteDetailsLink.getVisibility()==View.VISIBLE && curLink.equals(newLink))
            onBackPressed();
        else if(newTitle.trim().isEmpty() && newContent.trim().isEmpty() && noteDetailsLink.getVisibility()==View.GONE){
            //delete the note.
            deleteNote();
        }
        else {
            Map<String, Object> note= new HashMap<>();
            note.put("title",newTitle);
            note.put("content", newContent);
            note.put("bkgColor", newBkgColor);
            note.put("link", newLink);
            docRef.update(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Error! Try again.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void initQuickOptions() {
        LinearLayout quickOptionsLayout = findViewById(R.id.quickOptionsLayout);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(quickOptionsLayout);
        quickOptionsLayout.findViewById(R.id.quickOptionsArrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED)
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                else
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        quickOptionsLayout.findViewById(R.id.deleteNote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteNote();
            }
        });
        quickOptionsLayout.findViewById(R.id.addLink).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                showAddLinkDialog();
            }
        });
        imageColor0 = quickOptionsLayout.findViewById(R.id.imageColor0);
        imageColor1 = quickOptionsLayout.findViewById(R.id.imageColor1);
        imageColor2 = quickOptionsLayout.findViewById(R.id.imageColor2);
        imageColor3 = quickOptionsLayout.findViewById(R.id.imageColor3);
        imageColor4 = quickOptionsLayout.findViewById(R.id.imageColor4);
        imageColor5 = quickOptionsLayout.findViewById(R.id.imageColor5);
        setSelectedColorImage(curBkgColor);
        quickOptionsLayout.findViewById(R.id.viewColor0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newBkgColor= "#181818";
                setSelectedColorImage(newBkgColor);
                setNoteBkgColor(newBkgColor);
            }
        });
        quickOptionsLayout.findViewById(R.id.viewColor1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newBkgColor = "#4F7942";
                setSelectedColorImage(newBkgColor);
                setNoteBkgColor(newBkgColor);
            }
        });
        quickOptionsLayout.findViewById(R.id.viewColor2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newBkgColor = "#2F575D";
                setSelectedColorImage(newBkgColor);
                setNoteBkgColor(newBkgColor);
            }
        });
        quickOptionsLayout.findViewById(R.id.viewColor3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newBkgColor = "#534666";
                setSelectedColorImage(newBkgColor);
                setNoteBkgColor(newBkgColor);
            }
        });
        quickOptionsLayout.findViewById(R.id.viewColor4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newBkgColor = "#5A6868";
                setSelectedColorImage(newBkgColor);
                setNoteBkgColor(newBkgColor);
            }
        });
        quickOptionsLayout.findViewById(R.id.viewColor5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newBkgColor = "#653060";
                setSelectedColorImage(newBkgColor);
                setNoteBkgColor(newBkgColor);
            }
        });
    }

    private void showAddLinkDialog() {
        final View addLinkView= LayoutInflater.from(this).inflate(R.layout.add_link_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(addLinkView);
        final TextInputLayout textLinkLayout= addLinkView.findViewById(R.id.textLinkLayout);
        final AlertDialog addLinkDialog= builder.create();
        addLinkView.findViewById(R.id.addLink).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText textLink= addLinkView.findViewById(R.id.textLink);
                final String textLinkVal= textLink.getText().toString().trim();
                if(textLinkVal.isEmpty()){

                }else if(!Patterns.WEB_URL.matcher(textLinkVal).matches()){
                    textLinkLayout.setError(getText(R.string.linkError));
                }else {
                    noteDetailsLink.setText(textLinkVal);
                    noteDetailsLink.setVisibility(View.VISIBLE);
                    addLinkDialog.dismiss();
                }
            }
        });
        addLinkView.findViewById(R.id.cancelLink).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addLinkDialog.dismiss();
            }
        });
        addLinkDialog.show();
    }

    private void setSelectedColorImage(String curColor){
        if(curColor.equals("#181818")){
            imageColor0.setImageResource(R.drawable.ic_done);
            imageColor1.setImageResource(0);
            imageColor2.setImageResource(0);
            imageColor3.setImageResource(0);
            imageColor4.setImageResource(0);
            imageColor5.setImageResource(0);
        }
        else if(curColor.equals("#4F7942")){
            imageColor0.setImageResource(0);
            imageColor1.setImageResource(R.drawable.ic_done);
            imageColor2.setImageResource(0);
            imageColor3.setImageResource(0);
            imageColor4.setImageResource(0);
            imageColor5.setImageResource(0);
        }
        else if(curColor.equals("#2F575D")){
            imageColor0.setImageResource(0);
            imageColor1.setImageResource(0);
            imageColor2.setImageResource(R.drawable.ic_done);
            imageColor3.setImageResource(0);
            imageColor4.setImageResource(0);
            imageColor5.setImageResource(0);
        }
        else if(curColor.equals("#534666")){
            imageColor0.setImageResource(0);
            imageColor1.setImageResource(0);
            imageColor2.setImageResource(0);
            imageColor3.setImageResource(R.drawable.ic_done);
            imageColor4.setImageResource(0);
            imageColor5.setImageResource(0);
        }
        else if(curColor.equals("#5A6868")){
            imageColor0.setImageResource(0);
            imageColor1.setImageResource(0);
            imageColor2.setImageResource(0);
            imageColor3.setImageResource(0);
            imageColor4.setImageResource(R.drawable.ic_done);
            imageColor5.setImageResource(0);
        }
        else if(curColor.equals("#653060")){
            imageColor0.setImageResource(0);
            imageColor1.setImageResource(0);
            imageColor2.setImageResource(0);
            imageColor3.setImageResource(0);
            imageColor4.setImageResource(0);
            imageColor5.setImageResource(R.drawable.ic_done);
        }
    }
    private void setNoteBkgColor(String bkgColor){
        noteDetailsLayout.setBackgroundColor(Color.parseColor(bkgColor));
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
            case R.id.updateNote:
                updateNoteFun();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}