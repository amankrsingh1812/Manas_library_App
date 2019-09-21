package com.example.manas_library;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.Result;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class scanner extends AppCompatActivity implements ZXingScannerView.ResultHandler{
    private ProgressDialog progressDialog;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ZXingScannerView mScannerView;
    private String uid;
    private String author;
    private String name;
    private Long cps;
    private String uemail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        Intent i = getIntent();
        uid=i.getStringExtra("uid");
//        uemail=i.getStringExtra("uemail");
        progressDialog=new ProgressDialog(this);

        mScannerView = new ZXingScannerView(this);
        Toast.makeText(getApplicationContext(),"Scan ISBN to issue", Toast.LENGTH_SHORT).show();
        setContentView(mScannerView);
// Programmatically initialize the scanner view

    }
    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        mScannerView.stopCamera();
        Log.d("e",rawResult.getBarcodeFormat().toString());
        if(rawResult.getBarcodeFormat().toString().equals("EAN_13")){
            long isbn=Long.parseLong(rawResult.getText());
            progressDialog.setMessage("Loading ...");
            progressDialog.show();
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            db.collection("BOOKS")
                    .whereEqualTo("ISBN", isbn)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if(task.getResult().isEmpty())
                                {
                                    progressDialog.hide();
                                    Toast.makeText(getApplicationContext(),"This Book is unavailabe in library ", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        name=document.get("Name").toString();
                                        author=document.get("Author").toString();
                                        cps=document.getLong("copies");
                                    }
                                    if(cps>0)
                                    {
                                        Intent intent=new Intent(scanner.this,Issue.class);
                                        intent.putExtra("IssueList",name);
                                        intent.putExtra("Author",author);
                                        intent.putExtra("uid",uid);
//                                        intent.putExtra("uemail",uemail);

                                        startActivity(intent);
                                        progressDialog.hide();
                                        finish();

                                    }
                                    else
                                    {
                                        progressDialog.hide();
                                        Toast.makeText(getApplicationContext(),"No Copies Left!", Toast.LENGTH_SHORT).show();
                                    }

                                }

                            } else {
                                progressDialog.hide();
                                Log.d("e", "Error getting documents: ", task.getException());
                                Toast.makeText(getApplicationContext(),"This Book is unavailabe in library ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });



        }
        else{
            Toast.makeText(getApplicationContext(),"Scan Valid ISBN!", Toast.LENGTH_SHORT).show();
            mScannerView.startCamera();          // Start camera on resume

        }
        mScannerView.resumeCameraPreview(this);



    }
}
