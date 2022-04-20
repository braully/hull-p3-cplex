package io.github.braully.cplex;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Braully Rocha da Silva
 */
public class HullP3Cplex {
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
    
    public static void main(String[] args) {
        // Create the modeler/solver object
        try (IloCplex cplex = new IloCplex()) {
            IloIntVar[] var;
            IloRange[] rng;
            int n = matrix.length;
            //k é o max iteracoes
            int k = n;
            
            List<String> xnames = new ArrayList();
            for (int i = 0; i < n; i++) {
                xnames.add("x" + i);
            }
            IloIntVar[] x = cplex.boolVarArray(n, xnames.toArray(new String[0]));
            var = x;

            //Variavel yi,n
            IloIntVar[][] y = new IloIntVar[n][];
            for (int i = 0; i < n; i++) {
                y[i] = cplex.intVarArray(k, 0, 2);
            }

            //yi0=x0
            for (int i = 0; i < n; i++) {
//                y[0][i] = cplex.prod(2, x[i]);
                y[0][i] = x[i];
//                cplex.eq(y[0][i], x[i]);
            }

            //    Maximize
            //     x1 + 2 x2 + 3 x3
            cplex.addMinimize(cplex.sum(x));

            //    Subject To
            //     - x1 + x2 + x3 <= 20
            //     x1 - 3 x2 + x3 <= 30
//            rng = new IloRange[1];
//            rng[0] = new IloRange[2];
            //X tem que ter pelo menos duas variavies
//            rng[0] = 
            cplex.addGe(cplex.sum(x), 2, "c0");

            //Ao final de k iterações, todo os vertices precisa estar em H(S)
            for (int i = 0; i < k - 1; i++) {
//                cplex.addGe(y[k - 1][i], 1);
                cplex.addGe(y[k - 1][i], 2);
            }

            //yij+1>=yij
            for (int i = 1; i < n; i++) {
                for (int j = 0; j < k - 1; j++) {
                    cplex.addGe(y[i][j + 1], y[i][j]);
                }
            }

            //Se yij tem dois vizinhos em y*j-1, então 
            //yij>=1
            for (int i = 1; i < n; i++) {
                for (int j = 0; j < k - 1; j++) {
//                    cplex.addGe(, 1);
                    //yij>=(N(j)*yij-1)/2
                    cplex.eq(y[i][j], cplex.prod(cplex.scalProd(matrix[j], y[i - 1]), 0.5), "v_" + j);
                }
            }

            // solve the model and display the solution if one was found
            if (cplex.solve()) {
                double[] xv = cplex.getValues(var);
//                double[] slack = cplex.getSlacks(rng[0]);
                cplex.output().println("Solution status = " + cplex.getStatus());
                cplex.output().println("|S|=" + cplex.getObjValue());
                
                int nvars = xv.length;
                cplex.output().print("S={");
                for (int j = 0; j < nvars; ++j) {
                    if (xv[j] >= 0) {
                        cplex.output().print(j + ", ");
                    }
                }
                cplex.output().println("}");
                
                double[] yk = cplex.getValues(y[k - 1]);
                double[] y0 = cplex.getValues(y[0]);
                double[] y1 = cplex.getValues(y[1]);
                nvars = yk.length;
                cplex.output().print("YK={");
                for (int j = 0; j < nvars; ++j) {
                    if (yk[j] >= 0) {
                        cplex.output().print(j + ":" + yk[j] + ", ");
                    }
                }
                cplex.output().println("}");
                
                cplex.output().print("Y0={");
                for (int j = 0; j < nvars; ++j) {
                    if (y0[j] >= 0) {
                        cplex.output().print(j + ":" + y0[j] + ", ");
                    }
                }
                cplex.output().println("}");
                
                cplex.output().print("Y1={");
                for (int j = 0; j < nvars; ++j) {
                    if (y1[j] >= 0) {
                        cplex.output().print(j + ":" + y1[j] + ", ");
                    }
                }
                cplex.output().println("}");

//                int ncons = slack.length;
//                for (int i = 0; i < ncons; ++i) {
//                    cplex.output().println("Constraint " + i
//                            + ": Slack = " + slack[i]);
//                }
            }
        } catch (IloException e) {
            System.err.println("Concert exception '" + e + "' caught");
            e.printStackTrace();
        }
    }
}
