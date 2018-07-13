package com.pratham.admin.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.pratham.admin.R;
import com.pratham.admin.interfaces.OnCheckBoxSelectedItem;
import com.pratham.admin.modalclasses.Student;

import java.util.List;

public class RightSideAdapter extends RecyclerView.Adapter<RightSideAdapter.LeftViewHolder> {
    private List<Student> studentList  ;
    private OnCheckBoxSelectedItem onCheckBoxSelectedItem;

    public RightSideAdapter(OnCheckBoxSelectedItem onCheckBoxSelectedItem, List<Student> studentList) {
        this.studentList=studentList;
        this.onCheckBoxSelectedItem =onCheckBoxSelectedItem;
    }

    @NonNull
    @Override
    public LeftViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new LeftViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final LeftViewHolder holder, int position) {
        //Student student= (Student) studentList.get(position);
        String fname = studentList.get(holder.getAdapterPosition()).getFullName();

        holder.checkBox_student.setText(fname );
        holder.checkBox_student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckBoxSelectedItem.setSelectedRightSide(holder.getAdapterPosition(), studentList.get(holder.getAdapterPosition()));
            }
        });
        if (studentList.get(holder.getAdapterPosition()).isChecked()) {
            holder.checkBox_student.setChecked(true);
        } else {
            holder.checkBox_student.setChecked(false);
        }

    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    class LeftViewHolder extends RecyclerView.ViewHolder {
        // @BindView(R.id.checkBox_student)
        CheckBox checkBox_student;

        public LeftViewHolder(View itemView) {
            super(itemView);
            // ButterKnife.bind(this,itemView);
            checkBox_student = itemView.findViewById(R.id.checkBox_student);
        }
    }
}
