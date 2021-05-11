package practice.todoList.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import practice.todoList.domain.Member;
import practice.todoList.repository.MemberRepository;
import practice.todoList.repository.MemoryMemberRepository;

public class DuplicateValidationServiceTest {
    public DuplicateValidationService duplicateValidationService;

    @BeforeEach
    void beforeEach() {
        MemberRepository memberRepository = new MemoryMemberRepository();
        Member member = new Member();
        member.setId("jsy8481");
        member.setPassword("12341234");

        memberRepository.save(member);
        duplicateValidationService = new DuplicateValidationServiceImpl(memberRepository);
    }

    @Test
    @DisplayName("중복이 없는 ID를 입력한 케이스")
    void validSuccess() {
        duplicateValidationService.validate("jsy8482");
    }
    @Test
    @DisplayName("중복이 있는 ID를 입력한 케이스")
    void validFailure() {
        IllegalStateException e = Assertions.assertThrows(IllegalStateException.class,
                () -> duplicateValidationService.validate("jsy8481"));
        org.assertj.core.api.Assertions.assertThat(e.getMessage()).isEqualTo("ID already exists.");
    }


}
