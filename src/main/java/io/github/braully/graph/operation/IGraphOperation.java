package io.github.braully.graph.operation;

import io.github.braully.graph.UndirectedSparseGraphTO;
import java.util.Map;

/**
 * Graph operation.
 *
 * @author braully
 */
public interface IGraphOperation {

    public static final String DEFAULT_PARAM_NAME_RESULT = "result";
    public static final String PARAM_NAME_TOTAL_TIME_MS = "Time(s)";

    public String getTypeProblem();

    public String getName();

    public Map<String, Object> doOperation(UndirectedSparseGraphTO<Integer, Integer> graph);
}
