package com.shata.myeducationstudentapp.UI.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.shata.myeducationstudentapp.MainActivity;
import com.shata.myeducationstudentapp.R;
import com.shata.myeducationstudentapp.databinding.ActivityLoginBinding;

import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity {


    ActivityLoginBinding loginBinding;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(loginBinding.getRoot());

        loginBinding.btnForgetPassword.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
            finish();
        });

        loginBinding.btnRegisterNow.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });

        loginBinding.btnSignin.setOnClickListener(v -> {

            loginBinding.progressCircular.setVisibility(View.VISIBLE);


            String emailAdd = loginBinding.email.getText().toString();
            if (emailAdd.isEmpty()) {
                loginBinding.email.setError("" + getResources().getString(R.string.email_required));
                loginBinding.email.setFocusable(true);
                loginBinding.email.requestFocus();
                loginBinding.progressCircular.setVisibility(View.GONE);
                return;
            }

            String passwords = loginBinding.password.getText().toString();
            if (passwords.isEmpty()) {
                loginBinding.password.setError("" + getResources().getString(R.string.password_required));
                loginBinding.password.setFocusable(true);
                loginBinding.password.requestFocus();
                loginBinding.progressCircular.setVisibility(View.GONE);
                return;
            }
            mAuth.signInWithEmailAndPassword(emailAdd, passwords).addOnCompleteListener(LoginActivity.this, task -> {
                if (task.isSuccessful()) {
                    Toasty.success(LoginActivity.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    loginBinding.progressCircular.setVisibility(View.GONE);
                    finish();
                } else {
                    Toasty.error(LoginActivity.this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
                }
            });

        });

    }
}