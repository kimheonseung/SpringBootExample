package com.devheon.springboot.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    /**
     * 화면에 필요한 모든 정보를 가지고 있어야 한다.
     */
    private Long reviewnum;
    /* Movie mno */
    private Long mno;
    /* Member id */
    private Long mid;
    private String nickname;
    private String email;
    private int grade;
    private String text;
    private LocalDateTime regDate, modDate;
}