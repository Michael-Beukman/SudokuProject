package Test;

import main.Board;
import main.Solver;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SolverTest {

    @Test
    void solve() {
        String s = "" +
                "0 0 8 7 0 6 2 0 0\n" +
                "0 0 7 0 1 0 3 0 0\n" +
                "0 0 0 4 0 0 0 0 0\n" +
                "3 0 0 0 0 0 6 0 9\n" +
                "0 6 0 0 7 0 0 5 0\n" +
                "1 0 5 0 0 0 0 0 2\n" +
                "0 0 0 0 0 7 0 0 0\n" +
                "0 0 1 0 6 0 8 0 0\n" +
                "0 0 4 2 0 5 7 0 0";
        String out = "9 3 8 7 5 6 2 4 1\n" +
                "2 4 7 8 1 9 3 6 5\n" +
                "5 1 6 4 2 3 9 8 7\n" +
                "3 8 2 5 4 1 6 7 9\n" +
                "4 6 9 3 7 2 1 5 8\n" +
                "1 7 5 6 9 8 4 3 2\n" +
                "6 2 3 1 8 7 5 9 4\n" +
                "7 5 1 9 6 4 8 2 3\n" +
                "8 9 4 2 3 5 7 1 6";

        Solver solver = new Solver(new Board(s));
        Board answer = solver.solve();
        assertFalse(
                answer.isInvalidBoard()
        );

        System.out.println(answer.print());
        System.out.println("ISVALID: " + answer.isValid());

        assertEquals(answer, new Board(out));
    }

    @Test
    void testSolveLarger(){
        int[][] s = new int[16][16];
        Board b = new Board(s);
        System.out.println(b.print());
        Solver solver = new Solver(b);
        solver.random = true;
        Board newB = solver.solve();
        System.out.println(newB.print());
        assertTrue(newB.isValid());
    }
}