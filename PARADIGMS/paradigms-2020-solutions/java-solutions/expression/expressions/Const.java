package expressions;

import generics.GenericType;

public class Const<T extends Number> implements TripleExpression<T> {
    private final T value;

    public Const(T value) {
        this.value = value;
    }

    public T evaluate(T x, T y, T z, GenericType<T> op) {
        return value;
    }
}

