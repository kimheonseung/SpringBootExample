package com.devheon.springboot.example.service;

import com.devheon.springboot.example.dto.NoteDTO;
import com.devheon.springboot.example.entity.ClubMember;
import com.devheon.springboot.example.entity.Note;

import java.util.List;

public interface NoteService {
    Long register(NoteDTO noteDTO);
    NoteDTO get(Long num);
    void modify(NoteDTO noteDTO);
    void remove(Long num);
    List<NoteDTO> getAllWithWriter(String writerEmail);

    default Note dtoToEntiry(NoteDTO noteDTO) {
        Note note = Note.builder()
                .num(noteDTO.getNum())
                .title(noteDTO.getTitle())
                .content(noteDTO.getContent())
                .writer(ClubMember.builder().email(noteDTO.getWriterEmail()).build())
                .build();
        return note;
    }

    default NoteDTO entityToDTO(Note note) {
        NoteDTO noteDTO = NoteDTO.builder()
                .num(note.getNum())
                .title(note.getTitle())
                .content(note.getContent())
                .writerEmail(note.getWriter().getEmail())
                .regDate(note.getRegDate())
                .modDate(note.getModDate())
                .build();
        return noteDTO;
    }
}
