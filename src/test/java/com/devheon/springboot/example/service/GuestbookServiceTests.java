package com.devheon.springboot.example.service;

import com.devheon.springboot.example.dto.GuestbookDTO;
import com.devheon.springboot.example.dto.PageRequestDTO;
import com.devheon.springboot.example.dto.PageResultDTO;
import com.devheon.springboot.example.entity.Guestbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class GuestbookServiceTests {
    @Autowired
    private GuestbookService service;

    //    @Test
    public void testRegister() {
        GuestbookDTO guestbookDTO = GuestbookDTO.builder()
                .title("Sample Title...")
                .content("Sample Content...")
                .writer("user0")
                .build();

        System.out.println(service.register(guestbookDTO));

        /**
         * DTO------------------------
         * GuestbookDTO(gno=null, title=Sample Title..., content=Sample Content..., writer=user0, regDate=null, modDate=null)
         * Guestbook(gno=null, title=Sample Title..., content=Sample Content..., writer=user0)
         */
    }

    //    @Test
    public void testList() {
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder().page(1).size(10).build();
        PageResultDTO<GuestbookDTO, Guestbook> resultDTO = service.getList(pageRequestDTO);


        System.out.println("PREV  : "+resultDTO.isPrev());
        System.out.println("NEXT  : "+resultDTO.isNext());
        System.out.println("TOTAL : "+resultDTO.getTotalPage());

        System.out.println("----------------------------------");
        for(GuestbookDTO guestbookDTO : resultDTO.getDtoList()) {
            System.out.println(guestbookDTO);
        }

        System.out.println("==================================");
        resultDTO.getPageList().forEach(i -> System.out.println(i));

    }

    @Test
    public void testSearch() {
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .page(1)
                .size(10)
                .type("tc")
                .keyword("한글")
                .build();

        PageResultDTO<GuestbookDTO, Guestbook> resultDTO = service.getList(pageRequestDTO);

        System.out.println("PREV  : " + resultDTO.isPrev());
        System.out.println("NEXT  : " + resultDTO.isNext());
        System.out.println("TOTAL : " + resultDTO.getTotalPage());

        System.out.println("--------------------------------------");
        for(GuestbookDTO guestbookDTO : resultDTO.getDtoList()) {
            System.out.println(guestbookDTO);
        }
        System.out.println("======================================");
        resultDTO.getPageList().forEach(i -> System.out.println(i));
    }
}