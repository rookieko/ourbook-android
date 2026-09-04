package com.example.ourbook.DataTool.Response.DTO;

import com.squareup.moshi.Json;

public abstract class commentItem {

    @Json(name = "score")
    public float score;
    @Json(name = "profile_image")
    public String profile_image;
    @Json(name = "user_name")
    public String user_name;
    @Json(name = "like_score")
    public int like_score;
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
    
    

}
