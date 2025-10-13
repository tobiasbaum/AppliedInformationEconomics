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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.function.Function;

public class Model {

    public class Instance {
        private final ConcurrentHashMap<VarId, RandomVariable> vars = new ConcurrentHashMap<>();
        private final ConcurrentHashMap<String, Object> objects = new ConcurrentHashMap<>();

        public RandomVariable get(final VarId name) {
            final RandomVariable v = this.vars.get(name);
            if (v != null) {
                return v;
            }
            final Function<Instance, RandomVariable> f = Model.this.map.get(name);
            if (f == null) {
                throw new IllegalStateException("no definition for " + name);
            }
            final RandomVariable newV = f.apply(this);
            return this.vars.computeIfAbsent(name, (VarId n) -> newV);
        }

        public<T> T getObject(String name) {
            final T t = (T) this.objects.get(name);
            if (t != null) {
                return t;
            }
            final Function<Instance, Object> f = Model.this.objectMap.get(name);
            if (f == null) {
                throw new IllegalStateException("no definition for object " + name);
            }
            final T newT = (T) f.apply(this);
            return (T) this.objects.computeIfAbsent(name, (String n) -> newT);
        }

        public Map<VarId, double[]> createSamples(final long seed, final int sampleCount, final VarId... valueVariables)
            throws InterruptedException, ExecutionException {

            final Map<VarId, double[]> samples = new LinkedHashMap<>();
            for (final VarId v : valueVariables) {
                samples.put(v, new double[sampleCount]);
            }
            final RandomSource r1 = RandomSource.wrap(new Random(seed));
            final List<Future<?>> futures = new ArrayList<>();

            for (int i = 0; i < sampleCount; i += 100) {
                final int base = i;
                final int max = Math.min(100, sampleCount - base);
                final RandomSource rChild = r1.spawnChild();

                final Future<?> f = ForkJoinPool.commonPool().submit(() -> {
                    for (int j = 0; j < max; j++) {
                        final SimulationRun run = new SimulationRun();
                        for (final VarId v : valueVariables) {
                            final RandomVariable r = this.get(v);
                            final double[] numbers = samples.get(v);
                            numbers[base + j] = r.observe(rChild, run).getNumber();
                        }
                    }
                });
                futures.add(f);
            }
            for (final Future<?> f : futures) {
                f.get();
            }
            return samples;
        }

    }

    private final Map<VarId, Function<Instance, RandomVariable>> map = new LinkedHashMap<>();
    private final Map<String, Function<Instance, Object>> objectMap = new LinkedHashMap<>();

    public void addRaw(final VarId name, final RandomVariable var) {
        this.addRaw(name, (final Instance i) -> var);
    }

    public void add(final VarId name, final RandomVariable var) {
        this.add(name, (final Instance i) -> var);
    }

    public void addRaw(final VarId name, final Function<Instance, RandomVariable> producer) {
        if (this.map.containsKey(name)) {
            throw new IllegalStateException(name + " already contained");
        }
        this.map.put(name, producer);
    }

    public void add(final VarId name, final Function<Instance, RandomVariable> producer) {
        this.addRaw(name, producer.andThen((RandomVariable v) -> PersistentRandomVariable.ensurePersistent(name, v)));
    }

    public void addRaw(final VarId name, final MultiVariableFactory factoryFunction) {
        for (final Entry<VarId, Function<Instance, RandomVariable>> f : factoryFunction.create(name).entrySet()) {
            this.addRaw(f.getKey(), f.getValue());
        }
    }

    public void add(final VarId name, final MultiVariableFactory factoryFunction) {
        for (final Entry<VarId, Function<Instance, RandomVariable>> f : factoryFunction.create(name).entrySet()) {
            this.add(f.getKey(), f.getValue());
        }
    }

    public void addObject(final String name, final Function<Instance, Object> producer) {
        if (this.objectMap.containsKey(name)) {
            throw new IllegalStateException(name + " already contained");
        }
        this.objectMap.put(name, producer);
    }

    public Instance instantiate() {
        return new Instance();
    }

    public void analyze(final long seed, final VarId... valueVariables)
        throws InterruptedException, ExecutionException {

        final AnalysisResultHandler rh = new AnalysisResultHandler() {
            @Override
            public void handleValueVariables(Map<VarId, Sample> samples, VarId bestChoice) {
                System.out.println("=== Value variables");
                for (final Entry<VarId, Sample> e : samples.entrySet()) {
                    System.out.println("For " + e.getKey() + " ...");
                    System.out.println(e.getValue());
                }

                System.out.println("Choice with best expected value: " + bestChoice);
                System.out.println(new Date());
                System.out.println();
            }

            @Override
            public void handleVariableOverview(Map<VarId, Sample> samples) {
                System.out.println("=== Remaining variable overview");
                for (final Entry<VarId, Sample> e : samples.entrySet()) {
                    System.out.println("For " + e.getKey() + " ...");
                    System.out.println(e.getValue());
                }

                System.out.println(new Date());
                System.out.println();
            }

            @Override
            public void handleVOI(int iter, Map<VarId, Mean> means, Map<VarId, String> types) {
                final List<VarId> sorted = new ArrayList<>(means.keySet());
                Collections.sort(sorted,
                        (final VarId n1, final VarId n2) -> Double.compare(means.get(n2).get(), means.get(n1).get()));
                System.out.println("=== Value of information (iter=" + iter + ")");
                for (final VarId name : sorted) {
                    System.out.println("value of information for " + types.get(name) + " " + name + ": " + means.get(name));
                }
                System.out.println(new Date());
                System.out.println();
            }
        };
        this.analyze(seed, rh, valueVariables);
    }

    public static interface AnalysisResultHandler {

        public abstract void handleValueVariables(Map<VarId, Sample> samples, VarId bestChoice);

        public abstract void handleVariableOverview(Map<VarId, Sample> samples);

        public abstract void handleVOI(int iter, Map<VarId, Mean> means, Map<VarId, String> types);

    }

    public void analyze(final long seed, AnalysisResultHandler rh, final VarId... valueVariables)
        throws InterruptedException, ExecutionException {

        assert valueVariables.length >= 2;
        final Instance fullInstance = this.instantiate();
        final Map<VarId, double[]> originalSamples = fullInstance.createSamples(seed, 10_000, valueVariables);
        final Map<VarId, Sample> originalSamplesWithUnits = new LinkedHashMap<>();
        for (final Entry<VarId, double[]> e : originalSamples.entrySet()) {
            originalSamplesWithUnits.put(e.getKey(), new Sample(e.getValue(), fullInstance.get(e.getKey()).getUnit()));
        }

        final VarId bestChoice = this.determineBestChoice(originalSamples);
        rh.handleValueVariables(originalSamplesWithUnits, bestChoice);

        final Map<VarId, Sample> otherVarSamples = new TreeMap<>(Comparator.comparing(VarId::toString));
        final Set<VarId> vvSet = new HashSet<>(Arrays.asList(valueVariables));
        for (final VarId var : this.map.keySet()) {
            if (!vvSet.contains(var)) {
                final double[] sample = fullInstance.createSamples(seed, 10_000, var).get(var);
                otherVarSamples.put(var, new Sample(sample, fullInstance.get(var).getUnit()));
            }
        }
        rh.handleVariableOverview(otherVarSamples);

        final Map<VarId, String> types = new LinkedHashMap<>();
        for (final VarId name : this.map.keySet()) {
            types.put(name, fullInstance.get(name).getType());
        }

        final RandomSource sampleRandom = RandomSource.wrap(new Random(seed));
        final Map<VarId, Mean> meanLosses = new LinkedHashMap<>();
        for (final VarId name : this.map.keySet()) {
            meanLosses.put(name, Mean.undefined());
        }
        long lastPrintTime = 0;
        for (int j = 0; j < 10_000; j++) {
            for (final VarId name : this.map.keySet()) {
                for (int i = 0; i < 10; i++) {
                    final long iterSeed = seed + i + 100 * j;
                    final Instance reducedInstance = this.createReducedInstance(name, sampleRandom);
                    final Map<VarId, double[]> reducedSamples = reducedInstance.createSamples(iterSeed, 2_000, valueVariables);
                    final VarId bestChoiceWithInformation = this.determineBestChoice(reducedSamples);
                    final double[] loss = this.minus(reducedSamples.get(bestChoiceWithInformation), reducedSamples.get(bestChoice));
                    meanLosses.compute(name, (final VarId k, final Mean v) -> v.add(Mean.of(loss)));
                }
            }
            final long curTime = System.currentTimeMillis();
            if (curTime - lastPrintTime > 15_000) {
                lastPrintTime = curTime;
                rh.handleVOI(j, new LinkedHashMap<>(meanLosses), types);
            }
        }
        rh.handleVOI(10_000, meanLosses, types);
    }

    private double[] minus(double[] ds1, double[] ds2) {
        assert ds1.length == ds2.length;
        final double[] ret = new double[ds1.length];
        for (int i = 0; i < ds1.length; i++) {
            ret[i] = ds1[i] - ds2[i];
        }
        return ret;
    }

    private VarId determineBestChoice(final Map<VarId, double[]> samples) {
        VarId bestName = null;
        double best = Double.NEGATIVE_INFINITY;
        for (final Entry<VarId, double[]> e : samples.entrySet()) {
            final double cur = Mean.of(e.getValue()).get();
            if (cur > best) {
                bestName = e.getKey();
                best = cur;
            }
        }
        return bestName;
    }

    private Instance createReducedInstance(final VarId toReduce, final RandomSource sampleRandom) {
        final Quantity sample = this.sampleValue(toReduce, sampleRandom);
        final Instance reducedInstance = this.instantiate();
        reducedInstance.vars.put(toReduce, Distributions.fixed(sample));
        return reducedInstance;
    }

    private Quantity sampleValue(final VarId toReduce, final RandomSource sampleRandom) {
        return this.instantiate().get(toReduce).observe(sampleRandom, new SimulationRun());
    }

    public List<VarId> getAllPersistentVariables() throws AssertionError {
        final List<VarId> allPersistentVariables = new ArrayList<>();
        final Instance i = this.instantiate();
        for (final VarId name : this.map.keySet()) {
            if (i.get(name) instanceof PersistentRandomVariable) {
                allPersistentVariables.add(name);
            }
        }
        return allPersistentVariables;
    }

    public void printDistributions(final File file, final long seed) throws IOException {
        final List<VarId> cols = this.getAllPersistentVariables();
        cols.addAll(this.getAdditionalPersistedValues(seed, cols));

        this.printDistributions(file, seed, cols);
    }

    private Collection<? extends VarId> getAdditionalPersistedValues(long seed, List<VarId> cols) {
        final Instance inst = this.instantiate();
        final SimulationRun run = new SimulationRun();
        final RandomSource r = RandomSource.wrap(new Random(seed));

        for (final VarId col : cols) {
            inst.get(col).observe(r, run);
        }

        final List<VarId> ret = new ArrayList<>(run.getPersistentValueNames());
        ret.removeAll(cols);
        Collections.sort(ret, Comparator.comparing(VarId::toString));
        return ret;
    }

    public void printDistributions(final File file, final long seed, final Collection<VarId> columns) throws IOException {
        this.printDistributions(file, seed, columns.toArray(new VarId[columns.size()]));
    }

    public void printDistributions(final File file, final long seed, final VarId... columns) throws IOException {
        final RandomSource r = RandomSource.wrap(new Random(seed));
        try (FileOutputStream out = new FileOutputStream(file);
                BufferedWriter w = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"))) {
            final Instance inst = this.instantiate();
            final List<RandomVariable> v = new ArrayList<>();
            w.write("i");
            for (final VarId colName : columns) {
                if (this.map.containsKey(colName)) {
                    final RandomVariable var = inst.get(colName);
                    v.add(var);
                    w.write(";" + colName + " (" + var.getUnit() + ")");
                } else {
                    w.write(";" + colName);
                }
            }
            w.write('\n');
            final DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.GERMAN));
            df.setMaximumFractionDigits(5);
            for (int i = 0; i < 10_000; i++) {
                w.write(Integer.toString(i));
                final SimulationRun run = new SimulationRun();
                for (final RandomVariable var : v) {
                    var.observe(r, run);
                }
                for (final VarId colName : columns) {
                    final Quantity q = run.getPersistentValue(colName);
                    w.write(';');
                    w.write(df.format(q.getNumber()));
                }
                w.write('\n');
            }
        }
    }

}
