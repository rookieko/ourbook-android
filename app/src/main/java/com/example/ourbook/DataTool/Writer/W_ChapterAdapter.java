package com.example.ourbook.DataTool.Writer;

import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.ourbook.Constants;
import com.example.ourbook.DataTool.Response.Chapter;
import com.example.ourbook.R;

import java.util.List;

public class W_ChapterAdapter extends RecyclerView.Adapter<W_ChapterAdapter.W_ChapterViewHolder> {
    private static final String TAG = "W_ChapterAdapter " + Constants.AddTAG;
    private List<Chapter> ChapterList;
    private OnItemClickListener listener;
    private Context context;
    public W_ChapterAdapter(List<Chapter> Chapterlist, OnItemClickListener listener) {
        this.ChapterList = Chapterlist;
        this.listener = listener;

    }

    @NonNull
    @Override
    public W_ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_w_chapter, parent, false);
        return new W_ChapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull W_ChapterViewHolder holder, int position) {
        Chapter chapter = ChapterList.get(position);
        holder.bind(chapter, listener,position);
//        holder.chapterNumView.setText(position);
    }

    @Override
    public int getItemCount() {
        return ChapterList.size();
    }

    public static class W_ChapterViewHolder extends RecyclerView.ViewHolder{
        private TextView bookTitleView;
        private TextView bookInfoView;
        private TextView chapterNumView;
        private ImageView bookImageView;
//        private int i = 1;

        //
        private View mView; // 이게 맞나..

        public W_ChapterViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mView=itemView;
            bookTitleView = mView.findViewById(R.id.chapter_item_title);
            bookInfoView = mView.findViewById(R.id.chapter_item_info);
            chapterNumView = mView.findViewById(R.id.chapter_item_number);
        }

        public void bind(Chapter chapter, OnItemClickListener listener,int position) {
            bookTitleView.setText(chapter.chapter_name);
            bookInfoView.setText(chapter.chapter_content);
            // TODO 미완 chapter . num 으로 설정 해야함
//            chapterNumView.setText(String.valueOf(chapter.num));
            if( chapter.num >= 0) {
                Log.d(TAG, " bind: 회차 번호 : "+ chapter.num);
                String numChapter = String.valueOf(chapter.num )+"화";
                chapterNumView.setText(numChapter);
                chapterNumView.setVisibility(View.VISIBLE);
            }else {
                chapterNumView.setVisibility(View.INVISIBLE);
            }
//            bookImageView.setImageDrawable(book.getPicture());
            itemView.setOnClickListener(v -> listener.onItemClick(chapter));
//            i++;
        }
    }
    public interface OnItemClickListener {
        void onItemClick(Chapter chapter);

    }
}

