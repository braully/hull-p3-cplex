package io.github.braully.cplex;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloNumVar;
import ilog.concert.IloObjective;
import ilog.concert.IloObjectiveSense;
import ilog.cplex.IloCplex;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Braully Rocha da Silva
 */
public class CplexColor {
//Help: https://or.stackexchange.com/questions/6316/combinatorial-optimization-problem-using-ibm-ilog-cplex

//# Algoritmo coloração
//
//# set V;
//# set E within (V cross V); 
//# param N;
//
//# var w{1..N} binary;
//# var x{V,1..N} binary;
//
//# #objective function that yields the chromatic number of a graph
//# minimize Colors: sum{i in 1..N} w[i]; 
//
//# #Ensures each vertex is assigned a color
//# subject to Assigned {i in V}:
//# sum{j in 1..N}x[i,j]=1;
//
//# #Ensures no two adjacent vertices are assigned the same color
//# subject to Edges {(i,j) in E, k in 1..N}:
//# x[i,k] + x[j,k] <= w[k];
//
//
//# - Construção de um modelo próprio para $P_3$:
//#   - Inspiração: Modelo de coloração grafos [exemplo](http://math.ucdenver.edu/~sborgwardt/wiki/index.php/An_Integer_Linear_Programming_Approach_to_Graph_Coloring)
//#   - Subset sum
//#   - Ideia geral:
//#     - Restrição: - $\forall i\in V(G)$ se $S_{i}=1$ então $v\in S$
//#                  - $\forall i \in S$  $H_{i}=1$
//#                  - $H_{v}=1$ se $v\in H$
    static int[][] matrix = new int[][]{
        {0, 1, 0, 0, 1},
        {1, 0, 1, 0, 0},
        {0, 1, 0, 1, 0},
        {0, 0, 1, 0, 1},
        {1, 0, 0, 1, 0}
    };

    static double[] dv = new double[]{0.5, 0.5, 0.5, 0.5, 0.5};

    public static void main(String[] args) {
        // Create the modeler/solver object
        try (IloCplex cplex = new IloCplex()) {
            //https://lists.gnu.org/archive/html/help-glpk/2003-07/msg00009.html
            //https://www.ibm.com/docs/api/v1/content/SSSA5P_12.8.0/ilog.odms.cplex.help/refjavacplex/html/ilog/cplex/IloCplex.Param.html
            //https://stackoverflow.com/questions/62023542/cplex-find-infeasible-solution
//            cplex.setParam(IloCplex.Param.MIP.Tolerances.Integrality, 1e-05);
//            cplex.setParam(IloCplex.Param.MIP.Tolerances.Integrality, 0);
            cplex.setParam(IloCplex.Param.MIP.Tolerances.Integrality, 1e-07);

            int n = matrix.length;
            //k é o max iteracoes
            int k = n;

            List<String> xnames = new ArrayList();
            for (int i = 0; i < n; i++) {
                xnames.add("y" + i);
            }
            //Vetor de cores usadas
            IloIntVar[] yj = cplex.boolVarArray(n, xnames.toArray(new String[0]));

            //Objetivo, menor número de cores possiveis
            cplex.addMinimize(cplex.sum(yj));

            IloIntVar[][] xij = new IloIntVar[n][];
            for (int i = 0; i < k; i++) {
                List<String> ynames = new ArrayList();
                for (int j = 0; j < n; j++) {
                    ynames.add("x" + i + "," + j);
                }
                xij[i] = cplex.boolVarArray(n, ynames.toArray(new String[0]));
            }
            //# #Ensures each vertex is assigned a color
            //# subject to Assigned {i in V}:
            //# sum{j in 1..N}x[i,j]=1;
            for (int i = 0; i < n; i++) {
                cplex.addEq(cplex.sum(xij[i]), 1, "v-cor");
            }

            //# #Ensures no two adjacent vertices are assigned the same color
            //# subject to Edges {(i,j) in E, k in 1..N}:
            //# x[i,k] + x[j,k] <= w[k];
            for (int lin = 0; lin < n; lin++) {
                for (int col = lin + 1; col < n; col++) {
                    for (int j = 0; j < k; j++) {
                        //Se vertice lin é adjacente a vertice col
                        if (matrix[lin][col] == 1) {
                            //Não podem usar a mesma cor
//                            cplex.addLe(cplex.sum(xij[lin][j], xij[col][j]), 1);
                            cplex.addLe(cplex.sum(xij[lin][j], xij[col][j]), yj[j]);
                        }
                    }
                }
            }

            cplex.exportModel("cplexColor.lp");

            // solve the model and display the solution if one was found
            if (cplex.solve()) {
                double[] xv = cplex.getValues(yj);
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
                    double[] yv = cplex.getValues(xij[i]);

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
