package com.example.manas_library;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    private TextInputEditText emailf ;
    private TextInputEditText passwordf;
    private Button login;
    private String email,password;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;


    private void signIn(String email, String password) {
        Log.d("a", "signIn:" + email);
        if(TextUtils.isEmpty(email)){
            emailf.setError("Email is required");
            emailf.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailf.setError("Enter correcct email");
            emailf.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(password)){
            passwordf.setError("Password is required");
            passwordf.requestFocus();
            return;
        }
        progressDialog.setMessage("Logging you ...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("a", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent=new Intent(Login.this,MainActivity.class);
                            intent.putExtra("uid",user.getUid());
//                            intent.putExtra("uemail",user.getEmail());

                            startActivity(intent);
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("a", "signInWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed. Please try again!",
                                    Toast.LENGTH_SHORT).show();
                        }

                        progressDialog.hide();
                    }
                });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailf = (TextInputEditText)findViewById(R.id.emailfe);
        passwordf = (TextInputEditText)findViewById(R.id.passwordfe);
        login = (Button)findViewById(R.id.material_button);
        mAuth = FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = (emailf.getText()).toString();
                password = passwordf.getText().toString();

                signIn(email,password);


            }
        });

    }



}
