package com.example.ourbook.DataTool.Response;
import android.graphics.drawable.Drawable;
public class BookItem {

        private String bookTitle;
        private String bookInfo;
        private Drawable picture;

        public BookItem(String bookTitle, String BookInfo,  Drawable picture ){
            this.bookTitle = bookTitle;
            this.picture = picture;
            this.bookInfo = BookInfo;
        }



        public Drawable getPicture() {
            return picture;
        }

        public String getBookTitle() {
            return bookTitle;
        }

        public String getBookInfo() {
            return bookInfo;
        }
}
