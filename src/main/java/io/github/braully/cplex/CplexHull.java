package io.github.braully.cplex;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.cplex.IloCplex;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Braully Rocha da Silva
 */
public class CplexHull {
//Help: https://or.stackexchange.com/questions/6316/combinatorial-optimization-problem-using-ibm-ilog-cplex

    static int[][] matrix = new int[][]{
        {0, 1, 0, 0, 1},
        {1, 0, 1, 0, 0},
        {0, 1, 0, 1, 0},
        {0, 0, 1, 0, 1},
        {1, 0, 0, 1, 0}
    };

    public static void main(String[] args) {
        try (IloCplex cplex = new IloCplex()) {
            int n = matrix.length;
            //k é o max iteracoes
            int k = n;

            List<String> xnames = new ArrayList();
            for (int i = 0; i < n; i++) {
                xnames.add("s" + i);
            }
            //Vetor de cores usadas
            IloIntVar[] s = cplex.boolVarArray(n, xnames.toArray(new String[0]));

            //Objetivo, menor número de vertices
            cplex.addMinimize(cplex.sum(s));

            //variables h_ij indicate node i is contamined in k-step;
            IloIntVar[][] hij = new IloIntVar[n][];
            for (int i = 0; i < k; i++) {
                List<String> ynames = new ArrayList();
                for (int j = 0; j < n; j++) {
                    ynames.add("h" + i + "," + j);
                }
                hij[i] = cplex.boolVarArray(n, ynames.toArray(new String[0]));
            }

            //# #Ensures each vertex is in HS
            for (int i = 0; i < n; i++) {
//                cplex.addEq(cplex.sum(hij[i]), 1);
                cplex.addEq(hij[i][k - 1], 1, "hs=v");
            }

            //os vertices em s estão contaminados na iteracao 0
            for (int i = 0; i < n; i++) {
                cplex.addEq(hij[i][0], s[i], "s");
            }

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < k - 1; j++) {
                    cplex.addGe(hij[i][j + 1], hij[i][j], "propag");
                }
            }

            for (int i = 0; i < n; i++) {
                for (int j = 1; j < k - 1; j++) {

                    List<IloIntVar> adjs = new ArrayList<>();
                    //Situação dos vizinhos de i na iteração anterior
                    for (int c = 0; c < n; c++) {
                        if (matrix[i][c] == 1) {
                            adjs.add(hij[c][j - 1]);
                        }
                    }
                    adjs.add(hij[i][0]);
                    adjs.add(hij[i][0]);

                    IloIntVar[] vizinhosi = adjs.toArray(new IloIntVar[0]);
                    //Se i tem mais de dois vizinhos em h na iteração j-1, 
                    // então i precisa estar na iteração j 
//                    cplex.addGe(hij[i][j], 
//                            cplex.prod(cplex.sum(vizinhosi), 0.5), 
//                            "edge"
//                    );
                    cplex.addGe(hij[i][j],
                            cplex.addGe(cplex.sum(vizinhosi), 2),
                            "edge"
                    );
                }
            }

//            for (int lin = 0; lin < n; lin++) {
//                for (int col = lin + 1; col < n; col++) {
//                    for (int j = 1; j < k; j++) {
//                        //Se vertice lin é adjacente a vertice col
//                        if (matrix[lin][col] == 1) {
//                            //Não podem usar a mesma cor, 
//                            //cplex.addLe(cplex.sum(xij[lin][j], xij[col][j]), 1);
//                            cplex.addLe(cplex.sum(hij[lin][j], hij[col][j]), s[j]);
//                        }
//                    }
//                }
//            }
            cplex.exportModel("cplexHull.lp");

            // solve the model and display the solution if one was found
            if (cplex.solve()) {
                double[] xv = cplex.getValues(s);
//                double[] slack = cplex.getSlacks(rng[0]);
                cplex.output().println("|S|=" + cplex.getObjValue());

                int nvars = xv.length;
                cplex.output().print("S={");
                for (int j = 0; j < nvars; ++j) {
                    if (xv[j] > 0) {
                        cplex.output().print(j + ", ");
                    }
                }
                cplex.output().println("}");

                for (int i = 0; i < k; i++) {
                    double[] yv = cplex.getValues(hij[i]);

                    nvars = yv.length;
                    cplex.output().print("Y" + i + "={");
                    for (int j = 0; j < nvars; ++j) {
//                    if (yv[j] >= 0) {
                        cplex.output().print(j + ": " + yv[j] + ", ");
//                    }
                    }
                    cplex.output().println("}");
                }

            }
            cplex.output().println("Solution status = " + cplex.getStatus());

        } catch (IloException e) {
            System.err.println("Concert exception '" + e + "' caught");
            e.printStackTrace();
        }
    }
}
