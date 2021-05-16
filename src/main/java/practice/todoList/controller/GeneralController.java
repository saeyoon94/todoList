package practice.todoList.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import practice.todoList.Vo.LoginVo;
import practice.todoList.domain.Member;

import practice.todoList.service.*;

import javax.servlet.http.HttpSession;


@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class GeneralController {

    private final LoginService loginService;
    private final DuplicateValidationService duplicateValidationService;
    private final JoinService joinService;
    private final HttpSession httpSession;


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginVo loginVo) {

        loginService.login(loginVo);
        httpSession.setAttribute("userId", loginVo.getId());
        return new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
    }

    @GetMapping("/join/checkId/{id}")
    public ResponseEntity<String> validate(@PathVariable("id") String id) {
        duplicateValidationService.validate(id);
        return new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        httpSession.invalidate();
        return new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
    }

    @PostMapping("/join")
    public ResponseEntity<String> join(@RequestBody Member member) {

        joinService.join(member);
        return new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
    }
}