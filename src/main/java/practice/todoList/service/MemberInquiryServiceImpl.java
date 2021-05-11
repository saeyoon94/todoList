package practice.todoList.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practice.todoList.domain.Member;
import practice.todoList.repository.MemberRepository;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberInquiryServiceImpl implements MemberInquiryService{
    private final MemberRepository memberRepository;

    @Override
    public Member findMemberInfo(String id) {
        return memberRepository.findById(id).get();
    }
}
