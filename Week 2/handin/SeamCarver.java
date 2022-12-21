/* *****************************************************************************
 *  Name:        Dale Young
 *  Date:        12/21/2022
 *  Description: mutable data type
 **************************************************************************** */

import edu.princeton.cs.algs4.DijkstraSP;
import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.Stack;

import java.awt.Color;

public class SeamCarver {

    private static final double BORDER = 1000;
    private int top;
    private int bottom;

    private Picture p;
    private int width, height;
    private double[][] energy;
    private EdgeWeightedDigraph digraph;

    /** create a seam carver object based on given picture */
    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException("Null!");
        }

        p = picture;
        width = p.width();
        height = p.height();

        updateEnergy();
        initializeDigraph();
    }

    /** current picture */
    public Picture picture() {
        Picture picture = new Picture(width, height);
        for (int col = 0; col < width; col += 1) {
            for (int row = 0; row < height; row += 1) {
                picture.set(col, row, p.get(col, row));
            }
        }
        return picture;
    }

    /** change the 2D index to 1D index for digraph */
    private int indexChanger(int col, int row) {
        return row * width + col;
    }

    private void indexChecker(int col, int row) {
        if (col < 0 || col >= width || row < 0 || row >= height) {
            throw new IllegalArgumentException("Invalid index!");
        }
    }

    /**  */
    private void initializeDigraph() {
        digraph = new EdgeWeightedDigraph(width * height + 2);
        top = width * height;
        bottom = width * height + 1;
        // top edges
        for (int col = 0; col < width; col += 1) {
            digraph.addEdge(new DirectedEdge(top, col, energy[col][0]));
        }
        // bottom edges
        for (int col = 0; col < width; col += 1) {
            digraph.addEdge(new DirectedEdge(indexChanger(col, height - 1), bottom, 0));
        }
        // middle edges
        if (width == 1) {
            for (int row = 0; row < height - 1; row += 1) {
                digraph.addEdge(new DirectedEdge(row, row + 1, energy[0][row]));
            }
        }
        else {
            for (int row = 0; row < height - 1; row += 1) {
                for (int col = 0; col < width; col += 1) {
                    int down = indexChanger(col, row + 1);
                    int cur = indexChanger(col, row);
                    digraph.addEdge(new DirectedEdge(cur, down, energy(down)));
                    if (col == 0) {
                        digraph.addEdge(new DirectedEdge(cur, down + 1, energy(down + 1)));
                    }
                    else if (col == width - 1) {
                        digraph.addEdge(new DirectedEdge(cur, down - 1, energy(down - 1)));
                    }
                    else {
                        digraph.addEdge(new DirectedEdge(cur, down + 1, energy(down + 1)));
                        digraph.addEdge(new DirectedEdge(cur, down - 1, energy(down - 1)));
                    }
                }
            }
        }
    }

    /** width of the current picture */
    public int width() {
        return width;
    }

    /** height of the current picture */
    public int height() {
        return height;
    }

    private void updateEnergy() {
        energy = new double[width][height];
        for (int col = 1; col < width - 1; col += 1) {
            for (int row = 1; row < height - 1; row += 1) {
                energy[col][row] = calEnergy(col, row);
            }
        }
        for (int col = 0; col < width; col += 1) {
            energy[col][0] = BORDER;
            energy[col][height - 1] = BORDER;
        }
        for (int row = 0; row < height; row += 1) {
            energy[0][row] = BORDER;
            energy[width - 1][row] = BORDER;
        }
    }

    private double energy(int vertex) {
        int col = vertex % width;
        int row = vertex / width;
        return energy[col][row];
    }

    /** energy of the pixel at column x and row y */
    public double energy(int x, int y) {
        indexChecker(x, y);
        return energy[x][y];
    }

    /** calculate the energy of the pixel at column i and row j */
    private double calEnergy(int i, int j) {
        return Math.sqrt(calEnergyX(i, j) + calEnergyY(i, j));
    }

    private double calEnergyX(int i, int j) {
        int left = (i - 1 + width) % width;
        int right = (i + 1) % width;
        Color colorLeft = p.get(left, j);
        Color colorRight = p.get(right, j);
        double redX = Math.pow((colorLeft.getRed() - colorRight.getRed()), 2);
        double greenX = Math.pow((colorLeft.getGreen() - colorRight.getGreen()), 2);
        double blueX = Math.pow((colorLeft.getBlue() - colorRight.getBlue()), 2);
        return redX + greenX + blueX;
    }

    private double calEnergyY(int i, int j) {
        int up = (j - 1 + height) % height;
        int down = (j + 1) % height;
        Color colorUp = p.get(i, up);
        Color colorDown = p.get(i, down);
        double redY = Math.pow((colorUp.getRed() - colorDown.getRed()), 2);
        double greenY = Math.pow((colorUp.getGreen() - colorDown.getGreen()), 2);
        double blueY = Math.pow((colorUp.getBlue() - colorDown.getBlue()), 2);
        return redY + greenY + blueY;
    }

    /** sequence of indices for horizontal seam */
    public int[] findHorizontalSeam() {
        transpose();
        int[] seam = findVerticalSeam();
        transpose();
        return seam;
    }

    /** transpose energy[][] and re-initialize digraph */
    private void transpose() {
        double[][] trans = new double[height][width];
        for (int col = 0; col < width; col += 1) {
            for (int row = 0; row < height; row += 1) {
                trans[row][col] = energy[col][row];
            }
        }
        width = trans.length;
        height = trans[0].length;
        energy = trans;
        initializeDigraph();
    }

    /** sequence of indices for vertical seam */
    public int[] findVerticalSeam() {
        DijkstraSP sp = new DijkstraSP(digraph, top);
        Stack<DirectedEdge> path = (Stack<DirectedEdge>) sp.pathTo(bottom);
        int[] seam = new int[height];
        for (int i = 0; i < height; i += 1) {
            seam[i] = path.pop().to() % width;
        }
        return seam;
    }

    /** remove horizontal seam from the current picture */
    public void removeHorizontalSeam(int[] seam) {
        validSeam(seam, width);
        Picture temp = new Picture(width, height - 1);

        for (int col = 0; col < width; col += 1) {
            int row;
            for (row = 0; row < seam[col]; row += 1) {
                temp.set(col, row, p.get(col, row));
            }
            for (row = seam[col] + 1; row < height; row += 1) {
                temp.set(col, row - 1, p.get(col, row));
            }
        }
        p = temp;
        height -= 1;
        updateEnergy();
        initializeDigraph();
    }

    /** remove vertical seam from the current picture */
    public void removeVerticalSeam(int[] seam) {
        validSeam(seam, height);
        Picture temp = new Picture(width - 1, height);

        for (int row = 0; row < height; row += 1) {
            int col;
            for (col = 0; col < seam[row]; col += 1) {
                temp.set(col, row, p.get(col, row));
            }
            for (col = seam[row] + 1; col < width; col += 1) {
                temp.set(col - 1, row, p.get(col, row));
            }
        }
        p = temp;
        width -= 1;
        updateEnergy();
        initializeDigraph();
    }


    private void validSeam(int[] seam, int expected) {
        if (seam == null) {
            throw new IllegalArgumentException("Null Seam!");
        }
        if (seam.length != expected) {
            throw new IllegalArgumentException("Incorrect length!");
        }
        for (int i = 0; i < seam.length - 1; i += 1) {
            if (Math.abs(seam[i] - seam[i + 1]) > 1) {
                throw new IllegalArgumentException("Invalid seam!");
            }
        }
        if ((expected == width && height == 1)
                || (expected == height && width == 1)) {
            throw new IllegalArgumentException("Small picture!");
        }
    }

    /** unit testing */
    public static void main(String[] args) {

    }
}
