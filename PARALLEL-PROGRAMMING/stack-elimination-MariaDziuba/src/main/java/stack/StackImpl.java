package stack;

import kotlinx.atomicfu.AtomicArray;
import kotlinx.atomicfu.AtomicRef;

import java.util.Random;

public class StackImpl implements Stack {

    private static final int eliminationArraySize = 128;
    private static final int maxSearchIterCnt = 16;
    private static final Random rnd = new Random();
    private final AtomicRef<Node> head = new AtomicRef<>(null);
    private final AtomicArray<Integer> eliminationArray = new AtomicArray<>(eliminationArraySize);

    private static class Node {
        final AtomicRef<Node> next;
        final int x;

        public Node(int x, Node next) {
            this.next = new AtomicRef<>(next);
            this.x = x;
        }
    }

    private void basicPush(final int x) {
        while (true) {
            final Node curHead = head.getValue();
            final Node newHead = new Node(x, curHead);
            if (head.compareAndSet(curHead, newHead)) {
                return;
            }
        }
    }

    @Override
    public void push(final int x) {

        final int startIdx = rnd.nextInt(eliminationArraySize - maxSearchIterCnt);
        final Integer intRef = x;

        for (int offset = 0; offset < maxSearchIterCnt; ++offset) {

            final AtomicRef<Integer> wrapped = eliminationArray.get(startIdx + offset);

            if (wrapped.compareAndSet(null, intRef)) {

                for (int searchIter = 0; searchIter < maxSearchIterCnt; ++searchIter) {
                    final Integer val = wrapped.getValue();
                    if (val == null || val != x) {
                        return;
                    }
                }

                if (wrapped.compareAndSet(intRef, null)) {
                    break;
                }
                return;
            }
        }

        basicPush(x);
    }


    private int basicPop() {
        while (true) {
            final Node curHead = head.getValue();
            if (curHead == null) {
                return Integer.MIN_VALUE;
            }
            if (head.compareAndSet(curHead, curHead.next.getValue())) {
                return curHead.x;
            }
        }
    }

    @Override
    public int pop() {

        final int startIdx = rnd.nextInt(eliminationArraySize - maxSearchIterCnt);

        for (int offset = 0; offset < maxSearchIterCnt; ++offset) {

            final AtomicRef<Integer> wrapped = eliminationArray.get(startIdx + offset);
            final Integer val = wrapped.getValue();
            if (val != null && wrapped.compareAndSet(val, null)) {
                return val;
            }
        }

        return basicPop();
    }
}