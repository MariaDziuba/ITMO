package exceptions_reduced_version;

public class DivideByZeroException extends EvaluatingException {
    public DivideByZeroException() {
        super("Division by zero");
    }
}
