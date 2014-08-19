public class Solver {
    private boolean isSolvable;
    private Stack<Board> solution = new Stack<Board>();

    private final class SearchNode implements Comparable<SearchNode> {
        private final Board board;
        private final int moves;
        private final SearchNode previous;

        SearchNode(Board board, SearchNode previous) {
            this.board = board;
            this.previous = previous;

            if (previous == null)
                moves = 0;
            else
                moves = previous.moves + 1;
        }

        private int priority() {
            return board.manhattan() + moves;
        }

        public int compareTo(SearchNode that) {
            return Integer.signum(priority() - that.priority());
        }
    }

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        MinPQ<SearchNode> pq = new MinPQ<SearchNode>();
        MinPQ<SearchNode> twinpq = new MinPQ<SearchNode>();

        pq.insert(new SearchNode(initial, null));
        twinpq.insert(new SearchNode(initial.twin(), null));

        MinPQ<SearchNode> queue = pq;
        SearchNode node;

        while (true) {
            if (!queue.isEmpty()) {
                node = queue.delMin();

                for (Board neighor: node.board.neighbors())
                    if (node.previous == null || !neighor.equals(node.previous.board))
                        queue.insert(new SearchNode(neighor, node));

                if (node.board.isGoal()) {
                    isSolvable = queue == pq;

                    if (!isSolvable) return;

                    while (node != null) {
                        solution.push(node.board);
                        node = node.previous;
                    }

                    return;
                }
            }

            if (queue == pq)
                queue = twinpq;
            else
                queue = pq;
        }
    }

    // is the initial board solvable?
    public boolean isSolvable() {
        return isSolvable;
    }

    // min number of moves to solve initial board; -1 if no solution
    public int moves() {
        return solution.size() - 1;
    }

    // sequence of boards in a shortest solution; null if no solution
    public Iterable<Board> solution() {
        if (isSolvable)
            return solution;
        else
            return null;
    }

    // solve a slider puzzle (given below)
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int N = in.readInt();
        int[][] blocks = new int[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}
