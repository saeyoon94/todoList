package practice.todoList.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import practice.todoList.domain.Member;

import java.util.Optional;


public interface SpringDataJpaMemberRepository extends JpaRepository<Member, String>, MemberRepository {
    //기본적으로 JpaRepository를 상속해야 함. 처음엔 엔티티, 두번쨰에는 PK의 타입을 적는다.
    //JpaRepository이걸 상속하면 SpringDataJpa가 자동으로 구현체를 만들어서 빈에 등록.


    @Override
    Optional<Member> findByName(String name);  //이렇게만 해 놓으면 다 만든거다...?? 구현할 필요가 없음
    //이것만 재정의한 이유는 나머지는 다  JpaRepository에서 정의되어 있음.
    // JpaRepository에는 생각할만한 것들은 다 있어서 이거 보고 형식 맞춰서
    // 사용하면 될 듯.
    //근데 이건 공통 인터페이스니 PK값이 아닌 다른 컬럼을 기준으로 조회하는건 없어서
    // findByName같은건 따로 정의해줘야 함.
    // 메소드 이름을 신경써야함 - findBy변수명 이렇게 해야
    // JPQL로 select m from Member m where m.name = ?로 번역되어 자동실행
    // findByNameAndId(String name, Long Id) 이런식으로 할 수도 있음.
    // 네이밍 규칙만 신경쓰면 된다.
}