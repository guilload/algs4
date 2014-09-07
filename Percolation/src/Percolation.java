public class Percolation {
    private boolean[] open;
    private final int width;

    private final int virtualBottomSite;
    private final int virtualTopSite;

    private final WeightedQuickUnionUF backwash;
    private final WeightedQuickUnionUF uf;

    // create N-by-N grid, with all sites blocked
    public Percolation(int N) {
        if (N <= 0)
            throw new IllegalArgumentException();

        open = new boolean[N * N];
        width = N;

        for (int i = 0; i < open.length; i++)
            open[i] = false;

        virtualBottomSite = open.length + 1;
        virtualTopSite = open.length;

        backwash = new WeightedQuickUnionUF(open.length + 1);
        uf = new WeightedQuickUnionUF(open.length + 2);
    }

    // throw an IndexOutOfBoundsException if the site is off the grid
    private void checkBounds(int i, int j) {
        if (isOffGrid(i, j))
            throw new IndexOutOfBoundsException();
    }

    // convert i, j coordinates to array index
    private int getIndex(int i, int j) {
        return (i - 1) * width + j - 1;
    }

    private boolean isOffGrid(int i, int j) {
        return (i < 1 || j < 1 || i > width || j > width);
    }

    // is site (row i, column j) full?
    public boolean isFull(int i, int j) {
        checkBounds(i, j);
        int p = getIndex(i, j);
        return backwash.connected(p, virtualTopSite);
    }

    // is site (row i, column j) open?
    public boolean isOpen(int i, int j) {
        checkBounds(i, j);
        int p = getIndex(i, j);
        return open[p];
    }

    // open site (row i, column j) if it is not already
    public void open(int i, int j) {
        checkBounds(i, j);

        if (isOpen(i, j))
            return;

        int p = getIndex(i, j);
        open[p] = true;

        if (i == 1) {  // Connect to virtual top site
            backwash.union(p, virtualTopSite);
            uf.union(p, virtualTopSite);
        }

        if (i == width)  // Connect to virtual bottom site
            uf.union(p, virtualBottomSite);

        int[] offsets = {-1, 1, 0, 0};

        for (int k = 0, l = offsets.length - 1; k < offsets.length; k++, l--) {
            int x = i + offsets[k];
            int y = j + offsets[l];

            if (isOffGrid(x, y))
                continue;

            if (isOpen(x, y)) {
                int q = getIndex(x, y);
                backwash.union(p, q);
                uf.union(p, q);
            }
        }
    }

    // does the system percolate?
    public boolean percolates() {
        return uf.connected(virtualBottomSite, virtualTopSite);
    }
}
