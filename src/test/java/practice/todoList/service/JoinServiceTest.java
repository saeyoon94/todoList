package practice.todoList.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import practice.todoList.domain.Member;
import practice.todoList.repository.MemberRepository;
import practice.todoList.repository.MemoryMemberRepository;

@Transactional
public class JoinServiceTest {

    public JoinService joinService;
    MemberRepository memberRepository = new MemoryMemberRepository();

    @BeforeEach
    void beforeEach() {
        Member member = new Member();
        member.setId("jsy8481");
        member.setPassword("12341234");
        member.setName("정수영");

        memberRepository.save(member);
        joinService = new JoinServiceImpl(memberRepository);
    }

    @Test
    @DisplayName("로그인 성공 케이스")
    void loginSuccess() {
        //given
        Member member = new Member();
        member.setId("jsy8483");
        member.setPassword("12341234");
        member.setName("실수영");

        //when
        joinService.join(member);

        //then
        Assertions.assertThat(member.getName()).isEqualTo(memberRepository.findById(member.getId()).get().getName());
    }

    @Test
    @DisplayName("로그인 실패 케이스 -> 메모리 기반이라 잘 작동 안하네")
    void loginFailure() {
        //given
        Member member = new Member();
        member.setId("jsy8481");
        member.setPassword("12341234");
        member.setName("정수영");

        //when//then
        org.junit.jupiter.api.Assertions.assertThrows(Exception.class,
                () -> joinService.join(member));

    }

}
