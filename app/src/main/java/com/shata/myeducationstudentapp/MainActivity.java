package com.shata.myeducationstudentapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.shata.myeducationstudentapp.Model.Professor.ModelProfessor;
import com.shata.myeducationstudentapp.UI.Auth.LoginActivity;
import com.shata.myeducationstudentapp.UI.Exam.ExamsActivity;
import com.shata.myeducationstudentapp.UI.HomeWork.AllHomeWorkActivity;
import com.shata.myeducationstudentapp.UI.Lecuter.AllLectuersActivity;
import com.shata.myeducationstudentapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    public static ModelProfessor professor;
    ActivityMainBinding mainBinding;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        //
        toolbar = findViewById(R.id.toolBar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        //

        professor = (ModelProfessor) getIntent().getSerializableExtra("ModelProfessor");

        mainBinding.examsActivity.setOnClickListener(v -> {
            Intent intentView = new Intent(this, ExamsActivity.class);
            intentView.putExtra("ModelProfessor", professor);
            startActivity(intentView);
        });
        mainBinding.homework.setOnClickListener(v -> {
            Intent intentView = new Intent(this, AllHomeWorkActivity.class);
            intentView.putExtra("ModelProfessor", professor);
            startActivity(intentView);
        });

        mainBinding.lectuers.setOnClickListener(v -> {
            Intent intentView = new Intent(this, AllLectuersActivity.class);
            intentView.putExtra("ModelProfessor", professor);
            startActivity(intentView);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.logout:
                auth.signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}