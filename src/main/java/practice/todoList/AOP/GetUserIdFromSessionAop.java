package practice.todoList.AOP;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import practice.todoList.controller.SessionController;

import javax.servlet.http.HttpSession;


@Aspect
@Component
@RequiredArgsConstructor
public class GetUserIdFromSessionAop {
    private final SessionController sessionController;
    private final HttpSession httpSession;

    //매개변수로 HttpSession이 안들어가네,,
    //리턴타입을 *로 했더니 setter 호출할때도 AOP가 적용되어 계속 순환참조가 일어나며 stackoverflow가 걸림
    //그래서 리런타입을 void는 받지 않도록 해서 Setter에는 AOP가 적용 안되도록 함.
    @Before("execution(!void practice.todoList..*.SessionController.*(..))")
    public void execute() throws Throwable {
        String userId = (String) httpSession.getAttribute("userId");
        sessionController.setUserId(userId);

    }
}

