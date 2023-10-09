package com.shata.myeducationstudentapp.UI.Lecuter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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
import com.shata.myeducationstudentapp.Adapter.Adapter_All_Lecuters;
import com.shata.myeducationstudentapp.Model.ModelLecuter.ModelLecuter;
import com.shata.myeducationstudentapp.Model.Professor.ModelProfessor;
import com.shata.myeducationstudentapp.R;
import com.shata.myeducationstudentapp.databinding.ActivityAllLectuersBinding;

import java.util.ArrayList;
import java.util.List;

public class AllLectuersActivity extends AppCompatActivity implements Adapter_All_Lecuters.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {


    ActivityAllLectuersBinding allLecuterBinding;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabaseLectuerRef = database.getReference("Lectuers");
    ValueEventListener mDBListener;
    List<ModelLecuter> lecutersList;
    Adapter_All_Lecuters adapterAllLecuters;
    SwipeRefreshLayout mSwipeRefreshLayout;

    private Toolbar toolbar;

    ModelProfessor professor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allLecuterBinding = ActivityAllLectuersBinding.inflate(getLayoutInflater());
        setContentView(allLecuterBinding.getRoot());

        //
        toolbar = findViewById(R.id.toolBar);
        toolbar.setTitle(getString(R.string.lec_page));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //

        professor = (ModelProfessor) getIntent().getSerializableExtra("ModelProfessor");


        Initialize_variables();
        loadingAllLecuters();
    }

    private void Initialize_variables() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        allLecuterBinding.recyclerViewExam.setHasFixedSize(true);
        allLecuterBinding.recyclerViewExam.setLayoutManager(new LinearLayoutManager(this));
        lecutersList = new ArrayList<>();
        adapterAllLecuters = new Adapter_All_Lecuters(this, lecutersList);
        allLecuterBinding.recyclerViewExam.setAdapter(adapterAllLecuters);
        adapterAllLecuters.setOnItemClickListener(this);
    }

    public void loadingAllLecuters() {
        allLecuterBinding.progressCircular.setVisibility(View.VISIBLE);
        mDBListener = mDatabaseLectuerRef
                .child(professor.getProfessorID())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        lecutersList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (snapshot != null) {
                                ModelLecuter lecuter = snapshot.getValue(ModelLecuter.class);
                                lecutersList.add(lecuter);
                            }
                        }
                        adapterAllLecuters.notifyDataSetChanged();
                        allLecuterBinding.progressCircular.setVisibility(View.GONE);
                        if (lecutersList.isEmpty())
                            allLecuterBinding.patientRecyclerviewImage.setVisibility(View.VISIBLE);
                        else
                            allLecuterBinding.patientRecyclerviewImage.setVisibility(View.GONE);
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
            loadingAllLecuters();
        }, 1000);
    }

    @Override
    public void onItem_RecyclerView_Click(int position) {
        ModelLecuter lecuter = lecutersList.get(position);
        Intent intentViewExam = new Intent(this, LectuerActivity.class);
        intentViewExam.putExtra("ModelLecuter", lecuter);
        startActivity(intentViewExam);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDatabaseLectuerRef.removeEventListener(mDBListener);
        lecutersList.clear();
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