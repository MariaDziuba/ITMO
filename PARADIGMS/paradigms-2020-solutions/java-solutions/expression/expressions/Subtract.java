package expressions;

import exceptions_reduced_version.EvaluatingException;
import generics.GenericType;

public class Subtract<T extends Number> extends AbstractBinaryOperations<T> {
    public Subtract(TripleExpression<T> firstOperand, TripleExpression<T> secondOperand) {
        super(firstOperand, secondOperand);
    }

    protected T evaluate(T firstOperand, T secondOperand, GenericType<T> type) throws EvaluatingException {
        return type.subtract(firstOperand, secondOperand);
    }
}
