package Test;

import main.Board;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {
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

    @org.junit.jupiter.api.Test
    public void testConstruct(){
        Board b = new Board(s);
        System.out.println(b.print());
        assertTrue(b.isValid());
    }

    @org.junit.jupiter.api.Test
    void makeMove() {
        Board b = new Board(s);
        b.makeMove(0,0, 8);
        assertFalse(b.isValid());
    }

    @org.junit.jupiter.api.Test
    void resetSquare() {
    }

    @org.junit.jupiter.api.Test
    void isValid() {
    }

    @org.junit.jupiter.api.Test
    void print() {
    }

    @org.junit.jupiter.api.Test
    void testLargerSize(){
        int[][] s = new int[16][16];
        Board b = new Board(s);
        assertEquals(16, b.getSize());

        System.out.println(b.print());
    }
}