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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUp extends AppCompatActivity {
    EditText userName, userEmailId, userPassword, userConfirmPass;
    String userNameVal, userEmailIdVal, userPasswordVal, userConfirmPassVal;
    Button sync;
    TextView loginHere;
    TextInputLayout confirmPassLayout, passLayout;
    ProgressBar progressSignUp;
    FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Toolbar toolbarLogin = findViewById(R.id.toolbarSignUp);
        setSupportActionBar(toolbarLogin);
        getSupportActionBar().setTitle(R.string.app_name);
        fAuth= FirebaseAuth.getInstance();
        if(fAuth.getCurrentUser()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        userName= findViewById(R.id.userName);
        userEmailId= findViewById(R.id.userEmailId);
        userPassword= findViewById(R.id.password);
        userConfirmPass= findViewById(R.id.passwordConfirm);
        confirmPassLayout= findViewById(R.id.confirmPassLayout);
        passLayout= findViewById(R.id.passLayout);
        loginHere= findViewById(R.id.loginPage);
        progressSignUp= findViewById(R.id.progressSignUp);
        progressSignUp.setVisibility(View.GONE);

        loginHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });
        sync= findViewById(R.id.sync);
        sync.setEnabled(false);
        sync.setAlpha(0.2f);
        TextWatcher textWatcher= new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                userNameVal= userName.getText().toString();
                userEmailIdVal= userEmailId.getText().toString();
                userPasswordVal= userPassword.getText().toString();
                userConfirmPassVal= userConfirmPass.getText().toString();

                if(userNameVal.length()==0 && userEmailIdVal.length()==0
                        && userPasswordVal.length()==0 && userConfirmPassVal.length()==0){
                    sync.setEnabled(false);
                    sync.setAlpha(0.2f);
                }else {
                    boolean hasData = (userNameVal.length() > 0 && userEmailIdVal.length() > 0
                            && userPasswordVal.length() > 0 && userConfirmPassVal.length() > 0);
                    sync.setEnabled(hasData);
                    if (hasData)
                        sync.setAlpha(1);
                    else
                        sync.setAlpha(0.2f);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        userName.addTextChangedListener(textWatcher);
        userEmailId.addTextChangedListener(textWatcher);
        userPassword.addTextChangedListener(textWatcher);
        userConfirmPass.addTextChangedListener(textWatcher);

        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userNameVal= userName.getText().toString().trim();
                userEmailIdVal= userEmailId.getText().toString().trim();
                userPasswordVal= userPassword.getText().toString().trim();
                userConfirmPassVal= userConfirmPass.getText().toString().trim();

                if(userPasswordVal.length()<6){
                    passLayout.setError(getText(R.string.passLengthError));
                    return;
                }
                if(!userPasswordVal.equals(userConfirmPassVal)){
                    confirmPassLayout.setError(getText(R.string.passError));
                    return;
                }
                confirmPassLayout.setError("");
                progressSignUp.setVisibility(View.VISIBLE);

                if(fAuth.getCurrentUser()==null){
                    createNewAccount();
                }else{
                    linkAnonymousAccount();
                }
            }
        });
    }

    private void linkAnonymousAccount() {
        AuthCredential userCredential= EmailAuthProvider.getCredential(userEmailIdVal, userPasswordVal);
        fAuth.getCurrentUser()
                .linkWithCredential(userCredential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        accountSuccess(R.string.syncSuccess);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressSignUp.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), R.string.syncError, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void accountSuccess(final int successMsg){
        new UserProfileChangeRequest.Builder()
                .setDisplayName(userNameVal)
                .build();
        progressSignUp.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), getText(successMsg), Toast.LENGTH_SHORT).show();
            }
        }, 1000);
        startActivity(new Intent(SignUp.this, Login.class));
    }
    public void createNewAccount(){
        fAuth.createUserWithEmailAndPassword(userEmailIdVal, userPasswordVal)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        accountSuccess(R.string.newAccountSuccess);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressSignUp.setVisibility(View.GONE);
                //Toast.makeText(getApplicationContext(), R.string.syncError, Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), e+"", Toast.LENGTH_LONG).show();
            }
        });
    }
   public void goBackToMain(){
       startActivity(new Intent(SignUp.this, MainActivity.class));
   }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            goBackToMain();
        return super.onOptionsItemSelected(item);
    }
}