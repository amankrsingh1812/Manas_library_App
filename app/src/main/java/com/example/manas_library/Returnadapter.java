package com.example.manas_library;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.AvailabilityException;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Returnadapter extends RecyclerView.Adapter<Returnadapter.ViewHolder> {
    private List<Return_details> itemList;
    private Context context;
    private   OnNoteListener mOnNoteListener;


    public Returnadapter(List<Return_details> itemList, Context context, OnNoteListener onNoteListener) {
        this.itemList = itemList;
        this.context = context;
        this.mOnNoteListener=onNoteListener;

    }

    @NonNull
    @Override
    public Returnadapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.return_item,parent,false);
        return new ViewHolder(v,mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull Returnadapter.ViewHolder holder, int position) {
        Return_details ne=itemList.get(position);
        holder.textViewName.setText(ne.getName());
        holder.textViewAuthor.setText(ne.getAuthor());
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(ne.getIssue_date().getSeconds()*1000L);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        holder.textissuedate.setText(date);
        cal.setTimeInMillis((ne.getIssue_date().getSeconds()+2592000)*1000L);
        String date2 = DateFormat.format("dd-MM-yyyy", cal).toString();
        holder.textreturnate.setText(date2);


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView textViewName;
        TextView textViewAuthor;
        TextView textissuedate;
        TextView textreturnate;

        OnNoteListener onNoteListener;

        public ViewHolder(@NonNull View itemView,OnNoteListener onNoteListener) {
            super(itemView);
            textViewName=itemView.findViewById(R.id.return_name);
            textViewAuthor=itemView.findViewById(R.id.return_author);
            textissuedate=itemView.findViewById(R.id.return_issue_date);
            textreturnate=itemView.findViewById(R.id.return_return_date);

            this.onNoteListener=onNoteListener;
            itemView.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {
            onNoteListener.onNoteClick(getAdapterPosition());
        }
    }
    public interface OnNoteListener{
        void onNoteClick(int position);
    }

    public void filterList(List<Return_details> filteredList) {
        itemList = filteredList;
        notifyDataSetChanged();
    }

}