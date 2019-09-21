package com.example.manas_library;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements Bookadapter.OnNoteListener{
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference booksref = db.collection("BOOKS");
    private List<Book_details> itemList;
    private Bookadapter adapter;
    private EditText search_et;
    private String uid;
    private ProgressDialog progressDialog;
    private Button buttonm2;
    private Button scan;
    private String token;
    private String uemail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
//        getSupportActionBar().hide(); // hide the title bar
        setContentView(R.layout.activity_main);
        scan=(Button)findViewById(R.id.scan1);
        search_et =(EditText)findViewById(R.id.search_et);
        itemList=new ArrayList<>();
        progressDialog=new ProgressDialog(this);
        buttonm2=(Button)findViewById(R.id.buttonm2);
        uid=getIntent().getStringExtra("uid");
//        uemail=getIntent().getStringExtra("uemail");
        buttonm2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,Main2Activity.class);
                intent.putExtra("uid",uid);
//                intent.putExtra("uemail",uemail);
                startActivity(intent);
            }

            });
        search_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("e", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                         token = task.getResult().getToken();
                         db.collection("USERS").document(uid).update("token",token);


                        // Log and toast
//                        String msg = getString(R.string.msg_token_fmt, token);
//                        Log.d(TAG, msg);
//                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

        setUpRecyclerView();
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
                                    Intent intent=new Intent(MainActivity.this,scanner.class);
                                    intent.putExtra("uid",uid);
//                                    intent.putExtra("uemail",uemail);
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
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter=new Bookadapter(itemList,this,this);
        recyclerView.setAdapter(adapter);
        itemList.clear();

        booksref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                        itemList.add(new Book_details(documentSnapshot.get("Name").toString(),documentSnapshot.get("Author").toString(),documentSnapshot.get("Description").toString(),(long)documentSnapshot.get("Availability"),(long)documentSnapshot.get("ISBN"),(long)documentSnapshot.get("copies")));
                    }
                    adapter.notifyDataSetChanged();
                    adapter.filterList(itemList);
                    progressDialog.hide();

                }
                else{
                    String error=task.getException().getMessage();
                    progressDialog.hide();
                    Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                }
//                bar.dismiss();
            }
        });
    }
    private void filter(String text) {
        List<Book_details> filteredList = new ArrayList<>();

        for (Book_details item : itemList) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())||item.getAuthor().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        adapter.filterList(filteredList);
    }
    @Override
    protected void onStart()
    {
        super.onStart();
//        Toast.makeText(getApplicationContext(),"Hello", Toast.LENGTH_SHORT).show();
        search_et.setText("");

    }

    @Override
    public void onNoteClick(Book_details ne) {
        final String Name=ne.getName();
        final String Author=ne.getAuthor();
        final String Description=ne.getDescription();
        final long Availability=ne.getAvailability();
        final long ISBN=ne.getISBN();
        final long copies = ne.getCopies();
        Toast.makeText(MainActivity.this, ne.getName(), Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(MainActivity.this,Book_profile.class);
                intent.putExtra("Name",Name);
                intent.putExtra("Author",Author);
                intent.putExtra("Description",Description);
                intent.putExtra("Availability",String.valueOf(Availability));
                intent.putExtra("ISBN",ISBN);
                intent.putExtra("copies",String.valueOf(copies));
                intent.putExtra("uid",uid);
//                intent.putExtra("uemail",uemail);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation((Activity)MainActivity.this).toBundle());
                }
                else{
                    startActivity(intent);
                }

    }
}
