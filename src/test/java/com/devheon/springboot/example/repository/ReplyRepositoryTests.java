package com.devheon.springboot.example.repository;

import com.devheon.springboot.example.entity.Board;
import com.devheon.springboot.example.entity.Reply;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@SpringBootTest
public class ReplyRepositoryTests {

    @Autowired
    private ReplyRepository replyRepository;

    //    @Test
    public void insertReply() {
        IntStream.rangeClosed(1, 300).forEach(i -> {
            long bno = (long)(Math.random() * 100) + 1;
            Board board = Board.builder().bno(bno).build();
            Reply reply = Reply.builder()
                    .text("Reply..."+i)
                    .board(board)
                    .replyer("guest")
                    .build();
            replyRepository.save(reply);
        });
    }

    //    @Test
    public void readReply1() {
        Optional<Reply> result = replyRepository.findById(1L);

        Reply reply = result.get();

        System.out.println(reply);
        System.out.println(reply.getBoard());

        /**
         * Hibernate:
         *     select
         *         reply0_.rno as rno1_2_0_,
         *         reply0_.moddate as moddate2_2_0_,
         *         reply0_.regdate as regdate3_2_0_,
         *         reply0_.board_bno as board_bn6_2_0_,
         *         reply0_.replyer as replyer4_2_0_,
         *         reply0_.text as text5_2_0_,
         *         board1_.bno as bno1_0_1_,
         *         board1_.moddate as moddate2_0_1_,
         *         board1_.regdate as regdate3_0_1_,
         *         board1_.content as content4_0_1_,
         *         board1_.title as title5_0_1_,
         *         board1_.writer_email as writer_e6_0_1_,
         *         member2_.email as email1_1_2_,
         *         member2_.moddate as moddate2_1_2_,
         *         member2_.regdate as regdate3_1_2_,
         *         member2_.name as name4_1_2_,
         *         member2_.password as password5_1_2_
         *     from
         *         reply reply0_
         *     left outer join
         *         board board1_
         *             on reply0_.board_bno=board1_.bno
         *     left outer join
         *         member member2_
         *             on board1_.writer_email=member2_.email
         *     where
         *         reply0_.rno=?
         *
         * Reply(rno=1, text=Reply...1, replyer=guest)
         * Board(bno=41, title=Title...41, content=Content....41)
         *
         * join이 복잡해지므로 좋은 방법은 아니다. (Eager loading - 즉시 로딩)
         * -> 한번에 연관관계가 있는 모든 엔티티를 가져온다는 장점
         * -> 관계가 복잡해질수록 조인으로 인한 성능 저하
         *
         * vs Lazy loading - 지연 로딩
         */
    }

    @Test
    public void testListByBoard() {
        List<Reply> replyList = replyRepository.getRepliesByBoardOrderByRno(Board.builder().bno(97L).build());
        replyList.forEach(reply -> {
            System.out.println(reply);
        });

        /**
         * Hibernate:
         *     select
         *         reply0_.rno as rno1_2_,
         *         reply0_.moddate as moddate2_2_,
         *         reply0_.regdate as regdate3_2_,
         *         reply0_.board_bno as board_bn6_2_,
         *         reply0_.replyer as replyer4_2_,
         *         reply0_.text as text5_2_
         *     from
         *         reply reply0_
         *     where
         *         reply0_.board_bno=?
         *     order by
         *         reply0_.rno asc
         * Reply(rno=65, text=Reply...65, replyer=guest)
         * Reply(rno=119, text=Reply...119, replyer=guest)
         * Reply(rno=199, text=Reply...199, replyer=guest)
         * Reply(rno=262, text=Reply...262, replyer=guest)
         */
    }
}