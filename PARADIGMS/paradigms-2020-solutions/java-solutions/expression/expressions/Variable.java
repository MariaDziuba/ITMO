package expressions;

import exceptions_reduced_version.EvaluatingException;
import generics.GenericType;

public class Variable<T extends Number> implements TripleExpression<T> {
    private final String name;

    public Variable(String name) {
        this.name = name;
    }

    public T evaluate(T x, T y, T z, GenericType<T> operation) throws EvaluatingException {
        switch (name) {
            case "x":
                return x;
            case "y":
                return y;
            case "z":
                return z;
            default:
                throw new EvaluatingException("Incorrect variable name");
        }
    }
}

