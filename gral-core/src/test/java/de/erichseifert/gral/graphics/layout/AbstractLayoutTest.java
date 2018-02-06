/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2018 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <mseifert[at]error-reports.org>
 *
 * This file is part of GRAL.
 *
 * GRAL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GRAL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GRAL.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.erichseifert.gral.graphics.layout;

import static org.junit.Assert.assertEquals;

import java.awt.geom.Dimension2D;
import java.io.IOException;

import de.erichseifert.gral.graphics.Container;
import org.junit.Test;

import de.erichseifert.gral.TestUtils;


public class AbstractLayoutTest {
	private static final double DELTA = 1e-15;
	private static final double GAP_H = 5.0;
	private static final double GAP_V = 10.0;

	private static class MockAbstractLayout extends AbstractLayout {
		private static final long serialVersionUID = 5812320021345698270L;

		public MockAbstractLayout(double gapX, double gapY) {
			super(gapX, gapY);
		}

		@Override
		public void layout(Container container) {
		}

		@Override
		public Dimension2D getPreferredSize(Container container) {
			return new de.erichseifert.gral.graphics.Dimension2D.Double();
		}
	}

	@Test
	public void testCreate() {
		AbstractLayout gapped = new MockAbstractLayout(GAP_H, GAP_V);
		assertEquals(GAP_H, gapped.getGapX(), DELTA);
		assertEquals(GAP_V, gapped.getGapY(), DELTA);
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		AbstractLayout original = new MockAbstractLayout(GAP_H, GAP_V);
		AbstractLayout deserialized = TestUtils.serializeAndDeserialize(original);

		assertEquals(original.getGapX(), deserialized.getGapX(), DELTA);
		assertEquals(original.getGapY(), deserialized.getGapY(), DELTA);
	}
}
