import java.util.Arrays;


public class SAP {
    private final Digraph graph;

    private class ComputeSAP {
        private final BreadthFirstDirectedPaths vbfs, wbfs;
        private int ancestor = -1;
        private int length = -1;
        private int size;

        public ComputeSAP(Digraph graph, int v, int w) {
            this(graph, Arrays.asList(v), Arrays.asList(w));
        }

        public ComputeSAP(Digraph graph, Iterable<Integer> v, Iterable<Integer> w) {
            size = graph.V();

            checkVertices(v);
            checkVertices(w);

            vbfs = new BreadthFirstDirectedPaths(graph, v);
            wbfs = new BreadthFirstDirectedPaths(graph, w);

            int min = Integer.MAX_VALUE;

            for (int i = 0; i < size; i++)
                if (hasPathTo(i) && distTo(i) < min) {
                    ancestor = i;
                    min = distTo(i);
                }

            if (ancestor != -1)
                length = min;
        }

        private void checkVertex(int vertex) {
            if (vertex < 0 || vertex > size - 1)
                throw new java.lang.IndexOutOfBoundsException();
        }

        private void checkVertices(Iterable<Integer> vertices) {
            if (vertices == null)
                throw new java.lang.NullPointerException();

            for (int vertex: vertices)
                checkVertex(vertex);
        }

        public int getAncestor() {
            return ancestor;
        }

        public int getLength() {
            return length;
        }

        private int distTo(int i) {
            return vbfs.distTo(i) + wbfs.distTo(i);
        }

        private boolean hasPathTo(int i) {
            return vbfs.hasPathTo(i) && wbfs.hasPathTo(i);
        }
    }

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null)
            throw new java.lang.NullPointerException();

        graph = new Digraph(G);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        return new ComputeSAP(graph, v, w).getLength();
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        return new ComputeSAP(graph, v, w).getAncestor();
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        return new ComputeSAP(graph, v, w).getLength();
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        return new ComputeSAP(graph, v, w).getAncestor();
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);

        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}