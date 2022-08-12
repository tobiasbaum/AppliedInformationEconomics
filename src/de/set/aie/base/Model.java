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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

import de.set.aie.base.Distributions.StdDist;

public class Model {

    private static final int VOPI_SAMPLE_COUNT = 7_000;
    private static final int VOI_SAMPLE_COUNT = 3_000;

    public class Instance {
        private final Map<String, RandomVariable> vars = new LinkedHashMap<>();

        public RandomVariable get(final String name) {
            if (this.vars.containsKey(name)) {
                return this.vars.get(name);
            }
            final Function<Instance, RandomVariable> f = Model.this.map.get(name);
            if (f == null) {
                throw new IllegalStateException("no definition for " + name);
            }
            final RandomVariable v = f.apply(this);
            this.vars.put(name, v);
            return v;
        }

        public Map<String, Sample> createSamples(final long seed, final int sampleCount, final String[] valueVariables) {
            final Map<String, Sample> samples = new LinkedHashMap<>();
            for (final String v : valueVariables) {
                samples.put(v, this.get(v).sample(seed, sampleCount));
            }
            return samples;
        }

    }

    private final Map<String, Function<Instance, RandomVariable>> map = new LinkedHashMap<>();

    public void add(final String name, final RandomVariable var) {
        this.add(name, (final Instance i) -> var);
    }

    public void add(final String name, final Function<Instance, RandomVariable> producer) {
        if (this.map.containsKey(name)) {
            throw new IllegalStateException(name + " already contained");
        }
        this.map.put(name, producer);
    }

    public void add(final String name, final MultiVariableFactory factoryFunction) {
        for (final Entry<String, Function<Instance, RandomVariable>> f : factoryFunction.create(name).entrySet()) {
            this.add(f.getKey(), f.getValue());
        }
    }

    public Instance instantiate() {
        return new Instance();
    }

    public void analyze(final long seed, final String... valueVariables) {
        System.out.println("=== Value variables");
        final Instance fullInstance = this.instantiate();
        final Map<String, Sample> originalSamples = fullInstance.createSamples(seed, 10_000, valueVariables);
        for (final Entry<String, Sample> e : originalSamples.entrySet()) {
            System.out.println("For " + e.getKey() + " ...");
            System.out.println(e.getValue());
        }

        final String bestChoice = this.determineBestChoice(originalSamples);
        System.out.println("Choice with best expected value: " + bestChoice);
        System.out.println();

        final Random sampleRandom = new Random(seed);
        final Map<String, Mean> meanLosses = new LinkedHashMap<>();
        for (final String name : this.map.keySet()) {
            meanLosses.put(name, Mean.undefined());
        }
        long lastPrintTime = 0;
        for (int j = 0; j < 10_000; j++) {
            for (final String name : this.map.keySet()) {
                for (int i = 0; i < 10; i++) {
                    final long iterSeed = seed + i + 100 * j;
                    final Instance reducedInstance = this.createReducedInstance(name, sampleRandom);
                    final Map<String, Sample> reducedSamples = reducedInstance.createSamples(iterSeed, 500, valueVariables);
                    final String bestChoiceWithInformation = this.determineBestChoice(reducedSamples);
                    if (bestChoice.equals(bestChoiceWithInformation)) {
                        meanLosses.compute(name, (final String k, final Mean v) -> v.add(0.0, 5_000));
                    } else {
                        final RandomVariable loss = reducedInstance.get(bestChoiceWithInformation).minus(reducedInstance.get(bestChoice));
                        meanLosses.compute(name, (final String k, final Mean v) -> v.add(loss.mean(iterSeed, 5_000)));
                    }
                }
            }
            final long curTime = System.currentTimeMillis();
            if (curTime - lastPrintTime > 15_000) {
                lastPrintTime = curTime;
                final List<String> sorted = new ArrayList<>(this.map.keySet());
                Collections.sort(sorted,
                        (final String n1, final String n2) -> Double.compare(meanLosses.get(n2).get(), meanLosses.get(n1).get()));
                System.out.println("=== Value of information (iter=" + j + ")");
                for (final String name : sorted) {
                    System.out.println("value of information for " + fullInstance.get(name).getType() + " " + name + ": " + meanLosses.get(name));
                }
                System.out.println();
            }
        }
    }

    private String determineBestChoice(final Map<String, Sample> samples) {
        String bestName = null;
        double best = Double.NEGATIVE_INFINITY;
        for (final Entry<String, Sample> e : samples.entrySet()) {
            final double cur = e.getValue().mean();
            if (cur > best) {
                bestName = e.getKey();
                best = cur;
            }
        }
        return bestName;
    }

    public void printValuesOfInformation(final long seed, final String valueVariable)
        throws InterruptedException, ExecutionException {

        System.out.println("Starting to calculate value of information ...");

        final Instance originalInstance = this.instantiate();
        final ExecutorService tp = Executors.newCachedThreadPool();
        final Future<Quantity> valueOfPerfectInformation =
                tp.submit(() -> this.determineMeanValueOfPerfectInformation(seed, originalInstance.get(valueVariable)));

        final Map<String, Future<Quantity>> reducedVois = new LinkedHashMap<>();
        final Instance typeInst = this.instantiate();
        for (final String name : this.map.keySet()) {
            reducedVois.put(typeInst.get(name).getType() + " " + name,
                    tp.submit(() -> this.reduceAndDetermineMeanVOI(name, seed, valueVariable)));
        }

        System.out.println("value of perfect information: " + valueOfPerfectInformation.get());
        final Map<String, Quantity> relativeVois = new LinkedHashMap<>();
        for (final String nameAndType : reducedVois.keySet()) {
            final Quantity valueOfInformationForReducedModel = reducedVois.get(nameAndType).get();
            Quantity relativeValueOfInformation = valueOfPerfectInformation.get().minus(valueOfInformationForReducedModel);
            if (relativeValueOfInformation.getNumber() < 0) {
                relativeValueOfInformation = Quantity.of(0, relativeValueOfInformation.getUnit());
            }
            relativeVois.put(nameAndType, relativeValueOfInformation);
        }
        final List<String> keys = new ArrayList<>(relativeVois.keySet());
        Collections.sort(keys, (final String k1, final String k2) ->
            Double.compare(relativeVois.get(k2).getNumber(), relativeVois.get(k1).getNumber()));
        for (final String key : keys) {
            System.out.println("value of information for " + key + ": " + relativeVois.get(key));
        }
    }

    private Quantity determineMeanValueOfPerfectInformation(final long seed, final RandomVariable randomVariable) {
        double sum = 0.0;
        for (int i = 0; i < VOI_SAMPLE_COUNT; i++) {
            final Quantity voi = this.determineValueOfPerfectInformation(seed + i, randomVariable);
            sum += voi.getNumber();
        }
        return Quantity.of(sum / VOI_SAMPLE_COUNT, randomVariable.getUnit());
    }

    private Quantity reduceAndDetermineMeanVOI(final String toReduce, final long seed, final String valueVariable) {
        double sum = 0.0;
        final Random sampleRandom = new Random(seed);
        for (int i = 0; i < VOI_SAMPLE_COUNT; i++) {
            final Instance reducedInstance = this.createReducedInstance(toReduce, sampleRandom);
            final Quantity voi = this.determineValueOfPerfectInformation(seed + i, reducedInstance.get(valueVariable));
            sum += voi.getNumber();
        }
        return Quantity.of(sum / VOI_SAMPLE_COUNT, this.instantiate().get(valueVariable).getUnit());
    }

    private Instance createReducedInstance(final String toReduce, final Random sampleRandom) {
        final Quantity sample = this.sampleValue(toReduce, sampleRandom);
        final Instance reducedInstance = this.instantiate();
        reducedInstance.vars.put(toReduce, Distributions.fixed(sample));
        return reducedInstance;
    }

    private Quantity sampleValue(final String toReduce, final Random sampleRandom) {
        return this.instantiate().get(toReduce).observe(sampleRandom, 0);
    }

    private Quantity determineValueOfPerfectInformation(final long seed, final RandomVariable randomVariable) {
        final Random r = new Random(seed);
        double costOfWrongDecision1 = 0.0;
        int count1 = 0;
        double costOfWrongDecision2 = 0.0;
        int count2 = 0;

        for (int i = 0; i < VOPI_SAMPLE_COUNT; i++) {
            final double result = randomVariable.observe(r, i).getNumber();
            if (result < 0) {
                costOfWrongDecision1 += -result;
                count1++;
            } else {
                costOfWrongDecision2 += result;
                count2++;
            }
        }

        if (count1 == 0 || count2 == 0) {
            return Quantity.of(0, randomVariable.getUnit());
        }

        final double expectedValue1 = costOfWrongDecision1 / count1;
        final double expectedValue2 = costOfWrongDecision2 / count2;

        if (expectedValue1 < expectedValue2) {
            return Quantity.of(expectedValue1, randomVariable.getUnit());
        } else {
            return Quantity.of(expectedValue2, randomVariable.getUnit());
        }
    }

    public void printDistributions(final File file, final long seed, final String... columns) throws IOException {
        final Random r = new Random(seed);
        try (FileOutputStream out = new FileOutputStream(file);
                BufferedWriter w = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"))) {
            final Instance inst = this.instantiate();
            final List<RandomVariable> v = new ArrayList<>();
            w.write("i");
            for (final String colName : columns) {
                final RandomVariable var = inst.get(colName);
                v.add(var);
                w.write(";" + colName + " (" + var.getUnit() + ")");
            }
            w.write('\n');
            for (int i = 0; i < 10_000; i++) {
                w.write(Integer.toString(i));
                for (final RandomVariable var : v) {
                    final Quantity q = var.observe(r, i);
                    w.write(';');
                    w.write(Double.toString(q.getNumber()).replace('.', ','));
                }
                w.write('\n');
            }
        }
    }

}
