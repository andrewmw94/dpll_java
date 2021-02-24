/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sat_solver;

import java.util.ArrayList;

/**
 *
 * @author awells
 */
public class DPLL {

    //This is the permanent formula
    Formula formula;
    ArrayList<int[]> new_f;

    DPLL(Formula formula) {
        this.formula = formula;

        int[][] f = formula.clauses;
        new_f = new ArrayList<int[]>(f.length);
        for (int i = 0; i < f.length; i++) {
            int[] a = new int[f[i].length];
            System.arraycopy(f[i], 0, a, 0, a.length);
            new_f.add(a);
        }
    }

    //This is the formula we are changing;
    int[][] f;

    boolean isSAT() {
        return DPLL(new_f);
    }

    boolean DPLL(ArrayList<int[]> f) {
        simplify(f);
        //all clauses satisfied
        if (f.size() == 0) {
            return true;
        }
        //any clauses are empty
        for (int i = 0; i < f.size(); i++) {
            if (f.get(i).length == 0) {
                return false;
            }
        }

        int chosenProp = chooseProp(f);

        //Creates copy rather than reference
        ArrayList<int[]> c1 = new ArrayList<int[]>(f);
        int[] a = new int[1];
        a[0] = chosenProp;
        c1.add(a);
        if (DPLL(c1)) {
            System.out.println(a[0]);
            return true;
        }

        //Creates copy rather than reference
        ArrayList<int[]> c2 = new ArrayList<int[]>(f);
        a = new int[1];
        a[0] = -chosenProp;
        c2.add(a);
        if (DPLL(c2)) {
            System.out.println(a[0]);
            return true;
        }

        //If we reach here, no assignment to chosenProp works
        //Thus the formula is unsatisfiable
        return false;
    }

    int chooseProp(ArrayList<int[]> f) {
        ArrayList<int[]> length_2_clauses = new ArrayList<int[]>();

        //loop through all clauses in formula
        for (int[] cls : f) {
            if (cls.length == 2) {
                length_2_clauses.add(cls);
            }
        }

        //find most common prop in these clauses
        //score for x_i will be stored at scores[i]
        int[] positiveScores = new int[formula.numVars+1];
        for (int[] cls : length_2_clauses) {
            for (int var : cls) {
                if (var > 0) {
                    positiveScores[var]++;
                }
            }
        }

        int[] negativeScores = new int[formula.numVars+1];
        for (int[] cls : length_2_clauses) {
            for (int var : cls) {
                if (var < 0) {
                    negativeScores[-var]++;
                }
            }
        }

        //find max score index
        int maxScoreIndex = 0;
        for (int i = 0; i < positiveScores.length; i++) {
            if (positiveScores[i] + negativeScores[i] > positiveScores[maxScoreIndex] + negativeScores[maxScoreIndex]) {
            //if (positiveScores[i] + negativeScores[i] > maxScoreIndex) {
                maxScoreIndex = i;
            }
        }
        
        //return maxScoreIndex;

        if (positiveScores[maxScoreIndex] > negativeScores[maxScoreIndex]) {
            return maxScoreIndex;
        } else {
            return -maxScoreIndex;
        }
    }

    ArrayList<int[]> simplify(ArrayList<int[]> new_f) {
        boolean formula_changed = true;

        while (formula_changed) {
            formula_changed = false;
            for (int i = 0; i < new_f.size(); i++) {
                //Find all clauses of length 1
                if (new_f.get(i).length == 1) {
                    int prop = new_f.get(i)[0];
                    if (prop > 0) {
                        for (int j = 0; j < new_f.size(); j++) {
                            int k = contains(new_f.get(j), prop);
                            if (k != -1) {
                                if (new_f.get(j)[k] > 0) {
                                    //remove clause because it is satisfied
                                    removeClause(new_f, j);
                                    j--;
                                } else {
                                    //remove prop from clause because it can't be satisfied
                                    removeProp(new_f, j, k);
                                }
                                formula_changed = true;
                            }
                        }
                    } else {
                        for (int j = 0; j < new_f.size(); j++) {
                            int k = contains(new_f.get(j), prop);
                            if (k != -1) {
                                if (new_f.get(j)[k] < 0) {
                                    //remove clause because it is satisfied
                                    removeClause(new_f, j);
                                    j--;
                                } else {
                                    //remove prop from clause because it can't be satisfied
                                    removeProp(new_f, j, k);
                                }
                                formula_changed = true;
                            }
                        }
                    }
                }
            }
        }
        return new_f;
    }

    void removeClause(ArrayList<int[]> f, int cls) {
        if (cls < f.size()) {
            f.set(cls, f.get(f.size() - 1));
            f.remove(f.size() - 1);
        } else {
            f.remove(f.size() - 1);
        }
    }

    void removeProp(ArrayList<int[]> f, int cls, int propIndex) {
        int[] newCls;
        int lastI = f.get(cls).length - 1;
        if (propIndex < f.get(cls).length) {
            f.get(cls)[propIndex] = f.get(cls)[lastI];
        }
        int[] n = new int[lastI];
        for (int i = 0; i < lastI; i++) {
            n[i] = f.get(cls)[i];
        }
        f.set(cls, n);
    }

    int contains(int[] clause, int prop) {
        for (int i = 0; i < clause.length; i++) {
            if (clause[i] == prop || clause[i] == -prop) {
                return i;
            }
        }
        return -1; //This means we didn't find prop in clause
    }
}
