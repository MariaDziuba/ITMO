package info.kgeorgiy.ja.dziuba.arrayset;

import java.util.*;

public class ArraySet<E> extends AbstractSet<E> implements NavigableSet<E> {

    private final Comparator<? super E> comparator;
    private final ReversibleArrayList<E> dataList;

    public ArraySet() {
        this(Collections.emptyList(), null);
    }

    // constructor from SortedSet

    public ArraySet(SortedSet<E> set) {
        this.comparator = set.comparator();
        this.dataList = new ReversibleArrayList<>(set);
    }

    public ArraySet(Collection<E> collection) {
        this(collection, null);
    }

    public ArraySet(Comparator<? super E> comparator) {
        this(Collections.emptyList(), comparator);
    }

    public ArraySet(Collection<? extends E> collection, Comparator<? super E> comparator) {
        TreeSet<E> treeSet = new TreeSet<>(comparator);
        treeSet.addAll(collection);
        this.comparator = comparator;
        this.dataList = new ReversibleArrayList<>(treeSet);
    }

    private ArraySet(ReversibleArrayList<E> reversibleArray, Comparator<? super E> comparator) {
        this.comparator = comparator;
        this.dataList = reversibleArray;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean contains(Object object) {
        return Collections.binarySearch(dataList, (E) object, comparator) >= 0;
    }

    @Override
    public int size() {
        return dataList.size();
    }

    @Override
    public Iterator<E> iterator() {
        return Collections.unmodifiableList(dataList).iterator();
    }

    @Override
    public Comparator<? super E> comparator() {
        return comparator;
    }

    @Override
    public NavigableSet<E> descendingSet() {
        return new ArraySet<>(new ReversibleArrayList<>(dataList), Collections.reverseOrder(comparator));
    }

    @Override
    public Iterator<E> descendingIterator() {
        return descendingSet().iterator();
    }

    private NavigableSet<E> subSetImpl(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {

        int fromIndex = fromInclusive ? getCeilingIndex(fromElement) : getHigherIndex(fromElement);
        int toIndex = toInclusive ? getFloorIndex(toElement) : getLowerIndex(toElement);

        if (fromIndex <= toIndex) {
            return new ArraySet<>(dataList.subList(fromIndex, toIndex + 1), comparator);
        }
        return new ArraySet<>(comparator);
    }

    @SuppressWarnings("unchecked")
    private int compare(E e1, E e2) {
        return (comparator == null) ? ((Comparable<E>) e1).compareTo(e2) : comparator.compare(e1, e2);
    }

    @Override
    public NavigableSet<E> subSet(E fromElement, boolean fromStrict, E toElement, boolean toStrict) {
        if (compare(fromElement, toElement) > 0) {
            throw new IllegalArgumentException();
        }
        return subSetImpl(fromElement, fromStrict, toElement, toStrict);
    }

    @Override
    public NavigableSet<E> headSet(E toElement, boolean strict) {
        if (isEmpty()) {
            return this;
        }
        return subSetImpl(first(), true, toElement, strict);
    }

    @Override
    public NavigableSet<E> tailSet(E fromElement, boolean strict) {
        if (isEmpty()) {
            return this;
        }
        return subSetImpl(fromElement, strict, last(), true);
    }

    @Override
    public SortedSet<E> subSet(E fromElement, E toElement) {
        return subSet(fromElement, true, toElement, false);
    }

    @Override
    public SortedSet<E> headSet(E toElement) {
        return headSet(toElement, false);
    }

    @Override
    public SortedSet<E> tailSet(E fromElement) {
        return tailSet(fromElement, true);
    }

    // removed unnecessary functions for more compact and beautiful code

    // -(index + 1) == index of insertion, if element is not found

    @Override
    public E lower(E e) {
        return getOrNull(getLowerIndex(e));
    }

    private int getLowerIndex(E e) {
        int index = Collections.binarySearch(dataList, e, comparator);
        return index >= 0 ? index - 1 : -(index + 1) - 1;
    }

    @Override
    public E floor(E e) {
        return getOrNull(getFloorIndex(e));
    }

    private int getFloorIndex(E e) {
        int index = Collections.binarySearch(dataList, e, comparator);
        return index >= 0 ? index : -(index + 1) - 1;
    }

    @Override
    public E higher(E e) {
        return getOrNull(getHigherIndex(e));

    }

    private int getHigherIndex(E e) {
        int index = Collections.binarySearch(dataList, e, comparator);
        return index >= 0 ? index + 1 : -(index + 1);
    }

    @Override
    public E ceiling(E e) {
        return getOrNull(getCeilingIndex(e));
    }

    private int getCeilingIndex(E e) {
        int index = Collections.binarySearch(dataList, e, comparator);
        return index >= 0 ? index : -(index + 1);
    }

    private E getOrNull(int index) {
        if (index < 0 || index >= size()) {
            return null;
        }
        return dataList.get(index);
    }

    // removed firstOrLast

    @Override
    public E first() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        } else {
            return dataList.get(0);
        }
    }

    @Override
    public E last() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        } else {
            return dataList.get(size() - 1);
        }
    }

    @Override
    public E pollFirst() {
        throw new UnsupportedOperationException();
    }

    @Override
    public E pollLast() {
        throw new UnsupportedOperationException();
    }
}