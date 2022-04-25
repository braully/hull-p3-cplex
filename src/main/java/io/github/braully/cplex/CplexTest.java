package io.github.braully.cplex;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloLinearNumExprIterator;
import ilog.concert.IloNumVar;
import ilog.concert.IloObjective;
import ilog.concert.IloObjectiveSense;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Braully Rocha da Silva
 */
public class CplexTest {
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
            int k = 4;

            List<String> xnames = new ArrayList();
            for (int i = 0; i < n; i++) {
                xnames.add("x" + i);
            }
            IloIntVar[] x = cplex.boolVarArray(n, xnames.toArray(new String[0]));

            IloObjective objective = cplex.addMinimize(cplex.sum(x));
            IloObjectiveSense sense = objective.getSense();
            System.out.println("Sense: " + sense);
//            cplex.addMaximize(cplex.sum(y));

            //X tem que ter pelo menos duas variavies
            cplex.addGe(cplex.sum(x), 2);

            IloIntVar aux[] = cplex.intVarArray(n, 0, 100);
            IloNumVar[][] y = new IloNumVar[n][];
            for (int i = 0; i < k; i++) {
//                y[i] = cplex.intVarArray(n, 0, 2);
                List<String> ynames = new ArrayList();
                for (int j = 0; j < n; j++) {
                    ynames.add("y" + i + "," + j);
                }
//                y[i] = cplex.intVarArray(n, 0, 1, ynames.toArray(new String[0]));
                y[i] = cplex.numVarArray(n, 0.0, 10.0, ynames.toArray(new String[0]));
            }
            //Se yij tem dois vizinhos em y*j-1, então 
            for (int i = 0; i < n; i++) {
//                cplex.addEq(y[i], cplex.scalProd(matrix[i], x));
//                cplex.addEq(y[0][i], cplex.scalProd(matrix[i], x));
//                cplex.addEq(y[0][i], cplex.prod(x[i], 2));
                y[0][i] = x[i];

                for (int j = 1; j < k - 1; j++) {
//                    cplex.addLe(y[j][i], y[j + 1][i], "I");
                }
            }

            for (int i = 0; i < n; i++) {
//                cplex.addEq(y[i], cplex.scalProd(matrix[i], x));
//                cplex.scalProd(y[0], dv);

//                cplex.addEq(y[1][i], cplex.sum(cplex.prod(cplex.scalProd(matrix[i], y[0]), 0.5), y[0][i]));
//                cplex.addGe(y[1][i], cplex.sum(cplex.scalProd(matrix[i], y[0]), -1));
//                cplex.addGe(y[1][i], cplex.prod(cplex.scalProd(matrix[i], y[0]), 0.5));
//                cplex.addGe(y[1][i], cplex.scalProd(matrix[i], y[0]));
//                cplex.addEq(y[1][i], cplex.scalProd(matrix[i], y[0]));
//                cplex.addGe(y[1][i], cplex.scalProd(matrix[i], y[0]));
//                cplex.addEq(y[1][i], cplex.prod(cplex.scalProd(matrix[i], y[0]), 0.5), "v" + i);
//
//                cplex.addLe(cplex.prod(cplex.scalProd(matrix[i], y[0]), 0.5), y[1][i], "v" + i);
//
//                cplex.addLe(cplex.prod(cplex.scalProd(matrix[i], y[0]), 0.5), cplex.prod(y[1][i], 2), "v" + i);
//                cplex.addEq(cplex.prod(cplex.scalProd(matrix[i], y[0]), 0.5), cplex.prod(y[1][i], 2), "y1," + i);
                cplex.addEq(y[1][i], cplex.max(cplex.prod(cplex.scalProd(matrix[i], y[0]), 0.5), y[0][i]), "y1," + i);

//                cplex.addEq(aux[i], cplex.scalProd(matrix[i], y[0]));
//                for (int j = 1; j < 3; j++) {
//                    cplex.addEq(y[j][i], cplex.sum(cplex.prod(cplex.scalProd(matrix[i], y[j - 1]), 0.5), y[0][i]));
//                }
            }

            for (int i = 0; i < n; i++) {
//                cplex.addEq(y[i], cplex.scalProd(matrix[i], x));
//                cplex.scalProd(y[0], dv);

//                cplex.addEq(y[2][i], cplex.prod(cplex.scalProd(matrix[i], y[1]), 0.5));
//                cplex.eq(aux[i], cplex.prod(cplex.scalProd(matrix[i], y[1]), 0.5));
                for (int j = 2; j < k; j++) {
//                    cplex.addGe(y[j][i], cplex.scalProd(matrix[i], y[j - 1]));
//                    cplex.addGe(y[j][i], cplex.prod(cplex.scalProd(matrix[i], y[j - 1]), 0.5));
//                    cplex.addEq(y[j][i], cplex.sum(cplex.prod(cplex.scalProd(matrix[i], y[j - 1]), 0.5), y[0][i]));
//                    cplex.addEq(y[j][i], y[j - 1][i]);

//                    cplex.addEq(cplex.prod(cplex.scalProd(matrix[i], y[j - 1]), 0.5), cplex.prod(y[j][i], 2), "y" + j + "," + i);
                    cplex.addEq(y[j][i], cplex.max(cplex.prod(cplex.scalProd(matrix[i], y[j - 1]), 0.5), y[j][i]), "y" + j + "," + i);

//                    cplex.addGe(y[j][i], y[j - 1][i]);
                }
            }

            //Ao final de k iteraçoes todos os vertices precisam estar contaminados
            for (int i = 0; i < n; i++) {
                cplex.addEq(y[k - 1][i], 1, "s_hull_v" + i);
            }
//            cplex.addEq(cplex.sum(y[k - 1]), n, "s_hull");

            cplex.exportModel("cplexTest.lp");

            // solve the model and display the solution if one was found
            if (cplex.solve()) {
                double[] xv = cplex.getValues(x);
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
                    double[] yv = cplex.getValues(y[i]);

                    nvars = yv.length;
                    cplex.output().print("Y" + i + "={");
                    for (int j = 0; j < nvars; ++j) {
//                    if (yv[j] >= 0) {
                        cplex.output().print(j + ": " + yv[j] + ", ");
//                    }
                    }
                    cplex.output().println("}");
                }

//                Iterator it = cplex.rangeIterator();
//                while (it.hasNext()) {
//                    IloRange r = (IloRange) it.next();
//                    System.out.println("Constraint: " + r.getName());
//                    IloLinearNumExprIterator it2
//                            = ((IloLinearNumExpr) r.getExpr()).linearIterator();
//                    while (it2.hasNext()) {
//                        System.out.println("\tVariable "
//                                + it2.nextNumVar().getName()
//                                + " has coefficient "
//                                + it2.getValue());
//                    }
//                    // get range bounds, checking for +/- infinity
//                    // (allowing for some rounding)
//                    String lb = (r.getLB() <= Double.MIN_VALUE + 1)
//                            ? "-infinity" : Double.toString(r.getLB());
//                    String ub = (r.getUB() >= Double.MAX_VALUE - 1)
//                            ? "+infinity" : Double.toString(r.getUB());
//                    System.out.println("\t" + lb + " <= LHS <= " + ub);
//                }
            }
            cplex.output().println("Solution status = " + cplex.getStatus());

        } catch (IloException e) {
            System.err.println("Concert exception '" + e + "' caught");
            e.printStackTrace();
        }
    }
}
