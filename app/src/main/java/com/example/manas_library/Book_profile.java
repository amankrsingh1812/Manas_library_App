package com.example.manas_library;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Book_profile extends AppCompatActivity {

    private TextView name;
    private TextView description;
    private TextView author;
    private TextView tc;
    private TextView ac;
    private Button button;
    private String issueList;
    private String uid;
    private long cps;
    private ProgressDialog progressDialog;

    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_profile);
        initInstancesDrawer();
        uid=getIntent().getStringExtra("uid");
        progressDialog=new ProgressDialog(this);

        name=(TextView)findViewById(R.id.namepqwo);
        description=(TextView)findViewById(R.id.Desc);
        author=(TextView)findViewById(R.id.textView19);
        tc=(TextView)findViewById(R.id.totalcopies);
        ac=(TextView)findViewById(R.id.availabecopies);
        button=(Button)findViewById(R.id.issuebut);
        Intent i=getIntent();
        name.setText(i.getStringExtra("Name"));
        description.setText(i.getStringExtra("Description"));
        author.setText(i.getStringExtra("Author"));
        tc.setText(i.getStringExtra("Availability"));
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        db.collection("BOOKS")
                .whereEqualTo("Name", i.getStringExtra("Name"))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("e", document.getId() + " => " + document.getData());
                                cps=document.getLong("copies");
                                ac.setText(Long.toString(cps));

                            }
                        } else {
                            Log.d("e", "Error getting documents: ", task.getException());
                        }
                    }
                });

        db.collection("USERS").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("e", "DocumentSnapshot data: " + document.getData());
                        if(document.getLong("allowed")>0){
                            progressDialog.hide();
//                                    finish();
                        }
                        else{
                            progressDialog.hide();
                            button.setClickable(false);
                            Toast.makeText(getApplicationContext(),"Issue Limit Over!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d("e", "No such document");
                    }
                } else {
                    Log.d("e", "get failed with ", task.getException());
                }
            }
        });;
        if(Long.parseLong(i.getStringExtra("copies"))==0){
            button.setClickable(false);
        }

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(cps>0){
                    Intent i = getIntent();
                    issueList=i.getStringExtra("Name");
                    Intent intent=new Intent(Book_profile.this,Issue.class);
                    intent.putExtra("IssueList",issueList);
                    intent.putExtra("Author",i.getStringExtra("Author"));
                    intent.putExtra("uid",uid);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"No Copies Available!", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    private void initInstancesDrawer() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        collapsingToolbarLayout.setTitle(" ");
    }
}

