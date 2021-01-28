package com.devheon.springboot.example.service;

import com.devheon.springboot.example.dto.BoardDTO;
import com.devheon.springboot.example.dto.PageRequestDTO;
import com.devheon.springboot.example.dto.PageResultDTO;
import com.devheon.springboot.example.entity.Board;
import com.devheon.springboot.example.entity.Member;
import com.devheon.springboot.example.repository.BoardRepository;
import com.devheon.springboot.example.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Log4j2
public class BoardServiceImpl implements BoardService {
    /* 자동주입 final */
    private final BoardRepository repository;

    /* 댓글 관련 기능 레파지토리 */
    private final ReplyRepository replyRepository;

    @Override
    public Long register(BoardDTO dto) {
        log.info(dto);
        Board board = dtoToEntity(dto);
        repository.save(board);
        return board.getBno();
    }

    @Override
    public PageResultDTO<BoardDTO, Object[]> getList(PageRequestDTO pageRequestDTO) {
        log.info(pageRequestDTO);

        Function<Object[], BoardDTO> fn = (en -> entityToDTO((Board) en[0], (Member) en[1], (Long) en[2]));

//        Page<Object[]> result = repository.getBoardWithReplyCount(pageRequestDTO.getPageable(Sort.by("bno").descending()));

        Page<Object[]> result = repository.searchPage(
                pageRequestDTO.getType(),
                pageRequestDTO.getKeyword(),
                pageRequestDTO.getPageable(Sort.by("bno").descending())
        );
        return new PageResultDTO<>(result, fn);
    }

    @Override
    public BoardDTO get(Long bno) {
        Object result = repository.getBoardByBno(bno);
        Object[] arr = (Object[]) result;

        return entityToDTO((Board) arr[0], (Member) arr[1], (Long) arr[2]);
    }

    /**
     * 게시물 삭제처리
     * - 보편적으로는 게시물의 상태(state) 컬럼의 값에 따라 삭제 여부를 판단한다.
     * - 예제에서는 다음 순서로 삭제 처리를 한다.
     * 1) 게시물 FK를 참조하는 모든 reply를 삭제한다.
     * 2) 게시물을 삭제한다.
     * -> 두 작업은 트랜잭션으로 처리되어야 한다.
     */
    @Transactional
    @Override
    public void removeWithReplies(Long bno) {
        /* 댓글부터 삭제 */
        replyRepository.deleteByBno(bno);
        repository.deleteById(bno);
    }

    /**
     * 게시물 수정처리
     */
    @Transactional
    @Override
    public void modify(BoardDTO boardDTO) {
        System.out.println(boardDTO);
        /**
         * findById 대신 필요 순간까지 로딩을 지연하는 getOne 이용
         * 객체는 항상 반환하며, 레코드가 없다면 EntityNotFoundException 발생
         */
        Board board = repository.getOne(boardDTO.getBno());
        System.out.println(board);

        board.changeTitle(boardDTO.getTitle());
        board.changeContent(boardDTO.getContent());
        repository.save(board);

        /**
         * BoardDTO(bno=2, title=2번 제목 변경, content=2번 내용 변경, writerEmail=null, writerName=null, regDate=null, modDate=null, replyCount=0)
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
         * Board(bno=2, title=Title...2, content=Content....2)
         * Hibernate:
         *     update
         *         board
         *     set
         *         moddate=?,
         *         content=?,
         *         title=?,
         *         writer_email=?
         *     where
         *         bno=?
         */
    }
}
