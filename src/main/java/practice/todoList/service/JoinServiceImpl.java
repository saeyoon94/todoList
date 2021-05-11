package practice.todoList.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practice.todoList.domain.Member;
import practice.todoList.repository.MemberRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class JoinServiceImpl implements JoinService{
    private final MemberRepository memberRepository;

    @Override
    public void join(Member member) {
        memberRepository.save(member);
    }
}
