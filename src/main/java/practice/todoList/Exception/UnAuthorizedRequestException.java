package practice.todoList.Exception;

public class UnAuthorizedRequestException extends RuntimeException{
    public UnAuthorizedRequestException() {}

    public UnAuthorizedRequestException(String errorMessage) {
        super(errorMessage);
    }
}
