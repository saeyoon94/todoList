package practice.todoList.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import practice.todoList.Exception.AlreadyExistsUserIdException;
import practice.todoList.Exception.UnAuthorizedRequestException;
import practice.todoList.Exception.LoginFailureException;
import practice.todoList.Exception.NoUserIdSessionException;

import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
@ControllerAdvice
@RestController
public class GlobalExceptionHandler {
    @ExceptionHandler(value = NoUserIdSessionException.class)
    public ResponseEntity noUserIdSessionException(NoUserIdSessionException e) throws URISyntaxException {
        log.debug("NoUserIdSessionException Handler");
        e.printStackTrace();

        HttpHeaders httpHeaders = new HttpHeaders();
        URI uri = new URI("/");   //종착치 url을 정해야한다.
        httpHeaders.setLocation(uri);
        return new ResponseEntity(e.getMessage(), httpHeaders, HttpStatus.FOUND);
    }

    @ExceptionHandler(value = LoginFailureException.class)
    public ResponseEntity loginFailureException(LoginFailureException e)  {
        log.debug("LoginFailureException Handler");
        e.printStackTrace();

        return new ResponseEntity<String>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = AlreadyExistsUserIdException.class)
    public ResponseEntity alreadyExistsUserIdException(AlreadyExistsUserIdException e)  {
        log.debug("AlreadyExistsUserIdException Handler");
        e.printStackTrace();
        return new ResponseEntity<String>(e.getMessage(), HttpStatus.EXPECTATION_FAILED);
    }

    @ExceptionHandler(value = UnAuthorizedRequestException.class)
    public ResponseEntity unAuthorizedRequestException(UnAuthorizedRequestException e)  {
        log.debug("UnAuthorizedRequestException Handler");
        e.printStackTrace();
        return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = CloneNotSupportedException.class)
    public ResponseEntity cloneNotSupportedException(CloneNotSupportedException e)  {
        log.debug("CloneNotSupportedException Handler");
        e.printStackTrace();
        return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity defaultException(Exception e)  {
        log.debug("DefaultException Handler");
        e.printStackTrace();
        return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
