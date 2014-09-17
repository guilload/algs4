import java.util.Iterator;
import java.util.NoSuchElementException;


public class Deque<Item> implements Iterable<Item> {
    private int sz;
    private Node head;
    private Node tail;

    private class Node {
        private Item item;
        private Node next;
        private Node prev;

        public Node(Item item) {
            this.item = item;
        }
    }

    private class DequeIterator implements Iterator<Item> {
        private Node current = head;

        public boolean hasNext() {
            return current != null;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public Item next() {
            if (current == null)
                throw new NoSuchElementException();

            Item item = current.item;
            current = current.next;
            return item;
        }
    }

    // is the deque empty?
    public boolean isEmpty() {
        return sz == 0;
    }

    // return the number of items in the deque
    public int size() {
        return sz;
    }

    // insert the item at the front
    public void addFirst(Item item) {
        if (item == null)
            throw new NullPointerException();

        Node node = new Node(item);

        if (isEmpty())
            tail = node;
        else {
            head.prev = node;
            node.next = head;
        }

        head = node;
        sz++;
    }

    // insert the item at the end
    public void addLast(Item item) {
        if (item == null)
            throw new NullPointerException();

        Node node = new Node(item);

        if (isEmpty())
            head = node;
        else {
            tail.next = node;
            node.prev = tail;
        }

        tail = node;
        sz++;
    }

    // delete and return the item at the front
    public Item removeFirst() {
        if (isEmpty())
            throw new NoSuchElementException();

        Item item = head.item;

        if (--sz == 0) {
            head = null;
            tail = null;
        }

        else {
            head = head.next;
            head.prev = null;
        }

        return item;
    }

    // delete and return the item at the end
    public Item removeLast() {
        if (isEmpty())
            throw new java.util.NoSuchElementException();

        Item item = tail.item;

        if (--sz == 0) {
            head = null;
            tail = null;
        }

        else {
            tail = tail.prev;
            tail.next = null;
        }

        return item;
    }

    // return an iterator over items in order from front to end
    public Iterator<Item> iterator() {
        return new DequeIterator();
    }

    // unit testing
    public static void main(String[] args) {
    }
}
