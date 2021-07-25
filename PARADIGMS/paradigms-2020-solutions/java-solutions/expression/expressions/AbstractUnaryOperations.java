package expressions;

import exceptions_reduced_version.EvaluatingException;
import exceptions_reduced_version.ParsingException;
import generics.GenericType;

public abstract class AbstractUnaryOperations<T extends Number> implements TripleExpression<T> {
    private final TripleExpression<T> operand;

    public AbstractUnaryOperations(TripleExpression<T> operand) {
        this.operand = operand;
    }

    protected abstract T evaluate(T x, GenericType<T> type) throws ParsingException, EvaluatingException;

    public T evaluate(T x, T y, T z, GenericType<T> type) throws ParsingException, EvaluatingException {
        return evaluate(operand.evaluate(x, y, z, type), type);
    }
}

