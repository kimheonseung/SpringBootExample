package com.devheon.springboot.example.repository;

import com.devheon.springboot.example.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    /**
     * 페이지 처리되는 영화별 평균 점수 / 리뷰 개수 구하기
     * - 영화, 영화 이미지, 리뷰를 같이 조인한다.
     * SELECT m.mno, m.regdate, m.moddate, m.title, mi.img_name,
     *         AVG(COALESCE(r.grade,0)), COUNT(DISTINCT(r.reviewnum))
     * FROM MOVIE m
     * LEFT OUTER JOIN REVIEW r
     *     ON r.movie_mno = m.mno
     * LEFT OUTER JOIN MOVIE_IMAGE mi
     *     ON mi.movie_mno = m.mno
     * GROUP BY m.mno;
     */
    @Query(
            "select m, mi, avg(coalesce(r.grade,0)), count(distinct r) " +
                    "from Movie m " +
                    "left outer join MovieImage mi on mi.movie = m " +
                    "left outer join Review r on r.movie = m " +
                    "group by m"
    )
    Page<Object[]> getListPage(Pageable pageable);

    /**
     * 특정 영화의 모든 이미지와 평균 평점 / 리뷰 개수
     * SELECT m.mno, m.regdate, m.moddate, m.title, mi.img_name,
     *         AVG(COALESCE(r.grade,0)), COUNT(DISTINCT(r.reviewnum))
     * FROM MOVIE m
     * LEFT OUTER JOIN REVIEW r
     *     ON r.movie_mno = m.mno
     * LEFT OUTER JOIN MOVIE_IMAGE mi
     *     ON mi.movie_mno = m.mno
     * WHERE m.mno = 5
     * GROUP BY mi.inum;
     */
    @Query(
            "select m, mi, avg(coalesce(r.grade,0)), count(r)  " +
                    "from Movie m " +
                    "left outer join MovieImage mi on mi.movie = m " +
                    "left outer join Review r on r.movie = m " +
                    "where m.mno = :mno " +
                    "group by mi"
    )
    List<Object[]> getMovieWithAll(Long mno);    /* 특정 영화 조회 */

    /**
     * 특정 영화의 모든 리뷰와 회원의 닉네임
     */
}