/* *****************************************************************************
 *  Name:        Dale Young
 *  Date:        12/19/2022
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;

public class Outcast {

    private WordNet w;

    /** constructor takes a WordNet object */
    public Outcast(WordNet wordNet) {
        w = wordNet;
    }

    /** given an array of WordNet nouns, return an outcast */
    public String outcast(String[] nouns) {
        int number = nouns.length;
        int[] dis = new int[number];
        for (int i = 0; i < number; i += 1) {
            for (int j = 0; j < number; j += 1) {
                dis[i] += w.distance(nouns[i], nouns[j]);
            }
        }

        int max = 0;
        String result = null;
        for (int i = 0; i < number; i += 1) {
            if (dis[i] > max) {
                max = dis[i];
                result = nouns[i];
            }
        }

        return result;
    }

    /** test client */
    public static void main(String[] args) {
        WordNet wordNet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordNet);
        for (int t = 2; t < args.length; t += 1) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            System.out.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
