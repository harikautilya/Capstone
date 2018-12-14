package com.example.kautilya.application.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.example.kautilya.application.base.BaseActivity;
import com.example.kautilya.application.R;
import com.example.kautilya.application.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends BaseActivity<ActivityLoginBinding> {

    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        firebaseAuth = FirebaseAuth.getInstance();


    }


    public void loginUser(View v) {
        String username = getViewBinding().username.getText().toString();
        String password = getViewBinding().password.getText().toString();

        if (username.equals("")) {
            showToast(getString(R.string.enter_username));
            return;
        }
        if (password.equals("")) {
            showToast(getString(R.string.enter_password));
            return;
        }

        progressDialog = ProgressDialog.show(this, getString(R.string.logging_in), getString(R.string.logging_in_message), true);
        firebaseAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loginSuccess();
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            loginFailure(getString(R.string.auth_failed));
                        }

                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "signInWithEmail:failure", e);
                        loginFailure(getString(R.string.auth_failed));

                    }
                });

    }

    public void registerUser(View v) {
        String username = getViewBinding().username.getText().toString();
        String password = getViewBinding().password.getText().toString();

        if (username.equals("")) {
            showToast(getString(R.string.enter_username));
            return;
        }
        if (password.equals("")) {
            showToast(getString(R.string.enter_password));
            return;
        }
        progressDialog = ProgressDialog.show(this, getString(R.string.logging_in), getString(R.string.create_user), true);
        firebaseAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loginSuccess();
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            loginFailure(getString(R.string.auth_failed));
                        }

                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "signInWithEmail:failure", e);
                        loginFailure(getString(R.string.auth_failed));

                    }
                });

    }

    void loginFailure(String message) {
        progressDialog.dismiss();
        showToast(message);

    }

    void loginSuccess() {
        progressDialog.dismiss();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }
}
