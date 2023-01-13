/* *****************************************************************************
 *  Name:        Dale Young
 *  Date:        1/13/2023
 *  Description: The Burrows-Wheeler transform
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;
import edu.princeton.cs.algs4.Queue;

import java.util.Arrays;
import java.util.HashMap;

public class BurrowsWheeler {

    // apply Burrows-Wheeler transform
    // reading from standard input and writing to standard output
    public static void transform() {
        String s = BinaryStdIn.readString();
        CircularSuffixArray csa = new CircularSuffixArray(s);
        for (int i = 0; i < csa.length(); i += 1) {
            if (csa.index(i) == 0) {
                BinaryStdOut.write(i);
                break;
            }
        }

        for (int i = 0; i < csa.length(); i += 1) {
            int index = csa.index(i);
            if (index == 0) {
                BinaryStdOut.write(s.charAt(s.length() - 1), 8);
            }
            else {
                BinaryStdOut.write(s.charAt(index - 1), 8);
            }
        }
        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler inverse transform
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        int first = BinaryStdIn.readInt();
        String s = BinaryStdIn.readString();
        char[] t = s.toCharArray();
        HashMap<Character, Queue<Integer>> table = new HashMap<>();
        for (int i = 0; i < t.length; i += 1) {
            if (!table.containsKey(t[i])) {
                table.put(t[i], new Queue<>());
            }
            table.get(t[i]).enqueue(i);
        }
        Arrays.sort(t);
        int[] next = new int[t.length];
        for (int i = 0; i < t.length; i += 1) {
            next[i] = table.get(t[i]).dequeue();
        }

        for (int i = 0; i < t.length; i += 1) {
            BinaryStdOut.write(t[first], 8);
            first = next[first];
        }
        BinaryStdOut.close();
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("Need Argument!");
        }
        if (args[0].equals("-")) {
            transform();
        }
        else if (args[0].equals("+")) {
            inverseTransform();
        }
        else {
            throw new IllegalArgumentException("Invalid Argument!");
        }
    }
}
