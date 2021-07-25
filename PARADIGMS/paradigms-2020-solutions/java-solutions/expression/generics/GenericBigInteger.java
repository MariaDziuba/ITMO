package generics;

import exceptions_reduced_version.EvaluatingException;
import exceptions_reduced_version.DivideByZeroException;
import exceptions_reduced_version.ParsingException;

import java.math.BigInteger;

public class GenericBigInteger implements GenericType<BigInteger> {

    public BigInteger add(final BigInteger x, final BigInteger y) {
        return x.add(y);
    }

    public BigInteger subtract(final BigInteger x, final BigInteger y) {
        return x.subtract(y);
    }

    public BigInteger multiply(final BigInteger x, final BigInteger y) {
        return x.multiply(y);
    }

    public BigInteger divide(final BigInteger x, final BigInteger y)  throws EvaluatingException {
        if (y.equals(BigInteger.ZERO)) {
            throw new DivideByZeroException();
        }
        return x.divide(y);
    }

    public BigInteger min(final BigInteger x, final BigInteger y) {
        return x.min(y);
    }

    public BigInteger max(final BigInteger x, final BigInteger y) {
        return x.max(y);
    }

    public BigInteger negate(final BigInteger x) {
        return x.negate();
    }

    public BigInteger count(final BigInteger x) {
        return BigInteger.valueOf(x.bitCount());
    }

    public BigInteger convertToNumber(String str) throws ParsingException {
        try {
            return new BigInteger(str);
        } catch (Exception e) {
            throw new ParsingException("Could not convert " + str + " to Double");
        }
    }
}

