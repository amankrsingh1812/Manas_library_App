package com.example.manas_library;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Book_details {
    private String Name;
    private String Author;
    private String Description;
    private long Availability;
    private long ISBN;
    private long copies;

    public Book_details( String Name,String Author,String Description,long Availability,long ISBN,long copies){
//        super();
        this.Name=Name;
        this.Author=Author;
        this.Availability=Availability;
        this.ISBN=ISBN;
        this.Description=Description;
        this.copies=copies;
    }


    public long getCopies() {
        return copies;
    }

    public String getName() {
        return Name;
    }

    public String getAuthor() {
        return Author;
    }

    public String getDescription() {
        return Description;
    }

    public long getAvailability() {
        return Availability;
    }

    public long getISBN() {
        return ISBN;
    }
}
