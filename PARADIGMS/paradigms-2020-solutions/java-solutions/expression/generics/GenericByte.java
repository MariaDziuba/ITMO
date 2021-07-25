package generics;

import exceptions_reduced_version.ParsingException;

public class GenericByte implements GenericType<Byte> {

    public Byte add(final Byte x, final Byte y) {
        return (byte) (x + y);
    }

    public Byte subtract(final Byte x, final Byte y) {
        return (byte) (x - y);
    }

    public Byte multiply(final Byte x, final Byte y) {
        return (byte) (x * y);
    }

    public Byte divide(final Byte x, final Byte y) {
        return (byte) (x / y);
    }

    public Byte min(final Byte x, final Byte y) {
        return (byte) (Math.min(x, y));
    }

    public Byte max(final Byte x, final Byte y) {
        return (byte) (Math.max(x, y));
    }

    public Byte negate(final Byte x) {
        return (byte) -x;
    }

    public Byte count(final Byte x) {
        return (byte) Integer.bitCount(x & 0xff);
    }

    public Byte convertToNumber(String str) throws ParsingException {
        try {
            return (byte) Integer.parseInt(str);
        } catch (Exception e) {
            throw new ParsingException("Could not convert " + str + " to Byte");
        }
    }
}
