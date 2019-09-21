package com.example.manas_library;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Issue extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private String issueList;
    private ZXingScannerView mScannerView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String code;
    private String uid;
    private String uemail;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    private String author;
    DocumentReference docRef,docRef2,docRef3;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue);
        Intent i = getIntent();
        Toast.makeText(getApplicationContext(),"Scan Verification Code to issue", Toast.LENGTH_SHORT).show();
        uid=i.getStringExtra("uid");
        author=i.getStringExtra("Author");
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        issueList= i.getStringExtra("IssueList");
        docRef2 = db.collection("USERS").document(uid);
        docRef = db.collection("VERIFICATION_CODE").document("ISSUE");
        progressDialog=new ProgressDialog(this);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(this, MyBroadCastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        code = document.get("value").toString();
                        Log.d("e", code);
                        setContentView(mScannerView);

                    } else {
                        Log.d("e", "No such document");
                    }
                } else {
                    Log.d("e", "get failed with ", task.getException());
                }
            }
        });
//        docRef = db.collection("VERIFICATION_CODE").document("ISSUE");



                    // Set the scanner view as the content view

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

        // Do something with the result here
        Log.v("e", rawResult.getText()); // Prints scan results
        Log.v("e", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)

        if(code.equals(rawResult.getText())){
            mScannerView.stopCamera();
            progressDialog.setMessage("Issuing your Book ...");
            progressDialog.show();
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
// Stop camera on pause

            Toast.makeText(Issue.this, issueList,
                        Toast.LENGTH_SHORT).show();
                Map<String, Object> book = new HashMap<>();
                book.put("issue_date",new Timestamp(new Date()));
                book.put("Author",author);
                book.put("Name",issueList);
                docRef2.update("allowed", FieldValue.increment(-1));

                docRef2.collection("ISSUED").document(issueList).set(book);

                db.collection("BOOKS")
                    .whereEqualTo("Name", issueList)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("e", document.getId() + " => " + document.getData());
                                    db.collection("BOOKS").document(document.getId()).update("copies",FieldValue.increment(-1)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("e", "DocumentSnapshot successfully written!");
                                            progressDialog.hide();
//                                            startAlarm();
                                            Toast.makeText(getApplicationContext(),"Sucess", Toast.LENGTH_SHORT).show();

                                            finish();
                                        }
                                    })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w("e", "Error writing document", e);
                                                }
                                            });
                                    ;
                                }
                            } else {
                                Log.d("e", "Error getting documents: ", task.getException());
                            }
                        }
                    });



        }
        else
        {
            Toast.makeText(getApplicationContext(),"QR Code Doesn't Match! Try again", Toast.LENGTH_SHORT).show();
            mScannerView.startCamera();          // Start camera on resume


        }
            // If you would like to resume scanning, call this method below:
            mScannerView.resumeCameraPreview(this);
        }

    }


