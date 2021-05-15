package practice.todoList.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import practice.todoList.Vo.LoginVo;
import practice.todoList.domain.Member;
import practice.todoList.domain.Plan;
import practice.todoList.domain.WeekDay;
import practice.todoList.service.*;

import javax.servlet.http.HttpSession;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class TodoListController {

    private final LoginService loginService;
    private final DuplicateValidationService duplicateValidationService;
    private final JoinService joinService;
    private final MemberInquiryService memberInquiryService;
    private final PlanService planService;
    private final HttpSession httpSession;


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginVo loginVo) {
        try {
            loginService.login(loginVo);
            httpSession.setAttribute("userId", loginVo.getId());

            return new ResponseEntity<String>("SUCCESS", HttpStatus.OK);

        } catch (IllegalStateException e) {
            e.printStackTrace();
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/join/checkId/{id}")
    public ResponseEntity<String> validate(@PathVariable("id") String id) {
        try {
            duplicateValidationService.validate(id);
            return new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        httpSession.invalidate();
        return new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
    }

    @PostMapping("/join")
    public ResponseEntity<String> join(@RequestBody Member member) {
        try {
            joinService.join(member);
            return new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }



    @GetMapping("/api/user")
    public ResponseEntity<Member> findMemberInfo() throws URISyntaxException {

        String userId = (String) httpSession.getAttribute("userId");
        /**
        //null 처리를 공통화하고싶다.. /api 이하만 다른 컨트롤러로 빼서 어떻게 공통으로 하도록 해보자.
        if (userId == null) {
            HttpHeaders httpHeaders = new HttpHeaders();
            URI uri = new URI("/");   //종착치 url을 정해야한다.
            httpHeaders.setLocation(uri);
            return new ResponseEntity(httpHeaders, HttpStatus.FOUND);
        }
         */

        Member member = memberInquiryService.findMemberInfo(userId);
        return new ResponseEntity<Member>(member, HttpStatus.OK);
    }

    @GetMapping("/api/plan/{currentDay}")
    public ResponseEntity<Map<LocalDate, List<Map<String, List<?>>>>> findPlanAndMemoOfWeek
            (@PathVariable("currentDay") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate currentDate) throws URISyntaxException {
        //RequestBody로 받으면 알아서 직렬화되어 localDate객체로 받을 수 있으나, 그게 아니면 @DateTimeFormat 사용


        String userId = (String) httpSession.getAttribute("userId");
        /**
        //null 처리를 공통화하고싶다.. /api 이하만 다른 컨트롤러로 빼서 어떻게 공통으로 하도록 해보자.
        if (userId == null) {
            HttpHeaders httpHeaders = new HttpHeaders();
            URI uri = new URI("/");   //종착치 url을 정해야한다.
            httpHeaders.setLocation(uri);
            return new ResponseEntity(httpHeaders, HttpStatus.FOUND);
        }
         */

        Map<LocalDate, List<Map<String, List<?>>>> plansAndMemosOfWeek = planService.findPlanAndMemoOfWeek(userId, currentDate);
        return new ResponseEntity<>(plansAndMemosOfWeek, HttpStatus.OK);
    }

    @PostMapping("/api/plan")
    public ResponseEntity<Plan> registerPlan(@RequestBody Plan plan) throws URISyntaxException {

        String userId = (String) httpSession.getAttribute("userId");
        //null 처리를 공통화하고싶다.. /api 이하만 다른 컨트롤러로 빼서 어떻게 공통으로 하도록 해보자.
        if (userId == null) {
            HttpHeaders httpHeaders = new HttpHeaders();
            URI uri = new URI("/");   //종착치 url을 정해야한다.
            httpHeaders.setLocation(uri);
            return new ResponseEntity(httpHeaders, HttpStatus.FOUND);
        }

        plan.setUserId(userId);
        planService.registerPlan(plan);
        return new ResponseEntity<>(plan, HttpStatus.OK);
    }


    @PatchMapping("/api/plan")
    public ResponseEntity<Plan> modifyPlan(@RequestBody Plan plan) throws URISyntaxException {

        String userId = (String) httpSession.getAttribute("userId");
        //null 처리를 공통화하고싶다.. /api 이하만 다른 컨트롤러로 빼서 어떻게 공통으로 하도록 해보자.
        if (userId == null) {
            HttpHeaders httpHeaders = new HttpHeaders();
            URI uri = new URI("/");   //종착치 url을 정해야한다. -> 클라이언트에서 처리해줄것. 300번대만 보내면
            httpHeaders.setLocation(uri);
            return new ResponseEntity(httpHeaders, HttpStatus.FOUND);
        }

        try {
            Plan modifiedPlan = planService.modifyPlan(userId, plan);
            return new ResponseEntity<>(modifiedPlan, HttpStatus.OK);
        } catch (IllegalStateException e) {
            //자기 계획이 아닌데 수정하려고 한 경우
            e.printStackTrace();
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @DeleteMapping("/api/plan/{id}")
    public ResponseEntity<String> deletePlan(@PathVariable("id") int id) throws URISyntaxException {

        String userId = (String) httpSession.getAttribute("userId");
        //null 처리를 공통화하고싶다.. /api 이하만 다른 컨트롤러로 빼서 어떻게 공통으로 하도록 해보자.
        if (userId == null) {
            HttpHeaders httpHeaders = new HttpHeaders();
            URI uri = new URI("/");   //종착치 url을 정해야한다.
            httpHeaders.setLocation(uri);
            return new ResponseEntity(httpHeaders, HttpStatus.FOUND);
        }

        try {
            planService.deletePlan(userId, id);
            return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/api/plan/everyday")
    public ResponseEntity<?> registerWholeWeekPlan(@RequestBody Plan plan) throws URISyntaxException {

        String userId = (String) httpSession.getAttribute("userId");
        //null 처리를 공통화하고싶다.. /api 이하만 다른 컨트롤러로 빼서 어떻게 공통으로 하도록 해보자.
        if (userId == null) {
            HttpHeaders httpHeaders = new HttpHeaders();
            URI uri = new URI("/");   //종착치 url을 정해야한다.
            httpHeaders.setLocation(uri);
            return new ResponseEntity(httpHeaders, HttpStatus.FOUND);
        }
        plan.setUserId(userId);

        try {
            List<Plan> plans = planService.registerPlans(plan, WeekDay.WHOLE_WEEK);
            return new ResponseEntity<>(plans, HttpStatus.OK);
        } catch (CloneNotSupportedException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/api/plan/weekday")
    public ResponseEntity<?> registerWeekDayPlan(@RequestBody Plan plan) throws URISyntaxException {

        String userId = (String) httpSession.getAttribute("userId");
        //null 처리를 공통화하고싶다.. /api 이하만 다른 컨트롤러로 빼서 어떻게 공통으로 하도록 해보자.
        if (userId == null) {
            HttpHeaders httpHeaders = new HttpHeaders();
            URI uri = new URI("/");   //종착치 url을 정해야한다.
            httpHeaders.setLocation(uri);
            return new ResponseEntity(httpHeaders, HttpStatus.FOUND);
        }
        plan.setUserId(userId);

        try {
            List<Plan> plans = planService.registerPlans(plan, WeekDay.WEEK_DAY);
            return new ResponseEntity<>(plans, HttpStatus.OK);
        } catch (CloneNotSupportedException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




}
