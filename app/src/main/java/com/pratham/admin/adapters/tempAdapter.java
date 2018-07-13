package com.pratham.admin.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pratham.admin.R;
import com.pratham.admin.modalclasses.TempStudent;

import java.util.List;

public class tempAdapter extends RecyclerView.Adapter<tempAdapter.ViewHolder>{
    private List<TempStudent> studentList  ;

    public tempAdapter( List<TempStudent> studentList) {
        this.studentList=studentList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_unuploaded, parent, false);
        return new tempAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String fname = studentList.get(holder.getAdapterPosition()).getFullName();
        holder.checkBox_student.setText(fname );
    }


    @Override
    public int getItemCount() {
        return studentList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        // @BindView(R.id.checkBox_student)
        TextView checkBox_student;

        public ViewHolder(View itemView) {
            super(itemView);
            // ButterKnife.bind(this,itemView);
            checkBox_student = itemView.findViewById(R.id.checkBox_student);
        }
    }
}
