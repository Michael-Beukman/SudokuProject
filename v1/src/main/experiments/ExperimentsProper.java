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
import java.util.concurrent.*;

public class ExperimentsProper {

    static boolean verbose=false;
    static String mode = "v3";
    static String[] modes = {"v5"};
    public static void main(String[] args) {
        for (String mode : modes) {
            System.out.println("MODE IS " + mode);
            if (mode.equals("v1")) {
                for (int s = 2; s <= 4; ++s) {
                    int size = s * s;
                    if (size == 16) verbose = true;
//                else continue;
                    DataPoint arr[] = v1_best_case(size, 1000);
                    System.out.println(Arrays.toString(arr));
                    String name = "v1_best_case_size" + size + "_count_" + 1000;
                    makeCSV(name, arr);
                    System.out.println("Done with v1::" + size);
                }
            } else if (mode.equals("v2")) {
                for (int s = 2; s <= 4; ++s) {
                    int size = s * s;
//                    if (size < 16) continue;
                    if (size >= 0) verbose = true;
                    int count = 20;
                    int numToDo = 9;
                    DataPoint arr[] = v2_worst_case_no_stopping(size, count, numToDo);
                    System.out.println(Arrays.toString(arr));
                    String name = "v2_worst_case_size" + size + "_count_" + count + "_numtodo_" + numToDo;
                    makeCSV(name, arr);
                    System.out.println("Done with v2::" + size);
                }
            } else if (mode.equals("v3")) {
                for (int s = 2; s <= 4; ++s) {
                    int size = s * s;
//                    if (size < 16) continue;
                    if (size >= 0) verbose = true;
                    int count = 20;
                    int numToDo = 10;
                    if (size == 9)
                        numToDo = 65;
                    else if (size == 16)
                        numToDo = 120;

                    DataPoint arr[] = v3_average_case_lots(size, count, numToDo);
                    System.out.println(Arrays.toString(arr));
                    String name = "v3_average_case_size" + size + "_count_" + count + "_numtodo_" + numToDo;
                    makeCSV(name, arr);
                    System.out.println("Done with v3::" + size);
                }
            } else if (mode.equals("v4")) {
                for (int s = 2; s <= 4; ++s) {
                    int size = s * s;
                    if (size >= 0) verbose = true;
                    int count = 20;
                    DataPoint arr[] = v4_simple_boards("src/main/data/" + size + "_100", count);
                    System.out.println(Arrays.toString(arr));
                    String name = "v4_boards" + size + "_count_" + count;
                    makeCSV(name, arr);
                    System.out.println("Done with v4::" + size);
                }
            }
            else if (mode.equals("v5")) {
                for (int s = 2; s <= 4; ++s) {
                    int size = s * s;
                    if (size >= 0) verbose = true;
                    int count = 100;
                    int numToDo = 15;
                    if (size == 9)
                        numToDo = 70;
                    else if (size == 16)
                        numToDo = 130;

                    DataPoint arr[] = v5_average_case_lots_timeout(size, count, numToDo);
                    System.out.println(Arrays.toString(arr));
                    String name = "v5_average_case_size" + size + "_count_" + count + "_numtodo_" + numToDo;
                    makeCSV(name, arr);
                    System.out.println("Done with v5::" + size);
                }
            }
        }
    }


    /**
     * This tries to do the best case.
     * @param size
     * @param count
     * @return
     */
    public static DataPoint[] v1_best_case(int size, int count){
        // try best case, i.e. where it doesn't recurse a lot
        int numToDo = size * size;
        double[] times = new double[numToDo];
        double[] numRecursed = new double[numToDo];
        double[] numBasicOps = new double[numToDo];
        int[] counts = new int[numToDo];
        for (int i=0; i< count; ++i){
            if (verbose) System.out.println("In count " + i +"/" + count);
            Board b = new Board(size);
            Solver s = new Solver(b);
            b = s.solve();
            ArrayList<Integer> indices = ListGen.getRandomList(size * size);
            indices.sort(Comparator.reverseOrder()); // go in order

            for (int j=0; j< size*size; ++j){
                Board newBoard = new Board(b.toString());
                // clear board
                for (int k=0; k< j; ++k){
                    int index = indices.get(k) - 1;
                    int row = index / size;
                    int col = index % size;
                    newBoard.resetSquare(row, col);
                }
                int empty = j;
                if (size == 9){
                    // super hacky way to do things to make it proper
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
                long start = System.nanoTime();
                Board k  = solver.solve();
                long end = System.nanoTime();

                times[empty] += ((end - start)/1000.0);
                numRecursed[empty] += solver.getNumRecursions();
                numBasicOps[empty] += solver.getNumberOfBasicOperations();
                counts[empty] ++;

            }
        }
        int numberOfPoints = 0;
        ArrayList<Integer> indices = new ArrayList<>();
        for (int i=0; i< numToDo; ++i){
            if (counts[i] != 0) {
                numberOfPoints++;
                indices.add(i);
                times[i] /= counts[i];
                numRecursed[i] /= (counts[i] + 0.0);
                numBasicOps[i] /= (counts[i] + 0.0);
            }
        }
        DataPoint ds[] = new DataPoint[numberOfPoints];
        for (int i: indices) {
            ds[i] = new DataPoint(i, numRecursed[i], numBasicOps[i], times[i]);
        }
        return ds;
    }

    public static DataPoint[] v2_worst_case_no_stopping(int size, int count, int numToDo){
        // try best case, i.e. where it doesn't recurse a lot

        double[] times = new double[numToDo];
        double[] numRecursed = new double[numToDo];
        double[] numBasicOps = new double[numToDo];
        int[] counts = new int[numToDo];
        for (int i=0; i< count; ++i){
            if (verbose) System.out.println("In count " + i +"/" + count);
            Board b = new Board(size);
            Solver s = new Solver(b);
            b = s.solve();
            ArrayList<Integer> indices = ListGen.getRandomList(size * size);
            indices.sort(Comparator.reverseOrder()); // go in order

            for (int j=0; j< numToDo; ++j){
                Board newBoard = new Board(b.toString());
                // clear board
                for (int k=0; k< j; ++k){
                    int index = indices.get(k) - 1;
                    int row = index / size;
                    int col = index % size;
                    newBoard.resetSquare(row, col);
                }

                // now solve it
                Solver solver = new Solver(newBoard);
                solver.earlyStopping = false;
                long start = System.nanoTime();
                Board k  = solver.solve();
                long end = System.nanoTime();

                times[j] += ((end - start)/1000000.0);
                numRecursed[j] += solver.getNumRecursions();
                numBasicOps[j] += solver.getNumberOfBasicOperations();
                counts[j] ++;

            }
        }
        int numberOfPoints = 0;
        ArrayList<Integer> indices = new ArrayList<>();
        System.out.println(Arrays.toString(counts));
        for (int i=0; i< numToDo; ++i){
            if (counts[i] != 0) {
                numberOfPoints++;
                indices.add(i);
                times[i] /= counts[i];
                numRecursed[i] /= (counts[i] + 0.0);
                numBasicOps[i] /= (counts[i] + 0.0);
            }
        }
        DataPoint ds[] = new DataPoint[numberOfPoints];
        for (int i: indices) {
            ds[i] = new DataPoint(i, numRecursed[i], numBasicOps[i], times[i]);
        }
        return ds;
    }

    public static DataPoint[] v3_average_case_lots(int size, int count, int numToDo){
        // Try random boards and make it go normally

        double[] times = new double[numToDo];
        double[] numRecursed = new double[numToDo];
        double[] numBasicOps = new double[numToDo];
        int[] counts = new int[numToDo];
        for (int i=0; i< count; ++i){
            if (verbose) System.out.println("In count " + i +"/" + count);
            Board b = new Board(size);
            Solver s = new Solver(b);
            s.random = true; // make random so that it is a better reflection.
            long tmpS = System.nanoTime();
            b = s.solve();
            long endS = System.nanoTime();
            if (verbose)
                System.out.println("Making map " + (endS - tmpS)/1000000000.0);
            ArrayList<Integer> indices = ListGen.getRandomList(size * size); // randomly sorted
            for (int j=0; j< numToDo; ++j){
                Board newBoard = new Board(b.toString());
                // clear board
                for (int k=0; k< j; ++k){
                    int index = indices.get(k) - 1;
                    int row = index / size;
                    int col = index % size;
                    newBoard.resetSquare(row, col);
                }

                // now solve it
                Solver solver = new Solver(newBoard);
                long start = System.nanoTime();
                Board k  = solver.solve();
                long end = System.nanoTime();
//                if (verbose) System.out.println("Doing thing " + (end - start)/1000000000.0);
                times[j] += ((end - start)/1000000.0);
                numRecursed[j] += solver.getNumRecursions();
                numBasicOps[j] += solver.getNumberOfBasicOperations();
                counts[j] ++;

            }
        }
        int numberOfPoints = 0;
        ArrayList<Integer> indices = new ArrayList<>();
        System.out.println(Arrays.toString(counts));
        for (int i=0; i< numToDo; ++i){
            if (counts[i] != 0) {
                numberOfPoints++;
                indices.add(i);
                times[i] /= counts[i];
                numRecursed[i] /= (counts[i] + 0.0);
                numBasicOps[i] /= (counts[i] + 0.0);
            }
        }
        DataPoint ds[] = new DataPoint[numberOfPoints];
        for (int i: indices) {
            ds[i] = new DataPoint(i, numRecursed[i], numBasicOps[i], times[i]);
        }
        return ds;
    }

    /**
     * This works with a 1 minute timeout, so yeah.
     * @param size
     * @param count
     * @param numToDo
     * @return
     */
    public static DataPoint[] v5_average_case_lots_timeout(int size, int count, int numToDo){
        // Try random boards and make it go normally

        double[] times = new double[numToDo];
        double[] numRecursed = new double[numToDo];
        double[] numBasicOps = new double[numToDo];
        int[] counts = new int[numToDo];
        for (int i=0; i< count; ++i){
            if (verbose)
                System.out.println("In count " + i +"/" + count);

            long tmpS = System.nanoTime();
            Board b = getBoardWithTimeOut(size);
            long endS = System.nanoTime();
            if (verbose)
                System.out.println("Making map " + (endS - tmpS)/1000000000.0);
            ArrayList<Integer> indices = ListGen.getRandomList(size * size); // randomly sorted
            for (int j=0; j< numToDo; ++j){
                Board newBoard = new Board(b.toString());
                // clear board
                for (int k=0; k< j; ++k){
                    int index = indices.get(k) - 1;
                    int row = index / size;
                    int col = index % size;
                    newBoard.resetSquare(row, col);
                }

                // now solve it
                Solver solver = new Solver(newBoard);
//                long start = System.nanoTime();
//                Board k  = solver.solve();
//                long end = System.nanoTime();
                long duration = doWithTimeout(solver);
//                if (verbose) System.out.println("Doing thing " + (end - start)/1000000000.0);
//                System.out.println("Solver has this many recursions " + solver.getNumRecursions() + " basic= " + solver.getNumberOfBasicOperations());
                times[j] += ((duration)/1000000.0);
                numRecursed[j] += solver.getNumRecursions();
                numBasicOps[j] += solver.getNumberOfBasicOperations();
                counts[j] ++;

            }
        }
        int numberOfPoints = 0;
        ArrayList<Integer> indices = new ArrayList<>();
        System.out.println(Arrays.toString(counts));
        for (int i=0; i< numToDo; ++i){
            if (counts[i] != 0) {
                numberOfPoints++;
                indices.add(i);
                times[i] /= counts[i];
                numRecursed[i] /= (counts[i] + 0.0);
                numBasicOps[i] /= (counts[i] + 0.0);
            }
        }
        DataPoint ds[] = new DataPoint[numberOfPoints];
        for (int i: indices) {
            ds[i] = new DataPoint(i, numRecursed[i], numBasicOps[i], times[i]);
        }
        return ds;
    }


    public static DataPoint[] v4_simple_boards(String filename, int count){
        String content = readFile(filename, StandardCharsets.US_ASCII);
        Board[] boards = Board.fromString(content);
        ArrayList<DataPoint> dps = new ArrayList<>();
        for (Board b: boards) {
            DataPoint dp = new DataPoint(b.countEmpty(), 0,0,0);
            for (int i = 0; i < count; ++i) {
                Board to_solve = new Board(b.toString());
                Solver s = new Solver(to_solve);
                long start = System.nanoTime();
                Board ans = s.solve();
                long end = System.nanoTime();
                assert ans.isValid();

                dp.numRecursions += s.getNumRecursions();
                dp.numBasicOps += s.getNumberOfBasicOperations();
                dp.time += (end-start)/1000000.0;
            }
            dp.numRecursions /= (count+ 0.0);
            dp.numBasicOps /= (count+ 0.0);
            dp.time /= (count+ 0.0);
            dps.add(dp);
        }

        return (DataPoint[])dps.toArray(new DataPoint[dps.size()]);
    }


    /**
     * Returns a time.
     * @param s
     * @return
     */
    public static long doWithTimeout(Solver s){

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Solver> future = executor.submit(() -> {
            s.solve();
            return s;
        });
//        long timeOutNanoSec = (long)60 * 1000 * 1000 * 1000;
        long timeOutNanoSec = (long)10 * 1000 * 1000 * 1000;
        try {
            long tmpS = System.nanoTime();
            future.get(timeOutNanoSec, TimeUnit.NANOSECONDS);
            long endS = System.nanoTime();
            return endS - tmpS;
        } catch (TimeoutException e) {
            future.cancel(true);
            System.out.println("Took longer than 1 minute");
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("bad 1");
        } catch (ExecutionException e) {
            e.printStackTrace();
            System.out.println("bad 2");
        }

        executor.shutdownNow();
        return timeOutNanoSec;
}


    public static Board getBoardWithTimeOut(int size){

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Board> future = executor.submit(() -> {
            Board b = new Board(size);
            Solver s = new Solver(b);
            s.random=  true;
            return s.solve();
        });
//        long timeOutNanoSec = (long)60 * 1000 * 1000 * 1000;
        long timeOutNanoSec = (long)10 * 1000 * 1000 * 1000;
        try {
            return future.get(timeOutNanoSec, TimeUnit.NANOSECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            System.out.println("Took longer than 1 minute");
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("bad 1");
        } catch (ExecutionException e) {
            e.printStackTrace();
            System.out.println("bad 2");
        }

        executor.shutdownNow();
        return getBoardWithTimeOut(size);
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

    static void makeCSVMultiple(String name, DataPoint[] ins){
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

    static void makeCSV(String name, DataPoint[] ins){
        try {
            FileWriter myWriter = new FileWriter("csvs/" + name);
            myWriter.write("input,basicOps,recursionCount,time\n");
            for (int i = 0; i < ins.length; ++i) {
                myWriter.write(ins[i].n + "," + ins[i].numBasicOps +"," + ins[i].numRecursions+"," + ins[i].time+ "\n");
            }
            myWriter.close();
        } catch (Exception e) {
            System.out.println("Ree " + e.getMessage());
        }
    }
}
