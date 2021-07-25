package exceptions_reduced_version;

public class OverflowException extends EvaluatingException {
    public OverflowException(String line) {
        super("Overflow : " + line);
    }
}
