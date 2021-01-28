package com.devheon.springboot.example.repository;

import com.devheon.springboot.example.entity.Member;
import com.devheon.springboot.example.entity.Movie;
import com.devheon.springboot.example.entity.MovieMember;
import com.devheon.springboot.example.entity.Review;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.IntStream;

@SpringBootTest
public class ReviewRepositoryTests {
    @Autowired
    private ReviewRepository reviewRepository;

    //    @Test
    public void insertMovieReviews() {
        /* 200개 리뷰 등록 */
        IntStream.rangeClosed(1, 200).forEach(i -> {
            /* 영화 번호 */
            Long mno = (long) (Math.random()*100) + 1;

            /* 리뷰어 번호 */
            Long mid = (long) (Math.random()*100) + 1;
            MovieMember movieMember = MovieMember.builder().mid(mid).build();

            Review movieReview = Review.builder()
                    .movieMember(movieMember)
                    .movie(Movie.builder().mno(mno).build())
                    .grade((int)(Math.random()*5) + 1)
                    .text("이 영화에 대한 느낌..." + i)
                    .build();

            reviewRepository.save(movieReview);
        });
    }

    @Test
    public void testGetMovieReviews() {
        Movie movie = Movie.builder().mno(92L).build();
        List<Review> result = reviewRepository.findByMovie(movie);

        /**
         * Review 클래스의 Member에 대한 Fetch 방식이 LAZY이므로
         * 한번에 Review와 Member 객체를 조회할 수 없다.
         * @Transactional을 적용한다 해도 각 Review의 getMember().getEmail()을 처리할 때 마다
         * Member객체를 로딩해야 하는 문제가 있다.
         *
         * 이 문제를 해결하는 방법으로는 크게
         *     1. @Query를 이용하여 조인 처리
         *     2. @EntityGraph를 이용하여 Review 객체를 가져올 때 Member 객체를 로딩
         *
         * @EntityGraph는 엔터티의 특정한 속성을 같이 로딩하도록 표시하는 어노테이션이다.
         * 기본적으로 JPA를 이용하는 경우 연관관계의 FETCH 속성값은 LAZY로 지정하는 것이 일반적이다.
         * @EntityGraph는 이러한 상황에서 특정 기능을 수행할 때만 EAGER 로딩을 하도록 지정할 수 있다.
         * - attributePaths : 로딩 설정을 변경하고 싶은 속성의 이름을 배열로 명시
         * - type : @EntityGraph를 어떤 방식으로 적용할 것인지 설정
         * - FETCH 속성값은 attributePaths에 명시한 속성은 EAGER로 처리하고 나머지는 LAZY로 처리
         * - LOAD 속성값은 attributePaths에 명시한 속성은 EAGER로 처리하고 나머지는 엔티티 클래스에 명시되거나 기본 방식으로 처리
         */

        result.forEach(movieReview -> {
            System.out.println(movieReview.getReviewnum());
            System.out.println("\t" + movieReview.getGrade());
            System.out.println("\t" + movieReview.getText());
            System.out.println("\t" + movieReview.getMovieMember().getEmail());
            System.out.println("---------------------");
        });
    }
}