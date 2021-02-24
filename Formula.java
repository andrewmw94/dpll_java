/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sat_solver;

/**
 * Formula in CNF
 *
 * @author awells
 */
public class Formula {

    int numVars;
    int numClauses;
    int[][] clauses;

    private int lastClause;

    public Formula(int numVars, int numClauses) {
        this.numVars = numVars;
        this.numClauses = numClauses;
        clauses = new int[numClauses][3];
        lastClause = 0;
    }

    public int getNumVars() {
        return numVars;
    }

    public int getNumClauses() {
        return numClauses;
    }

    public int[][] getClauses() {
        return clauses;
    }

    public void addClause(int[] clause) {
        clauses[lastClause++] = clause;
    }

    //Test whether a formula is satisfied by an assignment
    public boolean isSAT(int[] assignment) {
        // loop over clauses
        for (int[] clause : clauses) {
            // loop over variables in each clause
            boolean clause_satisfied = false;
            for (int i : clause) {
                if (i > 0) {
                    if (assignment[i - 1] == 1) {
                        clause_satisfied = true;
                        continue;
                    }
                } else {
                    if (assignment[-i - 1] == 0) {
                        clause_satisfied = true;
                        continue;
                    }
                }
            }
            if (!clause_satisfied) {
                return false;
            }
        }
        return true;
    }
}
