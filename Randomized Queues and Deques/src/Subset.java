public class Subset {

    public static void main(String[] args) {
        // Reservoir sampling algorithm
        if (args.length != 1)
            throw new java.lang.IllegalArgumentException();

        int k = Integer.parseInt(args[0]);
        int N = k;
        RandomizedQueue<String> reservoir = new RandomizedQueue<String>();

        for (int i = 0; i < k; i++) {
            String str = StdIn.readString();
            reservoir.enqueue(str);
        }

        while (!StdIn.isEmpty()) {
            String str = StdIn.readString();

            if (StdRandom.uniform(++N) < k) {
                reservoir.dequeue();
                reservoir.enqueue(str);
            }
        }

        for (String str: reservoir)
            StdOut.println(str);
    }
}
