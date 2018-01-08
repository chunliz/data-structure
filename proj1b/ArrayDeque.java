public class ArrayDeque<Item> {

    private Item[] items;
    private int size;
    private int first, last;

    public ArrayDeque() {
        items = (Item[]) new Object[8];
        size = 0;
        first = items.length-1;
        last = 0;
    }

    private void resize(int capacity) {
        Item[] A = (Item[]) new Object[capacity];

        System.arraycopy(items, 0, A, 0, items.length);
        items = A;

        first = items.length-1;
        last = size;
    }

    public void addFirst(Item i) {
        if (size == items.length) resize(size + 1);
        items[first] = i;
        size = size + 1;
        first = (first - 1) % items.length;
    }

    public void addLast(Item i) {
        if (size == items.length) resize(size + 1);
        items[last] = i;
        size = size + 1;
        last = (last + 1) % items.length;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        for (int i = (first + 1) % items.length; i != last; i = (i + 1) % items.length) {
            System.out.println(items[i]);
        }
    }

    public Item removeFirst() {
        if (size == 0) return null;

        first = (first + 1) % items.length;
        Item firstItem = items[first];
        items[first] = null;

        return firstItem;
    }

    public Item removeLast() {
        if (size == 0) return null;

        last = (last - 1) % items.length;
        Item lastItem = items[last];
        items[last] = null;

        return lastItem;
    }

    public Item get(int index) {
        return items[(first + 1 + index) % items.length];
    }

}
