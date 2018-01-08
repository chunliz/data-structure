public interface Deque<Item> {
    void addFirst(Item i);
    void addLast(Item i);
    Item removeFirst();
    Item removeLast();
    Item get(int index);
    Item getRecursive(int index);
    int size();
    boolean isEmpty();
    void printDeque();
}

