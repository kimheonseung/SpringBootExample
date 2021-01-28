package com.devheon.springboot.example.repository;

import com.devheon.springboot.example.entity.Board;
import com.devheon.springboot.example.entity.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@SpringBootTest
public class BoardRepositoryTests {

    @Autowired
    private BoardRepository boardRepository;

    //    @Test
    public void insertBoard() {
        IntStream.rangeClosed(1, 100).forEach(i -> {
            Member member = Member.builder().email("user"+i+"@aaa.com").build();
            Board board = Board.builder()
                    .title("Title..."+i)
                    .content("Content...."+i)
                    .writer(member)
                    .build();
            boardRepository.save(board);
        });
    }

//    @Test
    /**
     * Lazy loading 방식인 경우 board.getWriter() 부분에서 member 테이블을 로딩 해야 하지만
     * 이미 데이터베이스와의 연결은 종료된 상태이기 때문에 Transactional 처리
     * -> 필요할때 다시 커넥션이 생성됨
     *
     * Lazy loading 장점
     * - 조인을 하지 않으므로 단순하게 하나의 테이블을 이용하는 경우에 빠른 처리속도
     * 단점
     * - 필요한 순간에 쿼리가 실행되므로 연관관계가 복잡한 경우 여러번 쿼리가 실행
     *
     * => 보편적으로 Lazy loading을 기본으로 사용하고, 상황에 맞는 방법을 찾는다.
     */
    @Transactional
    public void testRead1() {
        Optional<Board> result = boardRepository.findById(100L);

        Board board = result.get();

        System.out.println(board);
        System.out.println(board.getWriter());

        /**
         * Hibernate:
         *     select
         *         board0_.bno as bno1_0_0_,
         *         board0_.moddate as moddate2_0_0_,
         *         board0_.regdate as regdate3_0_0_,
         *         board0_.content as content4_0_0_,
         *         board0_.title as title5_0_0_,
         *         board0_.writer_email as writer_e6_0_0_
         *     from
         *         board board0_
         *     where
         *         board0_.bno=?
         * Board(bno=100, title=Title...100, content=Content....100)
         * Hibernate:
         *     select
         *         member0_.email as email1_1_0_,
         *         member0_.moddate as moddate2_1_0_,
         *         member0_.regdate as regdate3_1_0_,
         *         member0_.name as name4_1_0_,
         *         member0_.password as password5_1_0_
         *     from
         *         member member0_
         *     where
         *         member0_.email=?
         * Member(email=user100@aaa.com, password=1111, name=USER100)
         */
    }

    //    @Test
    public void testReadWriter() {
        Object result = boardRepository.getBoardWithWriter(100L);
        Object[] arr = (Object[]) result;
        System.out.println(Arrays.toString(arr));
        /**
         * Hibernate:
         *     select
         *         board0_.bno as bno1_0_0_,
         *         member1_.email as email1_1_1_,
         *         board0_.moddate as moddate2_0_0_,
         *         board0_.regdate as regdate3_0_0_,
         *         board0_.content as content4_0_0_,
         *         board0_.title as title5_0_0_,
         *         board0_.writer_email as writer_e6_0_0_,
         *         member1_.moddate as moddate2_1_1_,
         *         member1_.regdate as regdate3_1_1_,
         *         member1_.name as name4_1_1_,
         *         member1_.password as password5_1_1_
         *     from
         *         board board0_
         *     left outer join
         *         member member1_
         *             on board0_.writer_email=member1_.email
         *     where
         *         board0_.bno=?
         * [Board(bno=100, title=Title...100, content=Content....100), Member(email=user100@aaa.com, password=1111, name=USER100)]
         */
    }

    //    @Test
    public void testGetBoardWithReply() {
        List<Object[]> result = boardRepository.getBoardWithReply(14L);

        for(Object[] arr : result) {
            System.out.println(Arrays.toString(arr));
        }
        /**
         * Hibernate:
         *     select
         *         board0_.bno as bno1_0_0_,
         *         reply1_.rno as rno1_2_1_,
         *         board0_.moddate as moddate2_0_0_,
         *         board0_.regdate as regdate3_0_0_,
         *         board0_.content as content4_0_0_,
         *         board0_.title as title5_0_0_,
         *         board0_.writer_email as writer_e6_0_0_,
         *         reply1_.moddate as moddate2_2_1_,
         *         reply1_.regdate as regdate3_2_1_,
         *         reply1_.board_bno as board_bn6_2_1_,
         *         reply1_.replyer as replyer4_2_1_,
         *         reply1_.text as text5_2_1_
         *     from
         *         board board0_
         *     left outer join
         *         reply reply1_
         *             on (
         *                 reply1_.board_bno=board0_.bno
         *             )
         *     where
         *         board0_.bno=?
         * [Board(bno=14, title=Title...14, content=Content....14), Reply(rno=76, text=Reply...76, replyer=guest)]
         * [Board(bno=14, title=Title...14, content=Content....14), Reply(rno=103, text=Reply...103, replyer=guest)]
         */
    }

    //    @Test
    public void testWithReplyCount() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("bno").descending());
        Page<Object[]> result = boardRepository.getBoardWithReplyCount(pageable);
        result.get().forEach(row -> {
            Object[] arr = (Object[]) row;
            System.out.println(Arrays.toString(arr));
        });

        /**
         * Hibernate:
         *     select
         *         board0_.bno as col_0_0_,
         *         member1_.email as col_1_0_,
         *         count(reply2_.rno) as col_2_0_,
         *         board0_.bno as bno1_0_0_,
         *         member1_.email as email1_1_1_,
         *         board0_.moddate as moddate2_0_0_,
         *         board0_.regdate as regdate3_0_0_,
         *         board0_.content as content4_0_0_,
         *         board0_.title as title5_0_0_,
         *         board0_.writer_email as writer_e6_0_0_,
         *         member1_.moddate as moddate2_1_1_,
         *         member1_.regdate as regdate3_1_1_,
         *         member1_.name as name4_1_1_,
         *         member1_.password as password5_1_1_
         *     from
         *         board board0_
         *     left outer join
         *         member member1_
         *             on board0_.writer_email=member1_.email
         *     left outer join
         *         reply reply2_
         *             on (
         *                 reply2_.board_bno=board0_.bno
         *             )
         *     group by
         *         board0_.bno
         *     order by
         *         board0_.bno desc limit ?
         * Hibernate:
         *     select
         *         count(board0_.bno) as col_0_0_
         *     from
         *         board board0_
         * [Board(bno=100, title=Title...100, content=Content....100), Member(email=user100@aaa.com, password=1111, name=USER100), 1]
         * [Board(bno=99, title=Title...99, content=Content....99), Member(email=user99@aaa.com, password=1111, name=USER99), 1]
         * [Board(bno=98, title=Title...98, content=Content....98), Member(email=user98@aaa.com, password=1111, name=USER98), 3]
         * [Board(bno=97, title=Title...97, content=Content....97), Member(email=user97@aaa.com, password=1111, name=USER97), 4]
         * [Board(bno=96, title=Title...96, content=Content....96), Member(email=user96@aaa.com, password=1111, name=USER96), 2]
         * [Board(bno=95, title=Title...95, content=Content....95), Member(email=user95@aaa.com, password=1111, name=USER95), 3]
         * [Board(bno=94, title=Title...94, content=Content....94), Member(email=user94@aaa.com, password=1111, name=USER94), 7]
         * [Board(bno=93, title=Title...93, content=Content....93), Member(email=user93@aaa.com, password=1111, name=USER93), 4]
         * [Board(bno=92, title=Title...92, content=Content....92), Member(email=user92@aaa.com, password=1111, name=USER92), 0]
         * [Board(bno=91, title=Title...91, content=Content....91), Member(email=user91@aaa.com, password=1111, name=USER91), 0]
         */
    }

    //    @Test
    public void testRead3() {
        Object result = boardRepository.getBoardByBno(14L);
        Object[] arr = (Object[]) result;
        System.out.println(Arrays.toString(arr));

        /**
         * Hibernate:
         *     select
         *         board0_.bno as bno1_0_0_,
         *         reply1_.rno as rno1_2_1_,
         *         board0_.moddate as moddate2_0_0_,
         *         board0_.regdate as regdate3_0_0_,
         *         board0_.content as content4_0_0_,
         *         board0_.title as title5_0_0_,
         *         board0_.writer_email as writer_e6_0_0_,
         *         reply1_.moddate as moddate2_2_1_,
         *         reply1_.regdate as regdate3_2_1_,
         *         reply1_.board_bno as board_bn6_2_1_,
         *         reply1_.replyer as replyer4_2_1_,
         *         reply1_.text as text5_2_1_
         *     from
         *         board board0_
         *     left outer join
         *         reply reply1_
         *             on (
         *                 reply1_.board_bno=board0_.bno
         *             )
         *     where
         *         board0_.bno=?
         * [Board(bno=14, title=Title...14, content=Content....14), Reply(rno=76, text=Reply...76, replyer=guest)]
         * [Board(bno=14, title=Title...14, content=Content....14), Reply(rno=103, text=Reply...103, replyer=guest)]
         * Hibernate:
         *     select
         *         board0_.bno as col_0_0_,
         *         member1_.email as col_1_0_,
         *         count(reply2_.rno) as col_2_0_,
         *         board0_.bno as bno1_0_0_,
         *         member1_.email as email1_1_1_,
         *         board0_.moddate as moddate2_0_0_,
         *         board0_.regdate as regdate3_0_0_,
         *         board0_.content as content4_0_0_,
         *         board0_.title as title5_0_0_,
         *         board0_.writer_email as writer_e6_0_0_,
         *         member1_.moddate as moddate2_1_1_,
         *         member1_.regdate as regdate3_1_1_,
         *         member1_.name as name4_1_1_,
         *         member1_.password as password5_1_1_
         *     from
         *         board board0_
         *     left outer join
         *         member member1_
         *             on board0_.writer_email=member1_.email
         *     left outer join
         *         reply reply2_
         *             on (
         *                 reply2_.board_bno=board0_.bno
         *             )
         *     where
         *         board0_.bno=?
         * [Board(bno=14, title=Title...14, content=Content....14), Member(email=user14@aaa.com, password=1111, name=USER14), 2]
         */
    }

    //    @Test
    public void testSearch1() {
        boardRepository.search1();
        /**
         * 2021-01-17 00:43:35.906  INFO 9176 --- [    Test worker] c.z.b.r.s.SearchBoardRepositoryImpl      : search1...
         * 2021-01-17 00:43:35.994  INFO 9176 --- [    Test worker] c.z.b.r.s.SearchBoardRepositoryImpl      : ------------------------
         * 2021-01-17 00:43:35.998  INFO 9176 --- [    Test worker] c.z.b.r.s.SearchBoardRepositoryImpl      :
         * select board, member1.email, count(reply)
         * from Board board
         *   left join Member member1 with board.writer = member1
         *   left join Reply reply with reply.board = board
         * group by board
         * 2021-01-17 00:43:35.999  INFO 9176 --- [    Test worker] c.z.b.r.s.SearchBoardRepositoryImpl      : ------------------------
         * Hibernate:
         *     select
         *         board0_.bno as col_0_0_,
         *         member1_.email as col_1_0_,
         *         count(reply2_.rno) as col_2_0_,
         *         board0_.bno as bno1_0_,
         *         board0_.moddate as moddate2_0_,
         *         board0_.regdate as regdate3_0_,
         *         board0_.content as content4_0_,
         *         board0_.title as title5_0_,
         *         board0_.writer_email as writer_e6_0_
         *     from
         *         board board0_
         *     left outer join
         *         member member1_
         *             on (
         *                 board0_.writer_email=member1_.email
         *             )
         *     left outer join
         *         reply reply2_
         *             on (
         *                 reply2_.board_bno=board0_.bno
         *             )
         *     group by
         *         board0_.bno
         * 2021-01-17 00:43:36.343  INFO 9176 --- [    Test worker] c.z.b.r.s.SearchBoardRepositoryImpl      :
         * [
         *  [Board(bno=2, title=2번 제목 변경, content=2번 내용 변경), user2@aaa.com, 1],
         *  [Board(bno=3, title=Title...3, content=Content....3), user3@aaa.com, 5],
         *  [Board(bno=4, title=Title...4, content=Content....4), user4@aaa.com, 2],
         *  [Board(bno=5, title=Title...5, content=Content....5), user5@aaa.com, 2],
         *  [Board(bno=6, title=Title...6, content=Content....6), user6@aaa.com, 4],
         *  [Board(bno=7, title=Title...7, content=Content....7), user7@aaa.com, 2],
         *  [Board(bno=8, title=Title...8, content=Content....8), user8@aaa.com, 1],
         *  [Board(bno=9, title=Title...9, content=Content....9), user9@aaa.com, 4],
         *  [Board(bno=10, title=Title...10, content=Content....10), user10@aaa.com, 2],
         *  [Board(bno=11, title=Title...11, content=Content....11), user11@aaa.com, 3],
         *  [Board(bno=12, title=Title...12, content=Content....12), user12@aaa.com, 3],
         *  [Board(bno=13, title=Title...13, content=Content....13), user13@aaa.com, 2],
         *  [Board(bno=14, title=Title...14, content=Content....14), user14@aaa.com, 2],
         *  [Board(bno=15, title=Title...15, content=Content....15), user15@aaa.com, 3],
         *  [Board(bno=16, title=Title...16, content=Content....16), user16@aaa.com, 2],
         *  [Board(bno=17, title=Title...17, content=Content....17), user17@aaa.com, 3],
         *  [Board(bno=18, title=Title...18, content=Content....18), user18@aaa.com, 4],
         *  [Board(bno=19, title=Title...19, content=Content....19), user19@aaa.com, 7],
         *  [Board(bno=20, title=Title...20, content=Content....20), user20@aaa.com, 1],
         *  [Board(bno=21, title=Title...21, content=Content....21), user21@aaa.com, 1],
         *  [Board(bno=22, title=Title...22, content=Content....22), user22@aaa.com, 2],
         *  [Board(bno=23, title=Title...23, content=Content....23), user23@aaa.com, 5],
         *  [Board(bno=24, title=Title...24, content=Content....24), user24@aaa.com, 3],
         *  [Board(bno=25, title=Title...25, content=Content....25), user25@aaa.com, 2],
         *  [Board(bno=26, title=Title...26, content=Content....26), user26@aaa.com, 2],
         *  [Board(bno=27, title=Title...27, content=Content....27), user27@aaa.com, 3],
         *  [Board(bno=28, title=Title...28, content=Content....28), user28@aaa.com, 3],
         *  [Board(bno=29, title=Title...29, content=Content....29), user29@aaa.com, 0],
         *  [Board(bno=30, title=Title...30, content=Content....30), user30@aaa.com, 6],
         *  [Board(bno=31, title=Title...31, content=Content....31), user31@aaa.com, 5],
         *  [Board(bno=32, title=Title...32, content=Content....32), user32@aaa.com, 3],
         *  [Board(bno=33, title=Title...33, content=Content....33), user33@aaa.com, 2],
         *  [Board(bno=34, title=Title...34, content=Content....34), user34@aaa.com, 4],
         *  [Board(bno=35, title=Title...35, content=Content....35), user35@aaa.com, 0],
         *  [Board(bno=36, title=Title...36, content=Content....36), user36@aaa.com, 2],
         *  [Board(bno=37, title=Title...37, content=Content....37), user37@aaa.com, 4],
         *  [Board(bno=38, title=Title...38, content=Content....38), user38@aaa.com, 4],
         *  [Board(bno=39, title=Title...39, content=Content....39), user39@aaa.com, 2],
         *  [Board(bno=40, title=Title...40, content=Content....40), user40@aaa.com, 2],
         *  [Board(bno=41, title=Title...41, content=Content....41), user41@aaa.com, 3],
         *  [Board(bno=42, title=Title...42, content=Content....42), user42@aaa.com, 3],
         *  [Board(bno=43, title=Title...43, content=Content....43), user43@aaa.com, 6],
         *  [Board(bno=44, title=Title...44, content=Content....44), user44@aaa.com, 2],
         *  [Board(bno=45, title=Title...45, content=Content....45), user45@aaa.com, 6],
         *  [Board(bno=46, title=Title...46, content=Content....46), user46@aaa.com, 1],
         *  [Board(bno=47, title=Title...47, content=Content....47), user47@aaa.com, 7],
         *  [Board(bno=48, title=Title...48, content=Content....48), user48@aaa.com, 1],
         *  [Board(bno=49, title=Title...49, content=Content....49), user49@aaa.com, 4],
         *  [Board(bno=50, title=Title...50, content=Content....50), user50@aaa.com, 3],
         *  [Board(bno=51, title=Title...51, content=Content....51), user51@aaa.com, 9],
         *  [Board(bno=52, title=Title...52, content=Content....52), user52@aaa.com, 1],
         *  [Board(bno=53, title=Title...53, content=Content....53), user53@aaa.com, 1],
         *  [Board(bno=54, title=Title...54, content=Content....54), user54@aaa.com, 2],
         *  [Board(bno=55, title=Title...55, content=Content....55), user55@aaa.com, 2],
         *  [Board(bno=56, title=Title...56, content=Content....56), user56@aaa.com, 3],
         *  [Board(bno=57, title=Title...57, content=Content....57), user57@aaa.com, 1],
         *  [Board(bno=58, title=Title...58, content=Content....58), user58@aaa.com, 3],
         *  [Board(bno=59, title=Title...59, content=Content....59), user59@aaa.com, 2],
         *  [Board(bno=60, title=Title...60, content=Content....60), user60@aaa.com, 6],
         *  [Board(bno=62, title=Title...62, content=Content....62), user62@aaa.com, 4],
         *  [Board(bno=63, title=Title...63, content=Content....63), user63@aaa.com, 3],
         *  [Board(bno=64, title=Title...64, content=Content....64), user64@aaa.com, 3],
         *  [Board(bno=65, title=Title...65, content=Content....65), user65@aaa.com, 4],
         *  [Board(bno=66, title=Title...66, content=Content....66), user66@aaa.com, 4],
         *  [Board(bno=67, title=Title...67, content=Content....67), user67@aaa.com, 3],
         *  [Board(bno=68, title=Title...68, content=Content....68), user68@aaa.com, 2],
         *  [Board(bno=69, title=Title...69, content=Content....69), user69@aaa.com, 6],
         *  [Board(bno=70, title=Title...70, content=Content....70), user70@aaa.com, 3],
         *  [Board(bno=71, title=Title...71, content=Content....71), user71@aaa.com, 2],
         *  [Board(bno=72, title=Title...72, content=Content....72), user72@aaa.com, 2],
         *  [Board(bno=73, title=Title...73, content=Content....73), user73@aaa.com, 2],
         *  [Board(bno=74, title=Title...74, content=Content....74), user74@aaa.com, 2],
         *  [Board(bno=75, title=Title...75, content=Content....75), user75@aaa.com, 2],
         *  [Board(bno=76, title=Title...76, content=Content....76), user76@aaa.com, 1],
         *  [Board(bno=77, title=Title...77, content=Content....77), user77@aaa.com, 2],
         *  [Board(bno=78, title=Title...78, content=Content....78), user78@aaa.com, 1],
         *  [Board(bno=79, title=Title...79, content=Content....79), user79@aaa.com, 5],
         *  [Board(bno=80, title=Title...80, content=Content....80), user80@aaa.com, 6],
         *  [Board(bno=81, title=Title...81, content=Content....81), user81@aaa.com, 3],
         *  [Board(bno=82, title=Title...82, content=Content....82), user82@aaa.com, 6],
         *  [Board(bno=83, title=Title...83, content=Content....83), user83@aaa.com, 3],
         *  [Board(bno=84, title=Title...84, content=Content....84), user84@aaa.com, 3],
         *  [Board(bno=85, title=Title...85, content=Content....85), user85@aaa.com, 3],
         *  [Board(bno=86, title=Title...86, content=Content....86), user86@aaa.com, 4],
         *  [Board(bno=87, title=Title...87, content=Content....87), user87@aaa.com, 3],
         *  [Board(bno=88, title=Title...88, content=Content....88), user88@aaa.com, 6],
         *  [Board(bno=89, title=Title...89, content=Content....89), user89@aaa.com, 2],
         *  [Board(bno=90, title=Title...90, content=Content....90), user90@aaa.com, 3],
         *  [Board(bno=91, title=Title...91, content=Content....91), user91@aaa.com, 0],
         *  [Board(bno=92, title=Title...92, content=Content....92), user92@aaa.com, 0],
         *  [Board(bno=93, title=Title...93, content=Content....93), user93@aaa.com, 4],
         *  [Board(bno=94, title=Title...94, content=Content....94), user94@aaa.com, 7],
         *  [Board(bno=95, title=Title...95, content=Content....95), user95@aaa.com, 3],
         *  [Board(bno=96, title=Title...96, content=Content....96), user96@aaa.com, 2],
         *  [Board(bno=97, title=Title...97, content=Content....97), user97@aaa.com, 4],
         *  [Board(bno=98, title=Title...98, content=Content....98), user98@aaa.com, 3],
         *  [Board(bno=99, title=Title...99, content=Content....99), user99@aaa.com, 1],
         *  [Board(bno=100, title=Title...100, content=Content....100), user100@aaa.com, 1],
         *  [Board(bno=101, title=Test Title, content=Test Content), user55@aaa.com, 0],
         *  [Board(bno=102, title=한글테스트, content=테스트본문), user30@aaa.com, 0]
         * ]
         */
    }

    @Test
    public void testSearchPage() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("bno").descending().and(Sort.by("title").ascending()));
        /**
         * 제목(t)에 1이 포함된 데이터 검색
         */
        Page<Object[]> result = boardRepository.searchPage("t", "1", pageable);

        /**
         * 2021-01-17 01:32:31.328  INFO 7236 --- [    Test worker] c.z.b.r.s.SearchBoardRepositoryImpl      : searchPage...
         * prop - bno
         * prop - title
         * Hibernate:
         *     select
         *         board0_.bno as col_0_0_,
         *         member1_.email as col_1_0_,
         *         count(reply2_.rno) as col_2_0_,
         *         board0_.bno as bno1_0_0_,
         *         member1_.email as email1_1_1_,
         *         board0_.moddate as moddate2_0_0_,
         *         board0_.regdate as regdate3_0_0_,
         *         board0_.content as content4_0_0_,
         *         board0_.title as title5_0_0_,
         *         board0_.writer_email as writer_e6_0_0_,
         *         member1_.moddate as moddate2_1_1_,
         *         member1_.regdate as regdate3_1_1_,
         *         member1_.name as name4_1_1_,
         *         member1_.password as password5_1_1_
         *     from
         *         board board0_
         *     left outer join
         *         member member1_
         *             on (
         *                 board0_.writer_email=member1_.email
         *             )
         *     left outer join
         *         reply reply2_
         *             on (
         *                 reply2_.board_bno=board0_.bno
         *             )
         *     where
         *         board0_.bno>?
         *         and (
         *             board0_.title like ? escape '!'
         *         )
         *     group by
         *         board0_.bno
         *     order by
         *         board0_.bno desc,
         *         board0_.title asc limit ?
         * 2021-01-17 01:32:31.751  INFO 7236 --- [    Test worker] c.z.b.r.s.SearchBoardRepositoryImpl      : [[Board(bno=100, title=Title...100, content=Content....100), Member(email=user100@aaa.com, password=1111, name=USER100), 1], [Board(bno=91, title=Title...91, content=Content....91), Member(email=user91@aaa.com, password=1111, name=USER91), 0], [Board(bno=81, title=Title...81, content=Content....81), Member(email=user81@aaa.com, password=1111, name=USER81), 3], [Board(bno=71, title=Title...71, content=Content....71), Member(email=user71@aaa.com, password=1111, name=USER71), 2], [Board(bno=51, title=Title...51, content=Content....51), Member(email=user51@aaa.com, password=1111, name=USER51), 9], [Board(bno=41, title=Title...41, content=Content....41), Member(email=user41@aaa.com, password=1111, name=USER41), 3], [Board(bno=31, title=Title...31, content=Content....31), Member(email=user31@aaa.com, password=1111, name=USER31), 5], [Board(bno=21, title=Title...21, content=Content....21), Member(email=user21@aaa.com, password=1111, name=USER21), 1], [Board(bno=19, title=Title...19, content=Content....19), Member(email=user19@aaa.com, password=1111, name=USER19), 7], [Board(bno=18, title=Title...18, content=Content....18), Member(email=user18@aaa.com, password=1111, name=USER18), 4]]
         * Hibernate:
         *     select
         *         count(distinct board0_.bno) as col_0_0_
         *     from
         *         board board0_
         *     left outer join
         *         member member1_
         *             on (
         *                 board0_.writer_email=member1_.email
         *             )
         *     left outer join
         *         reply reply2_
         *             on (
         *                 reply2_.board_bno=board0_.bno
         *             )
         *     where
         *         board0_.bno>?
         *         and (
         *             board0_.title like ? escape '!'
         *         )
         * 2021-01-17 01:32:31.767  INFO 7236 --- [    Test worker] c.z.b.r.s.SearchBoardRepositoryImpl      : COUNT : 18
         */
    }
}