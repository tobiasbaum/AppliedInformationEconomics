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

import java.util.Random;

public interface RandomSource {

    public static RandomSource wrap(Random r) {
        return new RandomSource() {
            @Override
            public double nextDouble() {
                return r.nextDouble();
            }
            @Override
            public int nextInt(int bound) {
                return r.nextInt(bound);
            }
            @Override
            public double nextGaussian() {
                return r.nextGaussian();
            }
            @Override
            public RandomSource spawnChild() {
                return RandomSource.wrap(new Random(r.nextLong()));
            }
        };
    }

    public abstract double nextDouble();

    public abstract int nextInt(int bound);

    public abstract double nextGaussian();

    public abstract RandomSource spawnChild();

    public default<T> T pickAtRandom(T[] array) {
        return array[this.nextInt(array.length)];
    }


}
