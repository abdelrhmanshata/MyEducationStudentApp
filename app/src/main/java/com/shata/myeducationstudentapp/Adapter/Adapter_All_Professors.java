package com.shata.myeducationstudentapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shata.myeducationstudentapp.Model.ModelLecuter.ModelLecuter;
import com.shata.myeducationstudentapp.Model.Professor.ModelProfessor;
import com.shata.myeducationstudentapp.R;

import java.util.List;

public class Adapter_All_Professors extends RecyclerView.Adapter<Adapter_All_Professors.ProfessorsViewHolder> {

    private Context mContext;
    private List<ModelProfessor> professorsList;
    private OnItemClickListener mListener;

    public Adapter_All_Professors(Context mContext, List<ModelProfessor> professorsList) {
        this.mContext = mContext;
        this.professorsList = professorsList;
    }

    @NonNull
    @Override
    public ProfessorsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.all_professor_layout, parent, false);
        return new ProfessorsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfessorsViewHolder holder, int position) {

        ModelProfessor professor = professorsList.get(position);
        holder.Name.setText(professor.getProfessorName());
    }

    @Override
    public int getItemCount() {
        return professorsList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {

        mListener = listener;
    }

    public interface OnItemClickListener {
        void onItem_RecyclerView_Click(int position);
    }

    public class ProfessorsViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener
    {
        public TextView Name;
        public ProfessorsViewHolder(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.professorName);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItem_RecyclerView_Click(position);
                }
            }
        }
    }
}

