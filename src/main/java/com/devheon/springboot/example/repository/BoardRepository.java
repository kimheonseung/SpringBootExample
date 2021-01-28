package com.devheon.springboot.example.repository;

import com.devheon.springboot.example.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long>, SearchBoardRepository {
    /**
     * # left (outer) join
     * - 스프링부트 2버전 이후 포함되는 JPA 버전은 엔티티 클래스 내에 전혀 연관관계가 없어도 join을 이용할 수 있다.
     * - inner join <=> join
     * - left outer join <=> left join
     *
     * 엔티티 클래스 내부에 연관관계가 있는 경우의 JPQL
     * - Board 엔티티 클래스 내부에는 Member 엔티티 클래스를 변수로 선언하고 연관관계를 맺고 있다.
     *   이러한 경우 writer를 이용하여 조인
     *
     * 연관관계가 없는 엔티티 조인 처리에는 on
     * - Board와 Member 사이에는 내부적 참조로 연관관계가 있지만,
     *   Board와 Reply는 Reply쪽이 @ManyToOne으로 참조하고 있으나
     *   Board 입장에서는 Replay 객체들을 참조하고 있지 않기 때문에 문제가 된다.
     *   이런 경우 직접 조인이 필요하므로 on을 이용하여 조건을 작성한다.
     */

    /**
     * 한개의 로우(Object) 내에 Object[]로 나옴
     * - b.writer로 조인을 맺는다.
     */
    @Query("select b, w from Board b left join b.writer w where b.bno =:bno")
    Object getBoardWithWriter(@Param("bno") Long bno);

    /**
     * 특정 게시물과 해당 게시물에 속한 댓글들을 조회
     * - select
     *      board.bno, board.title, board.writer_email, rno, text
     *   from board left outer join reply on reply.board_bno = board.bno
     *   where board.bno = 14;
     */
    @Query("select b, r from Board b left join Reply r on r.board = b where b.bno =:bno")
    List<Object[]> getBoardWithReply(@Param("bno") Long bno);

    /**
     * 목록 화면에 필요한 JPQL 만들기
     * - 게시물 : 게시물의 번호, 제목, 작성시간
     * - 회원   : 회원 이름/이메일
     * - 댓글   : 해당 게시물의 댓글 수
     *
     * 위 세 엔티티 중 가장 많은 데이터를 가져오는 쪽은 Board이므로 Board를 중심으로 조인 관계를 작성한다.
     * Member는 Board 내에 writer라는 필드로 연관관계를 맺고 있고,
     * Reply는 연관관계가 없다.
     * 조인 후 Board를 기준으로 group by를 통해 하나의 게시물 당 하나의 라인이 되도록 처리한다.
     */
    @Query(
            value = "select b, w, count(r) from Board b " +
                    "left join b.writer w " +
                    "left join Reply r on r.board = b " +
                    "group by b",
            countQuery = "select count(b) from Board b"
    )
    Page<Object[]> getBoardWithReplyCount(Pageable pageable);

    /**
     * 조회 화면에서 필요한 JPQL 구성하기
     * Board와 Member를 주로 이용하고, 해당 게시물이 몇 개의 댓글이 있는지 알려주는 수준
     * 실제 댓글은 주로 Ajax를 이용해 필요 순간에 동적으로 가져오는 방식이 일반적이다.
     */
    @Query("select b, w, count(r) from Board b " +
            "left join b.writer w " +
            "left outer join Reply r on r.board = b " +
            "where b.bno = :bno")
    Object getBoardByBno(@Param("bno") Long bno);
}
