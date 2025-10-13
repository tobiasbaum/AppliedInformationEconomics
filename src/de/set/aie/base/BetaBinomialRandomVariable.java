/**
 * Copyright 2021-2023 SET GmbH, Tobias Baum.
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

import org.apache.commons.math3.distribution.BetaDistribution;
import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.random.RandomGenerator;

/**
 * Zufallsvariable mit Beta-Binomialverteilung.
 *
 * Die Beta-Binomialverteilung ist "conjugate prior" zur hypergeometrischen Verteilung und deshalb von Nutzen,
 * wenn über eine Stichprobe aus einer Grundgesamtheit fester Größe Wissen gewonnen wurde (z.B. von insgesamt 100
 * Kunden wurden 10 befragt, davon traf die Bedingung auf 4 zu).
 */
public class BetaBinomialRandomVariable extends RandomVariable {

    private final int n;
    private final double a;
    private final double b;
    private final double shift;
    private final Unit unit;

    public BetaBinomialRandomVariable(int n, double a, double b, double shift, Unit unit) {
        this.n = n;
        this.a = a;
        this.b = b;
        this.shift = shift;
        this.unit = unit;
    }

    @Override
    public Quantity observe(RandomSource r, SimulationRun run) {
        final RandomGenerator rng = randomAdapter(r);
        final BetaDistribution beta = new BetaDistribution(rng, this.a, this.b);
        final BinomialDistribution binom = new BinomialDistribution(rng, this.n, beta.sample());
        return Quantity.of(binom.sample() + this.shift, this.unit);
    }

    private static RandomGenerator randomAdapter(RandomSource r) {
        return new RandomGenerator() {
            @Override
            public void setSeed(long seed) {
                throw new AssertionError();
            }
            @Override
            public void setSeed(int[] seed) {
                throw new AssertionError();
            }
            @Override
            public void setSeed(int seed) {
                throw new AssertionError();
            }
            @Override
            public long nextLong() {
                throw new AssertionError();
            }
            @Override
            public int nextInt(int n) {
                return r.nextInt(n);
            }
            @Override
            public int nextInt() {
                throw new AssertionError();
            }
            @Override
            public double nextGaussian() {
                return r.nextGaussian();
            }
            @Override
            public float nextFloat() {
                throw new AssertionError();
            }
            @Override
            public double nextDouble() {
                return r.nextDouble();
            }
            @Override
            public void nextBytes(byte[] bytes) {
                throw new AssertionError();
            }
            @Override
            public boolean nextBoolean() {
                throw new AssertionError();
            }
        };
    }

    @Override
    public Unit getUnit() {
        return this.unit;
    }

    @Override
    public VarKind getType() {
        return VarKind.D; //$NON-NLS-1$
    }

}
