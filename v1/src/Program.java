import main.Board;
import main.Solver;

import java.util.Scanner;

public class Program {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        StringBuilder s = new StringBuilder();
        while (scanner.hasNext()) {

            s.append(scanner.nextLine());
            if (scanner.hasNext()) s.append("\n");
        }

        Board b = new Board(s.toString());
        Solver solver = new Solver(b);
        solver.reverse = true;
//        solver.wrong = true;
//        solver.random = true;
        Board ans = solver.solve();
        System.out.println(ans.toString());
    }
}

