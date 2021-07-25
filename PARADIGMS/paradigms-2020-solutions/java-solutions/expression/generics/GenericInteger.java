package generics;

import exceptions_reduced_version.EvaluatingException;
import exceptions_reduced_version.DivideByZeroException;
import exceptions_reduced_version.OverflowException;
import exceptions_reduced_version.ParsingException;

public class GenericInteger implements GenericType<Integer> {

    private final boolean needCheck;

    public GenericInteger(boolean needCheck) {
        this.needCheck = needCheck;
    }

    public Integer add(final Integer x, final Integer y) throws EvaluatingException {
        if (needCheck) {
            if (y > 0 && x > Integer.MAX_VALUE - y || y <= 0 && x < Integer.MIN_VALUE - y) {
                throw new OverflowException(x + " + " + y);
            }
        }
        return (x + y);
    }

    public Integer subtract(final Integer x, final Integer y) throws EvaluatingException {
        if (needCheck) {
            if (y > 0 && x < Integer.MIN_VALUE + y || y <= 0 && x > Integer.MAX_VALUE + y) {
                throw new OverflowException(x + " - " + y);
            }
        }
        return (x - y);
    }

    public Integer multiply(final Integer x, final Integer y) throws EvaluatingException {
        if (needCheck) {
            if (x > 0 && y > 0 && Integer.MAX_VALUE / x < y) {
                throw new OverflowException(x + "*" + y);
            }
            if (x > 0 && y < 0 && Integer.MIN_VALUE / x > y) {
                throw new OverflowException(x + "*" + y);
            }
            if (x < 0 && y > 0 && Integer.MIN_VALUE / y > x) {
                throw new OverflowException(x + "*" + y);
            }
            if (x < 0 && y < 0 && Integer.MAX_VALUE / x > y) {
                throw new OverflowException(x + "*" + y);
            }
        }
        return (x * y);
    }

    public Integer divide(final Integer x, final Integer y) throws EvaluatingException {
        if (needCheck) {
            if ((x == Integer.MIN_VALUE) && (y == -1)) {
                throw new OverflowException(x + "/" + y);
            }
            if (y == 0) throw new DivideByZeroException();
        }
        return (x / y);
    }

    public Integer min(final Integer x, final Integer y) {
        return (Math.min(x, y));
    }

    public Integer max(final Integer x, final Integer y) {
        return (Math.max(x, y));
    }

    public Integer negate(final Integer x) throws EvaluatingException {
        if (needCheck) {
            if (x == Integer.MIN_VALUE) {
                throw new OverflowException("Overflow: " + -x);
            }
        }
        return -x;
    }

    public Integer count(final Integer x) {
        return Integer.bitCount(x);
    }

    public Integer convertToNumber (String str) throws ParsingException {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            throw new ParsingException("Could not convert " + str + " to Integer");
        }
    }
}
