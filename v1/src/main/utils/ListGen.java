package main.utils;

import java.util.ArrayList;
import java.util.Collections;

public class ListGen {
    /**
     * returns a list of random numbers 1 to n inclusive
     * @param n
     * @return
     */
    public static ArrayList<Integer> getRandomList(int n){
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i=1; i<=n; i++) {
            list.add(i);
        }
        Collections.shuffle(list);
        return list;
    }
}
