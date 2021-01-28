package com.devheon.springboot.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Builder
@AllArgsConstructor
@Data
public class PageRequestDTO {
    /**
     * 페이지 요청 처리
     */
    private int page;
    private int size;

    /**
     * 검색 처리
     * - 제목(t), 내용(c), 작성자(w)
     */
    private String type;
    private String keyword;

    public PageRequestDTO() {
        /* 기본값 */
        this.page = 1;
        this.size = 10;
    }

    /**
     * JPA 쪽에서 사용하는 Pageable 객체 생성
     * @param sort
     * @return
     */
    public Pageable getPageable(Sort sort) {
        return PageRequest.of(page - 1, size, sort);
    }
}
