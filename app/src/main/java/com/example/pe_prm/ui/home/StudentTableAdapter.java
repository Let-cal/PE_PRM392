package com.example.pe_prm.ui.home;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pe_prm.R;

import java.util.List;

public class StudentTableAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private List<Student> students;
    private OnItemClickListener listener;
    private int lastPosition = -1;

    public interface OnItemClickListener {
        void onItemClick(Student student, View itemView);
    }

    public StudentTableAdapter(List<Student> students, OnItemClickListener listener) {
        this.students = students;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<Student> newStudents) {
        this.students = newStudents;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View headerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_table_header, parent, false);
            return new HeaderViewHolder(headerView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_row_item, parent, false);
            return new ItemViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            Student student = students.get(position - 1); // Adjust for header
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            itemHolder.mssvTextView.setText(student.getId());  // Changed from getMssv to getId
            itemHolder.nameTextView.setText(student.getName());
            itemHolder.majorTextView.setText(student.getIdMajor()); // Changed from getMajor to getIdMajor

            setAnimation(holder.itemView, position);
            itemHolder.itemView.setOnClickListener(v -> listener.onItemClick(student, v));
        }
    }


    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return students.size() + 1; // +1 for the header
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        HeaderViewHolder(View itemView) {
            super(itemView);
            // Initialize header views if needed
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView mssvTextView;
        TextView nameTextView;
        TextView majorTextView;

        ItemViewHolder(View itemView) {
            super(itemView);
            mssvTextView = itemView.findViewById(R.id.mssv);
            nameTextView = itemView.findViewById(R.id.name);
            majorTextView = itemView.findViewById(R.id.major);
        }
    }
}
