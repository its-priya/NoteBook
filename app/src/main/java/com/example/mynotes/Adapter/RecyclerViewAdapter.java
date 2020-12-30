package com.example.mynotes.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mynotes.NoteDetails;
import com.example.mynotes.R;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    List<String> titles;
    List<String> contents;
    //static int[] colorCodes;
    public RecyclerViewAdapter(List<String> titles, List<String> contents){
        this.titles= titles;
        this.contents= contents;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent,false);
        //colorCodes= parent.getResources().getIntArray(R.array.colorCodeList);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        if(titles.get(position).isEmpty())
            holder.noteTitle.setVisibility(View.INVISIBLE);
        else {
            holder.noteTitle.setVisibility(View.VISIBLE);
            holder.noteTitle.setText(titles.get(position));
        }
        holder.noteContent.setText(contents.get(position));

        holder.noteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent noteDetailsIntent= new Intent(view.getContext(), NoteDetails.class);
                noteDetailsIntent.putExtra("title", titles.get(position));
                noteDetailsIntent.putExtra("content", contents.get(position));
                view.getContext().startActivity(noteDetailsIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView noteTitle, noteContent;
        View noteView;
        //ConstraintLayout noteLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle= itemView.findViewById(R.id.noteTitle);
            noteContent= itemView.findViewById(R.id.noteContent);
            //noteLayout= itemView.findViewById(R.id.noteLayout);
            noteView= itemView;
        }
    }
}
