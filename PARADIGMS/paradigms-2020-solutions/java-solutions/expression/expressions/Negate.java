package expressions;

import exceptions_reduced_version.EvaluatingException;
import generics.GenericType;

public class Negate<T extends Number> extends AbstractUnaryOperations<T> {
    public Negate(TripleExpression<T> operand) {
        super(operand);
    }

    protected T evaluate(T operand, GenericType<T> type) throws EvaluatingException {
        return type.negate(operand);
    }
}
