package com.shata.myeducationstudentapp.UI.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shata.myeducationstudentapp.MainActivity;
import com.shata.myeducationstudentapp.Model.Student.ModelStudent;
import com.shata.myeducationstudentapp.R;
import com.shata.myeducationstudentapp.databinding.ActivityRegisterBinding;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class RegisterActivity extends AppCompatActivity {

    ActivityRegisterBinding registerBinding;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference("Students");

    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBinding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(registerBinding.getRoot());

        registerBinding.btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });

        registerBinding.btnSignup.setOnClickListener(v -> {

            registerBinding.pbCircular.setVisibility(View.VISIBLE);

            String fullName = registerBinding.fullName.getText().toString();
            if (fullName.isEmpty()) {
                registerBinding.fullName.setError("" + getResources().getString(R.string.full_name_required));
                registerBinding.fullName.setFocusable(true);
                registerBinding.fullName.requestFocus();
                registerBinding.pbCircular.setVisibility(View.GONE);
                return;
            }

            String emailAdd = registerBinding.email.getText().toString();
            if (emailAdd.isEmpty()) {
                registerBinding.email.setError("" + getResources().getString(R.string.email_required));
                registerBinding.email.setFocusable(true);
                registerBinding.email.requestFocus();
                registerBinding.pbCircular.setVisibility(View.GONE);

                return;
            }

            String passwords = registerBinding.password.getText().toString();

            if (passwords.isEmpty()) {
                registerBinding.password.setError("" + getResources().getString(R.string.password_required));
                registerBinding.password.setFocusable(true);
                registerBinding.password.requestFocus();
                registerBinding.pbCircular.setVisibility(View.GONE);

                return;
            } else if (registerBinding.password.getText().toString().length() < 8) {
                registerBinding.password.setError("" + getResources().getString(R.string.short_pasword));
                registerBinding.password.setFocusable(true);
                registerBinding.password.requestFocus();
                registerBinding.pbCircular.setVisibility(View.GONE);

                return;
            }

            ModelStudent student = new ModelStudent();
            student.setStudentName(fullName);
            student.setStudentEmail(emailAdd);
            student.setStudentExams(new ArrayList<>());
            student.setStudentHomeWorks(new ArrayList<>());
            student.setStudentTasksID(new ArrayList<>());

            auth.createUserWithEmailAndPassword(emailAdd, passwords).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String studentID = auth.getUid();
                    student.setStudentID(studentID);
                    databaseReference
                            .child(studentID)
                            .setValue(student)
                            .addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Toasty.success(RegisterActivity.this, "" + getString(R.string.register_successfully), Toast.LENGTH_SHORT).show();
                                    registerBinding.pbCircular.setVisibility(View.GONE);

                                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                    finish();
                                }
                            });
                } else {
                    Toasty.error(RegisterActivity.this, "" + getString(R.string.register_failed), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}