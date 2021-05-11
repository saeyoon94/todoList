package practice.todoList.repository;

import practice.todoList.domain.Note;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface NoteRepository {
    Note save(Note note);
    Optional<Note> findById(int id);
    List<Note> findAll();
    List<Note> findByUserIdAndDate(String userId, LocalDate date);



    //Optional<Plan> findByUserIdAndPlan(String plan, String userId);

    //List<Plan> findAllByPlanAndDateAndUserId(String plan, Date date, String userId);
    //List<Plan> findAllById(int id);
    //List<Plan> findAllByPlan();
    //void deleteById();
    //void deleteAll(List<Plan> plans);
    //id로 찾아서 수정하는거랑 날짜와 plan으로 찾아서 삭제하는거
    //수정하는건 그냥 엔티티 겍체의 세터로 값 바꾸면 되는 것 같다 -> 영속성 떄문에 객체만 바꿔도 트랜잭션이 끝나는 시점에 쿼리 수행

}
