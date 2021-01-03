package com.example.mynotes.Auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mynotes.MainActivity;
import com.example.mynotes.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    Button createTempAccount;
    TextView signUpHere;
    FirebaseAuth fAuth;
    ProgressBar progressLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbarLogin = findViewById(R.id.toolbarLogin);
        setSupportActionBar(toolbarLogin);
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fAuth= FirebaseAuth.getInstance();
        createTempAccount= findViewById(R.id.createTempAccount);
        progressLogin= findViewById(R.id.progressLogin);
        progressLogin.setVisibility(View.GONE);
        signUpHere= findViewById(R.id.signUpPage);

        createTempAccount.setVisibility(View.GONE);
        if(fAuth.getCurrentUser()!=null)
            createTempAccount.setVisibility(View.GONE);
        else
            createTempAccount.setVisibility(View.VISIBLE);
        createTempAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fAuth.signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        goBackToMain();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        signUpHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SignUp.class));
                finish();
            }
        });
    }
    public void goBackToMain(){
        startActivity(new Intent(Login.this, MainActivity.class));
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            goBackToMain();
        return super.onOptionsItemSelected(item);
    }
}