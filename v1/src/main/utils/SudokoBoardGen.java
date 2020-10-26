package main.utils;

import main.Board;
import main.Solver;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates a sudoku board
 */
public class SudokoBoardGen {
    public static Board getBoard(int size){
        Board tmp = new Board(size);
        Solver solver = new Solver(tmp);
        solver.random = true;

        solver.solve();
        // now with the solved board, remove things and see if it still permits a unique solution
        boolean canStop = false;

        ArrayList<Integer> indices = ListGen.getRandomList(tmp.getSize() * tmp.getSize());
        int row=-1, col=-1;
        int num=-1;
        while (!canStop) {
            int index = indices.get(0) - 1; // because from 0 to 9
            indices.remove(0);

            row = index / size;
            col = index % size;
            num = tmp.getSquare(row, col);
            tmp.resetSquare(row, col);
            ArrayList<Board> allBoards = solver.getAllSolutions(0, tmp, true);
            canStop = allBoards.size() >= 2;
        }

        // go until it isn't unique anymore
        tmp.makeMove(row, col, num);
        return tmp;
    }

    public static Board getBoard(int size, int numEmptySquares){
        // Might not permit a unique solution
        Board tmp = new Board(size);
        Solver solver = new Solver(tmp);
        solver.random = true;

        solver.solve();
        ArrayList<Integer> indices = ListGen.getRandomList(tmp.getSize() * tmp.getSize());
        int row=-1, col=-1;
        for (int i=0; i < numEmptySquares; ++i) {
            int index = indices.get(0) - 1; // because from 1 to 9 -> 0 to 8
            indices.remove(0);

            row = index / size;
            col = index % size;
            tmp.resetSquare(row, col);
        }

        return tmp;
    }
}
