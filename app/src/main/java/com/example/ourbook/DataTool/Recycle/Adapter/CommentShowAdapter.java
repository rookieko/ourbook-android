package com.example.ourbook.DataTool.Recycle.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ourbook.Constants;
import com.example.ourbook.DataTool.BaseUrl;
import com.example.ourbook.DataTool.Response.CommentDTO;
import com.example.ourbook.R;
import com.example.ourbook.ReviewInfoActivity;
import com.example.ourbook.databinding.ItemCommentBinding;

import java.util.List;

public class CommentShowAdapter extends RecyclerView.Adapter<CommentShowAdapter.CommentViewHolder> {
    private final String TAG = "CommentShowAdapter" + Constants.AddTAG;
    private Context context;
    public OnItemClickListener likeListener;
    public OnItemClickListener commentListener;
    private List<CommentDTO> commentDTOList;
    private Intent infoIntent;

    public CommentShowAdapter( List<CommentDTO> commentDTOList , OnItemClickListener likeonItemClickListener ,Context context ) {
        this.likeListener = likeonItemClickListener;
//        this.commentListener= commentItemClickListener;
        this.commentDTOList = commentDTOList;
        this.context = context; // intent 객체 생성을 위해 생성자에서 받도록 수정함 그전 viewGroup 애서 받음
        infoIntent = new Intent(context, ReviewInfoActivity.class);
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment,parent,false);

//        context = parent.getContext();


        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.bind(commentDTOList.get(position),/*likeListener,*/position); // 이미 생성자에서 받았음
        holder.commentItem_contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                infoIntent = new Intent(context, ReviewInfoActivity.class);
                infoIntent.putExtra(Constants.INTENT_REVIEW_ID,holder.mcomment.id);
                context.startActivity(infoIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentDTOList.size();
    }
    public void refreshData(){

    }
    public void putData(List<CommentDTO> addData){
        /*페이징 처리 , 데이터 추가 이후 초기화*/
        if(addData.size() > 0) {
            commentDTOList.addAll(addData);
            this.notifyDataSetChanged();
        };
    }
    public class CommentViewHolder extends RecyclerView.ViewHolder{
        private View mview;
        private ItemCommentBinding binding;
         TextView commentItem_contentView;
         TextView commentItem_writerName;
         TextView commentItem_ratingScore;
         ImageView writer_profile;
         TextView commentItem_date;
         ImageView commentItem_likeImage;
         TextView commentItem_likeScore;
         CommentDTO mcomment;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            mview =itemView;
            commentItem_ratingScore = mview.findViewById(R.id.commentItem_number_startRating);
            commentItem_contentView = mview.findViewById(R.id.commetnItem_textView_content);
            commentItem_writerName = mview.findViewById(R.id.commentItem_comment_username);
            writer_profile = mview.findViewById(R.id.commentItem_imageView_profile);
            commentItem_date = mview.findViewById(R.id.commentItem_date);
            commentItem_likeImage = mview.findViewById(R.id.commentItem_imageView_like);
            commentItem_likeScore = mview.findViewById(R.id.commentItem_textView_likeScore);
        }
        public void bind(CommentDTO commentDTO , /*OnItemClickListener listener ,*/int position){
            mcomment = commentDTO;
            if(commentDTO.profile_image != null) {
                Log.d(TAG, "bind: "+ commentDTO.profile_image +" url : "+ BaseUrl.BookCoverImage_URL+ commentDTO.profile_image);
                Glide.with(context).load(BaseUrl.ProfileImage_URL+ commentDTO.profile_image).circleCrop().into(writer_profile);
            }else {
                Glide.with(context).load(R.drawable.outline_account_circle_24).into(writer_profile);
            }
            String score = commentDTO.score.substring(0,3);
//            Log.d(TAG, "bind: commentDTO.score " + score);
            // 리뷰 평가 점수
            commentItem_ratingScore.setText(score);
            // 리뷰 내용
            commentItem_contentView.setText(commentDTO.comment_content);
            // 리뷰 작성자 닉네임
            commentItem_writerName.setText(commentDTO.user_name);
            // 리뷰 좋아요 점수
            commentItem_likeScore.setText(commentDTO.like_score);
            //리뷰 좋아요 이미지
            if(commentDTO.is_like/*Integer.parseInt(commentDTO.is_like)*/ == 1){
                // 사용자의 리뷰에 대한 좋아요 여부 표시
                // 좋아요 선택 정보가 존재하는 경우 빨간색 하트 , 아닐시 빈 하트 이미지
                Glide.with(mview.getContext())
                        .load(R.drawable.heart_red_icon)
                        .into(commentItem_likeImage);
            }else {
                Glide.with(mview.getContext())
                        .load(R.drawable.heart_empty_icon)
                        .into(commentItem_likeImage);
            }
            if(likeListener != null){
            commentItem_likeImage.setOnClickListener(v -> {
                /* 클릭 리스너 */
                likeListener.onItemClick(commentDTO);
            });
            }
            String date = commentDTO.createDate.substring(0,10);
            commentItem_date.setText(date);

        }
    }

    public interface OnItemClickListener {
        void onItemClick(CommentDTO commentDTO);
    }

}
