package com.example.ourbook.DataTool.Response.DTO;

import com.squareup.moshi.Json;

public abstract class BookInfoDTO {

    @Json(name = "data")
    private Data data;
    @Json(name = "message")
    private String message;
    @Json(name = "success")
    private boolean success;
    @Json(name = "status")
    private int status;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public static class Data {
        @Json(name = "total_views")
        private String total_views;
        @Json(name = "total_num")
        private String total_num;
        @Json(name = "category_id")
        private String category_id;
        @Json(name = "uid")
        private String uid;
        @Json(name = "coverImage")
        private String coverImage;
        @Json(name = "createTime")
        private String createTime;
        @Json(name = "summary")
        private String summary;
        @Json(name = "title")
        private String title;
        @Json(name = "id")
        private String id;
        @Json(name = "category")
        private String category;
        @Json(name = "writer_name")
        private String writer_name;

        public String getTotal_views() {
            return total_views;
        }

        public void setTotal_views(String total_views) {
            this.total_views = total_views;
        }

        public String getTotal_num() {
            return total_num;
        }

        public void setTotal_num(String total_num) {
            this.total_num = total_num;
        }

        public String getCategory_id() {
            return category_id;
        }

        public void setCategory_id(String category_id) {
            this.category_id = category_id;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
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

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getWriter_name() {
            return writer_name;
        }

        public void setWriter_name(String writer_name) {
            this.writer_name = writer_name;
        }
    }
}
