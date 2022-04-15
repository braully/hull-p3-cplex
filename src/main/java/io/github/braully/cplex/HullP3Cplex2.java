/*
 * The MIT License
 *
 * Copyright 2022 strike.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.github.braully.cplex;

import ilog.concert.IloException;
import ilog.concert.IloMPModeler;
import ilog.concert.IloNumVar;
import ilog.concert.IloObjective;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;

/**
 *
 * @author Braully Rocha da Silva
 */
public class HullP3Cplex2 {
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
    public static void main(String[] args) {
        // Create the modeler/solver object
        try (IloCplex cplex = new IloCplex()) {

            IloNumVar[][] var = new IloNumVar[1][];
            IloRange[][] rng = new IloRange[1][];

            double[] lb = {0.0, 0.0, 0.0};
            //    Bounds
            //     0 <= x1 <= 40
            //    End
            double[] ub = {40.0, Double.MAX_VALUE, Double.MAX_VALUE};
            String[] varname = {"x1", "x2", "x3"};
            IloNumVar[] x = cplex.numVarArray(3, lb, ub, varname);
            var[0] = x;

            //    Maximize
            //     x1 + 2 x2 + 3 x3
            double[] objvals = {1.0, 2.0, 3.0};
            cplex.addMaximize(cplex.scalProd(x, objvals));

            //    Subject To
            //     - x1 + x2 + x3 <= 20
            //     x1 - 3 x2 + x3 <= 30
            rng[0] = new IloRange[2];
            rng[0][0] = cplex.addLe(cplex.sum(cplex.prod(-1.0, x[0]), cplex.prod(1.0, x[1]), cplex.prod(1.0, x[2])), 20.0, "c1");
            rng[0][1] = cplex.addLe(cplex.sum(cplex.prod(1.0, x[0]), cplex.prod(-3.0, x[1]), cplex.prod(1.0, x[2])), 30.0, "c2");

            // write model to file
            cplex.exportModel("lpex1.lp");

            // solve the model and display the solution if one was found
            if (cplex.solve()) {
                double[] xv = cplex.getValues(var[0]);
                double[] dj = cplex.getReducedCosts(var[0]);
                double[] pi = cplex.getDuals(rng[0]);
                double[] slack = cplex.getSlacks(rng[0]);

                cplex.output().println("Solution status = " + cplex.getStatus());
                cplex.output().println("Solution value  = " + cplex.getObjValue());

                int nvars = xv.length;
                for (int j = 0; j < nvars; ++j) {
                    cplex.output().println("Variable " + j
                            + ": Value = " + xv[j]
                            + " Reduced cost = " + dj[j]);
                }

                int ncons = slack.length;
                for (int i = 0; i < ncons; ++i) {
                    cplex.output().println("Constraint " + i
                            + ": Slack = " + slack[i]
                            + " Pi = " + pi[i]);
                }
            }
        } catch (IloException e) {
            System.err.println("Concert exception '" + e + "' caught");
        }
    }
}
