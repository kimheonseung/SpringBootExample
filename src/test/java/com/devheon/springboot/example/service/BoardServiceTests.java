package com.devheon.springboot.example.service;

import com.devheon.springboot.example.dto.BoardDTO;
import com.devheon.springboot.example.dto.PageRequestDTO;
import com.devheon.springboot.example.dto.PageResultDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BoardServiceTests {

    @Autowired
    private BoardServiceImpl boardService;

    //    @Test
    public void testRegister() {
        BoardDTO dto = BoardDTO.builder()
                .title("Test Title")
                .content("Test Content")
                .writerEmail("user55@aaa.com")
                .build();

        Long bno = boardService.register(dto);

        /**
         * Hibernate:
         *     select
         *         member_.email,
         *         member_.moddate as moddate2_1_,
         *         member_.name as name4_1_,
         *         member_.password as password5_1_
         *     from
         *         member member_
         *     where
         *         member_.email=?
         * Hibernate:
         *     insert
         *     into
         *         board
         *         (moddate, regdate, content, title, writer_email)
         *     values
         *         (?, ?, ?, ?, ?)
         */
    }

    //    @Test
    public void testList() {
        PageRequestDTO pageRequestDTO = new PageRequestDTO();

        PageResultDTO<BoardDTO, Object[]> result = boardService.getList(pageRequestDTO);
        for(BoardDTO boardDTO : result.getDtoList()) {
            System.out.println(boardDTO);
        }
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
         * BoardDTO(bno=101, title=Test Title, content=Test Content, writerEmail=user55@aaa.com, writerName=USER55, regDate=2021-01-07T23:12:02.483305, modDate=2021-01-07T23:12:02.483305, replyCount=0)
         * BoardDTO(bno=100, title=Title...100, content=Content....100, writerEmail=user100@aaa.com, writerName=USER100, regDate=2021-01-05T22:40:55.870226, modDate=2021-01-05T22:40:55.870226, replyCount=1)
         * BoardDTO(bno=99, title=Title...99, content=Content....99, writerEmail=user99@aaa.com, writerName=USER99, regDate=2021-01-05T22:40:55.859226, modDate=2021-01-05T22:40:55.859226, replyCount=1)
         * BoardDTO(bno=98, title=Title...98, content=Content....98, writerEmail=user98@aaa.com, writerName=USER98, regDate=2021-01-05T22:40:55.845226, modDate=2021-01-05T22:40:55.845226, replyCount=3)
         * BoardDTO(bno=97, title=Title...97, content=Content....97, writerEmail=user97@aaa.com, writerName=USER97, regDate=2021-01-05T22:40:55.830374, modDate=2021-01-05T22:40:55.830374, replyCount=4)
         * BoardDTO(bno=96, title=Title...96, content=Content....96, writerEmail=user96@aaa.com, writerName=USER96, regDate=2021-01-05T22:40:55.816377, modDate=2021-01-05T22:40:55.816377, replyCount=2)
         * BoardDTO(bno=95, title=Title...95, content=Content....95, writerEmail=user95@aaa.com, writerName=USER95, regDate=2021-01-05T22:40:55.801328, modDate=2021-01-05T22:40:55.801328, replyCount=3)
         * BoardDTO(bno=94, title=Title...94, content=Content....94, writerEmail=user94@aaa.com, writerName=USER94, regDate=2021-01-05T22:40:55.786320, modDate=2021-01-05T22:40:55.786320, replyCount=7)
         * BoardDTO(bno=93, title=Title...93, content=Content....93, writerEmail=user93@aaa.com, writerName=USER93, regDate=2021-01-05T22:40:55.772320, modDate=2021-01-05T22:40:55.772320, replyCount=4)
         * BoardDTO(bno=92, title=Title...92, content=Content....92, writerEmail=user92@aaa.com, writerName=USER92, regDate=2021-01-05T22:40:55.757321, modDate=2021-01-05T22:40:55.757321, replyCount=0)
         */
    }

    //    @Test
    public void testGet() {
        Long bno = 100L;
        BoardDTO boardDTO = boardService.get(bno);
        System.out.println(boardDTO);

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
         *     where
         *         board0_.bno=?
         * BoardDTO(bno=100, title=Title...100, content=Content....100, writerEmail=user100@aaa.com, writerName=USER100, regDate=2021-01-05T22:40:55.870226, modDate=2021-01-05T22:40:55.870226, replyCount=1)
         */
    }

    //    @Test
    public void testRemove() {
        Long bno = 1L;
        boardService.removeWithReplies(bno);

        /**
         * Hibernate:
         *     delete
         *     from
         *         reply
         *     where
         *         board_bno=?
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
         * Hibernate:
         *     delete
         *     from
         *         board
         *     where
         *         bno=?
         */
    }

    @Test
    public void testModify() {
        BoardDTO boardDTO = BoardDTO.builder()
                .bno(2L)
                .title("2번 제목 변경")
                .content("2번 내용 변경")
                .build();

        boardService.modify(boardDTO);
    }
}