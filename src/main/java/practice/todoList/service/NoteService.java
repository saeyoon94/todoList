package practice.todoList.service;

import practice.todoList.domain.Note;
import practice.todoList.domain.Plan;
import practice.todoList.domain.WeekDay;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;


public interface NoteService {

    //public void registerPlan(Map<String, ?> newPlan);
    public void registerNote(String requestUserId, Note note);

    public Note modifyNote(String requestUserId, Note note);

    public void deleteNote(String requestUserId, int id);

}
