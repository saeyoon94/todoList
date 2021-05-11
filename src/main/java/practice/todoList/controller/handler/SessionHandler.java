package practice.todoList.controller.handler;

import org.springframework.http.ResponseEntity;

public interface SessionHandler {
    public ResponseEntity process(String userId);

}
