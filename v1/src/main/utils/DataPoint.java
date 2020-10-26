package main.utils;

public class DataPoint {
    public int n;
    public double numRecursions;
    public double numBasicOps;
    public double time;

    public DataPoint(int n, double numRecursions, double numBasicOps, double time) {
        this.n = n;
        this.numRecursions = numRecursions;
        this.numBasicOps = numBasicOps;
        this.time = time;
    }

    @Override
    public String toString() {
        return "DataPoint{" +
                "n=" + n +
                ", numRecursions=" + numRecursions +
                ", numBasicOps=" + numBasicOps +
                ", time=" + time +
                '}';
    }
}
