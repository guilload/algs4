import java.util.Iterator;
import java.util.NoSuchElementException;


public class RandomizedQueue<Item> implements Iterable<Item> {
    private int head = 0;
    private int tail = 0;
    private int sz = 0;
    private Item[] array;

    public RandomizedQueue() {
        array = (Item[]) new Object[1];
    }

    private class RandomizedQueueIterator implements Iterator<Item> {
        private int current = 0;
        private int[] indices = new int[sz];

        public RandomizedQueueIterator() {
            for (int i = 0; i < sz; i++)
                indices[i] = i;

            StdRandom.shuffle(indices);
        }

        public boolean hasNext() {
            return current < sz;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public Item next() {
            if (current >= sz)
                throw new NoSuchElementException();

            int index = (head + indices[current++]) % array.length;
            Item item = array[index];
            return item;
        }
    }

    // delete and return the last item of the queue
    private Item pop() {
        if (isEmpty())
            throw new NoSuchElementException();

        Item item = array[head];
        array[head] = null;

        head = (head + 1) % array.length;
        sz--;

        return item;
    }

    private void resize(int capacity) {
        Item[] copy = (Item[]) new Object[capacity];

        for (int i = 0, j = head; i < sz; i++, j++)
            copy[i] = array[j % array.length];

        array = copy;
        head = 0;
        tail = sz;
    }

    // is the queue empty?
    public boolean isEmpty() {
        return sz == 0;
    }

    // return the number of items on the queue
    public int size() {
        return sz;
    }

    // add the item
    public void enqueue(Item item) {
        if (item == null)
            throw new NullPointerException();

        if (sz == array.length)
            resize(array.length * 2);

        array[tail] = item;
        tail = (tail + 1) % array.length;
        sz++;
    }

    // delete and return a random item
    public Item dequeue() {
        if (isEmpty())
            throw new NoSuchElementException();

        int randint = StdRandom.uniform(sz);
        int index = (head + randint) % array.length;
        Item item = array[index];
        array[index] = null;
        sz--;

        if (index == head) {
            head = (head + 1) % array.length;
            return item;
        }

        else if (index == tail) {
            tail--;
            if (tail < 0)
                tail = array.length - 1;
            return item;
        }

        else {
            array[index] = pop();
            sz++;
        }

        if (sz > 0 && sz == array.length / 4)
            resize(array.length / 2);

        return item;
    }

    // return (but do not delete) a random item
    public Item sample() {
        if (isEmpty())
            throw new NoSuchElementException();

        int randint = StdRandom.uniform(sz);
        int index = (head + randint) % array.length;
        return array[index];
    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
        return new RandomizedQueueIterator();
    }

    // unit testing
    public static void main(String[] args) {
    }
}
