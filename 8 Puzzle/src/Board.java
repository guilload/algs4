public class Board {
    private int manhattan = -1;  // cache
    private final int N;
    private final int[][] tiles;
    private int zrow;
    private int zcol;

    // construct a board from an N-by-N array of blocks
    // (where blocks[i][j] = block in row i, column j)
    public Board(int[][] blocks) {
        N = blocks.length;
        tiles = new int[N][N];

        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++) {
                tiles[i][j] = blocks[i][j];

                if (blocks[i][j] == 0) {
                    zrow = i;
                    zcol = j;
                }
            }
    }

    // board dimension N
    public int dimension() {
        return N;
    }

    // number of blocks out of place
    public int hamming() {
        int distance = 0;

        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                if (tiles[i][j] != 0 && tiles[i][j] != i * N + j + 1)
                    distance += 1;

        return distance;
    }

    // sum of Manhattan distances between blocks and goal
    public int manhattan() {
        if (manhattan == -1) {
            manhattan = 0;

            for (int i = 0; i < N; i++)
                for (int j = 0; j < N; j++)
                    if (tiles[i][j] != 0) {
                        int tile = tiles[i][j] - 1;
                        manhattan += Math.abs(tile / N - i) + Math.abs(tile % N - j);
                    }
        }

        return manhattan;
    }

    // is this board the goal board?
    public boolean isGoal() {
        return manhattan() == 0;
    }

    // a board obtained by exchanging two adjacent blocks in the same row
    public Board twin() {
        Board twin = new Board(tiles);

        int row = (twin.zrow + 1) % N;
        int tmp = twin.tiles[row][0];
        twin.tiles[row][0] = twin.tiles[row][1];
        twin.tiles[row][1] = tmp;

        return twin;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (y == null)
            return false;

        if (y == this)
            return true;

        if (y.getClass() != getClass())
            return false;

        Board that = (Board) y;

        if (that.dimension() != dimension())
            return false;

        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                if (tiles[i][j] != that.tiles[i][j])
                    return false;

        return true;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        int[] offsets = {-1, 1, 0, 0};
        Queue<Board> neighbors = new Queue<Board>();

        for (int i = 0, j = offsets.length - 1; i < offsets.length; i++, j--) {
            int row = zrow + offsets[i];
            int col = zcol + offsets[j];

            if (row < 0 || row >= N || col < 0 || col >= N)
                continue;

            Board neighbor = new Board(tiles);
            neighbor.tiles[zrow][zcol] = neighbor.tiles[row][col];
            neighbor.tiles[row][col] = 0;
            neighbor.zrow = row;
            neighbor.zcol = col;

            neighbors.enqueue(neighbor);
        }

        return neighbors;
    }

    // string representation of the board (in the output format specified below)
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(N + "\n");

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++)
                str.append(String.format("%2d ", tiles[i][j]));
            str.append("\n");
        }

        return str.toString();
    }
}
