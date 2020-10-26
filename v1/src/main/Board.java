package main;

import java.util.Arrays;

/**
 * The Sudoku board.
 */
public class Board {
    private int[][] board;

    private int size = 9;

    public Board(int[][] board) {
        this.board = board;
        size = board.length;
    }

    public Board(String string) {
        //ASSUME SIZE = 9
        size = string.split("\n").length;
//        size = 9;
        board = new   int[size][size];
        String[] rows = string.split("\n");
        int i = -1;
        for (String row : rows) {
            i++;
            int j = -1;
            String[] cols = row.split(" ");
            for (String s : cols) {
                if (s.equals("")) continue;
                j++;
                int num = Integer.parseInt(s);
                board[i][j] = num;
            }
        }
    }



    public Board(int size) {
        this(new int[size][size]);
    }

    public int getSize() {
        return size;
    }

    public static Board InvalidBoard() {
        return InvalidBoard(9);
    }

    public static Board InvalidBoard(int size) {
        int[][] arr = new int[9][9];
        for (int i = 0; i < arr.length; ++i) {
            for (int j = 0; j < arr[0].length; ++j)
                arr[i][j] = -1;
        }
        return new Board(arr);
    }

    public boolean isInvalidBoard() {
        return board[0][0] == -1;
    }

    public void makeMove(int row, int col, int number) {
        assert row < size && row >=0 && col < size && col >=0;

        board[row][col] = number;
    }

    public void resetSquare(int row, int col) {
        assert row < size && row >=0 && col < size && col >=0;

        board[row][col] = 0;
    }

    public boolean isValid() {

        return !isInvalidBoard() &&
                isSquaresValid() &&
                isRowColValid();
    }

    private boolean isSquaresValid() {
        int smallSquareSize = (int) Math.round(Math.sqrt(size));

        for (int squareNum = 0; squareNum < size; ++squareNum) {
            int rowStart = (squareNum / smallSquareSize) * smallSquareSize;
            int rowEnd = rowStart + smallSquareSize;

            int colStart = (squareNum % smallSquareSize) * smallSquareSize;
            int colEnd = colStart + smallSquareSize;

            int[] squareCounts = new int[size];
            for (int r = rowStart; r < rowEnd; ++r) {
                for (int c = colStart; c < colEnd; ++c) {
                    int num = board[r][c] - 1;
                    if (num < 0) continue;
                    if (squareCounts[num] != 0) return false;
                    ++squareCounts[num];
                }
            }
        }
        return true;
    }

    private boolean isRowColValid() {
        int[][] colNums = new int[board[0].length][size];
        for (int row = 0; row < board.length; ++row) {
            int[] rowNums = new int[size];
            for (int col = 0; col < board[row].length; ++col) {
                int num = board[row][col] - 1;
                if (num < 0) continue;
                if (rowNums[num] != 0) return false;
                if (colNums[col][num] != 0) return false;

                ++rowNums[num];
                ++colNums[col][num];
            }
        }

        return true;
    }

    public String print() {
        StringBuilder s = new StringBuilder();
        int root =(int)Math.sqrt(size);
        int count = size * 4 + root * 4 + 1;
        for (int k = 0; k < count; ++k)
            s.append("-");
        s.append("\n");
        for (int i = 0; i < board.length; i++) {
            int[] row = board[i];
            for (int i1 = 0; i1 < row.length; i1++) {
                int col = row[i1];

                if (i1 == 0) s.append("|   ");

//                s.append(col == 0 ? "." : (size == 16 ? Integer.toHexString(col) : Integer.toString(col))).append("   ");
                s.append(getDisplayFromNumber(col)).append("   ");

                if ((i1 + 1) % root == 0) s.append("|   ");
            }
            s.append("\n");
            if ((i + 1) % root == 0)
                for (int k = 0; k <count; ++k)
                    s.append("-");
            s.append("\n");
        }

        return s.toString();
    }

    String getDisplayFromNumber(int col){
        if (col == 0)   return ".";
        if (size == 16) return Integer.toHexString(col-1);
        if (size == 25){
            char s[] = {
                    'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
            };
            if (col < 10) return Integer.toString(col);
            return String.valueOf(s[col - 10]);
        }
        return Integer.toString(col);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board board1 = (Board) o;
        if (board1.size != size) return false;

        for (int i = 0; i < board.length; ++i)
            for (int j = 0; j < board[i].length; ++j)
                if (board[i][j] != board1.board[i][j]) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(board);
    }

    public boolean isFilled(int row, int col) {
        return board[row][col] > 0;
    }

    public int getSquare(int row, int col) {
        return board[row][col];
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int i=0; i<board.length; ++i){
            for (int j=0; j< board[i].length; ++j){
                s.append(board[i][j]);
                if (j != board[i].length - 1 || true) s.append(" ");
            }
            if (i != board.length-1) s.append("\n");
        }
        return s.toString();
    }

    public static Board[] fromString(String s){
        String[] things = s.split("\n\n");
        Board bs[] = new Board[things.length];
        for (int i=0; i< things.length; ++i){
            bs[i] = new Board(things[i]);
        }
        return bs;
    }

    public int countEmpty() {
        int c = 0;
        for (int[] row: board){
            for (int col: row) c+= col <= 0 ? 1 : 0;
        }
        return c;
    }
}


