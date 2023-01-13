/* *****************************************************************************
 *  Name:        Dale Young
 *  Date:        1/13/2023
 *  Description: a data structure that describes the abstraction of a sorted array
                 of the n circular suffixes of a string of length n
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdOut;

import java.util.Arrays;
import java.util.Comparator;

public class CircularSuffixArray {

    private String string;
    private Integer[] index;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) {
            throw new IllegalArgumentException("No null!");
        }
        string = s;
        index = new Integer[s.length()];
        for (int i = 0; i < string.length(); i += 1) {
            index[i] = i;
        }
        Arrays.sort(index, suffix());
    }

    private Comparator<Integer> suffix() {
        return new SuffixCompare();
    }

    private class SuffixCompare implements Comparator<Integer> {
        public int compare(Integer o1, Integer o2) {
            int first1 = o1;
            int first2 = o2;
            for (int i = 0; i < string.length(); i += 1) {
                char c1 = string.charAt(first1);
                char c2 = string.charAt(first2);
                if (c1 < c2) {
                    return -1;
                }
                else if (c1 > c2) {
                    return 1;
                }
                first1 = (first1 + 1) % string.length();
                first2 = (first2 + 1) % string.length();
            }
            return 0;
        }
    }

    // length of s
    public int length() {
        return string.length();
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= string.length()) {
            throw new IllegalArgumentException("Invalid index");
        }
        return index[i];
    }

    // unit testing
    public static void main(String[] args) {
        CircularSuffixArray test = new CircularSuffixArray("ABRACADABRA!");
        for (int i = 0; i < test.length(); ++i) {
            BinaryStdOut.write(test.index(i) + " ");
        }
        BinaryStdOut.close();
    }
}
