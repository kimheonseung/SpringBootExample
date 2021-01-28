package com.devheon.springboot.example.repository;

import com.devheon.springboot.example.entity.Board;
import com.devheon.springboot.example.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    /* 게시물 번호에 해당하는 모든 댓글 삭제 */
    /* JPQL을 이용하여 update, delete를 실행할 때는 @Modifying 어노테이션을 추가해야 한다. */
    @Modifying
    @Query("delete from Reply r where r.board.bno = :bno")
    void deleteByBno(Long bno);
    /* 게시물로 댓글 목록 가져오기 */
    List<Reply> getRepliesByBoardOrderByRno(Board board);
}
