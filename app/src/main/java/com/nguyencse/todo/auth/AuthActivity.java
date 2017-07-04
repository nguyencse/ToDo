package com.nguyencse.todo.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nguyencse.todo.custom.EditTextCSE;
import com.nguyencse.todo.custom.ProgressDialogCSE;
import com.nguyencse.todo.main.MainActivity;
import com.nguyencse.todo.R;
import com.nguyencse.todo.objects.User;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener {

    private EditTextCSE edtEmail, edtPass;
    private Button btnLogin, btnSignup;
    private ProgressDialogCSE progressDialogCSE;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    goToMain();
                }
            }
        };

        edtEmail = (EditTextCSE) findViewById(R.id.edt_auth_email);
        edtPass = (EditTextCSE) findViewById(R.id.edt_auth_password);

        btnLogin = (Button) findViewById(R.id.btn_login);
        btnSignup = (Button) findViewById(R.id.btn_signup);

        progressDialogCSE = new ProgressDialogCSE(this, R.style.CircleLoading);

        btnLogin.setOnClickListener(this);
        btnSignup.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        progressDialogCSE.show();
        switch (id) {
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_signup:
                signup();
                break;
        }
    }

    private boolean validateUserInfo() {
        return (!edtEmail.getText().toString().isEmpty() && !edtPass.getText().toString().isEmpty());
    }

    private void goToMain() {
        startActivity(new Intent(AuthActivity.this, MainActivity.class));
        finish();
    }

    private void login() {
        if (validateUserInfo()) {
            mAuth.signInWithEmailAndPassword(edtEmail.getText().toString(), edtPass.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialogCSE.dismiss();
                            if (!task.isSuccessful()) {
                                Toast.makeText(AuthActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AuthActivity.this, R.string.login_success, Toast.LENGTH_SHORT).show();
                                goToMain();
                            }
                        }
                    });
        } else {
            Toast.makeText(this, getString(R.string.info_must_not_be_empty), Toast.LENGTH_SHORT).show();
        }
    }

    private void signup() {
        if (validateUserInfo()) {
            mAuth.createUserWithEmailAndPassword(edtEmail.getText().toString(), edtPass.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialogCSE.dismiss();
                            if (!task.isSuccessful()) {
                                Toast.makeText(AuthActivity.this, R.string.signup_failed, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AuthActivity.this, R.string.signup_success, Toast.LENGTH_SHORT).show();
                                addUser();
                                goToMain();
                            }
                        }
                    });
        }
    }

    private void addUser() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        String usernameDefault = "user_" + currentUser.getUid();
        database.child("users").child(currentUser.getUid()).setValue(new User(currentUser.getUid(), usernameDefault, currentUser.getEmail(), ""));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
