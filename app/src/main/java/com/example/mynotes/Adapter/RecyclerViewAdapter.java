package com.example.mynotes.Adapter;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mynotes.MainActivity;
import com.example.mynotes.Model.Note;
import com.example.mynotes.Notes.NoteDetails;
import com.example.mynotes.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class RecyclerViewAdapter extends FirestoreRecyclerAdapter<Note, RecyclerViewAdapter.ViewHolder>{
    FirestoreRecyclerOptions<Note> allNotes;

    public RecyclerViewAdapter(@NonNull FirestoreRecyclerOptions<Note> allNotes) {
        super(allNotes);
        this.allNotes= allNotes;
    }
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, final int position, @NonNull final Note note) {
        if(note.getTitle().isEmpty())
            holder.noteTitle.setVisibility(View.GONE);
        else {
            holder.noteTitle.setVisibility(View.VISIBLE);
            holder.noteTitle.setText(note.getTitle());
        }
        if(note.getContent().isEmpty())
            holder.noteContent.setVisibility(View.GONE);
        else {
            holder.noteContent.setVisibility(View.VISIBLE);
            holder.noteContent.setText(note.getContent());
        }
        if (note.getLink().isEmpty())
            holder.noteLink.setVisibility(View.GONE);
        else{
            holder.noteLink.setVisibility(View.VISIBLE);
            holder.noteLink.setText(note.getLink());
        }
        if(!note.getBkgColor().isEmpty()) {
            GradientDrawable gradientDrawable = (GradientDrawable) holder.noteView.getBackground().mutate();
            gradientDrawable.setColor(Color.parseColor(note.getBkgColor()));
            if (!note.getBkgColor().equals("#181818"))
                gradientDrawable.setStroke(1, Color.parseColor(note.getBkgColor()));
        }
        final String noteId= MainActivity.recyclerViewAdapter.getSnapshots().getSnapshot(position).getId();
        holder.noteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent noteDetailsIntent= new Intent(view.getContext(), NoteDetails.class);
                noteDetailsIntent.putExtra("title", note.getTitle());
                noteDetailsIntent.putExtra("content", note.getContent());
                noteDetailsIntent.putExtra("bkgColor", note.getBkgColor());
                noteDetailsIntent.putExtra("noteId", noteId);
                noteDetailsIntent.putExtra("link", note.getLink());
                view.getContext().startActivity(noteDetailsIntent);
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent,false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView noteTitle, noteContent, noteLink;
        View noteView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle= itemView.findViewById(R.id.noteTitle);
            noteContent= itemView.findViewById(R.id.noteContent);
            noteLink= itemView.findViewById(R.id.noteLink);
            noteView= itemView;
        }
    }
}
