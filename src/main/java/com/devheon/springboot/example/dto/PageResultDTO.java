package com.devheon.springboot.example.dto;

import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
public class PageResultDTO<DTO, EN> {
    /**
     * 페이지 결과 처리
     * 다양한 곳에서 사용할 수 있도록 제네릭 타입 (DTO, EN)을 지정한다.
     */

    private List<DTO> dtoList;

    /**
     * 화면에서 시작페이지(start)
     * 화면에서 끝페이지(end)
     * 이전.다음 이동 링크 여부(prev, next)
     * 현재 페이지(page)
     *
     * - 임시 끝번호
     *   tempEnd = (int)(Math.ceil(페이지번호/10.0)) * 10
     *   전체 데이터수가 적다면 10페이지로 끝나면 안됨..
     *   end를 먼저 계산하는 것은 start를 계산하기 쉽기 때문이다.
     * - 시작번호
     *   start = tempEnd - 9
     * - 끝번호
     *   만약 마지막 페이지가 33이면 tempEnd는 40이 된다.
     *   이를 위해 Page<Guestbook>의 getTotalPages()를 이용한다.
     *   totalPage = result.getTotalPages(); // result는 Page<Guestbook>
     *   end = totalPage > tempEnd ? tempEnd : totalPage;
     * - 이전
     *   시작번호가 1보다 큰경우
     *   prev = start > 1;
     * - 다음
     *   realEnd가 끝번호(endPage)보다 큰 경우
     *   next = totalPage > tempEnd
     */

    /* 총 페이지 번호 */
    private int totalPage;

    /* 현재 페이지 번호 */
    private int page;
    /* 목록 사이즈 */
    private int size;
    /* 시작 페이지 번호, 끝 페이지 번호 */
    private int start, end;
    /* 이전, 다음 */
    private boolean prev, next;

    /* 페이지 번호 목록 */
    private List<Integer> pageList;


    public PageResultDTO(Page<EN> result, Function<EN, DTO> fn) {
        /**
         * fn은 엔티티를 DTO로 변환해주는 기능이다.
         */
        dtoList = result.stream().map(fn).collect(Collectors.toList());
        this.totalPage = result.getTotalPages();
        makePageList(result.getPageable());
    }

    private void makePageList(Pageable pageable) {
        this.page = pageable.getPageNumber() + 1;    /* 0부터 시작하므로 1을 추가 */
        this.size = pageable.getPageSize();

        /* temp end page */
        int tempEnd = (int)(Math.ceil(page/10.0)) * 10;

        this.start = tempEnd - 9;

        this.prev = this.start > 1;
        this.next = this.totalPage > tempEnd;

        this.end = this.totalPage > tempEnd ? tempEnd : this.totalPage;

        this.pageList = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());

    }
}
