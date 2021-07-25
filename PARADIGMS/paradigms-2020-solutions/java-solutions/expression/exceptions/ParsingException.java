package exceptions_reduced_version;

public class ParsingException extends Exception {
    public ParsingException(String line) {
        super("Could not parse " + line);
    }
}
