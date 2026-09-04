package com.example.ourbook.DataTool.Response;
import com.example.ourbook.DataTool.Book;
import com.squareup.moshi.Json;

import java.util.List;

public class BooksData {
    // WriteMainActivity 에 보여지는 리사이클러뷰에 나타내기 위해 사용 되는 DTO , 공통 사용 ( RecyclerView Adapter , writerResponse 에서 응답을 받는데 사용 )
    @Json(name = "book")
    private List<Book> bookList;

    public List<Book> getBookList() {
        return bookList;
    }

    // Constructor, Getters, and Setters
}