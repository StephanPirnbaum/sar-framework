package com.buchmais.sarf.classification.criterion.cohesion;

import com.buchmais.sarf.repository.MetricRepository;
import org.jenetics.LongGene;
import org.jenetics.util.ISeq;
import org.jenetics.util.LongRange;

/**
 * @author Stephan Pirnbaum
 */
public class LongObjectiveCouplingChromosome extends  LongObjectiveChromosome {

    protected LongObjectiveCouplingChromosome(ISeq<LongGene> genes) {
        super(genes);
    }

    public LongObjectiveCouplingChromosome(Long min, Long max, int length) {
        super(min, max, length);
    }

    public LongObjectiveCouplingChromosome(Long min, Long max) {
        super(min, max);
    }

    @Override
    Double computeCohesion(MetricRepository mR, long[] ids) {
        return mR.computeCouplingCohesionInComponent(ids) / ids.length;
    }

    @Override
    Double computeCoupling(MetricRepository mR, long[] ids1, long[] ids2) {
        return mR.computeCouplingBetweenComponents(ids1, ids2);
    }

    @Override
    public LongObjectiveCouplingChromosome newInstance(ISeq<LongGene> genes) {
        return new LongObjectiveCouplingChromosome(genes);
    }

    @Override
    public LongObjectiveCouplingChromosome newInstance() {
        return new LongObjectiveCouplingChromosome(this.getMin(), this.getMax(), this.length());
    }

    /**
     * Create a new {@code LongObjectiveChromosome} with the given genes.
     *
     * @param genes the genes of the chromosome.
     * @return a new chromosome with the given genes.
     * @throws IllegalArgumentException if the length of the genes array is
     *         empty.
     * @throws NullPointerException if the given {@code genes} are {@code null}
     */
    public static LongObjectiveCouplingChromosome of(final LongGene... genes) {
        return new LongObjectiveCouplingChromosome(ISeq.of(genes));
    }

    /**
     * Create a new random {@code LongObjectiveChromosome}.
     *
     * @param min the min value of the {@link LongGene}s (inclusively).
     * @param max the max value of the {@link LongGene}s (inclusively).
     * @param length the length of the chromosome.
     * @return a new {@code LongObjectiveChromosome} with the given gene parameters.
     * @throws IllegalArgumentException if the {@code length} is smaller than
     *         one.
     */
    public static LongObjectiveCouplingChromosome of(
            final long min,
            final long max,
            final int length
    ) {
        return new LongObjectiveCouplingChromosome(min, max, length);
    }

    /**
     * Create a new random {@code LongObjectiveChromosome}.
     *
     * @since 3.2
     *
     * @param range the long range of the chromosome.
     * @param length the length of the chromosome.
     * @return a new random {@code LongObjectiveChromosome}
     * @throws NullPointerException if the given {@code range} is {@code null}
     * @throws IllegalArgumentException if the {@code length} is smaller than
     *         one.
     */
    public static LongObjectiveCouplingChromosome of(final LongRange range, final int length) {
        return new LongObjectiveCouplingChromosome(range.getMin(), range.getMax(), length);
    }

    /**
     * Create a new random {@code LongObjectiveChromosome} of length one.
     *
     * @param min the minimal value of this chromosome (inclusively).
     * @param max the maximal value of this chromosome (inclusively).
     * @return a new {@code LongObjectiveChromosome} with the given gene parameters.
     */
    public static LongObjectiveCouplingChromosome of(final long min, final long max) {
        return new LongObjectiveCouplingChromosome(min, max);
    }

    /**
     * Create a new random {@code LongObjectiveChromosome} of length one.
     *
     * @since 3.2
     *
     * @param range the long range of the chromosome.
     * @return a new random {@code LongObjectiveChromosome} of length one
     * @throws NullPointerException if the given {@code range} is {@code null}
     */
    public static LongObjectiveCouplingChromosome of(final LongRange range) {
        return new LongObjectiveCouplingChromosome(range.getMin(), range.getMax());
    }
}