package practice.todoList.Exception;

public class NoUserIdSessionException extends RuntimeException{
    public NoUserIdSessionException() {}

    public NoUserIdSessionException(String errorMessage) {
        super(errorMessage);
    }
}
