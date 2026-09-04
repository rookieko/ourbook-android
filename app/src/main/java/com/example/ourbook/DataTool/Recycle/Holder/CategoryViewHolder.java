package com.example.ourbook.DataTool.Recycle.Holder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourbook.DataTool.Response.CategoryDTO;
import com.example.ourbook.R;

public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    /*TODO holder 가 몇개 생기는지 확인 */
    private View mview;
    private TextView categoryNameView;
    public CategoryDTO data;

    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);
        mview = itemView;
        categoryNameView = mview.findViewById(R.id.item_textView_categoryName);

    }
    public void onBind(CategoryDTO data){
        categoryNameView.setText(String.valueOf(data.name));
        this.data = data;
    }


    @Override
    public void onClick(View v) {

    }
}
