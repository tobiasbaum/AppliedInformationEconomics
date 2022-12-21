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
        };
    }

    public abstract double nextDouble();

    public abstract int nextInt(int bound);

    public abstract double nextGaussian();

    public default<T> T pickAtRandom(T[] array) {
        return array[this.nextInt(array.length)];
    }

}
