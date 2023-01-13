/* *****************************************************************************
 *  Name:        Dale Young
 *  Date:        1/13/2023
 *  Description: Move-to -front encoding and decoding in Burrows_Wheeler data
                 compression
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

import java.util.LinkedList;

public class MoveToFront {

    private static LinkedList<Character> inialize() {
        LinkedList<Character> ascList = new LinkedList<>();
        for (int i = 255; i >= 0; i -= 1) {
            ascList.addFirst((char) i);
        }
        return ascList;
    }

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        LinkedList<Character> moveList = inialize();
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            int index = moveList.indexOf(c);
            moveList.remove(index);
            moveList.addFirst(c);
            BinaryStdOut.write(index, 8);
        }
        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        LinkedList<Character> moveList = inialize();
        while (!BinaryStdIn.isEmpty()) {
            int index = BinaryStdIn.readChar();
            char c = moveList.get(index);
            moveList.remove(index);
            moveList.addFirst(c);
            BinaryStdOut.write(c, 8);
        }
        BinaryStdOut.close();
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("No arguments?");
        }
        if (args[0].equals("-")) {
            encode();
        }
        else if (args[0].equals("+")) {
            decode();
        }
        else {
            throw new IllegalArgumentException("Argument false!");
        }
    }
}
