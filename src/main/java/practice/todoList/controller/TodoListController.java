package practice.todoList.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import practice.todoList.Vo.LoginVo;
import practice.todoList.controller.handler.FindMemberInfoHandler;
import practice.todoList.controller.handler.FindPlanAndMemoOfWeekHandler;
import practice.todoList.controller.handler.RegisterPlanHandler;
import practice.todoList.controller.handler.SessionHandler;
import practice.todoList.domain.Member;
import practice.todoList.domain.Note;
import practice.todoList.domain.Plan;
import practice.todoList.service.*;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/")
public class TodoListController {

    private final LoginService loginService;
    private final DuplicateValidationService duplicateValidationService;
    private final JoinService joinService;
    private final MemberInquiryService memberInquiryService;
    private final PlanService planService;
    private final HttpSession httpSession;
    private final ObjectMapper objectMapper;

    private static Map<String, SessionHandler> sessionHandlerMap = new HashMap<>();

    @Autowired
    public TodoListController(LoginService loginService, DuplicateValidationService duplicateValidationService, JoinService joinService, MemberInquiryService memberInquiryService, PlanService planService, HttpSession httpSession, ObjectMapper objectMapper) {
        this.loginService = loginService;
        this.duplicateValidationService = duplicateValidationService;
        this.joinService = joinService;
        this.memberInquiryService = memberInquiryService;
        this.planService = planService;
        this.httpSession = httpSession;
        this.objectMapper = objectMapper;

        sessionHandlerMap.put("/api/user_GET", new FindMemberInfoHandler(memberInquiryService));
        sessionHandlerMap.put("/api/plan_GET_param", new FindPlanAndMemoOfWeekHandler(planService));
        sessionHandlerMap.put("/api/plan_POST", new RegisterPlanHandler(planService));
    }


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

    @RequestMapping("/api/**")
    public ResponseEntity sessionDispatcher(HttpServletRequest request) throws URISyntaxException, IOException {
        String userId = (String) httpSession.getAttribute("userId");
        log.debug("userId = {}", userId);
        if (userId == null) {
            HttpHeaders httpHeaders = new HttpHeaders();
            URI uri = new URI("/");   //종착치 url을 정해야한다.
            httpHeaders.setLocation(uri);
            return new ResponseEntity(httpHeaders, HttpStatus.FOUND);
        }

        String requestURI = request.getRequestURI();
        String httpMethod = request.getMethod();
        log.debug("requestURI = {}, httpMethod = {}", requestURI, httpMethod);
        //URL과 http메소드를 동시에 고려하기 위함
        SessionHandler sessionHandler = sessionHandlerMap.get(requestURI + "_" + httpMethod);

        //pathvariable이 들어가는 경우를 처리하기 위함
        if (sessionHandler == null) {
            log.debug("first Session Handler Null!");
            //맨 뒤에 pathvariable이 하나만 있는 경우에 대해서만 처리 가능.
            //맨 뒤에를 뺀 부분을 uri, 맨 뒤의 값을 param
            Map<String, String> sessionHandlerWithParam = findSessionHandlerWithParam(requestURI);
            SessionHandler sessionHandlerAlternative = sessionHandlerMap.get(sessionHandlerWithParam.get("uri")
                    + "_" + httpMethod + "_param");
            log.debug("new_uri = {}", sessionHandlerWithParam.get("uri"));
            //param부분 짤라냈는데도 없는 경우
            if (sessionHandlerAlternative == null) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
            //param부분 잘라내면 찾을 수 있는 경우 핸들러에 param까지 지정해준다
            else {
                sessionHandler = sessionHandlerAlternative;
                sessionHandler.setParam(sessionHandlerWithParam.get("param"));
                log.debug("uri = {}, param = {}", sessionHandlerWithParam.get("uri"), sessionHandlerWithParam.get("param"));
            }
        }
        log.debug("httpMethod = {}", httpMethod);
        //request에 body가 있는 경우
        if (httpMethod.equals("POST") || httpMethod.equals("PATCH") || httpMethod.equals("PUT")) {
            log.debug("find request body!");
            Object entity = findRequestBody(requestURI, request);
            sessionHandler.setEntity(entity);
        }

        ResponseEntity responseEntity = sessionHandler.process(userId);
        return responseEntity;
    }

    //URL 경로 규칙이 바뀌는 경우나 plan, note이외의 다른 엔티티가 생기는 경우 로직을 수정해야하는.. 확장에 닫혀있는 코드
    private Object findRequestBody(String requestURI, HttpServletRequest request) throws IOException {
        ServletInputStream inputStream = request.getInputStream();
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

        String[] uriParts = requestURI.split("/");
        int lastIndex = uriParts.length - 1;
        log.debug("{} // {}", uriParts[lastIndex], uriParts[lastIndex-1]);
        if (uriParts[lastIndex].equals("plan") || uriParts[lastIndex - 1].equals("plan")) {
            Plan plan = objectMapper.readValue(messageBody, Plan.class);
            return plan;
        }
        else if (uriParts[lastIndex].equals("note") || uriParts[lastIndex - 1].equals("note")) {
            Note note = objectMapper.readValue(messageBody, Note.class);
            return note;
        }
        return null;
    }

    private Map<String, String> findSessionHandlerWithParam(String requestURI) {
        Map<String, String> uriAndParam = new HashMap<>();

        String[] uriParts = requestURI.split("/");
        int lastIndex = uriParts.length - 1;
        String uriCandidate = "";
        String paramCandidate = uriParts[lastIndex];
        for (int index = 1; index < lastIndex; index ++) {
            uriCandidate += "/";
            uriCandidate += uriParts[index];
        }
        uriAndParam.put("uri", uriCandidate);
        uriAndParam.put("param", paramCandidate);
        return uriAndParam;
    }


/**

    @GetMapping("/api/user")
    public ResponseEntity<Member> findMemberInfo() throws URISyntaxException {

        String userId = (String) httpSession.getAttribute("userId");
        //null 처리를 공통화하고싶다.. /api 이하만 다른 컨트롤러로 빼서 어떻게 공통으로 하도록 해보자.
        if (userId == null) {
            HttpHeaders httpHeaders = new HttpHeaders();
            URI uri = new URI("/");   //종착치 url을 정해야한다.
            httpHeaders.setLocation(uri);
            return new ResponseEntity(httpHeaders, HttpStatus.FOUND);
        }

        Member member = memberInquiryService.findMemberInfo(userId);
        return new ResponseEntity<Member>(member, HttpStatus.OK);
    }

    @GetMapping("/api/plan/{currentDay}")
    public ResponseEntity<Map<LocalDate, List<Map<String, List<?>>>>> findPlanAndMemoOfWeek
            (@PathVariable("currentDay") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate currentDate) throws URISyntaxException {
        //RequestBody로 받으면 알아서 직렬화되어 localDate객체로 받을 수 있으나, 그게 아니면 @DateTimeFormat 사용

        String userId = (String) httpSession.getAttribute("userId");
        //null 처리를 공통화하고싶다.. /api 이하만 다른 컨트롤러로 빼서 어떻게 공통으로 하도록 해보자.
        if (userId == null) {
            HttpHeaders httpHeaders = new HttpHeaders();
            URI uri = new URI("/");   //종착치 url을 정해야한다.
            httpHeaders.setLocation(uri);
            return new ResponseEntity(httpHeaders, HttpStatus.FOUND);
        }

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


*/

}
