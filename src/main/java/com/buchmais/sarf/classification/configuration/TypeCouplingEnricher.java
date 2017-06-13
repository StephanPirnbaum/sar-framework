package com.buchmais.sarf.classification.configuration;

import com.buchmais.sarf.SARFRunner;
import com.buchmais.sarf.repository.MetricRepository;
import com.buchmais.sarf.repository.TypeRepository;
import com.buschmais.jqassistant.plugin.java.api.model.TypeDescriptor;
import sun.awt.windows.WEmbeddedFrame;

import java.util.Set;
import java.util.TreeSet;

/**
 * @author Stephan Pirnbaum
 */
public class TypeCouplingEnricher {

    public static void enrich() {
        SARFRunner.xoManager.currentTransaction().begin();
        Set<String> orderedCoupling = new TreeSet<>();
        MetricRepository mR = SARFRunner.xoManager.getRepository(MetricRepository.class);
        for (TypeDescriptor t1 : SARFRunner.xoManager.getRepository(TypeRepository.class).getAllInternalTypes()) {
            final Long id1 = SARFRunner.xoManager.getId(t1);
            for (TypeDescriptor t2 : SARFRunner.xoManager.getRepository(TypeRepository.class).getInternalDependencies(id1)) {
                final Long id2 = SARFRunner.xoManager.getId(t2);
                Double coupling = computeCoupling(id1, id2);
                if (coupling > 0) {
                    mR.setCoupling(id1, id2, coupling);
                }
                orderedCoupling.add(coupling + " " + t1.getFullQualifiedName()+ " " + t2.getFullQualifiedName());
            }
        }
        orderedCoupling.forEach(System.out::println);
        SARFRunner.xoManager.currentTransaction().commit();
    }

    private static Double computeCoupling(Long id1, Long id2) {
        Double totalWeight =
                WeightConstants.INVOKES_WEIGHT +
                WeightConstants.INVOKES_STATIC_WEIGHT +
                WeightConstants.EXTENDS_WEIGHT +
                WeightConstants.IMPLEMENTS_WEIGHT +
                WeightConstants.RETURNS_WEIGHT +
                WeightConstants.PARAMETER_WEIGHT +
                WeightConstants.READS_WEIGHT +
                WeightConstants.READS_STATIC_WEIGHT +
                WeightConstants.WRITES_WEIGHT +
                WeightConstants.WRITES_STATIC_WEIGHT +
                WeightConstants.COMPOSES_WEIGHT +
                WeightConstants.INNER_CLASSES_WEIGHT;

        Double weightedCoupling =
                WeightConstants.INVOKES_WEIGHT * computeCouplingInvokes(id1, id2) +
                WeightConstants.INVOKES_STATIC_WEIGHT * computeCouplingInvokesStatic(id1, id2) +
                WeightConstants.EXTENDS_WEIGHT * computeCouplingExtends(id1, id2) +
                WeightConstants.IMPLEMENTS_WEIGHT * computeCouplingImplements(id1, id2) +
                WeightConstants.RETURNS_WEIGHT * computeCouplingReturns(id1, id2) +
                WeightConstants.PARAMETER_WEIGHT * computeCouplingParameterized(id1, id2) +
                WeightConstants.READS_WEIGHT * computeCouplingReads(id1, id2) +
                WeightConstants.READS_STATIC_WEIGHT * computeCouplingReadsStatic(id1, id2) +
                WeightConstants.WRITES_WEIGHT * computeCouplingWrites(id1, id2) +
                WeightConstants.WRITES_STATIC_WEIGHT * computeCouplingWritesStatic(id1, id2) +
                WeightConstants.COMPOSES_WEIGHT * computeCouplingComposes(id1, id2) +
                WeightConstants.INNER_CLASSES_WEIGHT * computeCouplingDeclaresInnerClass(id1, id2);

        /*System.out.println("1 " + computeCouplingInvokes(id1, id2));
        System.out.println("2 " + computeCouplingInvokesStatic(id1, id2));
        System.out.println("3 " + computeCouplingExtends(id1, id2));
        System.out.println("4 " + computeCouplingImplements(id1, id2));
        System.out.println("5 " + computeCouplingReturns(id1, id2));
        System.out.println("6 " + computeCouplingParameterized(id1, id2));
        System.out.println("7 " + computeCouplingReads(id1, id2));
        System.out.println("8 " + computeCouplingReadsStatic(id1, id2));
        System.out.println("9 " + computeCouplingWrites(id1, id2));
        System.out.println("10 " + computeCouplingWritesStatic(id1, id2));*/
        Double res = weightedCoupling / totalWeight;
        return Double.isNaN(res) ? 0 : res;
    }

    private static Double computeCouplingInvokes(Long id1, Long id2) {
        MetricRepository mR = SARFRunner.xoManager.getRepository(MetricRepository.class);
        Double res = (double) mR.countInvokes(id1, id2) / (2 * mR.countAllInvokesExternal(id1)) +
                     (double) mR.countInvokes(id2, id1) / (2 * mR.countAllInvokesExternal(id2));
        return Double.isNaN(res) ? 0 : res;
    }

    private static Double computeCouplingInvokesStatic(Long id1, Long id2) {
        MetricRepository mR = SARFRunner.xoManager.getRepository(MetricRepository.class);
        Double res = (double) mR.countInvokesStatic(id1, id2) / (2 * mR.countAllInvokesExternalStatic(id1)) +
                     (double) mR.countInvokesStatic(id2, id1) / (2 * mR.countAllInvokesExternalStatic(id2));
        return Double.isNaN(res) ? 0 : res;
    }

    private static Double computeCouplingExtends(Long id1, Long id2) {
        MetricRepository mR = SARFRunner.xoManager.getRepository(MetricRepository.class);
        return (mR.typeExtends(id1, id2) ? 1d : 0d) + (mR.typeExtends(id2, id1) ? 1d : 0d);
    }

    private static Double computeCouplingImplements(Long id1, Long id2) {
        MetricRepository mR = SARFRunner.xoManager.getRepository(MetricRepository.class);
        return (mR.typeImplements(id1, id2) ? 1d : 0d) + (mR.typeImplements(id2, id1) ? 1d : 0d);
    }

    private static Double computeCouplingReturns(Long id1, Long id2) {
        MetricRepository mR = SARFRunner.xoManager.getRepository(MetricRepository.class);
        // todo generics
        Double res = (double) mR.countReturns(id1, id2) / (2 * mR.countMethods(id1)) +
                     (double) mR.countReturns(id2, id1) / (2 * mR.countMethods(id2));
        return Double.isNaN(res) ? 0 : res;
    }

    private static Double computeCouplingParameterized(Long id1, Long id2) {
        MetricRepository mR = SARFRunner.xoManager.getRepository(MetricRepository.class);
        // todo generics
        Double res = (double) mR.countParameterized(id1, id2) / (2 * mR.countMethods(id1)) +
                     (double) mR.countParameterized(id2, id1) / (2 * mR.countMethods(id2));
        return Double.isNaN(res) ? 0 : res;
    }

    private static Double computeCouplingReads(Long id1, Long id2) {
        MetricRepository mR = SARFRunner.xoManager.getRepository(MetricRepository.class);
        final Long readsT1T2 = mR.countReads(id1, id2);
        final Long readsT2T1 = mR.countReads(id2, id1);
        Double res = ((double) readsT1T2 * readsT1T2) / (2 * mR.countReadsExternal(id1) * mR.countReadByExternal(id2)) +
                     ((double) readsT2T1 * readsT2T1) / (2 * mR.countReadsExternal(id2) * mR.countReadByExternal(id1));
        return Double.isNaN(res) ? 0 : res;
    }

    private static Double computeCouplingReadsStatic(Long id1, Long id2) {
        MetricRepository mR = SARFRunner.xoManager.getRepository(MetricRepository.class);
        final Long readsT1T2 = mR.countReadsStatic(id1, id2);
        final Long readsT2T1 = mR.countReadsStatic(id2, id1);
        Double res = ((double) readsT1T2 * readsT1T2) / (2 * mR.countReadsStaticExternal(id1) * mR.countReadByExternalStatic(id2)) +
                     ((double) readsT2T1 * readsT2T1) / (2 * mR.countReadsStaticExternal(id2) * mR.countReadByExternalStatic(id1));
        return Double.isNaN(res) ? 0 : res;
    }

    private static Double computeCouplingWrites(Long id1, Long id2) {
        MetricRepository mR = SARFRunner.xoManager.getRepository(MetricRepository.class);
        final Long writesT1T2 = mR.countWrites(id1, id2);
        final Long writesT2T1 = mR.countWrites(id2, id1);
        Double res = ((double) writesT1T2 * writesT1T2) / (2 * mR.countWritesExternal(id1) * mR.countWrittenByExternal(id2)) +
                     ((double) writesT2T1 * writesT2T1) / (2 * mR.countWritesExternal(id2) * mR.countWrittenByExternal(id1));
        return Double.isNaN(res) ? 0 : res;
    }

    private static Double computeCouplingWritesStatic(Long id1, Long id2) {
        MetricRepository mR = SARFRunner.xoManager.getRepository(MetricRepository.class);
        final Long writesT1T2 = mR.countWritesStatic(id1, id2);
        final Long writesT2T1 = mR.countWritesStatic(id2, id1);
        Double res = ((double) writesT1T2 * writesT1T2) / (2 * mR.countWritesStaticExternal(id1) * mR.countWrittenByExternalStatic(id2)) +
                     ((double) writesT2T1 * writesT2T1) / (2 * mR.countWritesStaticExternal(id2) * mR.countWrittenByExternalStatic(id1));
        return Double.isNaN(res) ? 0 : res;
    }

    private static Double computeCouplingComposes(Long id1, Long id2) {
        MetricRepository mR = SARFRunner.xoManager.getRepository(MetricRepository.class);
        return (mR.typeComposes(id1, id2) ? 0.5 : 0d) + (mR.typeComposes(id1, id2) ? 0.5 : 0d);
    }

    private static Double computeCouplingDeclaresInnerClass(Long id1, Long id2) {
        MetricRepository mR = SARFRunner.xoManager.getRepository(MetricRepository.class);
        return (mR.declaresInnerClass(id1, id2) || mR.declaresInnerClass(id2, id1)) ? 1d : 0d;
    }
}