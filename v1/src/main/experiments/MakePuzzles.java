package main.experiments;

import main.Board;

import java.io.FileWriter;

import static main.utils.SudokoBoardGen.getBoard;

public class MakePuzzles {
    public static void main(String[] args) {
//        Board[] fours = makePuzzle(4,100);
//        write(fours, "4_100");

//        Board[] nines = makePuzzle(9,100);
//        write(nines, "9_100");
        Board[] sixteens = makePuzzle(16,5);
//
        write(sixteens, "16_5");

//        Board[] twentyfives = makePuzzle(25,1);

//        write(twentyfives, "25_100");
//            Board[] twentyfives = makePuzzle25(25,1);
    }

    private static void write(Board[] vals, String s) {
        try {
            FileWriter myWriter = new FileWriter("src/main/data/"+s);

            for (int i = 0; i < vals.length; ++i) {
                myWriter.write(vals[i].toString());
                myWriter.write("\n\n");
            }
            myWriter.close();
        } catch (Exception e) {
            System.out.println("Ree " + e.getMessage());
        }
    }

    static Board[] makePuzzle(int size, int amount) {
        Board[] ans = new Board[amount];
        for (int i=0; i < amount; ++i){
            if (size >= 16) System.out.println(i);
            ans[i] = getBoard(size);
        }
        return ans;
    }

    static Board[] makePuzzle25(int size, int amount) {
        Board[] ans = new Board[amount];
        for (int i=0; i < amount; ++i){
            ans[i] = getBoard(size, 1);
        }
        return ans;
    }
}
