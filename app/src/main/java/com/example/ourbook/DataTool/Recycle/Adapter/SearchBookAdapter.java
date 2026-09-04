package com.example.ourbook.DataTool.Recycle.Adapter;

import android.app.Activity;
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
import com.example.ourbook.Chat.ChatRoomCreateActivity;
import com.example.ourbook.Constants;
import com.example.ourbook.DataTool.BaseUrl;
import com.example.ourbook.DataTool.Book;
import com.example.ourbook.DataTool.Response.DTO.SearchItemDTO;
import com.example.ourbook.R;
import com.example.ourbook.SearchNovelActivity;
import com.example.ourbook.exploreActivity.BookInfoActivity;

import java.util.List;

public class SearchBookAdapter extends RecyclerView.Adapter<SearchBookAdapter.BookViewHoler>{
    private static final String TAG = "SearchBookAdapter "+ Constants.AddTAG;
    private int option ; // 옵션 0 = main 화면에서의 검색  1 = 채팅방 생성시 태그 사용을 위해 사용 되는 RecyclerView adpater
    private Activity activity;
    private List<SearchItemDTO> data;
    private Context context;

    public SearchBookAdapter(List<SearchItemDTO> searchItemDTOList, Context context , int option , Activity mactivity) {
        this.data = searchItemDTOList;
        this.context = context;
        this.option = option;
        this.activity = mactivity;
    }

    @NonNull
    @Override
    public SearchBookAdapter.BookViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_explore_book,parent,false);

        return new BookViewHoler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchBookAdapter.BookViewHoler holder, int position) {
        holder.onBind(data.get(position)); // 먼저 book data 를 초기화 해준다 아래 북 데이터를 사용
        if( option == 0 ) {
            holder.mview.setOnClickListener(v -> {
                Intent intent = new Intent(context, BookInfoActivity.class);
                intent.putExtra(Constants.INTENT_WID, holder.bookData.getId());
                intent.putExtra(Constants.INTENT_BOOK_NAME, holder.bookData.getTitle());
                intent.putExtra(Constants.INTENT_BOOK_COVER, holder.bookData.getCoverImage());
                context.startActivity(intent);
            });
        }else if (option == 1 ){
            holder.mview.setOnClickListener(v -> {
//                Log.d(TAG, "onBindViewHolder: searchFor Chat ");
                Intent intent = new Intent();
                Log.d(TAG, "onBindViewHolder 선택한 책 wid : " +holder.bookData.getId());
                intent.putExtra(Constants.INTENT_WID,holder.bookData.getId()); // ** wid
                intent.putExtra(Constants.INTENT_BOOK_COVER, holder.bookData.getCoverImage()); // 커버 이미지
                intent.putExtra(Constants.INTENT_BOOK_NAME, holder.bookData.getTitle());// 책 제목
                intent.putExtra(Constants.INTENT_WRITER_NAME,holder.bookData.getUsername()); // 작가이름
                activity.setResult(Activity.RESULT_OK,intent);
                activity.finish(); //
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.size() ;
    }
    public void putData(List<SearchItemDTO> addData){
        // 이걸로 되나 ?
        if(addData.size() > 0) {
            data.addAll(addData);
            this.notifyDataSetChanged();
        };
    }

    protected class BookViewHoler extends RecyclerView.ViewHolder {
        //view holder 변수 설정
        private View mview;
        private TextView bookTitle; // 제목
        private TextView bookViews; // 조회수
        private TextView chapterShow; // 총 회차 정보
        private ImageView bookCoverImage; // 책 커버 이미지 정보 ;
        private TextView writerName ; // 작가 이름 정보 ( TODO )
        private TextView createDate ; // 등록 날짜
        private TextView categoryName; // 카테고리 이름
        String coverImagePath; // 등록 커버 이미지 경로
        private SearchItemDTO bookData;
        public BookViewHoler(@NonNull View itemView ) {
            super(itemView);
            mview = itemView;
            bookTitle = mview.findViewById(R.id.item_textView_bookTitle);
            bookViews = mview.findViewById(R.id.item_explore_textView_views);
            categoryName = mview.findViewById(R.id.item_explore_textView_categoryName);
            chapterShow = mview.findViewById(R.id.item_textView_chapterShow);
            bookCoverImage = mview.findViewById(R.id.item_imageView_bookCover);
            writerName = mview.findViewById(R.id.item_textView_writerName);
            createDate = mview.findViewById(R.id.item_explore_textView_createdDate);

        }

        private void setBook(SearchItemDTO book){
            if(book!= null){
                bookData = book;
            }
        }
        public void onBind(SearchItemDTO book){
            bookData = book;
            bookTitle.setText(book.getTitle());
            coverImagePath= book.getCoverImage();
            Log.d(TAG, "onBind: 커버 이미지 확인  " + coverImagePath);

            if( coverImagePath!= null && coverImagePath.length()>6){
                Glide.with(mview.getContext()).load(BaseUrl.BookCoverImage_URL+coverImagePath).into(bookCoverImage);
            }else {
                // 커버 이미지가 없는 경우
                Glide.with(mview.getContext())
                        .load(R.drawable.sample_bookcover)
                        .into(bookCoverImage);
            }
            String category = "카테고리 : "+book.getCategory_name() ;
            String writer = "작가명 :" +book.getUsername();
            String views = "조회수 :"+book.getTotal_views();
            String chapter_number = " 총 회차 : "+ book.getTotal_num();
            String createDateString = "등록 날짜 : "+ book.getCreateTime().substring(0,10);
            writerName.setText(writer);
            categoryName.setText(category);
            chapterShow.setText(chapter_number);
            bookViews.setText(views);
            createDate.setText(createDateString);
//            bookViews.setText(book.getNum()); // 조회수 정보인데 각 회차에서 정보를 가져 와야함  //( TODO )
//            chapterShow.setText(book.getNum());
            // TODO 작가 이름 , 카테고리 이름
        }
    }
}
