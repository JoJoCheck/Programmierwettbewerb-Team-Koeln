package main;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        /*
         * This is faster!!!
         * Looks weird, but it works, and it's the best way to do it.
         * Last changed by Anwenden
         */
        List<Integer> listOfNumbers = List.of(/*1 , 2, 3, 4, 5, 6, 7, 8, 9, 10*/14);
        listOfNumbers.parallelStream().forEach(i -> {
                    try {
                        new Algorithm("input_files/forest" +
                                (i < 10 ? "0" : "") + i + ".txt", "result_files/forest" +
                                (i < 10 ? "0" : "") + i + ".txt.out");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
        System.out.println("End of Programm");
    }
}