package linked_list_set;

import kotlinx.atomicfu.AtomicRef;

public class SetImpl implements Set {

    private final AtomicRef<Node> head = new AtomicRef<>(
            new Node(Integer.MIN_VALUE,
                    new Node(Integer.MAX_VALUE,
                            null)
            )
    );

    /**
     * Returns the {@link Window}, where prev.x < x <= cur.x
     */
    private Window findWindow(final int x) {
        final Window win = new Window();
        loopMark:
        while (true) {
            win.current = head.getValue();
            win.next = win.current.getNextValue();
            while (win.next.getKey() < x) {
                final AbstractNode next = win.next.getNextValue();
                if (next.isDeleted()) {
                    final DeletedNode curNext = (DeletedNode) next;
                    if (!win.current.next().compareAndSet(win.next, curNext.node)) {
                        continue loopMark;
                    }
                    win.next = curNext.node;
                } else {
                    win.current = win.next;
                    win.next = next;
                }
            }
            return win;
        }
    }

    public boolean add(final int x) {
        while (true) {
            final Window win = findWindow(x);
            if (win.next.getKey() == x && !win.next.getNextValue().isDeleted()) {
                return false;
            } else {
                final AbstractNode newNode = new Node(x, win.next);
                if (win.current.next().compareAndSet(win.next, newNode)) {
                    return true;
                }
            }
        }
    }

    public boolean remove(final int x) {
        while (true) {
            final Window win = findWindow(x);
            if (win.next.getKey() != x) {
                return false;
            } else {
                final AbstractNode curNext = win.next.next().getValue();
                if (win.next.next().getValue().isDeleted()) {
                    return false;
                } else {
                    if (win.next.next().compareAndSet(curNext, new DeletedNode((Node) curNext))) {
                        win.current.next().compareAndSet(win.next, curNext);
                        return true;
                    }
                }
            }
        }
    }

    public boolean contains(final int x) {
        final AbstractNode node = findWindow(x).next;
        return node.getKey() == x && !node.next().getValue().isDeleted();
    }

    /**
     * Window class representing
     */
    private static class Window {
        private AbstractNode current, next;
    }

    private interface AbstractNode {
        int getKey();

        AtomicRef<AbstractNode> next();

        AbstractNode getNextValue();

        boolean isDeleted();
    }

    private static class Node implements AbstractNode {
        private final int x;
        private final AtomicRef<AbstractNode> next;

        public Node(int x, AbstractNode next) {
            this.x = x;
            this.next = new AtomicRef<>(next);
        }

        @Override
        public int getKey() {
            return x;
        }

        @Override
        public AtomicRef<AbstractNode> next() {
            return next;
        }

        @Override
        public AbstractNode getNextValue() {
            return next.getValue();
        }

        @Override
        public boolean isDeleted() {
            return false;
        }
    }

    private static class DeletedNode implements AbstractNode {
        private final Node node;

        public DeletedNode(Node node) {
            this.node = node;
        }

        @Override
        public int getKey() {
            return node.x;
        }

        @Override
        public AtomicRef<AbstractNode> next() {
            return node.next;
        }

        @Override
        public AbstractNode getNextValue() {
            return node.next.getValue();
        }

        @Override
        public boolean isDeleted() {
            return true;
        }
    }
}