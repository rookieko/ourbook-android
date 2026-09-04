package com.example.ourbook.DataTool;

public class Book {
 // WriteMainActivity 에 보여지는 리사이클러뷰에 나타내기 위해 사용 되는 DTO  , BooksData 로 연결


    private int id;
    private String title;
    private String summary;
    private String createTime;
    private String coverImage;
    private String uid;
    private String average_rating;
    private String category_id;
    private String category;
    private int total_views;
    private int total_num =0;

    public int getTotal_views() {
        return total_views;
    }

    public void setTotal_views(int total_views) {
        this.total_views = total_views;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    private String writer_name;

    public String getWriter_name() {
        return writer_name;
    }

    public void setWriter_name(String writer_name) {
        this.writer_name = writer_name;
    }

//    private int num = 0; // 회차수

    public int getTotal_num() {
        return total_num;
    }

    public void setTotal_num(int total_num) {
        this.total_num = total_num;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public String getUid() {
        return uid;
    }

    public String getAverage_rating() {
        return average_rating;
    }

    public String getCategory_id() {
        return category_id;
    }

    public String getBookInfo(){
        return createTime;
    }
    // Constructor, Getters, and Setters

    public String getCreateDate(){
        String date = createTime.substring(0,11);
        return date;
    }
}