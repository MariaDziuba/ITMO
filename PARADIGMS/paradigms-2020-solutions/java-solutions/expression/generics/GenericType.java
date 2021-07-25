package generics;

import exceptions_reduced_version.EvaluatingException;
import exceptions_reduced_version.ParsingException;

public interface GenericType<T extends Number> {

    T add(final T x, final T y) throws EvaluatingException;

    T subtract(final T x, final T y) throws EvaluatingException;

    T divide(final T x, final T y) throws EvaluatingException;

    T multiply(final T x, final T y) throws EvaluatingException;

    T min(final T x, final T y);

    T max(final T x, final T y);

    T negate(final T x) throws EvaluatingException;

    T count(final T x) throws EvaluatingException;

    T convertToNumber(final String str) throws ParsingException;
}
