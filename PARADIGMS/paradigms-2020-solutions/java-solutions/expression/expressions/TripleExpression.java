package expressions;

import exceptions_reduced_version.EvaluatingException;
import exceptions_reduced_version.ParsingException;
import generics.GenericType;


/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public interface TripleExpression<T extends Number> {
    T evaluate(T x, T y, T z, GenericType<T> op) throws ParsingException, EvaluatingException;
}
