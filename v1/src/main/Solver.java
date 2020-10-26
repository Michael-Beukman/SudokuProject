package main;

import java.util.ArrayList;

import static main.utils.ListGen.getRandomList;

public class Solver {
    Board board;
    int size;
    public boolean random = false;
    public boolean reverse = false;
    public boolean wrong=false;

    public boolean earlyStopping=true;

    private long numRecursions = 0;


    private long numberOfBasicOperations = 0; // the number of isValid() checks

    public Solver(Board board) {
        this.board = board;
        size = board.getSize();
    }



    public Board solve(){
        numRecursions = 0;
        numberOfBasicOperations = 0;
        try {
            return solveRecursive(0, board);
        } catch (Exception e) {
            System.out.println("Exception");
            return Board.InvalidBoard();
        }
    }

    private Board solveRecursive(int index, Board b) throws Exception{

        if ((Thread.currentThread().isInterrupted())) {
            System.out.println("Interrupted");
//            Thread.currentThread().stop();
            throw new Exception();
        }
        numRecursions++;
//        if (meeps[index] >= 2) System.out.println("INDEX " + index +" " + meeps[index]);
        if (index == size * size) return b; // done
        int row = index / size;
        int col = index % size;


        ArrayList<Integer> li = new ArrayList<>();
        if (random){
             li = getRandomList(size);
        }

        for (int i=1; i <= size; ++i){
            if (b.isFilled(row, col)){
                return solveRecursive(index + 1, b);
            }
            int move = i;
            if (random){
                move = li.get(i-1);
            }
            if (reverse){
                move = (size - i) + 1;
            }
            b.makeMove(row, col, move);
            numberOfBasicOperations++; // checking once here
            if (!earlyStopping || wrong || b.isValid()) {
                Board b2 = solveRecursive(index + 1, b);
                numberOfBasicOperations++; // checking here again
                if (wrong || b2.isValid()) {
                    return b2;
                }
            }
            b.resetSquare(row, col);
        }
        return Board.InvalidBoard();
    }


    public ArrayList<Board> getAllSolutions(int index, Board b, boolean stopIfMultiple){
        if (index == size * size) {
            ArrayList<Board> s = new ArrayList<Board>();
            s.add(b); // done
            return s;
        }
        int row = index / size;
        int col = index % size;
        ArrayList<Board> sols = new ArrayList<>();
        for (int i=1; i <= size; ++i){
            if (b.isFilled(row, col)){
                return getAllSolutions(index + 1, b, stopIfMultiple);
            }
            b.makeMove(row, col, i);
            if (b.isValid()) {
                ArrayList<Board> tmp = getAllSolutions(index, b, stopIfMultiple);
                sols.addAll(tmp);
            }
            b.resetSquare(row, col);
            if (stopIfMultiple && sols.size() >= 2) return sols; // stop early if there are at least 2
        }
        return sols;
    }

    public long getNumRecursions() {
        return numRecursions;

    }

    public long getNumberOfBasicOperations() {
        return numberOfBasicOperations;
    }
}
