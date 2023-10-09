package com.shata.myeducationstudentapp.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shata.myeducationstudentapp.Adapter.Adapter_All_Lecuters;
import com.shata.myeducationstudentapp.Adapter.Adapter_All_Professors;
import com.shata.myeducationstudentapp.MainActivity;
import com.shata.myeducationstudentapp.Model.ModelLecuter.ModelLecuter;
import com.shata.myeducationstudentapp.Model.Professor.ModelProfessor;
import com.shata.myeducationstudentapp.R;
import com.shata.myeducationstudentapp.UI.Lecuter.LectuerActivity;
import com.shata.myeducationstudentapp.databinding.ActivityAllLectuersBinding;
import com.shata.myeducationstudentapp.databinding.ActivityProfessorsBinding;

import java.util.ArrayList;
import java.util.List;

public class Professors_Activity extends AppCompatActivity implements Adapter_All_Professors.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    ActivityProfessorsBinding professorsBinding;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabaseProfessorsRef = database.getReference("Professor");
    ValueEventListener mDBListener;
    List<ModelProfessor> professorsList;
    Adapter_All_Professors adapterAllProfessors;
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        professorsBinding = ActivityProfessorsBinding.inflate(getLayoutInflater());
        setContentView(professorsBinding.getRoot());

        Initialize_variables();
        loadingAllProfessors();
    }
    private void Initialize_variables() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        professorsBinding.recyclerViewProfessors.setHasFixedSize(true);
        professorsBinding.recyclerViewProfessors.setLayoutManager(new LinearLayoutManager(this));
        professorsList = new ArrayList<>();
        adapterAllProfessors = new Adapter_All_Professors(this, professorsList);
        professorsBinding.recyclerViewProfessors.setAdapter(adapterAllProfessors);
        adapterAllProfessors.setOnItemClickListener(this);
    }

    public void loadingAllProfessors() {
        professorsBinding.progressCircular.setVisibility(View.VISIBLE);
        mDBListener = mDatabaseProfessorsRef
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        professorsList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (snapshot != null) {
                                ModelProfessor professor = snapshot.getValue(ModelProfessor.class);
                                professorsList.add(professor);
                            }
                        }
                        adapterAllProfessors.notifyDataSetChanged();
                        professorsBinding.progressCircular.setVisibility(View.GONE);
                        if (professorsList.isEmpty())
                            professorsBinding.patientRecyclerviewImage.setVisibility(View.VISIBLE);
                        else
                            professorsBinding.patientRecyclerviewImage.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("ProfessorActivity", error.getMessage());
                    }
                });
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(() -> {
            mSwipeRefreshLayout.setRefreshing(false);
            loadingAllProfessors();
        }, 1000);
    }

    @Override
    public void onItem_RecyclerView_Click(int position) {
         ModelProfessor professor = professorsList.get(position);
        Intent intentViewExam = new Intent(this, MainActivity.class);
        intentViewExam.putExtra("ModelProfessor", professor);
        startActivity(intentViewExam);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDatabaseProfessorsRef.removeEventListener(mDBListener);
        professorsList.clear();
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