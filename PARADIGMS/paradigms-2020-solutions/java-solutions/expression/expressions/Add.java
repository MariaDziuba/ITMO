package expressions;

import exceptions_reduced_version.EvaluatingException;
import generics.GenericType;

public class Add<T extends Number> extends AbstractBinaryOperations<T> {
    public Add(TripleExpression<T> firstOperand, TripleExpression<T> secondOperand) {
        super(firstOperand, secondOperand);
    }

    protected T evaluate(T firstOperand, T secondOperand, GenericType<T> type) throws EvaluatingException {
        return type.add(firstOperand, secondOperand);
    }
}

