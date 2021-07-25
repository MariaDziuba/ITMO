package expressions;

import exceptions_reduced_version.EvaluatingException;
import generics.GenericType;

public class Divide<T extends Number> extends AbstractBinaryOperations<T> {
    public Divide(TripleExpression<T> firstOperand, TripleExpression<T> secondOperand) {
        super(firstOperand, secondOperand);
    }

    protected T evaluate(T firstOperand, T secondOperand, GenericType<T> type) throws EvaluatingException {
        return type.divide(firstOperand, secondOperand);
    }
}
