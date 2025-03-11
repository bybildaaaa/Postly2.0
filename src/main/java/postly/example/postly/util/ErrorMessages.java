package postly.example.postly.util;

public class ErrorMessages {
    public static final String USER_NOT_FOUND = "User not found";
    public static final String POST_NOT_FOUND = "Post not found";

    // Приватный конструктор, чтобы предотвратить создание экземпляров класса
    private ErrorMessages() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}