package com.example.ourbook.DataTool.Response;

import com.squareup.moshi.Json;

public class CommentDTO {
    /*DTO */

    @Json(name = "score")
    public String score;
    @Json(name = "profile_image")
    public String profile_image;
    @Json(name = "user_name")
    public String user_name;
    @Json(name = "like_score")
    public String like_score;
    @Json(name = "is_like")
    public int is_like;
    @Json(name = "parent_id")
    public int parent_id;
    @Json(name = "createDate")
    public String createDate;
    @Json(name = "type")
    public int type;
    @Json(name = "comment_content")
    public String comment_content;
    @Json(name = "wid")
    public int wid;
    @Json(name = "uid")
    public int uid;
    @Json(name = "id")
    public int id;

    //comment 의 종류 ,
    // 1 = 기본 리뷰 , 2 = 기본 부모 리뷰 , 3 = 리뷰의 댓글  , 자식 comment 인지 , 부모 comment 인지


}
