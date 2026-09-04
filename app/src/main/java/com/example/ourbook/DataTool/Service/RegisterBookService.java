package com.example.ourbook.DataTool.Service;

import com.example.ourbook.Constants;
import com.example.ourbook.DataTool.Book;
import com.example.ourbook.DataTool.Response.CategoryDTO;
import com.example.ourbook.DataTool.Response.Chapter;
import com.example.ourbook.DataTool.Response.ChapterItem;
import com.example.ourbook.DataTool.Response.DTO.SearchItemDTO;
import com.example.ourbook.DataTool.Response.NormalResponseDTO;
import com.example.ourbook.DataTool.Response.writerResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface RegisterBookService {
    // 이미지 업로드를 제외한 웹소설 수정 데이터 관련 서비스

    /* 책의 제목 중복 확인 */
    @GET("OurBook/webnovel/title-check.php")
    Call<NormalResponseDTO> TitleDuplicateCheck(@Header (Constants.HEADER_TOKEN) String Token, @Query(Constants.REQUEST_BOOK_TITLE) String title );

    /* 카테고리 장르 가져오기 */
    @GET("OurBook/webnovel/get-category-info.php")
    Call<List<CategoryDTO>> getCategoryInfo();

    /*책 WebNovel 등록 */
    @FormUrlEncoded
    @POST("OurBook/webnovel/regist-webnovel.php")
    Call<NormalResponseDTO> WebNovelRegister(@Header(Constants.HEADER_TOKEN) String Token ,@Field(Constants.REQUEST_BOOK_TITLE) String title, @Field(Constants.REQUEST_BOOK_CATEGORY) int categoryID , @Field(Constants.REQUEST_BOOK_TYPE) int Type);

//    @FormUrlEncoded
    @GET("OurBook/webnovel/get-own-webnovel.php")
    Call<writerResponse> getWriterBookList(@Header(Constants.HEADER_TOKEN) String Token);

    // 사용자가 작성 한 책 chapter 가져오기
    @FormUrlEncoded
    @POST("OurBook/webnovel/get-own-chapter.php")
    Call<ChapterItem> getWriteChapterData(@Header(Constants.HEADER_TOKEN) String Token , @Field("wid") int wid );
    // 사용자가 작성한 책 wid -> data 가져오기
    @FormUrlEncoded
    @POST("OurBook/webnovel/get-one-book-data.php")
    Call<NormalResponseDTO> getWebNovelData(@Header(Constants.HEADER_TOKEN) String Token , @Field("wid") int wid );
    /*책 , 유저 정보 상세 조회 서비스 */
    @FormUrlEncoded
    @POST("OurBook/webnovel/get-user-one-book-data.php")
    Call<NormalResponseDTO> getUserBookData(@Header(Constants.HEADER_TOKEN) String Token , @FieldMap Map<String,Object> field_map);

    // 회차 등록
    @FormUrlEncoded
    @POST("OurBook/webnovel/regist-chapter.php")
    Call<NormalResponseDTO> ChapterRegister(@Header(Constants.HEADER_TOKEN) String Token , @Field("wid") int wid ,@Field("title") String title , @Field("content") String content ,@Field("word_length") int word_length ,@Field("text_length") int text_length );

    // 책 데이터 수정
    @FormUrlEncoded
    @POST("OurBook/webnovel/webnovel-update.php")
    Call<NormalResponseDTO> UpdateWebNovelData(@Header(Constants.HEADER_TOKEN) String Token , @Field("wid") int wid , @FieldMap Map<String,String> field_map);

    // 회차 데이터 수정

    @FormUrlEncoded
    @POST("OurBook/webnovel/chapter-update.php")
    Call<NormalResponseDTO> ChangeChapter(@Header(Constants.HEADER_TOKEN) String Token , @Field("wid") int wid , @FieldMap Map<String,String> field_map);

    /* 책 정보 조회 서비스 */

    @GET("OurBook/webnovel/explore/item-category-book.php")
    Call<List<Book>> getBookItemList(@Header((Constants.HEADER_TOKEN)) String Token , @QueryMap Map<String,Object> query_map );



    /*회차 정보 조회 서비스*/
    @GET("OurBook/webnovel/explore/item-webnovel-chapter.php")
    Call<List<Chapter>> getChapterItemList(@Header((Constants.HEADER_TOKEN)) String Token , @QueryMap Map<String,Object> query_map );

    @GET("OurBook/webnovel/explore/item-main-book.php")
    Call<List<Book>> getMainBookItem(@Header(Constants.HEADER_TOKEN) String Token);


    /* 검색 서비스 제목 , 줄거리 */
    @GET("OurBook/webnovel/search/search-webnovel.php")
    Call<List<SearchItemDTO>> getSearchItem(@Header(Constants.HEADER_TOKEN) String Token , @Query("query") String query);





}
