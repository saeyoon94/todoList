package practice.todoList.Exception;

public class LoginFailureException extends RuntimeException{
    public LoginFailureException() {}

    public LoginFailureException(String errorMessage) {
        super(errorMessage);
    }
}
