/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sat_solver;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 *
 * @author awells
 */
public class Sat_Solver {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        long time = System.nanoTime();
        System.out.println("Hello");
        System.out.println(args[0]);
        Formula f = readFile(args[0]);
        System.out.println("Good bye");

//        int[][] assignments = generateAssignments(f.getNumVars());
//
//        for (int[] arr : assignments) {
//            if (f.isSAT(arr)) {
//                for (int i : arr) {
//                    System.out.print(i);
//                }
//                System.out.println("");
//                break;
//            }
//        }
        DPLL dpll = new DPLL(f);
        System.out.println("Time to build formula: " + (System.nanoTime() - time) / 1e6 + " milliseconds");
        time = System.nanoTime();

        System.out.println(dpll.isSAT());
        System.out.println("Time to solve formula: " + (System.nanoTime() - time) / 1e6 + " milliseconds");

        time = System.nanoTime();

        for (int i = 0; i < 10000; i++) {
            //have to use the result in order to have the benchmark work
            if (dpll.isSAT()) {
                System.out.println("Error");
            }
        }
        System.out.println("Total time to solve formula: " + (System.nanoTime() - time) / 1e6 + " milliseconds");

    }

    //an assignment can be represented as: int[n] where each element is 0, or 1
    //for the assignment of that variable
    public static int[][] generateAssignments(int numVars) {
        int[][] all_assignments;
        //base case
        if (numVars == 1) {
            all_assignments = new int[2][1];
            all_assignments[0][0] = 0;
            all_assignments[1][0] = 1;
            return all_assignments;
        } else if (numVars < 1) {
            return null;
        } else {
            all_assignments = new int[powerOf2(numVars)][numVars];
            //Gets truth table for n-1 variables
            int[][] firstHalf = generateAssignments(numVars - 1);

            //copy truth table with the nth variable as 0
            for (int i = 0; i < firstHalf.length; i++) {
                for (int j = 0; j < numVars - 1; j++) {
                    all_assignments[i][j] = firstHalf[i][j];
                }
                all_assignments[i][numVars - 1] = 0;
            }

            //copy truth table with the nth variable as 1
            for (int i = 0; i < firstHalf.length; i++) {
                for (int j = 0; j < numVars - 1; j++) {
                    all_assignments[i + firstHalf.length][j] = firstHalf[i][j];
                }
                all_assignments[i + firstHalf.length][numVars - 1] = 1;
            }

        }

        return all_assignments;
    }

    // should return 2^n
    public static int powerOf2(int n) {
        if (n == 0) {
            return 1;
        }
        return 2 * powerOf2(n - 1);
    }

    public static Formula readFile(String fileName) {
        int numVars = 0;
        int numClauses = 0;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            Formula f = null;

            String line = reader.readLine();
            while (line != null) {
                System.out.println(line);
                if (line.charAt(0) == 'c') {
                    //skip
                } else if (line.charAt(0) == 'p') {
                    String[] list_of_str = line.split(" ");
                    numVars = Integer.parseInt(list_of_str[2].trim());
                    numClauses = Integer.parseInt(list_of_str[3].trim());
                    f = new Formula(numVars, numClauses);
                } else {
                    String[] los = line.split(" ");
                    int[] arr = new int[4];
                    int lastI = 0;
                    int count_back = 0;
                    for (int i = 0; i < los.length; i++) {
                        if (los[i].isBlank()) {
                            count_back++;
                            continue;
                        }
                        arr[i - count_back] = Integer.parseInt(los[i].trim());
                        if (arr[i - count_back] == 0) {
                            lastI = i - count_back;
                            break;
                        }
                    }

                    int[] a = new int[lastI];
                    for (int i = 0; i < lastI; i++) {
                        a[i] = arr[i];
                    }
                    f.addClause(a);
                }

                //get next line
                line = reader.readLine();
            }
            return f;
        } catch (Exception e) {
            System.out.print(e);
            e.printStackTrace();

            return null;
        }
    }
}
