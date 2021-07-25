package expressions;

import generics.GenericType;

public class Min<T extends Number> extends AbstractBinaryOperations<T> {
    public Min(TripleExpression<T> firstOperand, TripleExpression<T> secondOperand) {
        super(firstOperand, secondOperand);
    }

    protected T evaluate(T firstOperand, T secondOperand, GenericType<T> type) {
        return type.min(firstOperand, secondOperand);
    }
}
