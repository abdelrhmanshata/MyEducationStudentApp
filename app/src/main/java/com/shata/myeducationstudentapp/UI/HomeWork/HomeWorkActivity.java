package com.shata.myeducationstudentapp.UI.HomeWork;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shata.myeducationstudentapp.Model.ModelHomeWork.ModelHomeWork;
import com.shata.myeducationstudentapp.Model.Professor.ModelProfessor;
import com.shata.myeducationstudentapp.Model.Student.ModelStudent;
import com.shata.myeducationstudentapp.Model.Student.ModelStudentHomeWork;
import com.shata.myeducationstudentapp.R;
import com.shata.myeducationstudentapp.databinding.ActivityHomeWorkBinding;

import es.dmoral.toasty.Toasty;

public class HomeWorkActivity extends AppCompatActivity {

    ActivityHomeWorkBinding homeWorkBinding;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference = firebaseStorage.getReference("StudentHomeWork");
    DatabaseReference mDatabaseStudentRef = database.getReference("Students");
    ModelHomeWork homeWork;
    ModelStudent student;
    public ModelProfessor professor;
    private Uri dataUriPdf;
    private Toolbar toolbar;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeWorkBinding = ActivityHomeWorkBinding.inflate(getLayoutInflater());
        setContentView(homeWorkBinding.getRoot());

        //
        toolbar = findViewById(R.id.toolBar);
        toolbar.setTitle(getString(R.string.assignment_page));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //

        professor = (ModelProfessor) getIntent().getSerializableExtra("ModelProfessor");

        homeWork = (ModelHomeWork) getIntent().getSerializableExtra("ModelHomeWork");
        getStudentData();

        homeWorkBinding.homeworkName.setText(homeWork.getHW_Name() + "");
        homeWorkBinding.hkStartDate.setText(homeWork.getHW_StartDate() + "");
        homeWorkBinding.hkEndDate.setText(homeWork.getHW_EndDate() + "");
        homeWorkBinding.homeWorkPdfDownLoad.setText(homeWork.getHW_PDF_URL() + "");

        homeWorkBinding.selectFile.setOnClickListener(v -> {
            selectPdf();
        });

        homeWorkBinding.downLoadPdf.setOnClickListener(v -> {
            downloadPdfHomeWork(homeWork);
        });

        homeWorkBinding.saveHomeWork.setOnClickListener(v -> {

            homeWork.setHW_Name(homeWorkBinding.homeworkName.getText().toString().trim());
            homeWork.setHW_StartDate(homeWorkBinding.hkStartDate.getText().toString().trim());
            homeWork.setHW_EndDate(homeWorkBinding.hkEndDate.getText().toString().trim());
            uploadPDFFile(getHomeWorkUriUPdf(), homeWork);
        });
    }

    public void getStudentData() {
        mDatabaseStudentRef
                .child(auth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ModelStudent modelStudent = snapshot.getValue(ModelStudent.class);
                        if (modelStudent != null)
                            student = modelStudent;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    @SuppressLint("IntentReset")
    private void downloadPdfHomeWork(ModelHomeWork work) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setType("application/pdf");
        intent.setData(Uri.parse(work.getHW_PDF_URL()));
        startActivity(intent);
    }

    private void selectPdf() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Pdf File Select"), 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            homeWorkBinding.hwPdfName.setText(data.getDataString());
            setHomeWorkUrlPdf(data.getData());
        }
    }

    private void uploadPDFFile(Uri dataUri, ModelHomeWork homeWork) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle(getString(R.string.file_loading));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        StorageReference reference = storageReference
                .child(professor.getProfessorID())
                .child(student.getStudentID())
                .child(homeWork.getHW_ID())
                .child("HomeWork.pdf");

        reference
                .putFile(dataUri)
                .addOnSuccessListener(taskSnapshot -> {
                    {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete()) ;
                        Uri uri = uriTask.getResult();

                        ModelStudentHomeWork studentHomeWork = new ModelStudentHomeWork();
                        studentHomeWork.setHomeworkID(homeWork.getHW_ID());
                        studentHomeWork.setHomeworkName(homeWork.getHW_Name());
                        studentHomeWork.setProfessorID(professor.getProfessorID());
                        studentHomeWork.setHomeworkPDFLink(uri.toString());

                        if (!student.taskIDIsFound(homeWork.getHW_ID())) {
                            student.addStudentHomeWork(studentHomeWork);
                            student.addStudentTasksID(homeWork.getHW_ID());
                            mDatabaseStudentRef
                                    .child(student.getStudentID())
                                    .setValue(student)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            showDialogHomeWork(getString(R.string.you_have_upload_homework));
                                        }
                                    });
                        } else {
                            Toasty.warning(this, "" + getString(R.string.you_have_upload_homework_before), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            onBackPressed();
                        }
                    }
                }).addOnProgressListener(snapshot -> {
            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
            dialog.setMessage(getString(R.string.file_upload) + (int) progress + " % ");
        });
    }

    private void showDialogHomeWork(String msg) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.homework_upload_layout, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        //
        TextView examDegree = dialogView.findViewById(R.id.textMsg);
        examDegree.setText(msg);
        Button ok = dialogView.findViewById(R.id.btnOK);

        ok.setOnClickListener(v -> {
            alertDialog.dismiss();
            finish();
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public Uri getHomeWorkUriUPdf() {
        return dataUriPdf;
    }

    public void setHomeWorkUrlPdf(Uri lecUrlPdf) {
        this.dataUriPdf = lecUrlPdf;
    }
}