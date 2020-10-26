package Test;

import main.Board;
import main.Solver;
import main.utils.SudokoBoardGen;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static main.utils.SudokoBoardGen.getBoard;
import static org.junit.jupiter.api.Assertions.*;

class SudokoBoardGenTest {

    @Test
    void testGetBoard() {
        Board b = getBoard(9);
        System.out.println(b.print());
        ArrayList<Board> all = (new Solver(b)).getAllSolutions(0, b, false);
        assertEquals(1, all.size());
    }

    @Test
    void testGetBoardLarge() {
        Board b = getBoard(16);
        System.out.println(b.print());
        Solver sol = new Solver(b);
        sol.solve();
        ArrayList<Board> all = (new Solver(b)).getAllSolutions(0, b, false);
        assertEquals(1, all.size());
    }

    @Test
    void testGetWithSquares(){
//        Board b = getBoard(9);
        for (int i=0; i< 10; ++i) {
            for (int j=0; j< 81; ++j) {
                Board b = SudokoBoardGen.getBoard(9, j);
                Solver s = new Solver(b);
                s.solve();
                System.out.println(j);
            }
        }

    }
}