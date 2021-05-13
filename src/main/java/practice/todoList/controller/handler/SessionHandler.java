package practice.todoList.controller.handler;

import org.springframework.http.ResponseEntity;


//필드값과 세터를 활용하기 위해 인터페이스가 아닌 추상클래스 사용
public abstract class SessionHandler {
    Object param = null;
    Object entity = null;


    public abstract ResponseEntity process(String userId);

    public void setParam(String param){
        this.param = param;
    }

    public void setEntity(Object entity) {
        this.entity = entity;
    }

}
