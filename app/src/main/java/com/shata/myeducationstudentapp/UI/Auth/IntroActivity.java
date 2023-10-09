package com.shata.myeducationstudentapp.UI.Auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.shata.myeducationstudentapp.MainActivity;
import com.shata.myeducationstudentapp.R;
import com.shata.myeducationstudentapp.UI.Professors_Activity;


public class IntroActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        firebaseAuth = FirebaseAuth.getInstance();
        Thread intro = new Thread(() -> {
            // Sleep UI 3 Seconds
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (firebaseAuth.getCurrentUser() != null) {
                //data is valid
                startActivity(new Intent(IntroActivity.this, Professors_Activity.class));
                finish();
            } else {
                startActivity(new Intent(IntroActivity.this, LoginActivity.class));
                finish();
            }
        });
        intro.start();

    }
}