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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUp extends AppCompatActivity {
    EditText userName, userEmailId, userPassword, userConfirmPass;
    String userNameVal, userEmailIdVal, userPasswordVal, userConfirmPassVal;
    Button sync;
    TextView loginHere;
    TextInputLayout confirmPassLayout, passLayout, emailLayout;
    ProgressBar progressSignUp;
    FirebaseAuth fAuth;
    String APP_TAG= "appTag";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Toolbar toolbarLogin = findViewById(R.id.toolbarSignUp);
        setSupportActionBar(toolbarLogin);
        getSupportActionBar().setTitle(R.string.app_name);
        fAuth= FirebaseAuth.getInstance();
        if(MainActivity.isAccountActive) {
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
        emailLayout= findViewById(R.id.emailLayout);
        loginHere= findViewById(R.id.loginPage);
        progressSignUp= findViewById(R.id.progressSignUp);
        progressSignUp.setVisibility(View.GONE);

        if(fAuth.getCurrentUser()!=null && fAuth.getCurrentUser().isAnonymous())
            loginHere.setVisibility(View.INVISIBLE);
        else
            loginHere.setVisibility(View.VISIBLE);
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
                emailLayout.setError("");
                confirmPassLayout.setError("");
                if(!Login.isValidEmail(userEmailIdVal)) {
                    emailLayout.setError(getString(R.string.emailInvalidError));
                    return;
                }

                if(!userPasswordVal.equals(userConfirmPassVal)){
                    confirmPassLayout.setError(getText(R.string.passError));
                    return;
                }
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
            public void onFailure(@NonNull Exception exception) {
                progressSignUp.setVisibility(View.GONE);
                emailLayout.setError("");
                passLayout.setError("");
                onFailureAction(exception);
            }
        });
    }
    private void accountSuccess(final int successMsg){
        new UserProfileChangeRequest.Builder()
                .setDisplayName(userNameVal)
                .build();
        fAuth.updateCurrentUser(fAuth.getCurrentUser());
        MainActivity.isAccountActive= false;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), getString(successMsg), Toast.LENGTH_SHORT).show();
            }
        }, 1000);
        startActivity(new Intent(SignUp.this, Login.class));
    }
    private void onFailureAction(Exception exception){
        try{
            throw exception;
        }
        catch (FirebaseAuthUserCollisionException e2){
            emailLayout.setError(getString(R.string.emailDuplicateError));
        }
        catch (FirebaseAuthWeakPasswordException e3) {
            passLayout.setError(getText(R.string.passLengthError));
        }
        catch (Exception e){
            Log.d(APP_TAG, e.getMessage());
            Toast.makeText(getApplicationContext(), R.string.syncError, Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(getApplicationContext(), R.string.syncError, Toast.LENGTH_SHORT).show();
    }
    public void createNewAccount(){
        fAuth.createUserWithEmailAndPassword(userEmailIdVal, userPasswordVal)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        progressSignUp.setVisibility(View.GONE);
                        accountSuccess(R.string.newAccountSuccess);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                progressSignUp.setVisibility(View.GONE);
                emailLayout.setError("");
                passLayout.setError("");
                onFailureAction(exception);
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