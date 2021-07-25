package expressions;

import generics.GenericType;

public class Max<T extends Number> extends AbstractBinaryOperations<T> {
    public Max(TripleExpression<T> firstOperand, TripleExpression<T> secondOperand) {
        super(firstOperand, secondOperand);
    }

    protected T evaluate(T firstOperand, T secondOperand, GenericType<T> type) {
        return type.max(firstOperand, secondOperand);
    }
}