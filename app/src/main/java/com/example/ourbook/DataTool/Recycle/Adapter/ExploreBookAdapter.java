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
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.example.ourbook.Constants;
import com.example.ourbook.DataTool.BaseUrl;
import com.example.ourbook.DataTool.Book;
import com.example.ourbook.GlideModules;
import com.example.ourbook.R;
import com.example.ourbook.exploreActivity.BookInfoActivity;

import java.util.List;

public class ExploreBookAdapter extends RecyclerView.Adapter<ExploreBookAdapter.BookViewHoler> {

    // 두가지 아이템을 구분하기 위해 ViewType 변수 선언[]
    final int VIEW_TYPE_POSITIVE = 1;
    final int VIEW_TYPE_NEGATIVE = 0;
    GlideModules glideModules = new GlideModules();

    private static String TAG = "ExploreBookAdapter" + Constants.AddTAG;
    private List<Book> data;
    private Context context;


    public ExploreBookAdapter(List<Book> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public BookViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_explore_book,parent,false);
        return new BookViewHoler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHoler holder, int position) {
        holder.onBind(data.get(position)); // 먼저 book data 를 초기화 해준다 아래 북 데이터를 사용
        holder.mview.setOnClickListener(v -> {
            Intent intent = new Intent(context, BookInfoActivity.class);
            intent.putExtra(Constants.INTENT_WID,holder.bookData.getId());
            intent.putExtra(Constants.INTENT_BOOK_NAME,holder.bookData.getTitle());
            intent.putExtra(Constants.INTENT_BOOK_COVER,holder.bookData.getCoverImage());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    public void putData(List<Book> addData){
        // 이걸로 되나 ?
        if(addData.size() > 0) {
            data.addAll(addData);
            this.notifyDataSetChanged();
        };
    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
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
        private Book bookData;
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

        private void setBook(Book book){
            if(book!= null){
                bookData = book;
            }
        }
        public void onBind(Book book){
            bookData = book;
            bookTitle.setText(book.getTitle());
            coverImagePath= book.getCoverImage();
            Log.d(TAG, "onBind: 커버 이미지 확인  " + coverImagePath);

            if( coverImagePath!= null && coverImagePath.length()>6){
                Glide.with(mview.getContext()).load(BaseUrl.BookCoverImage_URL+coverImagePath).into(bookCoverImage);
            }else {
                // 커버 이미지가 없는 경우
                Glide.with(mview.getContext()).load(R.drawable.sample_bookcover).into(bookCoverImage);
            }
            String category = "카테고리 : "+book.getCategory() ;
            String writer = "작가명 :" +book.getWriter_name();
            String views = "조회수 :"+book.getTotal_views();
            String chapter_number = " 총 회차 : "+ book.getTotal_num();
            String createDateString = "등록 날짜 : "+ book.getCreateDate();
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
