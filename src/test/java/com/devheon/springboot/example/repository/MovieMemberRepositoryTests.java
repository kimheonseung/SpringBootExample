package com.devheon.springboot.example.repository;

import com.devheon.springboot.example.entity.Member;
import com.devheon.springboot.example.entity.MovieMember;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import javax.transaction.Transactional;
import java.util.stream.IntStream;

@SpringBootTest
public class MovieMemberRepositoryTests {
    @Autowired
    private MovieMemberRepository movieMemberRepository;
    @Autowired
    private ReviewRepository reviewRepository;

    //    @Test
    public void insertMembers() {
        IntStream.rangeClosed(1, 100).forEach(i -> {
            MovieMember movieMember = MovieMember.builder()
                    .email("r"+i+"@zerock.com")
                    .pw("1111")
                    .nickname("reviewer"+i).build();
            movieMemberRepository.save(movieMember);
        });
    }

    @Transactional
    @Commit
    @Test
    public void testDeleteMember() {
        Long mid = 3L;    /* Member의 mid */
        MovieMember movieMember = MovieMember.builder().mid(mid).build();

        /**
         * 아래는 에러 발생
         * - FK를 갖는 Review쪽을 먼저 삭제해야 함
         * - 트랜잭션 관련 처리가 없음
         * 따라서 @Transactional, @Commit을 추가하고, 순서를 바꾼다.
         * memberRepository.deleteById(mid);
         * reviewRepository.deleteByMember(member);
         *
         * 실제로 실행되는 쿼리는 Review 테이블에서 회원이 작성한 리뷰 갯수만큼 delete문이 실행된다.
         * 이를 방지하기 위해 ReviewRepository의 deleteByMember 메소드에 Query를 지정한다.
         */

        reviewRepository.deleteByMovieMember(movieMember);
        movieMemberRepository.deleteById(mid);

    }
}