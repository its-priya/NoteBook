package com.example.mynotes.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mynotes.R;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    List<String> titles;
    List<String> contents;
    public RecyclerViewAdapter(List<String> titles, List<String> contents){
        this.titles= titles;
        this.contents= contents;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.noteTitle.setText(titles.get(position));
        holder.noteContent.setText(contents.get(position));
        holder.noteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Clicked!", Toast.LENGTH_SHORT).show();
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
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle= itemView.findViewById(R.id.noteTitle);
            noteContent= itemView.findViewById(R.id.noteContent);
            noteView= itemView;
        }
    }
}
