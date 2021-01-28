package com.devheon.springboot.example.service;

import com.devheon.springboot.example.dto.GuestbookDTO;
import com.devheon.springboot.example.dto.PageRequestDTO;
import com.devheon.springboot.example.dto.PageResultDTO;
import com.devheon.springboot.example.entity.Guestbook;
import com.devheon.springboot.example.entity.QGuestbook;
import com.devheon.springboot.example.repository.GuestbookRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Function;

@Service
@Log4j2
@RequiredArgsConstructor    /* 의존성 자동 주입 */
public class GuestbookServiceImpl implements GuestbookService {

    private final GuestbookRepository repository;

    @Override
    public Long register(GuestbookDTO dto) {
        log.info("DTO------------------------");
        log.info(dto);
        Guestbook entity = dtoToEntity(dto);
        log.info(entity);
        repository.save(entity);
        return entity.getGno();
    }

    @Override
    public PageResultDTO<GuestbookDTO, Guestbook> getList(PageRequestDTO requestDTO) {
        Pageable pageable = requestDTO.getPageable(Sort.by("gno").descending());

        /* 검색조건 처리 */
        BooleanBuilder booleanBuilder = getSearch(requestDTO);
        Page<Guestbook> result = repository.findAll(booleanBuilder, pageable);

        Function<Guestbook, GuestbookDTO> fn = (entity -> entityToDto(entity) );

        return new PageResultDTO<>(result, fn);
    }

    @Override
    public GuestbookDTO read(Long gno) {
        Optional<Guestbook> result = repository.findById(gno);
        return result.isPresent() ? entityToDto(result.get()) : null;
    }

    @Override
    public void remove(Long gno) {
        repository.deleteById(gno);
    }

    @Override
    public void modify(GuestbookDTO dto) {
        /**
         * 업데이트 하는 항목은 '제목', '내용'
         */
        Optional<Guestbook> result = repository.findById(dto.getGno());
        if(result.isPresent()) {
            Guestbook entity = result.get();

            entity.changeTitle(dto.getTitle());
            entity.changeContent(dto.getContent());

            repository.save(entity);
        }
    }

    /**
     * 검색 관련 메소드 (Querydsl)
     */
    private BooleanBuilder getSearch(PageRequestDTO requestDTO) {
        String type = requestDTO.getType();

        QGuestbook qGuestbook = QGuestbook.guestbook;

        String keyword = requestDTO.getKeyword();

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        /* gno > 0 */
        BooleanExpression expression = qGuestbook.gno.gt(0L);
        booleanBuilder.and(expression);

        /* 검색 조건이 없는 경우 */
        if(type == null || type.trim().length() == 0)
            return booleanBuilder;

        BooleanBuilder conditionBuilder = new BooleanBuilder();
        if(type.contains("t"))
            conditionBuilder.or(qGuestbook.title.contains(keyword));
        if(type.contains("c"))
            conditionBuilder.or(qGuestbook.content.contains(keyword));
        if(type.contains("w"))
            conditionBuilder.or(qGuestbook.writer.contains(keyword));

        booleanBuilder.and(conditionBuilder);

        return booleanBuilder;
    }
}