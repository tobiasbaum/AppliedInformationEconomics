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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import de.set.aie.base.Distributions.Between;
import de.set.aie.base.Distributions.StdDist;
import de.set.aie.base.Model.Instance;

class UnknownDistFactory implements MultiVariableFactory {

    private final Set<StdDist> possibleDistributions;
    private final double absoluteMin;
    private final List<Between> estimatedRanges;
    private final double absoluteMax;
    private final Unit unit;

    public UnknownDistFactory(
            final Set<StdDist> possibleDistributions,
            final double absoluteMin,
            final List<Between> estimatedRanges,
            final double absoluteMax,
            final Unit unit) {

        assert possibleDistributions.size() > 0;
        assert estimatedRanges.size() > 0;

        this.possibleDistributions = possibleDistributions;
        this.absoluteMin = absoluteMin;
        this.estimatedRanges = estimatedRanges;
        this.absoluteMax = absoluteMax;
        this.unit = unit;
    }

    @Override
    public Map<String, Function<Instance, RandomVariable>> create(final String baseName) {
        final List<RandomVariable> vars = new ArrayList<>();
        for (final StdDist dist : this.possibleDistributions) {
            for (final Between range : this.estimatedRanges) {
                vars.add(dist.create(range, this.unit));
            }
        }
        final double[] indices = new double[vars.size()];
        for (int i = 0; i <  indices.length; i++) {
            indices[i] = i;
        }
        final Map<String, Function<Instance, RandomVariable>> ret = new LinkedHashMap<>();
        ret.put(baseName + "_dist", //$NON-NLS-1$
                (final Instance m) -> Distributions.empirical(Unit.scalar(), indices));
        ret.put(baseName, (final Instance m) ->
            new UncertainDistributionVariable(
                    m.get(baseName + "_dist"), //$NON-NLS-1$
                    vars.toArray(new RandomVariable[vars.size()]))
            .bound(this.absoluteMin, this.absoluteMax));
        return ret;
    }

}
