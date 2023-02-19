/**
 * Copyright 2021-2022 SET GmbH, Tobias Baum.
 *
 * This file is part of AppliedInformationEconomics.
 *
 * AppliedInformationEconomics is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AppliedInformationEconomics is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package de.set.aie.base;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class Distributions {

    public enum StdDist {
        NORMAL {
            @Override
            public RandomVariable create(final Between range, final Unit unit) {
                return normal(range.lower, range.upper, unit);
            }
        },
        LOG_NORMAL {
            @Override
            public RandomVariable create(final Between range, final Unit unit) {
                return logNormal(range.lower, range.upper, unit);
            }
        },
        SHIFTED_EXP {
            @Override
            public RandomVariable create(final Between range, final Unit unit) {
                return shiftedExp(range.lower, range.upper, unit);
            }
        },
        INVERSE_SHIFTED_EXP {
            @Override
            public RandomVariable create(final Between range, final Unit unit) {
                return inverseShiftedExp(range.lower, range.upper, unit);
            }
        },
        BLOCK {
            @Override
            public RandomVariable create(final Between range, final Unit unit) {
                return block(range.lower, range.upper, unit);
            }
        };

        public abstract RandomVariable create(Between range, Unit unit);
    }

    public static class Between {
        private final double lower;
        private final double upper;

        public Between(final double lower, final double upper) {
            assert lower <= upper;
            this.lower = lower;
            this.upper = upper;
        }
    }

    public static class SingleMode {
        private final double lower;
        private final double mode;
        private final double upper;

        public SingleMode(final double lower, final double mode, final double upper) {
            assert lower <= mode;
            assert mode <= upper;
            this.lower = lower;
            this.mode = mode;
            this.upper = upper;
        }
    }

    public static Between between(final double lower, final double upper) {
        return new Between(lower, upper);
    }

    public static SingleMode singleMode(final double lower, final double mode, final double upper) {
        return new SingleMode(lower, mode, upper);
    }

    public static NormalRandomVariable normal(final double lower95, final double upper95, final Unit unit) {
        assert lower95 < upper95;
        final double mean = (lower95 + upper95) / 2.0;
        final double sd = (upper95 - lower95)  / 1.645 / 2.0;
        return new NormalRandomVariable(mean, sd, unit);
    }

    public static LogNormalRandomVariable logNormal(final double lower95, final double upper95, final Unit unit) {
        assert lower95 < upper95;
        assert 0.0 <= lower95;
        final double normalLower95 = lower95 > 0.0 ? Math.log(lower95) : 0.0;
        final double normalUpper95 = Math.log(upper95);
        final double mean = (normalLower95 + normalUpper95) / 2.0;
        final double sd = (normalUpper95 - normalLower95)  / 1.645 / 2.0;
        return new LogNormalRandomVariable(mean, sd, unit);
    }

    public static BinomRandomVariable binom(final int count, final RandomVariable prop, final Unit unit) {
        return binom(fixed(Quantity.of(count, unit)), prop);
    }

    public static BinomRandomVariable binom(final RandomVariable count, final RandomVariable prop) {
        return new BinomRandomVariable(count, prop);
    }

    public static FixedRandomVariable fixed(final Quantity q) {
        return new FixedRandomVariable(q);
    }

    public static RandomVariable empirical(final Unit unit, final double... values) {
        return new EmpiricalRandomVariable(unit, values);
    }

    public static RandomVariable empirical(final Unit unit, final List<? extends Number> values) {
        final double[] d = new double[values.size()];
        for (int i = 0; i < values.size(); i++) {
            d[i] = values.get(i).doubleValue();
        }
        return new EmpiricalRandomVariable(unit, d);
    }

    public static RandomVariable block(final double lower05, final double upper95, final Unit unit) {
        return new BlockDistributedRandomVariable(lower05, upper95, unit);
    }

    public static RandomVariable shiftedExp(final double lower, final double upper, final Unit unit) {
        return new ShiftedExponentialRandomVariable(lower, upper, false, unit);
    }

    public static RandomVariable inverseShiftedExp(final double lower, final double upper, final Unit unit) {
        return new ShiftedExponentialRandomVariable(lower, upper, true, unit);
    }

    public static RandomVariable conditional(final double v1Prop, final RandomVariable v1, final RandomVariable v2) {
        return new ConditionalDistribution(v1Prop, v1, v2);
    }

    public static RandomVariable conditional(final RandomVariable v1Prop, final RandomVariable v1, final RandomVariable v2) {
        return new ConditionalDistribution(v1Prop, v1, v2);
    }

    /**
     * Mit Wahrscheinlichkeit v1Prop kommt der Wert von v1, ansonsten kommt 0 (mit passender Einheit).
     */
    public static RandomVariable conditional(final RandomVariable v1Prop, final RandomVariable v1) {
        return conditional(v1Prop, v1, fixed(Quantity.of(0, v1.getUnit())));
    }

    public static RandomVariable triangleAbsolute(final SingleMode params, final Unit unit) {
        return TriangularRandomVariable.fromAbsoluteMinMax(
                Quantity.of(params.lower, unit),
                Quantity.of(params.mode, unit),
                Quantity.of(params.upper, unit));
    }

    public static MultiVariableFactory unknown(List<Between> ranges, final Unit unit) {
        return unknown(EnumSet.allOf(StdDist.class), Double.NEGATIVE_INFINITY, ranges, Double.POSITIVE_INFINITY, unit);
    }

    public static MultiVariableFactory unknown(final double lower, final double upper, final Unit unit) {
        return unknown(Double.NEGATIVE_INFINITY, lower, upper, Double.POSITIVE_INFINITY, unit);
    }

    public static MultiVariableFactory unknown(
            final double absoluteMin,
            final double lower,
            final double upper,
            final double absoluteMax,
            final Unit unit) {
        final EnumSet<StdDist> distributions = EnumSet.allOf(StdDist.class);
        return unknown(distributions, absoluteMin, lower, upper, absoluteMax, unit);
    }

    public static MultiVariableFactory unknown(
            final Set<StdDist> possibleDistributions,
            final double absoluteMin,
            final double lower,
            final double upper,
            final double absoluteMax,
            final Unit unit) {
        assert absoluteMin <= lower;
        assert lower < upper;
        assert upper <= absoluteMax;

        return unknown(possibleDistributions, absoluteMin,
                Collections.singletonList(Distributions.between(lower, upper)), absoluteMax, unit);
    }

    public static MultiVariableFactory unknown(
            final Set<StdDist> possibleDistributions,
            final double absoluteMin,
            final List<Between> estimatedRanges,
            final double absoluteMax,
            final Unit unit) {
        return new UnknownDistFactory(possibleDistributions, absoluteMin, estimatedRanges, absoluteMax, unit);
    }

    /**
     * Liefert die Verteilung für die Anzahl der Elemente der Grundgesamtheit mit einer Eigenschaft X, wenn
     * in einer zufälligen Stichprobe eine bestimmte Anzahl von Elementen mit X beobachtet wurden.
     *
     * Siehe auch https://stats.stackexchange.com/questions/311088/beta-binomial-as-conjugate-to-hypergeometric
     *
     * @param populationSize Anzahl der Elemente in der Grundgesamtheit.
     * @param sampleSize Anzahl der Elemente in der Stichprobe.
     * @param positiveInSample Anzahl der Elemente in der Stichprobe mit Eigenschaft X
     */
    public static BetaBinomialRandomVariable extrapolateFromSample(
            int populationSize, int sampleSize, int positiveInSample, Unit unit) {
        assert sampleSize <= populationSize;
        assert positiveInSample <= sampleSize;
        assert positiveInSample >= 0;
        return new BetaBinomialRandomVariable(
                populationSize - sampleSize,
                1 + positiveInSample,
                1 + sampleSize - positiveInSample,
                positiveInSample,
                unit);
    }
}
