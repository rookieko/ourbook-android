package com.example.ourbook.DataTool.Response;

import com.squareup.moshi.Json;

import java.util.Map;

public class CategoryDTO  {
    @Json(name = "id")
    public int id = -1;

    @Json(name = "name")
    public String name = "";

//    @Json(name = "category")
//    Map<Integer,String> categoryMap = null;
}
