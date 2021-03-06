/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2019 Erich Seifert <dev[at]erichseifert.de>,
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
package de.erichseifert.gral.plots.legends;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import de.erichseifert.gral.TestUtils;
import de.erichseifert.gral.data.Row;
import de.erichseifert.gral.graphics.AbstractDrawable;
import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawingContext;

public class ValueLegendTest {
	private static class MockValueLegend extends ValueLegend {
		@Override
		protected Drawable getSymbol(Row data) {
			return new AbstractDrawable() {
				public void draw(DrawingContext context) {
				}
			};
		}
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		ValueLegend original = new MockValueLegend();
		ValueLegend deserialized = TestUtils.serializeAndDeserialize(original);

		assertEquals(original.getLabelColumn(), deserialized.getLabelColumn());
		assertEquals(original.getLabelFormat(), deserialized.getLabelFormat());
    }
}
