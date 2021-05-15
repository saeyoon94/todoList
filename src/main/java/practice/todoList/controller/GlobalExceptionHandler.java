package practice.todoList.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import practice.todoList.Exception.NoUserIdSessionException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
@RestController
public class GlobalExceptionHandler {
    @ExceptionHandler(value = NoUserIdSessionException.class)
    public ResponseEntity noUserIdSessionException(NoUserIdSessionException e) throws URISyntaxException {
        log.debug("NoUserIdSessionException Handler");
        HttpHeaders httpHeaders = new HttpHeaders();
        URI uri = new URI("/");   //종착치 url을 정해야한다.
        httpHeaders.setLocation(uri);
        return new ResponseEntity(httpHeaders, HttpStatus.FOUND);
    }
}
