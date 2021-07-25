package generics;

import exceptions_reduced_version.ParsingException;

public class GenericShort implements GenericType<Short> {

    public Short add(final Short x, final Short y) {
        return (short) (x + y);
    }

    public Short subtract(final Short x, final Short y) {
        return (short) (x - y);
    }

    public Short multiply(final Short x, final Short y) {
        return (short) (x * y);
    }

    public Short divide(final Short x, final Short y) {
        return (short) (x / y);
    }

    public Short min(final Short x, final Short y) {
        return (short) (Math.min(x, y));
    }

    public Short max(final Short x, final Short y) {
        return (short) (Math.max(x, y));
    }

    public Short negate(final Short x) {
        return (short) -x;
    }

    public Short count(final Short x) {
        return (short) Integer.bitCount(x & 0xffff);
    }

    public Short convertToNumber(String str) throws ParsingException {
        try {
            return (short) Integer.parseInt(str);
        } catch (Exception e) {
            throw new ParsingException("Could not convert " + str + " to Short");
        }
    }
}
