package com.example.manas_library;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main2Activity extends AppCompatActivity implements Returnadapter.OnNoteListener{
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference returnref;
    private List<Return_details> itemList;
    private Returnadapter adapter;
    private EditText search_et;
    private String uid;
    private String uemail;
    private FirebaseAuth mAuth;

    private ProgressDialog progressDialog;
    View dialogView;
    private Button buttonm1;
    private Button scan;
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    DocumentReference docRef,docRef2,docRef3;
//    private ProgressDialog progressDialog;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        itemList=new ArrayList<>();
        progressDialog=new ProgressDialog(this);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        buttonm1=(Button)findViewById(R.id.buttonm1);
        scan=(Button)findViewById(R.id.scan2);
        uid=getIntent().getStringExtra("uid");
        uemail=user.getEmail();
//        uemail=getIntent().getStringExtra("uemail");
        returnref= db.collection("USERS").document(uid).collection("ISSUED");
//        setUpRecyclerView();
        buttonm1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Main2Activity.this,MainActivity.class);
                intent.putExtra("uid",uid);
//                intent.putExtra("uemail",user.getEmail());
//                startActivity(intent);
                finish();
            }

        });
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
                db.collection("USERS").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d("e", "DocumentSnapshot data: " + document.getData());
                                if(document.getLong("allowed")>0){
                                    Intent intent=new Intent(Main2Activity.this,scanner.class);
                                    intent.putExtra("uid",uid);
                                    startActivity(intent);
                                    progressDialog.hide();

//                                    finish();
                                }
                                else{
                                    progressDialog.hide();
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

            }

        });
    }
    @Override
    protected void onStart()
    {
        super.onStart();
        setUpRecyclerView();
//        Toast.makeText(getApplicationContext(),"Hello", Toast.LENGTH_SHORT).show();

    }
    private void setUpRecyclerView(){
        progressDialog.setMessage("Loading Book List ...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

//        Query query = booksref.orderBy("ISBN",Query.Direction.DESCENDING);
//
//        FirestoreRecyclerOptions<Book_details> options=new FirestoreRecyclerOptions.Builder<Book_details>().setQuery(query,Book_details.class).build();
//
//        adapter =new BookAdapter(options);
//
        RecyclerView recyclerView = findViewById(R.id.recycler_view2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter=new Returnadapter(itemList,this,this);
        recyclerView.setAdapter(adapter);
        itemList.clear();

        returnref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                        itemList.add(new Return_details(documentSnapshot.get("Name").toString(),documentSnapshot.get("Author").toString(),(Timestamp)documentSnapshot.get("issue_date")));
                    }
                    adapter.notifyDataSetChanged();
                    adapter.filterList(itemList);
                    progressDialog.hide();

                }
                else{
                    String error=task.getException().getMessage();
                    progressDialog.hide();
                    Toast.makeText(Main2Activity.this, error, Toast.LENGTH_SHORT).show();
                }
//                bar.dismiss();
            }
        });
    }
    @Override
    public void onNoteClick(int position) {
        Return_details ne=itemList.get(position);
        final String Name=ne.getName();
        final String Author=ne.getAuthor();
        Toast.makeText(Main2Activity.this, ne.getName(), Toast.LENGTH_SHORT).show();
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog, null);

        Button button1 = (Button) dialogView.findViewById(R.id.buttonOkay);
        Button button2 = (Button) dialogView.findViewById(R.id.buttonCancel);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> code = new HashMap<>();
                final String str=randomAlphaNumeric(15);
                code.put("value",str);
                code.put("uid",uid);
                code.put("uemail",uemail);
                code.put("date",new Timestamp(new Date()).toString());
                db.collection("VERIFICATION_CODE").document("RETURN").collection("USERS").document(uid).set(code).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intent=new Intent(Main2Activity.this,Return.class);
                        intent.putExtra("Name",Name);
                        intent.putExtra("uid",uid);

                        intent.putExtra("code",str);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation((Activity)Main2Activity.this).toBundle());
                        }
                        else{
                            startActivity(intent);
                        }
                        dialogBuilder.dismiss();
                    }}).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("e", "Error writing document", e);
                    }
                });;


//                finish();
            }
        });
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();


    }
}
