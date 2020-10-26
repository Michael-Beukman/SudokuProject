package main.experiments;

import main.Board;
import main.Solver;
import main.utils.DataPoint;
import main.utils.ListGen;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class FinalExperiments {

    static boolean verbose = false;
    static String[] modes = {"v1", "v2", "v4", "v5"};
    static int COUNT = 1;
    final static int NUM_SECONDS_TO_STOP_AFTER = 10;
    final static boolean ASSERT = false;
    static abstract class RunOneSize {
        abstract DataPoint[] run(int size);

        abstract String getName(int size);

        abstract String getLevel();
    }

    public static void main(String[] args) {
//        System.out.println(Arrays.toString(args));
//        if (1==1)return;
        if (1 == 1){
            testStuff();
            return;
        }
        modes = args;
        COUNT = Integer.parseInt(args[1]);
        System.out.println("We have a value of count: " + COUNT);
        if (modes.length == 0)
            System.out.println("No arguments given. Please specify one or more of v1, v2, v4, v5");
        for (String mode : modes) {
            System.out.println("MODE IS " + mode);
           doAllProper(mode);
        }
    }

    public static void doAllProper(String mode) {
        RunOneSize runner;
        if (mode.equals("v1")) {
            runner = new RunOneSize() {
                int count = COUNT;
                DataPoint[] run(int size) {
                    return v1_best_case(size, count);
                }

                String getName(int size) {
                    return "v1_best_case_size" + size + "_count_" + count;
                }

                String getLevel() {
                    return "v1";
                }
            };
        }else if (mode.equals("v2")){
            runner = new RunOneSize() {
                int numToDo=9;
                int count=COUNT;
                @Override
                DataPoint[] run(int size) {
                    return v2_worst_case_no_stopping(size, count, numToDo);
                }

                @Override
                String getName(int size) {
                    return "v2_worst_case_size" + size + "_count_" + count + "_numtodo_" + numToDo;
                }

                @Override
                String getLevel() {
                    return "v2";
                }
            };
        } else if (mode.equals("v4")) {
            // boards
            runner = new RunOneSize() {
                int count = COUNT;
                @Override
                DataPoint[] run(int size) {
                    return v4_simple_boards("src/main/data/" + size + "_100", count);
                }

                @Override
                String getName(int size) {
                    return "v4_boards" + size + "_count_" + count;
                }

                @Override
                String getLevel() {
                    return "v4";
                }
            };
        } else if (mode.equals("v5")) {
            runner = new RunOneSize() {
                int count = COUNT;
                @Override
                DataPoint[] run(int size) {
                    int numToDo =getNumToDo(size);
                    return v5_average_case_lots_timeout(size, count, numToDo);
                }

                @Override
                String getName(int size) {
                    int numToDo =getNumToDo(size);
                    return "v5_average_case_size" + size + "_count_" + count + "_numtodo_" + numToDo;
                }

                @Override
                String getLevel() {
                    return "v5";
                }

                int getNumToDo(int size){
                    int numToDo = 15;
                    if (size == 9)
                        numToDo = 70;
                    else if (size == 16)
                        numToDo = 130;
                    return numToDo;
                }
            };
        }else{
            System.out.println("Mode " + mode + " is invalid ");
            return;
        }
        doOneExperimentSet(runner);
    }

    public static void doOneExperimentSet(RunOneSize runner) {
        verbose = true;
        for (int s = 2; s <= 4; ++s) {
            int size = s * s;
            DataPoint[] arr = runner.run(size);
            String name = runner.getName(size);
            makeCSV(name, arr);
            System.out.println("Done with " + runner.getLevel() + "::" + size);
        }
    }

    public static double toMilliSeconds(long nano){
        return nano / 1000000.0;
    }

    /**
     * This tries to do the best case.
     *
     * @param size
     * @param count
     * @return
     */
    public static DataPoint[] v1_best_case(int size, int count) {
        // try best case, i.e. where it doesn't recurse a lot
        int numToDo = size * size;
        double[] times = new double[numToDo];
        double[] numRecursed = new double[numToDo];
        double[] numBasicOps = new double[numToDo];
        int[] counts = new int[numToDo];
        for (int i = 0; i < count; ++i) {
            if (verbose) System.out.println("In count " + i + "/" + count);
            Board b = new Board(size);
            Solver s = new Solver(b);
            b = s.solve();
            ArrayList<Integer> indices = ListGen.getRandomList(size * size);
            indices.sort(Comparator.reverseOrder()); // go in order

            for (int j = 0; j < size * size; ++j) {
                Board newBoard = new Board(b.toString());
                // clear board
                for (int k = 0; k < j; ++k) {
                    int index = indices.get(k) - 1;
                    int row = index / size;
                    int col = index % size;
                    newBoard.resetSquare(row, col);
                }
                int empty = j;
                if (size == 9) {
                    // super hacky way to do things to make it proper
                    newBoard.makeMove(1, 3, 7);
                    newBoard.makeMove(1, 4, 8);
                    newBoard.makeMove(1, 5, 9);
                    newBoard.makeMove(3, 8, 7);
                    newBoard.makeMove(4, 4, 9);
                    newBoard.makeMove(4, 6, 2);
                    newBoard.makeMove(7, 5, 8);
                    newBoard.makeMove(7, 6, 5);
                    newBoard.makeMove(4, 8, 4);

                    // count empty squares
                    empty = newBoard.countEmpty();
//                    System.out.println("J = " + j + " empty = " + empty);
                }

                // now solve it
                Solver solver = new Solver(newBoard);
//                long start = System.nanoTime();
//                Board k = solver.solve();
//                long end = System.nanoTime();
                long duration = doWithTimeout(solver);
                times[empty] += toMilliSeconds(duration);
                numRecursed[empty] += solver.getNumRecursions();
                numBasicOps[empty] += solver.getNumberOfBasicOperations();
                counts[empty]++;

            }
        }
        int numberOfPoints = 0;
        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 0; i < numToDo; ++i) {
            if (counts[i] != 0) {
                numberOfPoints++;
                indices.add(i);
                times[i] /= (counts[i] + 0.0);
                numRecursed[i] /= (counts[i] + 0.0);
                numBasicOps[i] /= (counts[i] + 0.0);
            }
        }
        DataPoint ds[] = new DataPoint[numberOfPoints];
        for (int i : indices) {
            ds[i] = new DataPoint(i, numRecursed[i], numBasicOps[i], times[i]);
        }
        return ds;
    }

    public static DataPoint[] v2_worst_case_no_stopping(int size, int count, int numToDo) {
        // try best case, i.e. where it doesn't recurse a lot

        double[] times = new double[numToDo];
        double[] numRecursed = new double[numToDo];
        double[] numBasicOps = new double[numToDo];
        int[] counts = new int[numToDo];
        for (int i = 0; i < count; ++i) {
            if (verbose) System.out.println("In count " + i + "/" + count);
            Board b = new Board(size);
            Solver s = new Solver(b);
            b = s.solve();
            ArrayList<Integer> indices = ListGen.getRandomList(size * size);
            indices.sort(Comparator.reverseOrder()); // go in order

            for (int j = 0; j < numToDo; ++j) {
                Board newBoard = new Board(b.toString());
                // clear board
                for (int k = 0; k < j; ++k) {
                    int index = indices.get(k) - 1;
                    int row = index / size;
                    int col = index % size;
                    newBoard.resetSquare(row, col);
                }

                // now solve it
                Solver solver = new Solver(newBoard);
                solver.earlyStopping = false;
//                long start = System.nanoTime();
//                Board k = solver.solve();
//                long end = System.nanoTime();
                long duration = doWithTimeout(solver);

                times[j] += toMilliSeconds(duration);
                numRecursed[j] += solver.getNumRecursions();
                numBasicOps[j] += solver.getNumberOfBasicOperations();
                counts[j]++;

            }
        }
        int numberOfPoints = 0;
        ArrayList<Integer> indices = new ArrayList<>();
        System.out.println(Arrays.toString(counts));
        for (int i = 0; i < numToDo; ++i) {
            if (counts[i] != 0) {
                numberOfPoints++;
                indices.add(i);
                times[i] /= (counts[i]+0.0);
                numRecursed[i] /= (counts[i] + 0.0);
                numBasicOps[i] /= (counts[i] + 0.0);
            }
        }
        DataPoint ds[] = new DataPoint[numberOfPoints];
        for (int i : indices) {
            ds[i] = new DataPoint(i, numRecursed[i], numBasicOps[i], times[i]);
        }
        return ds;
    }

    /**
     * This works with a 1 minute timeout, so yeah.
     *
     * @param size
     * @param count
     * @param numToDo
     * @return
     */
    public static DataPoint[] v5_average_case_lots_timeout(int size, int count, int numToDo) {
        // Try random boards and make it go normally

        double[] times = new double[numToDo];
        double[] numRecursed = new double[numToDo];
        double[] numBasicOps = new double[numToDo];
        int[] counts = new int[numToDo];
        for (int i = 0; i < count; ++i) {
            if (verbose)
                System.out.println("In count " + i + "/" + count);

            long tmpS = System.nanoTime();
            Board b = getBoardWithTimeOut(size);
            long endS = System.nanoTime();
            if (verbose)
                System.out.println("Making map " + (endS - tmpS) / 1000000000.0);
            ArrayList<Integer> indices = ListGen.getRandomList(size * size); // randomly sorted
            for (int j = 0; j < numToDo; ++j) {
                Board newBoard = new Board(b.toString());
                // clear board
                for (int k = 0; k < j; ++k) {
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
                times[j] += ((duration) / 1000000.0);
                numRecursed[j] += solver.getNumRecursions();
                numBasicOps[j] += solver.getNumberOfBasicOperations();
                counts[j]++;

            }
        }
        int numberOfPoints = 0;
        ArrayList<Integer> indices = new ArrayList<>();
        System.out.println(Arrays.toString(counts));
        for (int i = 0; i < numToDo; ++i) {
            if (counts[i] != 0) {
                numberOfPoints++;
                indices.add(i);
                times[i] /= counts[i];
                numRecursed[i] /= (counts[i] + 0.0);
                numBasicOps[i] /= (counts[i] + 0.0);
            }
        }
        DataPoint ds[] = new DataPoint[numberOfPoints];
        for (int i : indices) {
            ds[i] = new DataPoint(i, numRecursed[i], numBasicOps[i], times[i]);
        }
        return ds;
    }

    public static DataPoint[] v4_simple_boards(String filename, int count) {
        String content = readFile(filename, StandardCharsets.US_ASCII);
        Board[] boards = Board.fromString(content);
        ArrayList<DataPoint> dps = new ArrayList<>();
        for (Board b : boards) {
            DataPoint dp = new DataPoint(b.countEmpty(), 0, 0, 0);
            for (int i = 0; i < count; ++i) {
                Board to_solve = new Board(b.toString());
                Solver s = new Solver(to_solve);
                long start = System.nanoTime();
                Board ans = s.solve();
                long end = System.nanoTime();
                assert ans.isValid();

                dp.numRecursions += s.getNumRecursions();
                dp.numBasicOps += s.getNumberOfBasicOperations();
                dp.time += (end - start) / 1000000.0;
            }
            dp.numRecursions /= (count + 0.0);
            dp.numBasicOps /= (count + 0.0);
            dp.time /= (count + 0.0);
            dps.add(dp);
        }

        return (DataPoint[]) dps.toArray(new DataPoint[dps.size()]);
    }

    public static void testStuff(){
        DataPoint[] worstCase16 = v2_worst_case_no_stopping(16, 1, 10);
        DataPoint[] worstCase9 = v2_worst_case_no_stopping(9, 1, 10);
        System.out.println("SIZE 9");
        System.out.println(Arrays.toString(worstCase9));
        System.out.println("SIZE 16");
        System.out.println(Arrays.toString(worstCase16));

        makeCSV("abc_test_9_worst", worstCase9);
        makeCSV("abc_test_16_worst", worstCase16);

    }

    /**
     * Returns a time.
     *
     * @param s
     * @return
     */
    public static long doWithTimeout(Solver s) {

        ExecutorService executor = Executors.newSingleThreadExecutor();
//        ExecutorService executor = Executors.d();

        AtomicReference<Board> b = new AtomicReference<Board>();
        Future<Solver> future = executor.submit(() -> {
            b.set(s.solve());
            return s;
        });
        Thread t;
//        long timeOutNanoSec = (long)60 * 1000 * 1000 * 1000;
        long timeOutNanoSec = (long) NUM_SECONDS_TO_STOP_AFTER * 1000 * 1000 * 1000;
        try {
            long tmpS = System.nanoTime();
            future.get(timeOutNanoSec, TimeUnit.NANOSECONDS);
            long endS = System.nanoTime();

            assert !ASSERT || b.get().isValid();

            executor.shutdownNow();
            return endS - tmpS;
        } catch (TimeoutException e) {
            future.cancel(true);
            System.out.println("Took longer than " + NUM_SECONDS_TO_STOP_AFTER + " seconds " + "is done = " + future.isCancelled());

        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("bad 1");
        } catch (ExecutionException e) {
            e.printStackTrace();
            System.out.println("bad 2");
        }
        try {
            executor.shutdown();
            executor.shutdownNow();
            executor.awaitTermination(0, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            System.out.println("Exectuor error terminate");
        }
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            System.out.println("Sleep failed");
        }
        System.out.println("Executor is " +executor.isTerminated());
        return timeOutNanoSec;
    }


    public static Board getBoardWithTimeOut(int size) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Board> future = executor.submit(() -> {
            Board b = new Board(size);
            Solver s = new Solver(b);
            s.random = true;
            return s.solve();
        });
        long timeOutNanoSec = (long) NUM_SECONDS_TO_STOP_AFTER * 1000 * 1000 * 1000;
        try {
            Board b =  future.get(timeOutNanoSec, TimeUnit.NANOSECONDS);
            executor.shutdownNow();
            return b;
        } catch (TimeoutException e) {
            future.cancel(true);
            System.out.println("Board Took longer than " + NUM_SECONDS_TO_STOP_AFTER + " seconds");
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("bad 11");
        } catch (ExecutionException e) {
            e.printStackTrace();
            System.out.println("bad 22");
        }

        try {
            executor.shutdown();
            executor.shutdownNow();
            executor.awaitTermination(0, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            System.out.println("Exectuor error terminate");
        }
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            System.out.println("Sleep failed");
        }
        return getBoardWithTimeOut(size);
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
        for (int i = 0; i < inps.length; ++i) {
            inps[i] = i;
        }
        makeCSV(name, inps, times);
    }

    public static String getName(String n) {
        SimpleDateFormat DateFor = new SimpleDateFormat("dd-MM-yyyy");
        String s = DateFor.format(new Date());
        return n + '-' + (s) + ".csv";
    }

    static String readFile(String path, Charset encoding) {
        byte[] encoded = new byte[0];
        try {
            encoded = Files.readAllBytes(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(encoded, encoding);
    }

    static void makeCSVMultiple(String name, DataPoint[] ins) {
        int ns[] = new int[ins.length];
        double recurses[] = new double[ins.length];
        double basicOps[] = new double[ins.length];
        double times[] = new double[ins.length];
        for (int i = 0; i < ins.length; ++i) {
            ns[i] = ins[i].n;
            recurses[i] = ins[i].numRecursions;
            basicOps[i] = ins[i].numBasicOps;
            times[i] = ins[i].time;
        }
        makeCSV(getName(name + "_recurses"), ns, recurses);
        makeCSV(getName(name + "basicOps"), ns, basicOps);
        makeCSV(getName(name + "times"), ns, times);
    }

    static void makeCSV(String name, DataPoint[] ins) {
        try {
            FileWriter myWriter = new FileWriter("csvs/" + name);
            myWriter.write("input,basicOps,recursionCount,time\n");
            for (int i = 0; i < ins.length; ++i) {
                myWriter.write(ins[i].n + "," + ins[i].numBasicOps + "," + ins[i].numRecursions + "," + ins[i].time + "\n");
            }
            myWriter.close();
        } catch (Exception e) {
            System.out.println("Ree " + e.getMessage());
        }
    }
}

/**
 * Look at these:
 *
 SIZE 9
 [DataPoint{n=0, numRecursions=82.0, numBasicOps=0.0, time=0.0728274}, DataPoint{n=1, numRecursions=83.0, numBasicOps=4.0, time=9.9435743}, DataPoint{n=2, numRecursions=113.0, numBasicOps=66.0, time=0.0888955}, DataPoint{n=3, numRecursions=568.0, numBasicOps=978.0, time=0.2074948}, DataPoint{n=4, numRecursions=568.0, numBasicOps=980.0, time=0.2216246}, DataPoint{n=5, numRecursions=15330.0, numBasicOps=30506.0, time=7.1749431999999995}, DataPoint{n=6, numRecursions=281050.0, numBasicOps=561948.0, time=276.09455949999995}, DataPoint{n=7, numRecursions=4466147.0, numBasicOps=8932144.0, time=7171.3069118}, DataPoint{n=8, numRecursions=6329512.3, numBasicOps=1.26588697E7, time=10000.0}]
 SIZE 16
 [DataPoint{n=0, numRecursions=257.0, numBasicOps=0.0, time=0.1108642}, DataPoint{n=1, numRecursions=257.0, numBasicOps=2.0, time=0.6525486}, DataPoint{n=2, numRecursions=274.0, numBasicOps=38.0, time=0.08804500000000001}, DataPoint{n=3, numRecursions=820.0, numBasicOps=1132.0, time=0.9917914}, DataPoint{n=4, numRecursions=13927.0, numBasicOps=27348.0, time=16.532403499999997}, DataPoint{n=5, numRecursions=293547.0, numBasicOps=586590.0, time=549.612522}, DataPoint{n=6, numRecursions=4046903.8, numBasicOps=8093301.6, time=9108.251881}, DataPoint{n=7, numRecursions=4710200.9, numBasicOps=9419896.0, time=10000.0}, DataPoint{n=8, numRecursions=4766986.9, numBasicOps=9533468.8, time=10000.0}]

 */