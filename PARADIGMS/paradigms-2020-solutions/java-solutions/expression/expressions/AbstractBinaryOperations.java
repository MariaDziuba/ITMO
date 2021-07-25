package expressions;

import exceptions_reduced_version.EvaluatingException;
import exceptions_reduced_version.ParsingException;
import generics.GenericType;

public abstract class AbstractBinaryOperations<T extends Number> implements TripleExpression<T> {
    private final TripleExpression<T> firstObject;
    private final TripleExpression<T> secondObject;

    public AbstractBinaryOperations(TripleExpression<T> firstObject, TripleExpression<T> secondObject) {
        this.firstObject = firstObject;
        this.secondObject = secondObject;
    }

    protected abstract T evaluate(T x, T y, GenericType<T> op) throws ParsingException, EvaluatingException;

    public T evaluate(T x, T y, T z, GenericType<T> op) throws EvaluatingException, ParsingException {
        return evaluate(firstObject.evaluate(x, y, z, op), secondObject.evaluate(x, y, z, op), op);
    }
}
