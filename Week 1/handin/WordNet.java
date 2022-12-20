/* *****************************************************************************
 *  Name:        Dale Young
 *  Date:        12/19/2022
 *  Description: a semantic lexicon for the English language
 **************************************************************************** */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WordNet {

    private int numV;
    private Map<Integer, String> id2Nouns;
    private Map<String, ArrayList<Integer>> noun2Ids;
    private SAP sap;

    /** constructor takes the name of the two input files */
    public WordNet(String synsets, String hypernyms) {
        nullChecker(synsets);
        nullChecker(hypernyms);

        numV = 0;
        id2Nouns = new HashMap<>();
        noun2Ids = new HashMap<>();
        readSynsets(synsets);
        readHypernyms(hypernyms);

    }

    private void readSynsets(String synsets) {
        In in = new In(synsets);
        String line;
        while ((line = in.readLine()) != null) {
            String[] str = line.split(",");
            if (str.length >= 2) {
                numV += 1;
                int id = Integer.parseInt(str[0]);
                id2Nouns.put(id, str[1]);

                String[] nouns = str[1].split(" ");
                for (String noun : nouns) {
                    ArrayList<Integer> ids = noun2Ids.get(noun);
                    if (ids != null) {
                        ids.add(id);
                        noun2Ids.put(noun, ids);
                    }
                    else {
                        ArrayList<Integer> nids = new ArrayList<>();
                        nids.add(id);
                        noun2Ids.put(noun, nids);
                    }
                }
            }
        }
    }

    private void readHypernyms(String hypernyms) {
        In in = new In(hypernyms);
        String line;
        Digraph digraph = new Digraph(numV);

        while ((line = in.readLine()) != null) {
            String[] str = line.split(",");
            if (str.length >= 2) {
                int childId = Integer.parseInt(str[0]);
                for (int i = 1; i < str.length; i += 1) {
                    digraph.addEdge(childId, Integer.parseInt(str[i]));
                }
            }
        }
        digraphChecker(digraph);
        sap = new SAP(digraph);
    }

    private void digraphChecker(Digraph G) {
        DirectedCycle dc = new DirectedCycle(G);
        if (dc.hasCycle()) {
            throw new IllegalArgumentException("Cycle!");
        }
        int numberOfRoot = 0;
        for (int i = 0; i < G.V(); i += 1) {
            if (G.outdegree(i) == 0) {
                numberOfRoot += 1;
            }
            if (numberOfRoot > 1) {
                throw new IllegalArgumentException("More than 1 root!");
            }
        }
    }

    /** returns all WordNet nouns */
    public Iterable<String> nouns() {
        return noun2Ids.keySet();
    }

    /** is the word a WordNet noun? */
    public boolean isNoun(String word) {
        nullChecker(word);
        return noun2Ids.containsKey(word);
    }

    /** distance between nounA and nounB */
    public int distance(String nounA, String nounB) {
        nullChecker(nounA);
        nullChecker(nounB);

        return sap.length(noun2Ids.get(nounA), noun2Ids.get(nounB));
    }

    /**
     * a synset that is the common ancestor of nounA and nounB
     * in a shortest ancestral path
     */
    public String sap(String nounA, String nounB) {
        nullChecker(nounA);
        nullChecker(nounB);

        return id2Nouns.get(sap.ancestor(noun2Ids.get(nounA), noun2Ids.get(nounB)));
    }

    private void nullChecker(Object p) {
        if (p == null) {
            throw new IllegalArgumentException("No null!");
        }
    }

    /** unit testing */
    public static void main(String[] args) {

    }
}
