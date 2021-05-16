package practice.todoList.Exception;

public class AlreadyExistsUserIdException extends RuntimeException{
    public AlreadyExistsUserIdException() {}

    public AlreadyExistsUserIdException(String errorMessage) {
        super(errorMessage);
    }
}
