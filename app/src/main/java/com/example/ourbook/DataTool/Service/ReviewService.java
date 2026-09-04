package com.example.ourbook.DataTool.Service;

import com.example.ourbook.Constants;
import com.example.ourbook.DataTool.Response.CommentDTO;
import com.example.ourbook.DataTool.Response.NormalResponseDTO;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface ReviewService {
    /* 웹소설의 리뷰 등록 , 수정 , 삭제 관련 서비스 */

    /* 리뷰 등록 */
    @FormUrlEncoded
    @POST("OurBook/webnovel/review/regis-review.php")
    Call<NormalResponseDTO> reviewRegis(@Header(Constants.HEADER_TOKEN) String jwtToken , @FieldMap Map<String,Object> fieldMap);

    /*리뷰 호출*/
    @GET("")
    Call<NormalResponseDTO> getOwnReview(@Header(Constants.HEADER_TOKEN) String jwtToken , @FieldMap Map<String,Object> queryMaps);

    /*웹소설 리뷰 목록 호출*/
    @GET("OurBook/webnovel/review/item-webnovel-comment.php")
    Call<List<CommentDTO>> reviewGetList(@Header(Constants.HEADER_TOKEN) String jwt , @QueryMap Map<String,Object> queryMap /* 필수 wid , option , page  */);

    /*웹소설 베스트 목록 호출*/
    @GET("OurBook/webnovel/review/get-best-review.php")
    Call<List<CommentDTO>> bestReviewGetList(@Header(Constants.HEADER_TOKEN) String jwt , @QueryMap Map<String,Object> queryMap /* 필수 wid , option , page  */);

    /*리뷰 좋아요 등록*/
    // 현재 하나의 댓글 정보를 가져온다 
    @FormUrlEncoded
    @POST("OurBook/webnovel/review/regis-review-like.php")
    Call<NormalResponseDTO> setReviewLike(@Header(Constants.HEADER_TOKEN) String jwtToken , @FieldMap Map<String,Object> fieldMap);

    /* comment_id to get data */
    @GET("OurBook/webnovel/review/get-own-review.php")
    Call<NormalResponseDTO> getReviewOwnData(@Header(Constants.HEADER_TOKEN) String jwtToken , @QueryMap Map<String,Object> queryMap);
    /* review rating 통계 데이터 가져오기 */
    @GET("OurBook/webnovel/review/get-review-statics.php")
    Call<NormalResponseDTO> getReviewStatisticsData(@Header(Constants.HEADER_TOKEN) String jwtToken , @QueryMap Map<String ,Object> queryMap);
}
