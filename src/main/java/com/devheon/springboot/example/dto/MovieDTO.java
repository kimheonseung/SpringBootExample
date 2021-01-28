package com.devheon.springboot.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovieDTO {
    private Long mno;
    private String title;

    /**
     * The field annotated with @Default must have an initializing expression;
     *     that expression is taken as the default to be used if not explicitly set during building.
     */
    @Builder.Default
    private List<MovieImageDTO> imageDTOList = new ArrayList<>();

    /* 영화의 평균 평점 */
    private double avg;

    /* 리뷰 수 (jpa count()) */
    private int reviewCnt;

    private LocalDateTime regDate;
    private LocalDateTime modDate;
}