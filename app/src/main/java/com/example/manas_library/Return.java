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

public class Return extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private String name;
    private ZXingScannerView mScannerView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String code;
    private String uid;
    private String author;
    DocumentReference docRef,docRef2,docRef3;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return);
        Intent i = getIntent();
        Toast.makeText(getApplicationContext(),"Scan Verification Code to return", Toast.LENGTH_SHORT).show();
        uid=i.getStringExtra("uid");
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        name= i.getStringExtra("Name");
        docRef2 = db.collection("USERS").document(uid);
        docRef = db.collection("VERIFICATION_CODE").document("RETURN");
        progressDialog=new ProgressDialog(this);
        code=i.getStringExtra("code");
        setContentView(mScannerView);

//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//                        code = document.get("value").toString();
//                        Log.d("e", code);
//                    } else {
//                        Log.d("e", "No such document");
//                    }
//                } else {
//                    Log.d("e", "get failed with ", task.getException());
//                }
//            }
//        });
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
    public void onBackPressed() {
        super.onBackPressed();
        db.collection("VERIFICATION_CODE").document("RETURN").collection("USERS").document(uid).delete().addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        finish();

                    }
                });
    }


    @Override
    public void handleResult(Result rawResult) {
        mScannerView.stopCamera();          // Stop camera on resume

        // Do something with the result here
        Log.v("e", rawResult.getText()); // Prints scan results
        Log.v("e", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)

        if(code.equals(rawResult.getText())){
            mScannerView.stopCamera();
            progressDialog.setMessage("Returning your Book ...");
            progressDialog.show();
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
// Stop camera on pause

//            Toast.makeText(Return.this, name,
//                    Toast.LENGTH_SHORT).show();

            docRef2.update("allowed", FieldValue.increment(1)).addOnSuccessListener(new OnSuccessListener<Void>() {
               @Override
               public void onSuccess(Void aVoid) {
                   docRef2.collection("ISSUED").document(name).delete();
                   db.collection("VERIFICATION_CODE").document("RETURN").collection("USERS").document(uid).delete();
                   db.collection("BOOKS")
                           .whereEqualTo("Name", name)
                           .get()
                           .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                               @Override
                               public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                   if (task.isSuccessful()) {
                                       for (QueryDocumentSnapshot document : task.getResult()) {
                                           Log.d("e", document.getId() + " => " + document.getData());
                                           db.collection("BOOKS").document(document.getId()).update("copies",FieldValue.increment(1)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                               @Override
                                               public void onSuccess(Void aVoid) {
                                                   Log.d("e", "DocumentSnapshot successfully written!");
                                                   progressDialog.hide();
                                                   Toast.makeText(getApplicationContext(),"Sucess", Toast.LENGTH_SHORT).show();
//                                            Intent intent=new Intent(Return.this,Main2Activity.class);
//                                            intent.putExtra("uid",uid);
//                                            startActivity(intent);
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
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("e", "Error writing document", e);
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
