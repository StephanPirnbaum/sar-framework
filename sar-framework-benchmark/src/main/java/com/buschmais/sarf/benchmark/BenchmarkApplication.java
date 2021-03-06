package com.buschmais.sarf.benchmark;

import io.jenetics.Genotype;
import io.jenetics.LongGene;
import io.jenetics.engine.EvolutionResult;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;

/**
 * @author Stephan Pirnbaum
 */
public class BenchmarkApplication {

    public static void main(String[] args) throws URISyntaxException {
        /*DatabaseHelper.setUpDB(new URI("file:///E:/Development/trainingszeitverwaltung-kraftraum/target/jqassistant/store"));
        LongChromosome chromosome = LongChromosome.of(0, 3, 13);
        Genotype<LongGene> genotype = Genotype.of(chromosome);
        Engine<LongGene, Double> engine = Engine
                .builder(BenchmarkApplication::computeFitnessValue, genotype)
                .fitnessScaler(f -> Math.pow(f, 2))
                .maximizing()
                .offspringSelector(new RouletteWheelSelector<>())
                .survivorsSelector(new RouletteWheelSelector<>())
                .alterers(new MultiPointCrossover<>(0.8), new GaussianMutator<>(0.1))
                .executor(Executors.newSingleThreadExecutor())
                .populationSize(5)
                .build();
        engine
                .stream()
                .limit(50)
                .peek(BenchmarkApplication::peek)
                .collect(EvolutionResult.toBestGenotype());*/
    }

    private static Double computeFitnessValue(final Genotype<LongGene> prospect) {
        /*WeightConstants.DEPENDS_ON_WEIGHT = prospect.get(0, 0).doubleValue();
        WeightConstants.INVOKES_WEIGHT = prospect.get(0, 1).doubleValue();
        WeightConstants.READS_WEIGHT = prospect.get(0, 2).doubleValue();
        WeightConstants.WRITES_WEIGHT = prospect.get(0, 3).doubleValue();
        WeightConstants.READS_STATIC_WEIGHT = prospect.get(0, 4).doubleValue();
        WeightConstants.WRITES_STATIC_WEIGHT = prospect.get(0, 5).doubleValue();
        WeightConstants.EXTENDS_WEIGHT = prospect.get(0, 6).doubleValue();
        WeightConstants.IMPLEMENTS_WEIGHT = prospect.get(0, 7).doubleValue();
        WeightConstants.PARAMETER_WEIGHT = prospect.get(0, 8).doubleValue();
        WeightConstants.RETURNS_WEIGHT = prospect.get(0, 9).doubleValue();
        WeightConstants.INNER_CLASSES_WEIGHT = prospect.get(0, 10).doubleValue();
        WeightConstants.INVOKES_STATIC_WEIGHT = prospect.get(0, 11).doubleValue();
        WeightConstants.COMPOSES_WEIGHT = prospect.get(0, 12).doubleValue();
        ClassificationRunner runner = ClassificationRunner.getInstance();
        try {
            return runner.run(null,
                    new URL("file:///E:/Development/sar-framework/src/main/resources/benchmark3.xml"), null);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }*/
        return Double.MIN_VALUE;
    }

    private static void peek(final EvolutionResult<LongGene, Double> result) {
        try (FileWriter fw = new FileWriter("Result_Gen_" + result.getGeneration() + ".txt")) {
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            pw.println("Parameters: " + result.getBestPhenotype().getGenotype().getChromosome());
            pw.println("Quality: " + result.getBestPhenotype().getFitness());
            pw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
