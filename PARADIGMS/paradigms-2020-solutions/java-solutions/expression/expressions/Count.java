package expressions;

import exceptions_reduced_version.EvaluatingException;
import generics.GenericType;

public class Count<T extends Number> extends AbstractUnaryOperations<T> {
    public Count(TripleExpression<T> operand) {
        super(operand);
    }

    protected T evaluate(T operand, GenericType<T> type) throws EvaluatingException {
        return type.count(operand);
    }
}