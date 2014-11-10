import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class WordNet {
    private final SAP sap;
    private HashMap<String, ArrayList<Integer>> nounIndex = new HashMap<String, ArrayList<Integer>>();
    private HashMap<Integer, String> synsetIndex = new HashMap<Integer, String>();

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        checkNull(synsets);
        checkNull(hypernyms);

        int size = parseSynsets(synsets);
        Digraph graph = parseHypernyms(hypernyms, size);
        sap = new SAP(graph);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return Collections.unmodifiableSet(nounIndex.keySet());
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        checkNull(word);
        return nounIndex.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        checkNoun(nounA);
        checkNoun(nounB);
        return sap.length(nounIndex.get(nounA), nounIndex.get(nounB));
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        checkNoun(nounA);
        checkNoun(nounB);
        return synsetIndex.get(sap.ancestor(nounIndex.get(nounA), nounIndex.get(nounB)));
    }

    // do unit testing of this class
    public static void main(String[] args) {

    }

    private void checkNoun(String noun) {
        if (!isNoun(noun))
            throw new IllegalArgumentException();
    }

    private void checkNull(Object obj) {
        if (obj == null)
            throw new java.lang.NullPointerException();
    }

    private int parseSynsets(String path) {
        In in = new In(path);
        int synsetId = 0;

        while (!in.isEmpty()) {
            String line = in.readLine();
            String[] fields = line.split(",");
            String[] words = fields[1].split(" ");

            synsetIndex.put(synsetId, fields[1]);

            for (String word : words) {
                ArrayList<Integer> synsets = nounIndex.get(word);

                if (synsets == null) {
                    synsets = new ArrayList<Integer>();
                    nounIndex.put(word, synsets);
                }
                synsets.add(synsetId);
            }
            synsetId++;
        }
        return synsetId;
    }

    private Digraph parseHypernyms(String path, int size) {
        In in = new In(path);
        Digraph digraph = new Digraph(size);

        int count = 0;

        while (!in.isEmpty()) {
            count++;

            String line = in.readLine();
            String[] fields = line.split(",");
            int synsetId = Integer.parseInt(fields[0]);

            for (int i = 1; i < fields.length; i++)
                digraph.addEdge(synsetId, Integer.parseInt(fields[i]));
        }

        DirectedCycle dc = new DirectedCycle(digraph);

        if (dc.hasCycle() || size - count > 1)
            throw new IllegalArgumentException();

        return digraph;
    }
}