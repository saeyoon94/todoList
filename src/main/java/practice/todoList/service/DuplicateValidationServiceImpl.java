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
public class DuplicateValidationServiceImpl implements DuplicateValidationService{
    private final MemberRepository memberRepository;

    @Override
    public void validate(String id) {
        if(memberRepository.findById(id).isPresent()) {
            throw new IllegalStateException("ID already exists.");
        }
    }
}
