package parser;

import exceptions_reduced_version.ParsingException;
import expressions.TripleExpression;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public interface Parser<T extends Number> {
    TripleExpression<T> parse(String expression) throws ParsingException;
}
