package io.github.braully.graph.operation;

import io.github.braully.graph.UndirectedSparseGraphTO;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;

public class GraphHullNumberHeuristicV1
        extends GraphHullNumber implements IGraphOperation {

    private static final Logger log = Logger.getLogger(GraphHullNumberHeuristicV1.class);

    static final String description = "Hull Number Heuristic";

    @Override
    public String getName() {
        return description;
    }

    public GraphHullNumberHeuristicV1() {
    }

    @Override
    public Set<Integer> findMinHullSetGraph(UndirectedSparseGraphTO<Integer, Integer> graph) {
        return buildOptimizedHullSet(graph);
    }

//    @Override
    public Set<Integer> buildOptimizedHullSet(UndirectedSparseGraphTO<Integer, Integer> graphRead) {
        Set<Integer> hullSet = null;
        Collection<Integer> vertices = graphRead.getVertices();
        for (Integer v : vertices) {
            Set<Integer> tmp = buildOptimizedHullSetFromStartVertice(graphRead, v);
            if (hullSet == null || tmp.size() < hullSet.size()) {
                hullSet = tmp;
            }
        }
        return hullSet;
    }

    private Set<Integer> buildOptimizedHullSetFromStartVertice(UndirectedSparseGraphTO<Integer, Integer> graph,
            Integer v) {
        System.out.println("start-vert: " + v);
        Set<Integer> s = new HashSet<>();
        int vertexCount = graph.getVertexCount();
        int[] aux = new int[vertexCount];
        for (int i = 0; i < vertexCount; i++) {
            aux[i] = 0;
        }
        int sizeHs = addVertToS(v, s, graph, aux);
        int bv;
        do {
            bv = -1;
            int maiorGrau = 0;
            int maiorDeltaHs = 0;

            for (int i = 0; i < vertexCount; i++) {
                if (aux[i] >= INCLUDED) {
                    continue;
                }
                int[] auxb = aux.clone();
                int deltaHsi = addVertToS(i, null, graph, auxb);

                int neighborCount = 0;
                for (int j = 0; j < vertexCount; j++) {
                    if (auxb[j] == INCLUDED) {
                        neighborCount++;
                    }
                }

                if (bv == -1 || (deltaHsi >= maiorDeltaHs && neighborCount > maiorGrau)) {
                    maiorDeltaHs = deltaHsi;
                    maiorGrau = neighborCount;
                    bv = i;
                }
            }
            sizeHs = sizeHs + addVertToS(bv, s, graph, aux);
        } while (sizeHs < vertexCount && bv != -1);
        return s;
    }

}
