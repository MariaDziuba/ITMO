package generics;

import exceptions_reduced_version.ParsingException;

public class GenericLong implements GenericType<Long> {

    public Long add(final Long x, final Long y) {
        return (x + y);
    }

    public Long subtract(final Long x, final Long y) {
        return (x - y);
    }

    public Long multiply(final Long x, final Long y) {
        return (x * y);
    }

    public Long divide(final Long x, final Long y) {
        return (x / y);
    }

    public Long min(final Long x, final Long y) {
        return (Math.min(x, y));
    }

    public Long max(final Long x, final Long y) {
        return (Math.max(x, y));
    }

    public Long negate(final Long x) {
        return -x;
    }

    public Long count(final Long x) {
        return (long) Long.bitCount(x);
    }

    public Long convertToNumber(String str) throws ParsingException {
        try {
            return Long.parseLong(str);
        } catch (Exception e) {
            throw new ParsingException("Could not convert " + str + " to Long");
        }
    }
}