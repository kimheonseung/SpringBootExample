package com.devheon.springboot.example.service;

import com.devheon.springboot.example.dto.BoardDTO;
import com.devheon.springboot.example.dto.PageRequestDTO;
import com.devheon.springboot.example.dto.PageResultDTO;
import com.devheon.springboot.example.entity.Board;
import com.devheon.springboot.example.entity.Member;

public interface BoardService {
    Long register(BoardDTO dto);

    /* 목록처리 */
    PageResultDTO<BoardDTO, Object[]> getList(PageRequestDTO pageRequestDTO);

    /* 조회처리 */
    BoardDTO get(Long bno);

    /* 댓글 삭제처리 */
    void removeWithReplies(Long bno);

    /* 게시글 수정 */
    void modify(BoardDTO boardDTO);

    default Board dtoToEntity(BoardDTO dto) {
        Member member = Member.builder()
                .email(dto.getWriterEmail())
                .build();

        Board board = Board.builder()
                .bno(dto.getBno())
                .title(dto.getTitle())
                .content(dto.getContent())
                .writer(member)
                .build();

        return board;
    }

    /**
     * Object[]를 DTO로 변환하기
     */
    default BoardDTO entityToDTO(Board board, Member member, Long replyCount) {
        BoardDTO boardDTO = BoardDTO.builder()
                .bno(board.getBno())
                .title(board.getTitle())
                .content(board.getContent())
                .regDate(board.getRegDate())
                .modDate(board.getModDate())
                .writerEmail(member.getEmail())
                .writerName(member.getName())
                .replyCount(replyCount.intValue())
                .build();

        return boardDTO;
    }
}
