package practice.todoList.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import practice.todoList.Vo.LoginVo;
import practice.todoList.domain.Member;
import practice.todoList.repository.MemberRepository;
import practice.todoList.repository.MemoryMemberRepository;

public class loginServiceTest {

    public LoginService loginService;

    @BeforeEach
    void beforeEach() {
        MemberRepository memberRepository = new MemoryMemberRepository();
        Member member = new Member();
        member.setId("jsy8481");
        member.setPassword("12341234");

        memberRepository.save(member);
        loginService = new LoginServiceImpl(memberRepository);
    }


    @Test
    @DisplayName("로그인 성공 케이스")
    void loginSucess() {
        //given
        LoginVo loginVo = new LoginVo();
        loginVo.setId("jsy8481");
        loginVo.setPassword("12341234");


    }

    @Test
    @DisplayName("아이디는 같으나 비밀번호 틀린 경우")
    void loginPwFailure() {
        //given
        LoginVo loginVo = new LoginVo();
        loginVo.setId("jsy8481");
        loginVo.setPassword("11111111");

        //when
        IllegalStateException e = Assertions.assertThrows(IllegalStateException.class,
                () -> loginService.login(loginVo));

        //then
        org.assertj.core.api.Assertions.assertThat(e.getMessage()).isEqualTo("Invalid Password");

    }@Test
    @DisplayName("아이디가 틀린 경우")
    void loginIdFailure() {
        //given
        LoginVo loginVo = new LoginVo();
        loginVo.setId("nonmember");
        loginVo.setPassword("11111111");

        //when
        IllegalStateException e = Assertions.assertThrows(IllegalStateException.class,
                () -> loginService.login(loginVo));

        //then
        org.assertj.core.api.Assertions.assertThat(e.getMessage()).isEqualTo("Invalid Id");

    }
}
