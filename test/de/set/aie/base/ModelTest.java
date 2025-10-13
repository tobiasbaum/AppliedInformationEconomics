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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

import java.util.Map;

import org.junit.Test;

import de.set.aie.base.Model.AnalysisResultHandler;
import de.set.aie.base.Model.Instance;

@SuppressWarnings("nls")
public class ModelTest {

    private static final class VoiResultChecker implements AnalysisResultHandler {

        private Map<VarId, Mean> lastVois;

        @Override
        public void handleValueVariables(Map<VarId, Sample> samples, VarId bestChoice) {
        }

        @Override
        public void handleVariableOverview(Map<VarId, Sample> samples) {
        }

        @Override
        public void handleVOI(int iter, Map<VarId, Mean> means, Map<VarId, VarKind> types) {
            for (final Mean m : means.values()) {
                assertThat(m.get(), greaterThanOrEqualTo(0.0));
            }
            this.lastVois = means;
        }

        public void checkVoiIsApprox(VarId name, double expected) {
            assertThat(this.lastVois.get(name).get(), greaterThanOrEqualTo(expected - 0.2));
            assertThat(this.lastVois.get(name).get(), lessThanOrEqualTo(expected + 0.2));
        }

    }

    @Test
    public void test1() throws Exception {
        final Model m = new Model();
        m.addRaw(VarId.of("dir"), Distributions.empirical(QUnit.scalar(), -1.0, 1.0));
        m.addRaw(VarId.of("val"), Distributions.fixed(Quantity.of(10.0, QUnit.of("EUR"))));
        m.addRaw(VarId.of("combined"), (final Instance i) -> i.get(VarId.of("dir")).times(i.get(VarId.of("val"))));
        m.addRaw(VarId.of("zero"), Distributions.fixed(Quantity.of(0, QUnit.of("EUR"))));

        final VoiResultChecker ch = new VoiResultChecker();
        m.analyze(132, ch, VarId.of("combined"), VarId.of("zero"));
        ch.checkVoiIsApprox(VarId.of("dir"), 5.0);
        ch.checkVoiIsApprox(VarId.of("val"), 0.0);
    }

    @Test
    public void test2() throws Exception {
        final Model m = new Model();
        m.addRaw(VarId.of("dir"), Distributions.empirical(QUnit.scalar(), -1.0, 1.0));
        m.addRaw(VarId.of("val"), Distributions.shiftedExp(1.0, 10.0, QUnit.of("EUR")));
        m.addRaw(VarId.of("combined"), (final Instance i) -> i.get(VarId.of("dir")).times(i.get(VarId.of("val"))));
        m.addRaw(VarId.of("zero"), Distributions.fixed(Quantity.of(0, QUnit.of("EUR"))));

        final VoiResultChecker ch = new VoiResultChecker();
        m.analyze(132, ch, VarId.of("combined"), VarId.of("zero"));
        ch.checkVoiIsApprox(VarId.of("dir"), 2.5);
        ch.checkVoiIsApprox(VarId.of("val"), 0.0);
    }

}
