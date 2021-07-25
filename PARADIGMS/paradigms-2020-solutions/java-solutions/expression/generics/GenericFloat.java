package generics;

import exceptions_reduced_version.ParsingException;

public class GenericFloat implements GenericType<Float> {

    public Float add(final Float x, final Float y) {
        return (x + y);
    }

    public Float subtract(final Float x, final Float y) {
        return (x - y);
    }

    public Float multiply(final Float x, final Float y) {
        return (x * y);
    }

    public Float divide(final Float x, final Float y) {
        return (x / y);
    }

    public Float min(final Float x, final Float y) {
        return (Math.min(x, y));
    }

    public Float max(final Float x, final Float y) {
        return (Math.max(x, y));
    }

    public Float negate(final Float x) {
        return -x;
    }

    public Float count(final Float x) {
        return (float) Integer.bitCount(Float.floatToIntBits(x));
    }

    public Float convertToNumber(String str) throws ParsingException {
        try {
            return Float.parseFloat(str);
        } catch (Exception e) {
            throw new ParsingException("Could not convert " + str + " to Float");
        }
    }
}
