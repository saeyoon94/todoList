package practice.todoList.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practice.todoList.Exception.AlreadyExistsUserIdException;
import practice.todoList.repository.MemberRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class DuplicateValidationServiceImpl implements DuplicateValidationService{
    private final MemberRepository memberRepository;

    @Override
    public void validate(String id) {
        memberRepository.findById(id).ifPresent(s -> {throw new AlreadyExistsUserIdException("ID already exists.");});
    }
}
