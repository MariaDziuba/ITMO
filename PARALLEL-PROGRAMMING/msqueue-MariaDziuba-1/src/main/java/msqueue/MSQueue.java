package msqueue;

import kotlinx.atomicfu.AtomicRef;

public class MSQueue implements Queue {

    private final AtomicRef<Node> head, tail;
    
    private static class Node {
        final AtomicRef<Node> next;
        final int x;

        public Node(final int x) {
            this.next = new AtomicRef<>(null);
            this.x = x;
        }
    }

    public MSQueue() {
        final Node dummy = new Node(0);
        this.head = new AtomicRef<>(dummy);
        this.tail = new AtomicRef<>(dummy);
    }


    @Override
    public void enqueue(final int x) {
        final Node last = new Node(x);
        while (true) {
            final Node curTail = this.tail.getValue();
            final AtomicRef<Node> nextAfterTail = curTail.next;
            if (nextAfterTail.compareAndSet(null, last)) {
                this.tail.compareAndSet(curTail, last);
                return;
            } else {
                this.tail.compareAndSet(curTail, curTail.next.getValue());
            }
        }
    }

    @Override
    public int dequeue() {
        while (true) {
            final Node dummyFst = head.getValue();
            final Node fst = dummyFst.next.getValue();
            if (fst == null) {
                return Integer.MIN_VALUE;
            } else {
                if (head.compareAndSet(dummyFst, fst)) {
                    return fst.x;
                }
            }
        }
    }

    @Override
    public int peek() {
        final Node top = head.getValue().next.getValue();
        if (top != null) {
            return top.x;
        } else {
            return Integer.MIN_VALUE;
        }
    }
}