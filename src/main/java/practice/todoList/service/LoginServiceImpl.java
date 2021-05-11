package practice.todoList.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practice.todoList.Vo.LoginVo;
import practice.todoList.domain.Member;
import practice.todoList.repository.MemberRepository;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService{

    private final MemberRepository memberRepository;

    @Override
    public void login(LoginVo loginVo) {
        String id = loginVo.getId();
        String pw = loginVo.getPassword();

        Optional<Member> foundId = memberRepository.findById(id);

        if(foundId.isEmpty()) {
            throw new IllegalStateException("Invalid Id");
        }
        else {
            if(!foundId.map(Member::getPassword).get().equals(pw)) {
                throw new IllegalStateException("Invalid Password");
            }
        }

    }
}
