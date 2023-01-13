/* *****************************************************************************
 *  Name:        Dale Young
 *  Date:        12/29/2022
 *  Description: A Boggle solver that finds all valid words in a given Boggle
                 board, using a given dictionary.
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.TrieSET;

import java.util.ArrayList;
import java.util.List;

public class BoggleSolver {

    private TrieSET dic;

    private class Position {
        private int row, col;

        public Position(int prow, int pcol) {
            row = prow;
            col = pcol;
        }
    }

    /**
     * Initializes the data structure using the given array of strings as the dictionary.
     * assume each word in the dictionary contains only the uppercase letters from A to Z
     */
    public BoggleSolver(String[] dictionary) {
        dic = new TrieSET();
        for (String word : dictionary) {
            dic.add(word);
        }
    }

    /** Returns the set of all valid words in the given Boggle boar, as an Iterable. */
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        List<String> words = new ArrayList<>();
        for (int col = 0; col < board.cols(); col += 1) {
            for (int row = 0; row < board.rows(); row += 1) {
                start(words, new Position(row, col), board);
            }
        }
        return words;
    }

    private void start(List<String> words, Position p, BoggleBoard board) {
        int totalRow = board.rows();
        int totalCol = board.cols();
        boolean[][] track = new boolean[totalRow][totalCol];
        for (int row = 0; row < totalRow; row += 1) {
            for (int col = 0; col < totalCol; col += 1) {
                track[row][col] = false;
            }
        }
        track[p.row][p.col] = true;
        String word;
        if (board.getLetter(p.row, p.col) == 'Q') {
            word = String.valueOf(board.getLetter(p.row, p.col) + "U");
        }
        else {
            word = String.valueOf(board.getLetter(p.row, p.col));
        }
        move(words, p, board, word, track);

    }

    private void move(List<String> words, Position p, BoggleBoard board,
                      String word, boolean[][] track) {
        for (Position next : adjacent(p, board)) {
            int totalRow = board.rows();
            int totalCol = board.cols();
            boolean[][] newTrack = new boolean[totalRow][totalCol];
            for (int row = 0; row < totalRow; row += 1) {
                for (int col = 0; col < totalCol; col += 1) {
                    newTrack[row][col] = track[row][col];
                }
            }

            if (!newTrack[next.row][next.col]) {
                newTrack[next.row][next.col] = true;
                String newWord;
                if (board.getLetter(next.row, next.col) == 'Q') {
                    newWord = word + board.getLetter(next.row, next.col) + "U";
                }
                else {
                    newWord = word + board.getLetter(next.row, next.col);
                }

                if (dic.keysWithPrefix(newWord) != null) {
                    if (newWord.length() >= 3 && dic.contains(newWord)
                            && !words.contains(newWord)) {
                        words.add(newWord);
                    }
                    move(words, next, board, newWord, newTrack);
                }
            }
        }

    }

    private Iterable<Position> adjacent(Position p, BoggleBoard board) {
        List<Position> list = new ArrayList<>();
        for (int row = p.row - 1; row <= p.row + 1; row += 1) {
            for (int col = p.col - 1; col <= p.col + 1; col += 1) {
                if (row != p.row || col != p.col) {
                    Position position = new Position(row, col);
                    if (legalPosition(position, board)) {
                        list.add(position);
                    }
                }
            }
        }
        return list;
    }

    private boolean legalPosition(Position p, BoggleBoard board) {
        return p.row >= 0 && p.row < board.rows() && p.col >= 0 && p.col < board.cols();
    }

    /**
     * Returns the score of the given word if it is in the dictionary, zero otherwise.
     * assume each word in the dictionary contains only the uppercase letters from A to Z
     */
    public int scoreOf(String word) {
        if (!dic.contains(word) || word.length() < 3) {
            return 0;
        }
        else if (word.length() <= 4) {
            return 1;
        }
        else if (word.length() == 5) {
            return 2;
        }
        else if (word.length() == 6) {
            return 3;
        }
        else if (word.length() == 7) {
            return 5;
        }
        else {
            return 11;
        }
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}
