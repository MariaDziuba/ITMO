package generics;

import exceptions_reduced_version.ParsingException;

public class GenericDouble implements GenericType<Double> {

    public Double add(final Double x, final Double y) {
        return (x + y);
    }

    public Double subtract(final Double x, final Double y) {
        return (x - y);
    }

    public Double multiply(final Double x, final Double y) {
        return (x * y);
    }

    public Double divide(final Double x, final Double y) {
        return (x / y);
    }

    public Double min(final Double x, final Double y) {
        return (Math.min(x, y));
    }

    public Double max(final Double x, final Double y) {
        return (Math.max(x, y));
    }

    public Double negate(final Double x) {
        return -x;
    }

    public Double count(final Double x) {
        return (double) Long.bitCount(Double.doubleToLongBits(x));
    }

    public Double convertToNumber(String str) throws ParsingException {
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            throw new ParsingException("Could not convert " + str + " to Double");
        }
    }
}
