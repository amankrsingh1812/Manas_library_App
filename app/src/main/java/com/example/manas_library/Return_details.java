package com.example.manas_library;

import com.google.firebase.Timestamp;

public class Return_details {
    private String Name;
    private String Author;
    private Timestamp issue_date;


    public Return_details(String Name, String Author, Timestamp issue_date){
//        super();
        this.Name=Name;
        this.Author=Author;
        this.issue_date=issue_date;
    }


    public String getName() {
        return Name;
    }

    public String getAuthor() {
        return Author;
    }

    public Timestamp getIssue_date() {
        return issue_date;
    }
}
