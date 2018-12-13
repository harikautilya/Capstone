package com.example.kautilya.application;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.kautilya.application.databinding.ActivityLoginBinding;

public class LoginActivity extends BaseActivity<ActivityLoginBinding> {

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getViewBinding().login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String username = getViewBinding().username.getText().toString();
                String password = getViewBinding().password.getText().toString();

            }
        });


    }


    void loginUser(String username, String password) {
        progressDialog = ProgressDialog.show(this, "Logging in ...", "Please wait while we login", true);

    }

    void loginFailure(String message) {
        progressDialog.dismiss();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

    }

    void loginSuccess() {
        progressDialog.dismiss();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }
}
