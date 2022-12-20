/* *****************************************************************************
 *  Name:        Dale Young
 *  Date:        12/19/2022
 *  Description: shortest ancestral path
 **************************************************************************** */

import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;

public class SAP {

    private final Digraph digraph;

    /** constructor takes a digraph (not necessarily a DAG) */
    public SAP(Digraph G) {
        nullChecker(G);
        digraph = new Digraph(G);
    }

    /** returns the length and ancestor of v & w */
    private int[] shortest(int v, int w) {
        int[] result = new int[2];
        BreadthFirstDirectedPaths bfsv = new BreadthFirstDirectedPaths(digraph, v);
        BreadthFirstDirectedPaths bfsw = new BreadthFirstDirectedPaths(digraph, w);

        int shortestLen = Integer.MAX_VALUE;
        int ancestor = -1;
        for (int i = 0; i < digraph.V(); i += 1) {
            if (bfsv.hasPathTo(i) && bfsw.hasPathTo(i)) {
                int len = bfsv.distTo(i) + bfsw.distTo(i);
                if (len < shortestLen) {
                    shortestLen = len;
                    ancestor = i;
                }
            }
        }

        if (ancestor == -1) {
            result[0] = -1;
            result[1] = -1;
        }
        else {
            result[0] = shortestLen;
            result[1] = ancestor;
        }
        return result;
    }

    private int[] shortest(Iterable<Integer> v, Iterable<Integer> w) {
        int[] result = new int[2];
        BreadthFirstDirectedPaths bfsv = new BreadthFirstDirectedPaths(digraph, v);
        BreadthFirstDirectedPaths bfsw = new BreadthFirstDirectedPaths(digraph, w);

        int shortestLen = Integer.MAX_VALUE;
        int ancestor = -1;
        for (int i = 0; i < digraph.V(); i += 1) {
            if (bfsv.hasPathTo(i) && bfsw.hasPathTo(i)) {
                int len = bfsv.distTo(i) + bfsw.distTo(i);
                if (len < shortestLen) {
                    shortestLen = len;
                    ancestor = i;
                }
            }
        }

        if (ancestor == -1) {
            result[0] = -1;
            result[1] = -1;
        }
        else {
            result[0] = shortestLen;
            result[1] = ancestor;
        }
        return result;
    }

    /** length of shortest ancestral path between v and w; -1 if no such path */
    public int length(int v, int w) {
        indexChecker(v);
        indexChecker(w);
        int[] result = shortest(v, w);
        return result[0];
    }

    /**
     * a common ancestor of v and w that participates in a shortest ancestral path;
     * -1 if no such path
     */
    public int ancestor(int v, int w) {
        indexChecker(v);
        indexChecker(w);
        int[] result = shortest(v, w);
        return result[1];
    }

    /**
     * length of shortest ancestral path between any vertex in v and any vertex in w;
     * -1 if no such path
     */
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        indexChecker(v);
        indexChecker(w);
        int[] result = shortest(v, w);
        return result[0];
    }

    /**
     * a common ancestor that participates in shortest ancestral path;
     * -1 if no such path
     */
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        indexChecker(v);
        indexChecker(w);
        int[] result = shortest(v, w);
        return result[1];
    }

    private void nullChecker(Object p) {
        if (p == null) {
            throw new IllegalArgumentException("No null!");
        }
    }

    private void indexChecker(int v) {
        if (v < 0 || v >= digraph.V()) {
            throw new IllegalArgumentException("Invalid index!");
        }
    }

    private void indexChecker(Iterable<Integer> vertex) {
        nullChecker(vertex);
        for (Integer v : vertex) {
            nullChecker(v);
            indexChecker(v);
        }
    }

    /** unit testing */
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);

        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            System.out.println("length = " + String.valueOf(length));
            System.out.println("ancestor = " + String.valueOf(ancestor));
        }
    }
}
