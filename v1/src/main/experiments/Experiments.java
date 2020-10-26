package main.experiments;

import main.Board;
import main.Solver;
import main.utils.DataPoint;
import main.utils.ListGen;
import main.utils.SudokoBoardGen;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

public class Experiments {
    public static void main(String[] args) {
//        v1();
//        v2();
//        v3();
//        System.out.println(getName("mee"));
//        int size=16;
//        int count=100;
//        double[] arr = v4(size,  count);
//        makeCSV(getName("v4_best_case_" + size+"_"+count), arr);

//        int size=9;
//        int count=10;
//        double[] arr = v5WorstCase(size,  count, 47);
//        makeCSV(getName("v5_worst_case_46_" + size+"_"+count), arr);

//        int size=16;
//        int count=2;
//        int numToDo=7;
//
//        double[] arr = v6WorstCaseNoStopping(size,  count, numToDo);
//        makeCSV(getName("v6_worst_case_no_stop_"+count+"_"+numToDo+"_" + size+"_"+count), arr);

//        int size = 16;
//        int count = 1000;
//        int numToDo=size * size;
//
//        double[] arr = v7WorstCaseWithStopping(size,  count, numToDo);
//        makeCSV(getName("v7_worst_case_stop_"+count+"_"+numToDo+"_" + size+"_"+count), arr);


//        double[] arr = new double[3];
//        int[] inputs = new int[]{4, 9, 16};
//        for (int s=2; s<=4; ++s) {
//            int size = s * s;
//            String filename = "src/main/data/" + size + "_100";
//
//            double ans = v8DoFromFiles(filename);
//            arr[s-2] = ans;
//        }
//        makeCSV(getName("v8fromFiles" + "_4_9_16" ),  inputs, arr);
        if (false){
            meep();
            return;
        }
        // Best case things
//        for (int s=2; s<=4; ++s) {
////            if (s == 2) continue;
//            int size = s * s;
//            int count = size == 16 ? 1000 : 1000;
//            count = 100;
//            int numToDo = size * size;
//            DataPoint[] arr = v9TryBestCase(size, count, numToDo);
//            System.out.println(Arrays.toString(arr));
//            makeCSV("v9_rando_" + count + "_" + numToDo + "_" + size + "_" + count, arr);
//        }

        // BEST CASE ONES
//        for (int s=2; s<=4; ++s) {
////            if (s == 2) continue;
//            int size = s * s;
//            int count = size == 16 ? 1000 : 1000;
//            count = 1;
//            int numToDo = size * size;
//            DataPoint[] arr = v10TryBestCaseRandom(size, count, numToDo);
//            System.out.println(Arrays.toString(arr));
//            makeCSV("v10_actual_best_again_" + count + "_" + numToDo + "_" + size + "_" + count, arr);
//        }
        // AVERAGE CASE
//        for (int s=2; s<=4; ++s) {
//            if (s <=3) continue;
//            int size = s * s;
//            int count = size == 16 ? 1000 : 1000;
//            count = 10;
//            int numToDo = size * size;
//            DataPoint[] arr = v11AverageCase(size, count, 135);
//            System.out.println(Arrays.toString(arr));
//            makeCSV("v11_average_" + count + "_" + numToDo + "_" + size + "_" + count, arr);
//        }


        // v12

        for (int s=2; s<=4; ++s) {
            int size = s * s;
            String filename = "src/main/data/" + size + "_100";
            double[] ans = v12DoFromFilesStats(filename);
            makeCSV(getName("v12fromFiles" + "_" + size ), ans);
        }
    }

    private static void v1() {
        double average4 = doExp(4, 100);
        System.out.println(average4);

        double average9 = doExp(9, 100);
        System.out.println(average9);
        double average16 = doExp(16, 100);
        System.out.println(average16);
    }

    public static void v2() {
        for (int i=2; i<=3; ++i){
            double d[] = doOne(i*i, 5);
            System.out.println(Arrays.toString(d));
        }
    }

    public static void v3(){
        int count = 100;
        int size = 9;
        int small = (int)Math.sqrt(size);
        double[] times = new double[size * size];
        for (int i=0; i< count; ++i){
            Board b = new Board(size);
            Solver s = new Solver(b);
            s.random = true;
            b = s.solve();
            ArrayList<Integer> indices = ListGen.getRandomList(size * size);
            for (int j=0; j< size*size; ++j){
                System.out.println(j);
                // remove all indices up to and including j
                Board newBoard = new Board(b.toString());
                for (int k=0; k< j; ++k){
                    int index = indices.get(k) - 1;
                    int row = index / size;
                    int col = index % size;
                    newBoard.resetSquare(row, col);
                }

                // now solve it
                Solver solver = new Solver(newBoard);
                long start = System.currentTimeMillis();
                Board k  = solver.solve();
                System.out.println(k.print());
                long end = System.currentTimeMillis();
                times[j] += (double)((end - start)/1000.0);
                System.out.println(times[j]);
            }
        }
        System.out.println(Arrays.toString(times));
    }

    public static double[] v4(int size, int count){
        // try best case
        int small = (int)Math.sqrt(size);
        double[] times = new double[size * size];
        for (int i=0; i< count; ++i){
            Board b = new Board(size);
            Solver s = new Solver(b);

            // not random!

            b = s.solve();
            ArrayList<Integer> indices = ListGen.getRandomList(size * size);
            indices.sort(null); // go in order
            for (int j=0; j< size*size; ++j){
//                System.out.println(j);
                // remove all indices up to and including j
                Board newBoard = new Board(b.toString());
                for (int k=0; k< j; ++k){
                    int index = indices.get(k) - 1;
                    int row = index / size;
                    int col = index % size;
                    newBoard.resetSquare(row, col);
                }
                // now solve it
                Solver solver = new Solver(newBoard);
                long start = System.currentTimeMillis();
                Board k  = solver.solve();
//                System.out.println(k.print());
                long end = System.currentTimeMillis();
                times[j] += (double)((end - start)/1000.0);
                System.out.println(times[j]);
            }
        }
        return times;
//        System.out.println(Arrays.toString(times));
    }

    public static double[] v5WorstCase(int size, int count, int numToDo){
        // try best case
        int small = (int)Math.sqrt(size);
        double[] times = new double[numToDo];
        for (int i=0; i< count; ++i){
            Board b = new Board(size);
            Solver s = new Solver(b);

            s.reverse = true; // make reverse for worst case
            b = s.solve();
//            System.out.println(b.print());

            ArrayList<Integer> indices = ListGen.getRandomList(size * size);
            indices.sort(null); // go in order
            for (int j=0; j< numToDo; ++j){
                System.out.println(j);
                // remove all indices up to and including j
                Board newBoard = new Board(b.toString());
                for (int k=0; k< j; ++k){
                    int index = indices.get(k) - 1;
                    int row = index / size;
                    int col = index % size;
                    newBoard.resetSquare(row, col);
                }
                // now solve it
                Solver solver = new Solver(newBoard);
                long start = System.currentTimeMillis();
                Board k  = solver.solve();
//                System.out.println(k.print());
                long end = System.currentTimeMillis();
                times[j] += (double)((end - start)/1000.0);
//                System.out.println(times[j]);
            }
        }
        return times;
//        System.out.println(Arrays.toString(times));
    }

    public static double[] v6WorstCaseNoStopping(int size, int count, int numToDo){
        // try best case
        int small = (int)Math.sqrt(size);
        double[] times = new double[numToDo];
        for (int i=0; i< count; ++i){
            Board b = new Board(size);
            Solver s = new Solver(b);

            s.reverse = true; // make reverse for worst case
            b = s.solve();
//            System.out.println(b.print());

            ArrayList<Integer> indices = ListGen.getRandomList(size * size);
            indices.sort(null); // go in order
            for (int j=0; j< numToDo; ++j){
                System.out.println(j);
                // remove all indices up to and including j
                Board newBoard = new Board(b.toString());
                for (int k=0; k< j; ++k){
                    int index = indices.get(k) - 1;
                    int row = index / size;
                    int col = index % size;
                    newBoard.resetSquare(row, col);
                }
                // now solve it
                Solver solver = new Solver(newBoard);
                solver.earlyStopping=false;
                long start = System.currentTimeMillis();
                Board k  = solver.solve();
//                System.out.println(k.print());
                long end = System.currentTimeMillis();
                times[j] += (double)((end - start)/1000.0);
//                System.out.println(times[j]);
            }
        }
        return times;
//        System.out.println(Arrays.toString(times));
    }

    public static double[] v7WorstCaseWithStopping(int size, int count, int numToDo){
        // try best case
        int small = (int)Math.sqrt(size);
        double[] times = new double[numToDo];
        for (int i=0; i< count; ++i){
            Board b = new Board(size);
            Solver s = new Solver(b);

            s.reverse = false; // make reverse for worst case
            b = s.solve();
//            System.out.println(b.print());

            ArrayList<Integer> indices = ListGen.getRandomList(size * size);
            indices.sort(Comparator.reverseOrder()); // go in order

            System.out.println(i);
            for (int j=0; j< numToDo; ++j){
//                System.out.println(j);
                // remove all indices up to and including j
                Board newBoard = new Board(b.toString());
                for (int k=0; k< j; ++k){
                    int index = indices.get(k) - 1;
                    int row = index / size;
                    int col = index % size;
                    newBoard.resetSquare(row, col);
                }
                // now solve it
//                System.out.println(newBoard.print());
                Solver solver = new Solver(newBoard);
                // ArrayList<Board> allBoards = solver.getAllSolutions(0, newBoard, true);
                // System.out.println("LENGTH = " + " " + j  + " "  +  allBoards.size());
                long start = System.nanoTime();
                Board k  = solver.solve();
                System.out.println("j = " + j + " numrecurs " +solver.getNumRecursions() );
                long end = System.nanoTime();

                times[j] += ((end - start)/1000.0 / count);
//                System.out.println(times[j]);
            }
        }
        return times;
//        System.out.println(Arrays.toString(times));
    }

    public static double v8DoFromFiles(String filename){
        String content = readFile(filename, StandardCharsets.US_ASCII);
        Board[] boards = Board.fromString(content);
        double total = 0;
        for (int i = 0; i < boards.length; i++) {
            Board b = boards[i];
            Solver s = new Solver(b);
            long start = System.nanoTime();

            s.solve();

            long end = System.nanoTime();
            total += (end- start)/1000.0;
        }
        return total / boards.length;
    }

    public static double[] v12DoFromFilesStats(String filename){
        String content = readFile(filename, StandardCharsets.US_ASCII);
        Board[] boards = Board.fromString(content);
        double total[] = new double[boards.length];
        for (int i = 0; i < boards.length; i++) {
            Board b = boards[i];
            Solver s = new Solver(b);
            long start = System.nanoTime();

            s.solve();

            long end = System.nanoTime();
            total[i] += (end- start)/1000.0;
        }
        return total; // now per thing
    }


    public static DataPoint[] v9TryBestCase(int size, int count, int numToDo){
        // try best case
        int small = (int)Math.sqrt(size);
        double[] times = new double[numToDo];
        double[] numRecursed = new double[numToDo];
        double[] numBasicOps = new double[numToDo];

        for (int i=0; i< count; ++i){
//            System.out.println("Count " + i);
            Board b = new Board(size);
            Solver s = new Solver(b);
            // not random!
            b = s.solve();
//            System.out.println(b.print());
            ArrayList<Integer> indices = ListGen.getRandomList(size * size);
            // indices.sort(Comparator.reverseOrder()); // go in order
            for (int j=0; j< size*size; ++j){
                Board newBoard = new Board(b.toString());
                for (int k=0; k< j; ++k){
                    int index = indices.get(k) - 1;
                    int row = index / size;
                    int col = index % size;
                    newBoard.resetSquare(row, col);
                }
                // now solve it
//                System.out.println(newBoard.print());
                Solver solver = new Solver(newBoard);
//                System.out.println(newBoard.print());
                long start = System.nanoTime();
                Board k  = solver.solve();
                long end = System.nanoTime();
                times[j] += ((end - start)/1000.0);
                numRecursed[j] += solver.getNumRecursions();
                numBasicOps[j] += solver.getNumberOfBasicOperations();

            }
        }
        DataPoint ds[] = new DataPoint[numToDo];
        for (int i=0; i< numToDo; ++i){
            times[i] /= count;
            numRecursed[i] /= (count+0.0);
            numBasicOps[i] /= (count+0.0);
            ds[i] = new DataPoint(i, numRecursed[i], numBasicOps[i], times[i]);
        }

        return ds;
    }


    public static DataPoint[] v10TryBestCaseRandom(int size, int count, int numToDo){
        // try best case
        int small = (int)Math.sqrt(size);
        double[] times = new double[numToDo];
        double[] numRecursed = new double[numToDo];
        double[] numBasicOps = new double[numToDo];
        int[] counts = new int[numToDo];
        for (int i=0; i< count; ++i){
//            System.out.println("Count " + i);
            Board b = new Board(size);
            Solver s = new Solver(b);
            // not random!
            b = s.solve();
//            System.out.println(b.print());
            ArrayList<Integer> indices = ListGen.getRandomList(size * size);
            // indices.sort(Comparator.reverseOrder()); // go in order
            for (int j=0; j< size*size; ++j){
                Board newBoard = new Board(b.toString());
                for (int k=0; k< j; ++k){
                    int index = indices.get(k) - 1;
                    int row = index / size;
                    int col = index % size;
                    newBoard.resetSquare(row, col);
                }
                int empty = j;
                if (size == 9){
                    // super hacky way to do things
                    newBoard.makeMove(1,3, 7);
                    newBoard.makeMove(1,4, 8);
                    newBoard.makeMove(1,5, 9);
                    newBoard.makeMove(3,8, 7);
                    newBoard.makeMove(4,4, 9);
                    newBoard.makeMove(4,6, 2);
                    newBoard.makeMove(7,5, 8);
                    newBoard.makeMove(7,6, 5);
                    newBoard.makeMove(4,8, 4);

                    // count empty squares
                    empty = newBoard.countEmpty();
//                    System.out.println("J = " + j + " empty = " + empty);
                }
                // now solve it
                Solver solver = new Solver(newBoard);
//                System.out.println(newBoard.print());
                long start = System.nanoTime();
                Board k  = solver.solve();
                long end = System.nanoTime();

                times[empty] += ((end - start)/1000.0);
                numRecursed[empty] += solver.getNumRecursions();
                numBasicOps[empty] += solver.getNumberOfBasicOperations();
                counts[empty] ++;

            }
        }
        DataPoint ds[] = new DataPoint[numToDo];
        for (int i=0; i< numToDo; ++i){
            if (counts[i] != 0) {
                times[i] /= counts[i];
                numRecursed[i] /= (counts[i] + 0.0);
                numBasicOps[i] /= (counts[i] + 0.0);
                ds[i] = new DataPoint(i, numRecursed[i], numBasicOps[i], times[i]);
            }else{
                ds[i] = new DataPoint(i, 0, 0, 0);
            }
        }

        return ds;
    }

    public static DataPoint[] v11AverageCase(int size, int count, int numToDo){
        // Here we do an average case study. The board will be randomly generated, and solved normally.

        double[] times = new double[numToDo];
        double[] numRecursed = new double[numToDo];
        double[] numBasicOps = new double[numToDo];
        int[] counts = new int[numToDo];
        for (int i=0; i< count; ++i){
            System.out.println("Size " + size + " count " + i);
            Board b = new Board(size);
            Solver s = new Solver(b);
            s.random = true;

            b = s.solve();
            ArrayList<Integer> indices = ListGen.getRandomList(size * size);

            for (int j=0; j< numToDo; ++j){
                System.out.println("Size " + size + " index " + j);
                Board newBoard = new Board(b.toString());
                for (int k=0; k< j; ++k){
                    int index = indices.get(k) - 1;
                    int row = index / size;
                    int col = index % size;
                    newBoard.resetSquare(row, col);
                }
                // now solve it
                Solver solver = new Solver(newBoard);
//                System.out.println(newBoard.print());
                long start = System.nanoTime();
                Board k  = solver.solve();
                long end = System.nanoTime();

                times[j] += ((end - start)/1000.0);
                numRecursed[j] += solver.getNumRecursions();
                numBasicOps[j] += solver.getNumberOfBasicOperations();

            }
        }
        DataPoint ds[] = new DataPoint[numToDo];
        for (int i=0; i< numToDo; ++i){
            counts[i] = count;
            times[i] /= counts[i];
            numRecursed[i] /= (counts[i] + 0.0);
            numBasicOps[i] /= (counts[i] + 0.0);
            ds[i] = new DataPoint(i, numRecursed[i], numBasicOps[i], times[i]);
        }
        return ds;
    }

    public static void meep(){
        Board b = new Board(4);
        Solver s = new Solver(b);
        s.reverse = true;
        s.solve();
        System.out.println(s.getNumRecursions());
//        System.out.println(Arrays.toString(s.meeps));
//
//
        b = new Board(9);
//        b.makeMove(0,0, 1); // todo make puzzle so that it is good for 9x9
        b.makeMove(1,3, 7);
        b.makeMove(1,4, 8);
        b.makeMove(1,5, 9);
        b.makeMove(3,8, 7);
        b.makeMove(4,4, 9);
        b.makeMove(4,6, 2);
        b.makeMove(7,5, 8);
        b.makeMove(7,6, 5);
        b.makeMove(4,8, 4);
//        b.makeMove(1,6, 1); // todo make puzzle so that it is good for 9x9
//        b.makeMove(1,3, 7); // todo make puzzle so that it is good for 9x9
        s = new Solver(b);
//        s.reverse = true;

        Board d2 = s.solve();
        System.out.println(s.getNumRecursions());
//        System.out.println(Arrays.toString(s.meeps));
//        for (int i=0; i< s.meeps.length; ++i){
//            if (s.meeps[i] > 1){
//                System.out.println(i+ " "+ i / 9+ " " + i % 9+  " " + d2.getSquare(i/9, i%9) + " -" +s.meeps[i] );
//            }
//        }
        System.out.println(d2.print());


        // 9x9 again
        b = s.solve();
//            System.out.println(b.print());
        ArrayList<Integer> indices = ListGen.getRandomList(81);
        // indices.sort(Comparator.reverseOrder()); // go in order
        Board newBoard = new Board(b.toString());
        for (int k=0; k< 65; ++k){
            int index = indices.get(k) - 1;
            int row = index / 9;
            int col = index % 9;
            newBoard.resetSquare(row, col);
        }
        Solver kk = new Solver(newBoard);
        kk.solve();
        System.out.println(kk.getNumRecursions());


        b = new Board(16);
        s = new Solver(b);

        Board d = s.solve();
        System.out.println(s.getNumRecursions());
//        System.out.println(Arrays.toString(s.meeps));
        System.out.println(d.print());


    }

    public static double[] doOne(int size, int count){
        int numToDo = Math.min(size*size, 82);
        double[] arr = new double[size*size];

        for (int i =0 ;i < numToDo; ++i){
            if (size == 9) System.out.println(i);
            double a = doExp(size, count, i);
            arr[i] = a;
        }
        return arr;
    }

    private static double doExp(int size, int count) {

        double total = 0;
        for (int i=0;i<count; ++i){
            Board b = SudokoBoardGen.getBoard(size);
            Solver s = new Solver(b);
//            System.out.println("Done with making");
            long start = System.currentTimeMillis();
            Board newBoard = s.solve();
            // assert newBoard.isValid();
            long end = System.currentTimeMillis();
            total += end - start;
        }

        return total/count / 1000.0;
    }

    private static double doExp(int size, int count, int numEmptyCells){
        double total = 0;
        for (int i=0;i<count; ++i){
//            System.out.println(i);
            Board b = SudokoBoardGen.getBoard(size, numEmptyCells);
            Solver s = new Solver(b);
            long start = System.currentTimeMillis();
            Board newBoard = s.solve();
            // assert newBoard.isValid();
            long end = System.currentTimeMillis();
            total += end - start;
        }
        return total/count / 1000.0;
    }

    static void makeCSV(String name, int[] inputs, double[] times) {
        try {
            FileWriter myWriter = new FileWriter("csvs/" + name);

            myWriter.write("input,time\n");
            for (int i = 0; i < inputs.length; ++i) {
                myWriter.write(inputs[i] + "," + times[i] + "\n");
            }
            myWriter.close();
        } catch (Exception e) {
            System.out.println("Ree " + e.getMessage());
        }
    }

    static void makeCSV(String name, double[] times) {
            int[] inps = new int[times.length];
            for (int i=0; i< inps.length; ++i){
                inps[i] = i;
            }
            makeCSV(name, inps, times);
    }


    public static String getName(String n){
        SimpleDateFormat DateFor = new SimpleDateFormat("dd-MM-yyyy");
        String s = DateFor.format(new Date());
        return n + '-' +(s)+".csv";
    }

    static String readFile(String path, Charset encoding)
    {
        byte[] encoded = new byte[0];
        try {
            encoded = Files.readAllBytes(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(encoded, encoding);
    }

    static void makeCSV(String name, DataPoint[] ins){
        int ns[] = new int[ins.length];
        double recurses[] = new double[ins.length];
        double basicOps[] = new double[ins.length];
        double times[] = new double[ins.length];
        for (int i=0; i< ins.length; ++i){
            ns[i] = ins[i].n;
            recurses[i] = ins[i].numRecursions;
            basicOps[i] = ins[i].numBasicOps;
            times[i] = ins[i].time;
        }
        makeCSV(getName(name + "_recurses"), ns, recurses);
        makeCSV(getName(name + "basicOps"), ns, basicOps);
        makeCSV(getName(name + "times"), ns, times);
    }
}
