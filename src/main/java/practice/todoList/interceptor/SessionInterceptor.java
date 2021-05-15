package practice.todoList.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import practice.todoList.Exception.NoUserIdSessionException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Slf4j
@Component
public class SessionInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws NoUserIdSessionException {
        log.debug("SessionInterceptor!");
        Optional<Object> userId = Optional.ofNullable(request.getSession().getAttribute("userId"));
        userId.orElseThrow(() -> new NoUserIdSessionException("The session has no userId."));
        return true;
    }
}
