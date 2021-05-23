package practice.todoList.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import practice.todoList.Vo.LoginVo;
import practice.todoList.domain.Member;
import practice.todoList.domain.Note;
import practice.todoList.domain.Plan;
import practice.todoList.domain.WeekDay;
import practice.todoList.service.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


/**
 널체크 등 공통로직을 인터셉터로 뺐음에도 String userId = (String) httpSession.getAttribute("userId"); 가 중복
 각 메소드가 실행되기 전에 userId를 가져올 수 있도록 AOP로 뺴기 위해 /api 이하 부분은 컨트롤러를 따로 분리
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/**")
public class SessionController {

    private final MemberInquiryService memberInquiryService;
    private final PlanService planService;
    private final NoteService noteService;

    private String userId;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @GetMapping("/api/user")
    public ResponseEntity<Member> findMemberInfo() {

        Member member = memberInquiryService.findMemberInfo(userId);
        return new ResponseEntity<Member>(member, HttpStatus.OK);
    }

    @GetMapping("/api/plan/{currentDay}")
    public ResponseEntity<Map<LocalDate, List<Map<String, List<?>>>>> findPlanAndMemoOfWeek
            (@PathVariable("currentDay") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate currentDate) {

        Map<LocalDate, List<Map<String, List<?>>>> plansAndMemosOfWeek = planService.findPlanAndMemoOfWeek(userId, currentDate);
        return new ResponseEntity<>(plansAndMemosOfWeek, HttpStatus.OK);
    }

    @PostMapping("/api/plan")
    public ResponseEntity<Plan> registerPlan(@RequestBody Plan plan)  {

        planService.registerPlan(userId, plan);
        return new ResponseEntity<>(plan, HttpStatus.OK);
    }


    @PatchMapping("/api/plan")
    public ResponseEntity<Plan> modifyPlan(@RequestBody Plan plan)  {

        Plan modifiedPlan = planService.modifyPlan(userId, plan);
        return new ResponseEntity<>(modifiedPlan, HttpStatus.OK);
    }

    @DeleteMapping("/api/plan/{id}")
    public ResponseEntity<String> deletePlan(@PathVariable("id") int id) {

        planService.deletePlan(userId, id);
        return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
    }

    @PostMapping("/api/plan/everyday")
    public ResponseEntity<?> registerWholeWeekPlan(@RequestBody Plan plan) throws CloneNotSupportedException {

        List<Plan> plans = planService.registerPlans(userId, plan, WeekDay.WHOLE_WEEK);
        return new ResponseEntity<>(plans, HttpStatus.OK);

    }

    @PostMapping("/api/plan/weekday")
    public ResponseEntity<?> registerWeekDayPlan(@RequestBody Plan plan) throws CloneNotSupportedException {

        List<Plan> plans = planService.registerPlans(userId, plan, WeekDay.WEEK_DAY);
        return new ResponseEntity<>(plans, HttpStatus.OK);

    }

    //note

    @PostMapping("/api/note")
    public ResponseEntity<Note> registerNote(@RequestBody Note note)  {

        noteService.registerNote(userId, note);
        return new ResponseEntity<>(note, HttpStatus.OK);
    }


    @PatchMapping("/api/note")
    public ResponseEntity<Note> modifyNote(@RequestBody Note note)  {

        Note modifiedNote = noteService.modifyNote(userId, note);
        return new ResponseEntity<>(modifiedNote, HttpStatus.OK);
    }

    @DeleteMapping("/api/note/{id}")
    public ResponseEntity<String> deleteNote(@PathVariable("id") int id) {

        noteService.deleteNote(userId, id);
        return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
    }
}
