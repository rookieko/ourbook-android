package com.example.ourbook.DataTool.Response.DTO;

import com.squareup.moshi.Json;

public  class SearchItemDTO {

    @Json(name = "category_name")
    private String category_name;
    @Json(name = "profile_image")
    private String profile_image;
    @Json(name = "username")
    private String username;
    @Json(name = "total_views")
    private int total_views;
    @Json(name = "total_num")
    private int total_num;
    @Json(name = "category_id")
    private int category_id;
    @Json(name = "average_rating")
    private Double average_rating;
    @Json(name = "uid")
    private int uid;
    @Json(name = "coverImage")
    private String coverImage;
    @Json(name = "createTime")
    private String createTime;
    @Json(name = "summary")
    private String summary;
    @Json(name = "title")
    private String title;
    @Json(name = "id")
    private int id;

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getTotal_views() {
        return total_views;
    }

    public void setTotal_views(int total_views) {
        this.total_views = total_views;
    }

    public int getTotal_num() {
        return total_num;
    }

    public void setTotal_num(int total_num) {
        this.total_num = total_num;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public double getAverage_rating() {
        return average_rating;
    }

    public void setAverage_rating(Double average_rating) {
        this.average_rating = average_rating;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
