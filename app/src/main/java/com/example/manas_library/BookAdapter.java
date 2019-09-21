package com.example.manas_library;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.List;

public class BookAdapter extends FirestoreRecyclerAdapter<Book_details, BookAdapter.BookHolder> {


    public BookAdapter(@NonNull FirestoreRecyclerOptions<Book_details> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull BookHolder bookHolder, int i, @NonNull Book_details book_details) {
        bookHolder.textViewName.setText(book_details.getName());
        bookHolder.textViewAuthor.setText(book_details.getAuthor());
    }

    @NonNull
    @Override
    public BookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_item,parent,false);
        return new BookHolder(v);
    }

    class BookHolder extends RecyclerView.ViewHolder{
        TextView textViewName;
        TextView textViewAuthor;


        public BookHolder(@NonNull View itemView) {
            super(itemView);
            textViewName=itemView.findViewById(R.id.book_name);
            textViewAuthor=itemView.findViewById(R.id.book_author);
        }
    }

}
