package exceptions_reduced_version;

public class IllegalTypeException extends Exception {
    public IllegalTypeException(String message) {
        super("Unknown type : " + message);
    }
}
