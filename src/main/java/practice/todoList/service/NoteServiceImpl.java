package practice.todoList.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practice.todoList.Exception.UnAuthorizedRequestException;
import practice.todoList.domain.Note;
import practice.todoList.domain.Plan;
import practice.todoList.domain.WeekDay;
import practice.todoList.repository.NoteRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {
    private final NoteRepository noteRepository;

    @Override
    public void registerNote(String requestUserId, Note note) {
        note.setUserId(requestUserId);
        noteRepository.save(note);

    }

    @Override
    public Note modifyNote(String requestUserId, Note note) {
        int id = note.getId();
        String newText = note.getText();

        Note asIsNote = noteRepository.findById(id).get();
        if (asIsNote.getUserId() != requestUserId) {
            throw new UnAuthorizedRequestException("Unauthorized Request");
        }
        asIsNote.setText(newText);
        return asIsNote;
    }

    @Override
    public void deleteNote(String requestUserId, int id) {
        if (noteRepository.findById(id).get().getUserId() != requestUserId) {
            throw new UnAuthorizedRequestException("Unauthorized Request");
        }
        noteRepository.deleteById(id);
    }

}
