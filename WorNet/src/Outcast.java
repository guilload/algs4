
public class Outcast {
    private final WordNet wordnet;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        checkNull(wordnet);
        this.wordnet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        checkNull(nouns);
        
        int max = Integer.MIN_VALUE;
        String outcast = null;

        for (String nounA: nouns) {
            int distance = 0;
            
            for (String nounB: nouns)
                distance += wordnet.distance(nounA, nounB);
            
            if (distance > max) {
                max = distance;
                outcast = nounA;
            }
        }
        
        return outcast;
    }

    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }

    private void checkNull(Object obj) {
        if (obj == null)
            throw new java.lang.NullPointerException();
    }
}