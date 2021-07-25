package info.kgeorgiy.ja.dziuba.arrayset;

import java.util.*;

public class ReversibleArrayList<E> extends AbstractList<E> implements RandomAccess {

    private final boolean isReversed;
    private final List<E> dataList;

    private ReversibleArrayList(List<E> list, boolean isReversed) {
        this.isReversed = isReversed;
        this.dataList = Collections.unmodifiableList(list);
    }

    public ReversibleArrayList(Collection<E> collection) {
        this.isReversed = false;
        this.dataList = List.copyOf(collection);
    }

    public ReversibleArrayList(ReversibleArrayList<E> reversibleArrayList) {
        this.isReversed = !reversibleArrayList.isReversed;
        this.dataList = reversibleArrayList.dataList;
    }

    @Override
    public E get(int index) {
        return dataList.get(isReversed ? reversedIndex(index) : index);
    }

    @Override
    public int size() {
        return dataList.size();
    }

    @Override
    public ReversibleArrayList<E> subList(int fromIndex, int toIndex) {
        if (isReversed) {
            return new ReversibleArrayList<>(dataList.subList(reversedIndex(toIndex - 1), reversedIndex(fromIndex) + 1), isReversed);
        }
        return new ReversibleArrayList<>(dataList.subList(fromIndex, toIndex), isReversed);
    }

    private int reversedIndex(int index) {
        return  size() - 1 - index;
    }
}