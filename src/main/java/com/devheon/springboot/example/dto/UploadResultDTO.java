package com.devheon.springboot.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Data
@AllArgsConstructor
public class UploadResultDTO {
    /**
     * 파일 업로드 결과 브라우저로 전송할 정보를 가진 객체
     * 1. 업로드된 파일의 원래 이름
     * 2. UUID값
     * 3. 업로드된 파일의 저장 경로
     */

    private String fileName;
    private String uuid;
    private String folderPath;

    public String getImageURL() {
        try {
            return URLEncoder.encode(folderPath + File.separator + uuid + "_" + fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 썸네일 이미지를 링크로 처리하기 위한 메소드
     */
    public String getThumbnailURL() {
        try {
            return URLEncoder.encode(folderPath + File.separator + "s_" + uuid + "_" + fileName, "UTF-8");
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
}