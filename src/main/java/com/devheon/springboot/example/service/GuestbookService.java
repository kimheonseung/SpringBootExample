package com.devheon.springboot.example.service;

import com.devheon.springboot.example.dto.GuestbookDTO;
import com.devheon.springboot.example.dto.PageRequestDTO;
import com.devheon.springboot.example.dto.PageResultDTO;
import com.devheon.springboot.example.entity.Guestbook;

public interface GuestbookService {
    Long register(GuestbookDTO dto);

    /**
     * PageRequestDTO를 파라미터로, PageResultDTO를 리턴타입으로 사용
     */
    PageResultDTO<GuestbookDTO, Guestbook> getList(PageRequestDTO requestDTO);

    GuestbookDTO read(Long gno);

    void remove(Long gno);
    void modify(GuestbookDTO dto);

    default GuestbookDTO entityToDto(Guestbook entity) {
        GuestbookDTO dto = GuestbookDTO.builder()
                .gno(entity.getGno())
                .title(entity.getTitle())
                .content(entity.getContent())
                .writer(entity.getWriter())
                .regDate(entity.getRegDate())
                .modDate(entity.getModDate())
                .build();
        return dto;
    }

    default Guestbook dtoToEntity(GuestbookDTO dto) {
        Guestbook entity = Guestbook.builder()
                .gno(dto.getGno())
                .title(dto.getTitle())
                .content(dto.getContent())
                .writer(dto.getWriter())
                .build();
        return entity;
    }


}