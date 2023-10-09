package com.shata.myeducationstudentapp.UI.Exam;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shata.myeducationstudentapp.Adapter.Adapter_All_Exams_Name;
import com.shata.myeducationstudentapp.Model.ModelExams.ModelExamID;
import com.shata.myeducationstudentapp.Model.Professor.ModelProfessor;
import com.shata.myeducationstudentapp.R;
import com.shata.myeducationstudentapp.databinding.ActivityExamsBinding;

import java.util.ArrayList;
import java.util.List;

public class ExamsActivity extends AppCompatActivity implements Adapter_All_Exams_Name.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    ActivityExamsBinding examBinding;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabaseExamsRef = database.getReference("Exams");
    ValueEventListener mDBListener;
    List<ModelExamID> modelExamIDList;
    Adapter_All_Exams_Name adapterAllExamsName;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ModelProfessor professor;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        examBinding = ActivityExamsBinding.inflate(getLayoutInflater());
        setContentView(examBinding.getRoot());

        //
        toolbar = findViewById(R.id.toolBar);
        toolbar.setTitle(R.string.exam_page);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //
        professor = (ModelProfessor) getIntent().getSerializableExtra("ModelProfessor");
        Initialize_variables();
        loadingAllExams();
    }

    private void Initialize_variables() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        examBinding.recyclerViewExam.setHasFixedSize(true);
        examBinding.recyclerViewExam.setLayoutManager(new LinearLayoutManager(this));
        modelExamIDList = new ArrayList<>();
        adapterAllExamsName = new Adapter_All_Exams_Name(this, modelExamIDList);
        examBinding.recyclerViewExam.setAdapter(adapterAllExamsName);
        adapterAllExamsName.setOnItemClickListener(this);
    }

    public void loadingAllExams() {
        examBinding.progressCircular.setVisibility(View.VISIBLE);
        mDBListener = mDatabaseExamsRef
                .child(professor.getProfessorID())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        modelExamIDList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (snapshot != null) {
                                ModelExamID examID = new ModelExamID();
                                examID.setExamID(snapshot.child("examID").getValue(String.class));
                                examID.setExamName(snapshot.child("examName").getValue(String.class));
                                examID.setExamDate(snapshot.child("examDate").getValue(String.class));
                                examID.setExamDegree(snapshot.child("examDegree").getValue(Integer.class));
                                modelExamIDList.add(examID);
                            }
                        }
                        adapterAllExamsName.notifyDataSetChanged();
                        examBinding.progressCircular.setVisibility(View.GONE);
                        if (modelExamIDList.isEmpty())
                            examBinding.patientRecyclerviewImage.setVisibility(View.VISIBLE);
                        else
                            examBinding.patientRecyclerviewImage.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("ExamsActivity", error.getMessage());
                    }
                });
    }


    @Override
    public void onRefresh() {
        new Handler().postDelayed(() -> {
            mSwipeRefreshLayout.setRefreshing(false);
            loadingAllExams();
        }, 1000);
    }

    @Override
    public void onItem_RecyclerView_Click(int position) {
        ModelExamID examID = modelExamIDList.get(position);
        Intent intentViewExam = new Intent(this, QuestionsActivity.class);
        intentViewExam.putExtra("ModelExamID", examID);
        intentViewExam.putExtra("ModelProfessor", professor);
        startActivity(intentViewExam);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDatabaseExamsRef.removeEventListener(mDBListener);
        modelExamIDList.clear();
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
}