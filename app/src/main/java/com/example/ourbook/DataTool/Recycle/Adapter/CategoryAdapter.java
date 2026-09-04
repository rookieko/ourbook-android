package com.example.ourbook.DataTool.Recycle.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourbook.Constants;
import com.example.ourbook.DataTool.Recycle.Holder.CategoryViewHolder;
import com.example.ourbook.DataTool.Response.CategoryDTO;
import com.example.ourbook.R;
import com.example.ourbook.exploreActivity.ExploreActivity;

import java.util.ArrayList;
import java.util.List;
/*TODO 여기 까지 진행 하였음 2024.2.1 카테고리 item 클릭 이벤트 만들어야 함 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryViewHolder> {
    public static final String TAG = "Category Recyclerview CategoryAdapter "+ Constants.AddTAG;
    private Context context;
    private List<CategoryDTO> categoryDTOList = new ArrayList<CategoryDTO>();

    public CategoryAdapter(Context context ,List<CategoryDTO> categoryDTOList) {
        this.context = context;
        this.categoryDTOList = categoryDTOList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category,parent,false);
//        context = parent.getContext();

        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.onBind(categoryDTOList.get(position));
        holder.itemView.setOnClickListener(v -> {
            Log.d(TAG, "onBindViewHolder: "+ position);
            Log.d(TAG, "onClick: "+holder.data.name + position);
            Intent intent = new Intent(context, ExploreActivity.class);
            intent.putExtra(Constants.INTENT_CID,holder.data.id);
            intent.putExtra(Constants.INTENT_CATEGORY_NAME,holder.data.name);
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return categoryDTOList.size();
    }
}
