package com.example.mynotes.Notes;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.example.mynotes.MainActivity;
import com.example.mynotes.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class AddNote extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private EditText addNoteTitle, addNoteContent;
    private TextView addNoteLink;
    private String addNoteColor;
    private ProgressBar progressBar;
    private ImageButton saveNote;
    String selectedNoteColor;
    LinearLayout notePageLayout;
    ImageView imageColor0, imageColor1, imageColor2, imageColor3, imageColor4, imageColor5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        firestore= FirebaseFirestore.getInstance();

        notePageLayout= findViewById(R.id.notePageLayout);
        addNoteTitle= findViewById(R.id.addNoteTitle);
        addNoteContent= findViewById(R.id.addNoteContent);
        addNoteLink= findViewById(R.id.addNoteLink);
        saveNote= findViewById(R.id.saveNote);
        progressBar= findViewById(R.id.progressAddNote);
        progressBar.setVisibility(View.GONE);

        selectedNoteColor= "#181818";       // Default color
        initQuickOptions();
        setNoteBkgColor();
        saveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String titleVal= addNoteTitle.getText().toString();
                String contentVal= addNoteContent.getText().toString();
                String linkVal= addNoteLink.getText().toString();
                if(titleVal.trim().isEmpty() && contentVal.trim().isEmpty()
                        && addNoteLink.getVisibility()==View.GONE) {
                    onBackPressed();
                }

                progressBar.setVisibility(View.VISIBLE);

                DocumentReference docRef= firestore.collection("notes")
                        .document(MainActivity.fUser.getUid())
                        .collection("myNotes").document();
                Map<String, Object> note= new HashMap<>();
                note.put("title", titleVal);
                note.put("content", contentVal);
                note.put("bkgColor", selectedNoteColor);
                note.put("link", linkVal);

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
                onBackPressed();
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
        quickOptionsLayout.findViewById(R.id.viewColor0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoteColor= "#181818";
                imageColor0.setImageResource(R.drawable.ic_done);
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setNoteBkgColor();
            }
        });
        quickOptionsLayout.findViewById(R.id.viewColor1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoteColor = "#4F7942";
                imageColor0.setImageResource(0);
                imageColor1.setImageResource(R.drawable.ic_done);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setNoteBkgColor();
            }
        });
        quickOptionsLayout.findViewById(R.id.viewColor2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoteColor = "#2F575D";
                imageColor0.setImageResource(0);
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(R.drawable.ic_done);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setNoteBkgColor();
            }
        });
        quickOptionsLayout.findViewById(R.id.viewColor3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoteColor = "#534666";
                imageColor0.setImageResource(0);
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(R.drawable.ic_done);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setNoteBkgColor();
            }
        });
        quickOptionsLayout.findViewById(R.id.viewColor4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoteColor = "#5A6868";
                imageColor0.setImageResource(0);
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(R.drawable.ic_done);
                imageColor5.setImageResource(0);
                setNoteBkgColor();
            }
        });
        quickOptionsLayout.findViewById(R.id.viewColor5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoteColor = "#653060";
                imageColor0.setImageResource(0);
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(R.drawable.ic_done);
                setNoteBkgColor();
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
                    addNoteLink.setText(textLinkVal);
                    addNoteLink.setVisibility(View.VISIBLE);
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

    private void setNoteBkgColor(){
        notePageLayout.setBackgroundColor(Color.parseColor(selectedNoteColor));
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