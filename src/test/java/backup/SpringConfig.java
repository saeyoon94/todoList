package backup;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import practice.todoList.repository.MemberRepository;
import practice.todoList.service.LoginService;
import practice.todoList.service.LoginServiceImpl;

//@Configuration
public class SpringConfig {
    /*
    /JDBC
    private DataSource dataSource;

    @Autowired
    public SpringConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }
   -------------
   //JPA
    private EntityManager em;

    @Autowired
    public SpringConfig(EntityManager em) {
        this.em = em;
    }
     */
    //SpringDataJPA
    //그냥 이렇게 해두면 구현체 만들어진게 알아서 들어옴
    private final MemberRepository memberRepository;
    //@Autowired
    public SpringConfig(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;  //이걸 하면 이걸 상속한 인터페이스를 찾아서 알아서 구현체 만들어 빈에 등록
    }

    //@Bean
    public LoginService memberService() {
        return new LoginServiceImpl(memberRepository);
    }

    /*
    //AOP는 이렇게 빈으로 직접 등록하는게 좋음.
    @Bean
    public TimeTraceAop timeTraceAop(){
        return new TimeTraceAop();
    }
    */

    //SpringDataJpa를 쓸 경우 아래도 필요가 없음.
    /*
    @Bean
    public MemberRepository memberRepository() {
        //return new MemoryMemberRepository();
        //return new JdbcMemberRepository(dataSource);
        //return new JdbcTemplateMemberRepository(dataSource);
        return new JpaMemberRepository(em);
    }

     */
}

