package com.example.ourbook.DataTool.Recycle.Adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MainBookAdapter extends RecyclerView.Adapter<MainBookAdapter.MainBookHolder> {
    @NonNull
    @Override
    public MainBookAdapter.MainBookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MainBookAdapter.MainBookHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    protected class MainBookHolder extends RecyclerView.ViewHolder {
        public MainBookHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
