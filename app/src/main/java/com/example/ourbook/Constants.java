package com.example.ourbook;

public final class Constants {
    // 상수 아직 미 사용
    /*for debug log */
    public static final String AddTAG= " allOurBook ";
    /*for app intent */
    public static final String INTENT_JWT = "jwt";
    public static final String INTENT_UID = "uid";

    public static final String INTENT_CID = "cid"; // category id
    public static final String INTENT_CHAPTER_ID = "chapter_id";
    public static final String INTENT_CHAPTER_NUM = "chapter_num";
    public static final String INTENT_CATEGORY_NAME = "category_name";
    public static final String INTENT_TYPE = "type"; // 인텐트, 처리를 두 종류로 처리하기 위해서
    public static final String INTENT_SUMMARY = "summary"; // 인텐트, 처리를 두 종류로 처리하기 위해서
    public static final String INTENT_CATEGORY_ID = "category_id";
    public static final String INTENT_CHATROOM_ID = "chatRoom_id";
    public static final String INTENT_CHATROOM_USER_ID = "chatRoom_user_id";
    public static final String INTENT_CHATROOM_LAST_CHAT_ID = "last_chat_id";

    // local.properties 의 ourbook.ip / ourbook.port 가 build.gradle.kts 를 거쳐 들어온다
    public static final String SERVER_IP = BuildConfig.SERVER_IP;
    public static final int SERVER_PORT = BuildConfig.SERVER_PORT;



    public static final String INTENT_WID = "wid";
    public static final String INTENT_BOOK_NAME = "bookName";
    public static final String INTENT_BOOK_COVER = "bookCover";
    public static final String INTENT_REVIEW_ID = "comment_id";
    public static final String INTENT_WRITER_NAME = "writer_name";



    /*FOR RESPONSE */
    public static final String RESPONSE_TOKEN = "OBJWToken";

    // get user info 응답 데이터 [id, username, email, password, createDate, status] wrap data field
    public static final String RESPONSE_Data_Email = "email";
    public static final String RESPONSE_Data_UserName = "username";
    public static final String RESPONSE_Data_password = "password";
    public static final String RESPONSE_Data_ProfileImage = "profileImagePath";
    // 점수
    public static final String RESPONSE_DATA_UserScore = "my_score";
    // 내가 작성한  reviewID
    public static final String RESPONSE_DATA_UserReviewID = "my_comment_id";
    // 관심, 알림 설정 옵션 1 = 관심 , 2 = 알림
    public static final String RESPONSE_DATA_LikeOption = "likes";
    // 마지막 조회한 회차의 id
    public static final String RESPONSE_DATA_RecentChapter = "recent_view_chapter";
    public static final String RESPONSE_DATA_FirstChapter = "first_chapter_id";
    // 위치  history
    public static final String RESPONSE_DATA_RecentChapter_locate = "recent_view_locate";
    public static final String RESPONSE_DATA_ChapterContent = "chapter_content";
    public static final String RESPONSE_DATA_REVIEW_Count = "review_count";
    public static final String RESPONSE_DATA_ChapterNum = "num";
    public static final String RESPONSE_DATA_ChapterName= "chapter_name";
    public static final String RESPONSE_DATA_RATING_WORLD = "world_score";
    public static final String RESPONSE_DATA_RATING_CHARACTER = "character_score";
    public static final String RESPONSE_DATA_RATING_STORY = "story_score";
    public static final String RESPONSE_DATA_RATING_QUALITY = "quality_score";
    public static final String RESPONSE_DATA_RATING_UPDATE = "update_score";
    public static final String RESPONSE_DATA_RATING_TOTAL = "total_score";
    public static final String RESPONSE_DATA_REVIEW_Content = "comment_content";


    /*FOR REQUEST*/
//    public static final String REQUEST_
    public static final String HEADER_TOKEN = "Ojwt-Token";

    /*정렬 옵션 */
    // 인기순

    public static final int OPTION_Popular = 0;
    // 최신순
    public static final int OPTION_New = 1;
    // 등록
    public static final int OPTION_Old = 2;
    // Main 에 보여질 대표 5개 작품 가져오기
    public static final int OPTION_Main = 5;


    public static final String REQUEST_USERNAME ="userName";
    public static final String REQUEST_UID = "uid";
    public static final String REQUEST_WID = "wid";
    public static final String REQUEST_CHAPTER_ID = "chapter_id";
    public static final String REQUEST_HISTORY_Position = "position";
    public static final String REQUEST_HISTORY_Percent = "percent";
    public static final String REQUEST_EMAIL = "email";
    public static final String REQUEST_PASSWORD = "password";
    // 제목 - 중복 확인
    public static final String REQUEST_BOOK_TITLE = "title";
    public static final String REQUEST_BOOK_SUMMARY ="summary";
    public static final String REQUEST_BOOK_COVER = "book-cover";
    public static final String REQUEST_BOOK_CATEGORY = "category";
    public static final String REQUEST_BOOK_TYPE = "type";

    public static final String REQUEST_PROFILE_IMAGE = "imageKey";

    /*요청 request ItemList*/
//    public static final String REQUEST_PAGING_OPTION = "option";
//    public static final String REQUEST_PAGING_PAGE = "page";
    public static final String REQUEST_QUERY = "query";

    /*paging 처리 요청 관련 String 시험 삼아 suggest 등 import 에 제한을 둬
    캡슐화 느낌이 나게끔 해봤는데  그닥 인거 같음 */
    public static class Paging {
        public static final String REQUEST_PAGING_OPTION = "option";
        public static final String REQUEST_PAGING_PAGE = "page";
    }
    public static class Review{
        public static final String REQUEST_REVIEW_CONTENT ="content";
        public static final String REQUEST_REVIEW_SCORE_World = "score_world";
        public static final String REQUEST_REVIEW_SCORE_Character = "score_character";
        public static final String REQUEST_REVIEW_SCORE_Story ="score_story";
        public static final String REQUEST_REVIEW_SCORE_Quality ="score_quality";
        public static final String REQUEST_REVIEW_SCORE_Update ="score_update";
        public static final String REQUEST_REVIEW_Like_OR_NOT = "like_or_not";
        public static final String REQUEST_REVIEW_COMMENT_ID = "comment_id";

        public static final String RESULT_REVIEW_CONTENT = "content";
        public static final int RESULT_REVIEW_SCORE_World = 1;
        public static final int RESULT_REVIEW_SCORE_Character = 2;
        public static final int RESULT_REVIEW_SCORE_Story = 3;
        public static final int RESULT_REVIEW_SCORE_Total = 0; // 평균 값


    }
    public static class Read{
        public static final int TYPE_FIRST_READ = 0;
        public static final int TYPE_CONTINUE_READ = 1;
        public static final int TYPE_SELECT_READ = 2;

    }

}
