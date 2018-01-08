public class LinkedListDeque<Item> {

    private class DequeNode {
        public Item item;
        public DequeNode prev, next;

        public DequeNode(){}

        public DequeNode(Item i) {
            item = i;
            prev = null;
            next = null;
        }
    }

    private DequeNode sentinel;
    private int size;

    public LinkedListDeque() {
        sentinel = new DequeNode();
        sentinel.next = sentinel;
        sentinel.prev = sentinel;

        size = 0;
    }
    /*
    public LinkedListDeque(Item i) {
        DequeNode node = new DequeNode(i);

        sentinel.next = node;
        node.prev = sentinel;
        node.next = sentinel;
        sentinel.prev = node;

        size = 1;
    }
    */

    /* Note: no looping or recursion for add and remove operation, and constant time is required. */
    public void addFirst(Item i) {
        DequeNode node = new DequeNode(i);

        node.next = sentinel.next;
        sentinel.next.prev = node;
        sentinel.next = node;
        node.prev = sentinel;

        size = size + 1;
    }

    public void addLast(Item i) {
        DequeNode node = new DequeNode(i);

        node.prev = sentinel.prev;
        sentinel.prev.next = node;
        sentinel.prev = node;
        node.next = sentinel;

        size = size + 1;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    /* Note: constant time is required. */
    public int size() {
        return size;
    }

    public void printDeque() {
        DequeNode node = sentinel.next;
        while (node != sentinel) {
            System.out.println(node.item);
            node = node.next;
        }
    }

    public Item removeFirst() {
        if (size == 0) return null;

        Item firstItem = sentinel.next.item;
        sentinel.next = sentinel.next.next;
        sentinel.next.prev = sentinel;

        size = size - 1;
        return firstItem;
    }

    public Item removeLast() {
        if (size == 0) return null;

        Item lastItem = sentinel.prev.item;
        sentinel.prev = sentinel.prev.prev;
        sentinel.prev.next = sentinel;

        size = size - 1;
        return lastItem;
    }

    /* Note: iteration, not recursion. */
    public Item get(int index) {
        if (index >= size) return null;

        DequeNode node = sentinel;
        while (index >= 0) {
            node = node.next;
            index = index - 1;
        }

        return node.item;
    }

    /* Note: same as get(), but use recursion */
    public Item getRecursive(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        DequeNode node = getRecursiveHelper(index);
        return node.item;
    }

    private DequeNode getRecursiveHelper(int index) {
        if (index == 0) {
            return sentinel.next;
        } else {
            return getRecursiveHelper(index - 1).next;
        }
    }
}
