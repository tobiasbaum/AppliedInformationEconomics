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

        private Map<String, Mean> lastVois;

        @Override
        public void handleValueVariables(Map<String, Sample> samples, String bestChoice) {
        }

        @Override
        public void handleVOI(int iter, Map<String, Mean> means, Map<String, String> types) {
            for (final Mean m : means.values()) {
                assertThat(m.get(), greaterThanOrEqualTo(0.0));
            }
            this.lastVois = means;
        }

        public void checkVoiIsApprox(String name, double expected) {
            assertThat(this.lastVois.get(name).get(), greaterThanOrEqualTo(expected - 0.2));
            assertThat(this.lastVois.get(name).get(), lessThanOrEqualTo(expected + 0.2));
        }

    }

    @Test
    public void test1() {
        final Model m = new Model();
        m.addRaw("dir", Distributions.empirical(Unit.scalar(), -1.0, 1.0));
        m.addRaw("val", Distributions.fixed(Quantity.of(10.0, Unit.of("EUR"))));
        m.addRaw("combined", (final Instance i) -> i.get("dir").times(i.get("val")));
        m.addRaw("zero", Distributions.fixed(Quantity.of(0, Unit.of("EUR"))));

        final VoiResultChecker ch = new VoiResultChecker();
        m.analyze(132, ch, "combined", "zero");
        ch.checkVoiIsApprox("dir", 5.0);
        ch.checkVoiIsApprox("val", 0.0);
    }

    @Test
    public void test2() {
        final Model m = new Model();
        m.addRaw("dir", Distributions.empirical(Unit.scalar(), -1.0, 1.0));
        m.addRaw("val", Distributions.shiftedExp(1.0, 10.0, Unit.of("EUR")));
        m.addRaw("combined", (final Instance i) -> i.get("dir").times(i.get("val")));
        m.addRaw("zero", Distributions.fixed(Quantity.of(0, Unit.of("EUR"))));

        final VoiResultChecker ch = new VoiResultChecker();
        m.analyze(132, ch, "combined", "zero");
        ch.checkVoiIsApprox("dir", 2.5);
        ch.checkVoiIsApprox("val", 0.0);
    }

}
