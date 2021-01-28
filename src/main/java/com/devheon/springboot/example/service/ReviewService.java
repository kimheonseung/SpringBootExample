package com.devheon.springboot.example.service;

import com.devheon.springboot.example.dto.ReviewDTO;
import com.devheon.springboot.example.entity.Movie;
import com.devheon.springboot.example.entity.MovieMember;
import com.devheon.springboot.example.entity.Review;

import java.util.List;

public interface ReviewService {
    /* 영화의 모든 리뷰를 가져온다. */
    List<ReviewDTO> getListOfMovie(Long mno);
    /* 영화 리뷰를 추가한다. */
    Long register(ReviewDTO movieReviewDTO);
    /* 특정 영화의 리뷰를 수정한다. */
    void modify(ReviewDTO movieReviewDTO);
    /* 영화= 리뷰를 삭제한다. */
    void remove(Long reviewnum);

    default Review dtoToEntity(ReviewDTO movieReviewDTO) {
        Review movieReview = Review.builder()
                .reviewnum(movieReviewDTO.getReviewnum())
                .movie(Movie.builder().mno(movieReviewDTO.getMno()).build())
                .movieMember(MovieMember.builder().mid(movieReviewDTO.getMid()).build())
                .grade(movieReviewDTO.getGrade())
                .text(movieReviewDTO.getText())
                .build();

        return movieReview;
    }

    default ReviewDTO entityToDto(Review movieReview) {
        ReviewDTO movieReviewDTO = ReviewDTO.builder()
                .reviewnum(movieReview.getReviewnum())
                .mno(movieReview.getMovie().getMno())
                .mid(movieReview.getMovieMember().getMid())
                .nickname(movieReview.getMovieMember().getNickname())
                .email(movieReview.getMovieMember().getEmail())
                .grade(movieReview.getGrade())
                .text(movieReview.getText())
                .regDate(movieReview.getRegDate())
                .modDate(movieReview.getModDate())
                .build();

        return movieReviewDTO;
    }
}