package com.example.mynotes.Auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

public class Login extends AppCompatActivity {
    Button createTempAccount;
    TextView signUpHere;
    EditText userEmail, userPassword;
    Button syncL;
    FirebaseAuth fAuth;
    String anonymUserId;
    FirebaseFirestore fStore;
    ProgressBar progressLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbarLogin = findViewById(R.id.toolbarLogin);
        setSupportActionBar(toolbarLogin);
        getSupportActionBar().setTitle(R.string.app_name);

        fAuth= FirebaseAuth.getInstance();
        if(fAuth.getCurrentUser()!=null)
            anonymUserId= fAuth.getCurrentUser().getUid();
        createTempAccount= findViewById(R.id.createTempAccount);
        userEmail= findViewById(R.id.userEmail);
        userPassword= findViewById(R.id.userPassword);
        progressLogin= findViewById(R.id.progressLogin);
        progressLogin.setVisibility(View.GONE);
        syncL= findViewById(R.id.syncL);
        syncL.setEnabled(false);
        syncL.setAlpha(0.2f);
        signUpHere= findViewById(R.id.signUpPage);

        createTempAccount.setVisibility(View.GONE);
        if(fAuth.getCurrentUser()!=null) {
            createTempAccount.setVisibility(View.GONE);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        else {
            createTempAccount.setVisibility(View.VISIBLE);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
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
        TextWatcher textWatcher= new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String userEmailIdVal= userEmail.getText().toString();
                String userPasswordVal= userPassword.getText().toString();

                if(userEmailIdVal.length()==0 && userPasswordVal.length()==0){
                    syncL.setEnabled(false);
                    syncL.setAlpha(0.2f);
                }else {
                    boolean hasData = (userEmailIdVal.length() > 0 && userPasswordVal.length() > 0);
                    syncL.setEnabled(hasData);
                    if (hasData)
                        syncL.setAlpha(1);
                    else
                        syncL.setAlpha(0.2f);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        userEmail.addTextChangedListener(textWatcher);
        userPassword.addTextChangedListener(textWatcher);
        syncL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressLogin.setVisibility(View.VISIBLE);
                final String curEmail= userEmail.getText().toString().trim();
                final String curPassword= userPassword.getText().toString().trim();
                if (MainActivity.syncWithExistingAcc) {
                    // Sync with existing Account Code.
                    Toast.makeText(getApplicationContext(), "Syncing", Toast.LENGTH_SHORT).show();
                } else {
                    fAuth.signInWithEmailAndPassword(curEmail, curPassword)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    progressLogin.setVisibility(View.GONE);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), R.string.syncSuccess, Toast.LENGTH_SHORT).show();
                                        }
                                    }, 1000);
                                    goBackToMain();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressLogin.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), R.string.syncError, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
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
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            goBackToMain();
        return super.onOptionsItemSelected(item);
    }
}