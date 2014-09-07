public class PercolationStats {
    private double[] thresholds;

    // perform T independent computational experiments on an N-by-N grid
    public PercolationStats(int N, int T) {
        if (N < 1 || T < 1) {
            throw new java.lang.IllegalArgumentException();
        }

        thresholds = new double[T];

        for (int t = 0; t < T; t++) {
            double threshold = 0;
            Percolation percolation = new Percolation(N);

            while (!percolation.percolates()) {
                int i = StdRandom.uniform(N) + 1;
                int j = StdRandom.uniform(N) + 1;

                if (!percolation.isOpen(i, j)) {
                    threshold++;
                    percolation.open(i, j);
                }
            }
            thresholds[t] = threshold / (N * N);
        }
    }

    // sample mean of percolation threshold
    public double mean() {
        return StdStats.mean(thresholds);
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return StdStats.stddev(thresholds);
    }

    public double confidenceHi() {
        return mean() + (1.96 * stddev() / Math.sqrt(thresholds.length));
    }

    public double confidenceLo() {
        return mean() - (1.96 * stddev() / Math.sqrt(thresholds.length));
    }

    // test client, described below
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int t = Integer.parseInt(args[1]);

        PercolationStats percolationStats = new PercolationStats(n, t);
        double mean = percolationStats.mean();
        double stddev = percolationStats.stddev();
        double hi = percolationStats.confidenceHi();
        double lo = percolationStats.confidenceLo();

        StdOut.println("mean\t\t\t= " + mean);
        StdOut.println("stddev\t\t\t= " + stddev);
        StdOut.println("95% confidence interval\t= " + hi + ", " + lo);
    }
}
