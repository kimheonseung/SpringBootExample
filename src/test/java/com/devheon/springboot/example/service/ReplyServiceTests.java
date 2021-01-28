package com.devheon.springboot.example.service;

import com.devheon.springboot.example.dto.ReplyDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class ReplyServiceTests {

    @Autowired
    private ReplyService service;

    @Test
    public void testGetList() {
        Long bno = 97L;
        List<ReplyDTO> replyDTOList = service.getList(bno);
        replyDTOList.forEach(replyDTO -> System.out.println(replyDTO));

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
         * ReplyDTO(rno=65, text=Reply...65, replyer=guest, bno=null, regDate=2021-01-05T23:44:37.260778, modDate=2021-01-05T23:44:37.260778)
         * ReplyDTO(rno=119, text=Reply...119, replyer=guest, bno=null, regDate=2021-01-05T23:44:37.830240, modDate=2021-01-05T23:44:37.830240)
         * ReplyDTO(rno=199, text=Reply...199, replyer=guest, bno=null, regDate=2021-01-05T23:44:38.653526, modDate=2021-01-05T23:44:38.653526)
         * ReplyDTO(rno=262, text=Reply...262, replyer=guest, bno=null, regDate=2021-01-05T23:44:39.346606, modDate=2021-01-05T23:44:39.346606)
         */
    }
}