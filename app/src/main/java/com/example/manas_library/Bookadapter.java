package com.example.manas_library;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.AvailabilityException;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class Bookadapter extends RecyclerView.Adapter<Bookadapter.ViewHolder> {
    private List<Book_details> itemList;
    private Context context;
    private   OnNoteListener mOnNoteListener;


    public Bookadapter(List<Book_details> itemList, Context context,OnNoteListener onNoteListener) {
        this.itemList = itemList;
        this.context = context;
        this.mOnNoteListener=onNoteListener;

    }

    @NonNull
    @Override
    public Bookadapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.book_item,parent,false);
        return new ViewHolder(v,mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull Bookadapter.ViewHolder holder, int position) {
        Book_details ne=itemList.get(position);
        holder.bind(ne);


//
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(context,Book_profile.class);
//                intent.putExtra("Name",Name);
//                intent.putExtra("Author",Author);
//                intent.putExtra("Description",Description);
//                intent.putExtra("Availability", Availability);
//                intent.putExtra("ISBN",ISBN);
//                intent.putExtra("copies",copies);
//
//                //intent.putExtra("location",model);
////                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
////                    context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation((Activity)MainActivity.act).toBundle());
////                }
////                else{
////                    context.startActivity(intent);
////                }
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView textViewName;
        TextView textViewAuthor;
        private Book_details curentitem;
        OnNoteListener onNoteListener;

        public ViewHolder(@NonNull View itemView,OnNoteListener onNoteListener) {
            super(itemView);
            textViewName=itemView.findViewById(R.id.book_name);
            textViewAuthor=itemView.findViewById(R.id.book_author);
            this.onNoteListener=onNoteListener;
            itemView.setOnClickListener(this);

        }

        void bind (Book_details item) {
            textViewName.setText(item.getName());
            textViewAuthor.setText(item.getAuthor());
            curentitem=item;
        }
            @Override
        public void onClick(View v) {
            onNoteListener.onNoteClick(curentitem);
        }
    }
    public interface OnNoteListener{
        void onNoteClick(Book_details it);
    }

    public void filterList(List<Book_details> filteredList) {
        itemList = filteredList;
        notifyDataSetChanged();
    }

}