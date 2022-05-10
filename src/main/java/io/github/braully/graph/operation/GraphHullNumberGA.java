package io.github.braully.graph.operation;

import io.github.braully.graph.UndirectedSparseGraphTO;
import java.util.Set;
import org.apache.commons.math3.genetics.FixedGenerationCount;
import org.apache.commons.math3.genetics.GeneticAlgorithm;
import org.apache.commons.math3.genetics.OnePointCrossover;
import org.apache.commons.math3.genetics.Population;
import org.apache.commons.math3.genetics.RandomKeyMutation;
import org.apache.commons.math3.genetics.StoppingCondition;
import org.apache.commons.math3.genetics.TournamentSelection;
import org.apache.log4j.Logger;

public class GraphHullNumberGA
        extends GraphHullNumber implements IGraphOperation {

    private static final Logger log = Logger.getLogger(GraphHullNumberGA.class);

    private static final int DIMENSION = 20;
    private static final int POPULATION_SIZE = 80;
    private static final int NUM_GENERATIONS = 200;
    private static final double ELITISM_RATE = 0.2;
    private static final double CROSSOVER_RATE = 1;
    private static final double MUTATION_RATE = 0.08;
    private static final int TOURNAMENT_ARITY = 2;

    //https://www.demo2s.com/java/apache-commons-geneticalgorithm-getgenerationsevolved.html
    //https://commons.apache.org/proper/commons-math/userguide/genetics.html
    GeneticAlgorithm algorithm = new GeneticAlgorithm(
            new OnePointCrossover<Integer>(),
            CROSSOVER_RATE,
            new RandomKeyMutation(),
            MUTATION_RATE,
            new TournamentSelection(TOURNAMENT_ARITY)
    );

    static final String description = "Hull Number Genetic Algorithm";

    @Override
    public String getName() {
        return description;
    }

    public GraphHullNumberGA() {
    }

    @Override
    public Set<Integer> findMinHullSetGraph(UndirectedSparseGraphTO<Integer, Integer> graph) {
        Set<Integer> hullSet = null;

        Population initial = getInitialPopulation();

        // stopping condition
        StoppingCondition stopCond = new FixedGenerationCount(NUM_GENERATIONS);

        // run the algorithm
        Population finalPopulation = algorithm.evolve(initial, stopCond);
        finalPopulation.getFittestChromosome();

        return hullSet;

    }

    private Population getInitialPopulation() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
