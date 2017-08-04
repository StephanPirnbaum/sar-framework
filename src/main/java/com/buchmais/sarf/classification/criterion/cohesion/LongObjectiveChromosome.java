package com.buchmais.sarf.classification.criterion.cohesion;

import com.buchmais.sarf.benchmark.MoJoCalculator;
import com.buchmais.sarf.benchmark.ModularizationQualityCalculator;
import com.google.common.collect.Sets;
import org.jenetics.LongChromosome;
import org.jenetics.LongGene;
import org.jenetics.util.ISeq;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * @author Stephan Pirnbaum
 */
public abstract class LongObjectiveChromosome extends LongChromosome {

    private boolean evaluated = false;

    private Double cohesionObjective = 0d;

    private Double couplingObjective = 0d;

    private Double componentCountObjective = 0d;

    private Double componentSizeObjective = 0d;

    private Double componentRangeObjective = 0d;

    private Double mQ = 0d;

    public double moJoFM;

    protected LongObjectiveChromosome(ISeq<LongGene> genes) {
        super(genes);
    }

    public LongObjectiveChromosome(Long min, Long max, int length) {
        super(min, max, length);
    }

    public LongObjectiveChromosome(Long min, Long max) {
        super(min, max);
    }

    private void evaluate() {
        boolean invalid = false;
        // mapping from component id to a set of type ids
        Map<Long, Set<Long>> identifiedComponents = new HashMap<>();
        for (int i = 0; i < this.length(); i++) {
            identifiedComponents.merge(
                    this.getGene(i).getAllele(),
                    Sets.newHashSet(Partitioner.ids[i]),
                    (s1, s2) -> {
                        s1.addAll(s2);
                        return s1;
                    });
        }
        // compute fitness for intra-edge coupling (cohesiveness of components)
        for (Map.Entry<Long, Set<Long>> component1 : identifiedComponents.entrySet()) {
            if (!invalid) {
                Double cohesion = computeCohesion(component1.getValue());
                if (cohesion == 0) {
                    invalid = true;
                }
                this.cohesionObjective += cohesion;
            }
            // compute fitness for inter-edge coupling (coupling of components)
            // is compared twice -> punishing inter-edges
            for (Map.Entry<Long, Set<Long>> component2 : identifiedComponents.entrySet()) {
                if (!Objects.equals(component1.getKey(), component2.getKey())) {
                    this.couplingObjective -= computeCoupling(component1.getValue(), component2.getValue());
                }
            }
        }
        this.couplingObjective = normalizeCoupling(this.couplingObjective, identifiedComponents.size()); // TODO: 27.07.2017 Similarity undirected, coupling directed
        this.cohesionObjective /= identifiedComponents.size();
        // minimize the difference between min and max component size
        this.componentRangeObjective = ((double) (identifiedComponents.values().stream().mapToInt(Set::size).min().orElse(0) -
                identifiedComponents.values().stream().mapToInt(Set::size).max().orElse(0))) / (Partitioner.ids.length - 1);
        // punish one-type only components
        this.componentSizeObjective = -identifiedComponents.values().stream().mapToInt(Set::size).filter(i -> i == 1).count() / (double) identifiedComponents.size();
        // maximize component number
        this.componentCountObjective =
                identifiedComponents.size() <= 0.25 * Partitioner.ids.length ?
                        (identifiedComponents.size() / (Partitioner.ids.length / 4d)) :
                        (Partitioner.ids.length - identifiedComponents.size()) / (0.75 * Partitioner.ids.length);
        if (invalid) {
            //this.couplingObjective = -1D;
            this.cohesionObjective = -1D;
            //this.componentSizeObjective = -1D;
            //this.componentRangeObjective = -1D;
            //this.componentCountObjective = -1D;
        }
        if (MoJoCalculator.reference != null) {
            writeBenchmarkLine(identifiedComponents);
        }
        this.evaluated = true;

    }

    abstract Double computeCohesion(Collection<Long> ids);

    abstract Double computeCoupling(Collection<Long> ids1, Collection<Long> ids2);

    abstract Double normalizeCoupling(Double coupling, int components);

    abstract Double computeMQ(Map<Long, Set<Long>> decomposition);

    protected Double getCohesionObjective() {
        if (!this.evaluated) evaluate();
        return this.cohesionObjective;
    }

    protected Double getCouplingObjective() {
        if (!this.evaluated) evaluate();
        return this.couplingObjective;
    }

    protected Double getComponentSizeObjective() {
        if (!this.evaluated) evaluate();
        return this.componentSizeObjective;
    }

    protected Double getComponentRangeObjective() {
        if (!this.evaluated) evaluate();
        return this.componentRangeObjective;
    }

    protected Double getComponentCountObjective() {
        if (!this.evaluated) evaluate();
        return this.componentCountObjective;
    }

    /**
     * @param chromosome
     * @return True if this dominates, false otherwise (including non-pareto comparable
     */
    protected boolean dominates(LongObjectiveChromosome chromosome) {
        if (!evaluated) this.evaluate();
        int better = 0;
        int equal = 0;
        int worse = 0;
        if (this.cohesionObjective < chromosome.cohesionObjective) {
            worse++;
        } else if (Objects.equals(this.cohesionObjective, chromosome.cohesionObjective)) {
            equal++;
        } else {
            better++;
        }
        if (this.couplingObjective < chromosome.couplingObjective) {
            worse++;
        } else if (Objects.equals(this.couplingObjective, chromosome.couplingObjective)) {
            equal++;
        } else {
            better++;
        }
        if (this.componentSizeObjective < chromosome.componentSizeObjective) {
            worse++;
        } else if (Objects.equals(this.componentSizeObjective, chromosome.componentSizeObjective)) {
            equal++;
        } else {
            better++;
        }
        if (this.componentCountObjective < chromosome.componentCountObjective) {
            worse++;
        } else if (Objects.equals(this.componentCountObjective, chromosome.componentCountObjective)) {
            equal++;
        } else {
            better++;
        }
        if (this.componentRangeObjective < chromosome.componentRangeObjective) {
            worse++;
        } else if (Objects.equals(this.componentRangeObjective, chromosome.componentRangeObjective)) {
            equal++;
        } else {
            better++;
        }

        if (better > 0 && worse == 0) return true;
        return false;

    }

    private void writeBenchmarkLine(Map<Long, Set<Long>> identifiedComponents) {
        MoJoCalculator moJoCalculator1 = new MoJoCalculator(identifiedComponents, true);
        MoJoCalculator moJoCalculator2 = new MoJoCalculator(identifiedComponents, false);
        MoJoCalculator moJoFmCalculator = new MoJoCalculator(identifiedComponents, true);
        MoJoCalculator moJoPlusCalculator1 = new MoJoCalculator(identifiedComponents, true);
        MoJoCalculator moJoPlusCalculator2 = new MoJoCalculator(identifiedComponents, false);
        Long mojoCompRef = moJoCalculator1.mojo();
        Long mojoRefComp = moJoCalculator2.mojo();
        Long mojo = Math.min(mojoCompRef, mojoRefComp);
        Double mojoFm = moJoFmCalculator.mojofm();
        Long mojoPlusCompRef = moJoPlusCalculator1.mojoplus();
        Long mojoPlusRefComp = moJoPlusCalculator2.mojoplus();
        Long mojoPlus = Math.min(mojoPlusCompRef, mojoPlusRefComp);
        Double fitness = this.cohesionObjective + this.couplingObjective + this.componentCountObjective +
                this.componentRangeObjective + this.componentSizeObjective;
        Double mQSim = ModularizationQualityCalculator.computeSimilarityBasedMQ(identifiedComponents);
        Double mQCoup = ModularizationQualityCalculator.computeCouplingBasedMQ(identifiedComponents);
        try (FileWriter fw = new FileWriter("benchmark.csv", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.print(Partitioner.lastGeneration + 1 + " , ");
            out.print(identifiedComponents.size() + ", ");
            out.print(this.cohesionObjective + ", ");
            out.print(this.couplingObjective + ", ");
            out.print(this.componentSizeObjective + ", ");
            out.print(this.componentRangeObjective + ", ");
            out.print(this.componentCountObjective + ", ");
            out.print(mojo + ", ");
            out.print(mojoFm + ", ");
            out.print(mojoPlus + ", ");
            out.print(mQSim + ", ");
            out.print(mQCoup + ", ");
            out.println(fitness);
        } catch (IOException e) {
        }
    }
}
