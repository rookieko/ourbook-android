package com.example.ourbook.DataTool.Recycle.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourbook.Constants;
import com.example.ourbook.DataTool.Response.Chapter;
import com.example.ourbook.R;
import com.example.ourbook.ReadNovelActivity;

import java.util.List;

public class ChapterShowAdapter extends RecyclerView.Adapter<ChapterShowAdapter.ChapterViewHolder> {
    /* 독자 시점의 챕터 리싸이클러뷰 어댑터 */
    private Context context;
    private List<Chapter> chapterList;
//    private


    public ChapterShowAdapter( List<Chapter> chapterList, Context context) {
        this.context = context;
        this.chapterList = chapterList;
    }

    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chapter,parent,false);
        return new ChapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
        holder.onBind(chapterList.get(position));
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ReadNovelActivity.class);
            intent.putExtra("content",holder.mdata.chapter_content);
            // type 회차를 선택하는 경우
            intent.putExtra(Constants.INTENT_TYPE,Constants.Read.TYPE_SELECT_READ);
            intent.putExtra(Constants.INTENT_CHAPTER_ID,holder.mdata.id); //회차 id
            intent.putExtra(Constants.INTENT_CHAPTER_NUM,holder.mdata.num); //회차 번호
            intent.putExtra(Constants.INTENT_WID,holder.mdata.wid);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return chapterList.size();
    }

    public void putData(List<Chapter> addData) {

        if(addData.size() > 0) {
            chapterList.addAll(addData);
            this.notifyDataSetChanged();
        };
    }

    protected class ChapterViewHolder extends RecyclerView.ViewHolder {
        private View mview;
         TextView text_chapterNumber; // 회차 정보
         TextView text_chapterName; // 회차 제목
         ImageView imageView; // 회차 관련 정보 표시 , 이미 봤는지 , 결제 했는지 등
        Chapter mdata;

        public ChapterViewHolder(@NonNull View itemView) {
            super(itemView);
            mview = itemView;
            text_chapterName = mview.findViewById(R.id.item_textView_chapterName);
            text_chapterNumber = mview.findViewById(R.id.item_number_chapter);
            imageView = mview.findViewById(R.id.item_imageView_info_chapter);

        }
        public void onBind(Chapter data){
            mdata = data;
            String chapter_num_string = data.num + "화";
            text_chapterNumber.setText(chapter_num_string);
            text_chapterName.setText(data.chapter_name);
            /*TODO 임시로 회차의 내용 까지 가져온다 이후 chapter id 를 통해서 따로 호출하는 것을 변경!*/

        }
    }
}
