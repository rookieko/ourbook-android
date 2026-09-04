package com.example.ourbook.DataTool.Response;

import com.squareup.moshi.Json;

public class SignUp_Response {
    /** 목표 기능
     * 1. 이메일 중복 검사 , 닉네임 중복 검사
     * 2. 이메일 발송 성공 여부 (?)
     * 성공 여부 , 중복 유무
     *  응답 데이터
     * emailDuplicate
     * nameDuplicate
     * authCodeSave
     * authCodeSend
     * */

    // emailDuplicationCheckResponse = 이메일 중복 확인 응답;
    @Json(name = "emailDuplicate")
    public NormalResponseDTO emailDuplicationCheckResponse = new NormalResponseDTO() ;

    // emailDuplicationCheckResponse = 유저 이름 중복 확인 응답;
    @Json(name = "nameDuplicate")
    public NormalResponseDTO userNameDuplicationCheckResponse = new NormalResponseDTO() ; ;

    // sendAuthEmailResponse = 이메일 인증 코드 확인;
    @Json(name = "authCodeSave")
    public NormalResponseDTO authCodeSaveCheckResponse = new NormalResponseDTO() ;;
    @Json(name = "authCodeSend")
    public NormalResponseDTO sendAuthEmailResponse = new NormalResponseDTO() ;;




};
