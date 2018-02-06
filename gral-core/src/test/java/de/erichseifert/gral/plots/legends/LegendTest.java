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
package de.erichseifert.gral.plots.legends;

import static de.erichseifert.gral.TestUtils.assertNotEmpty;
import static de.erichseifert.gral.TestUtils.createTestImage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;

import de.erichseifert.gral.TestUtils;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DummyData;
import de.erichseifert.gral.graphics.AbstractDrawable;
import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawingContext;

public class LegendTest {
	private static final double DELTA = 1e-10;
	private MockLegend legend;

	private static class MockLegend extends SeriesLegend {
		/** Version id for serialization. */
		private static final long serialVersionUID = -6681407860400756446L;

		private boolean isDrawn;

		@Override
		protected Drawable getSymbol(DataSource data) {
			return new AbstractDrawable() {
				/** Version id for serialization. */
				private static final long serialVersionUID = 7336075728956564691L;

				public void draw(DrawingContext context) {
					isDrawn = true;
				}
			};
		}
	}

	@Before
	public void setUp() {
		legend = new MockLegend();
	}

	@Test
	public void testDataSources() {
		DataSource source = new DummyData(1, 1, 1.0);
		assertFalse(legend.contains(source));
		legend.add(source);
		assertTrue(legend.contains(source));
		legend.remove(source);
		assertFalse(legend.contains(source));
	}

	@Test
	public void testDraw() {
		legend.setBackground(Color.WHITE);
		legend.setBorderStroke(new BasicStroke(1f));
		legend.add(new DummyData(1, 1, 1.0));

		BufferedImage image = createTestImage();
		legend.setBounds(0.0, 0.0, image.getWidth(), image.getHeight());
		DrawingContext context = new DrawingContext((Graphics2D) image.getGraphics());
		legend.draw(context);
		assertTrue(legend.isDrawn);
		assertNotEmpty(image);
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		Legend original = legend;
		Legend deserialized = TestUtils.serializeAndDeserialize(original);

		assertEquals(original.getBackground(), deserialized.getBackground());
		assertEquals(original.getBorderStroke(), deserialized.getBorderStroke());
		assertEquals(original.getFont(), deserialized.getFont());
		assertEquals(original.getBorderColor(), deserialized.getBorderColor());
		assertEquals(original.getOrientation(), deserialized.getOrientation());
		assertEquals(original.getAlignmentX(), deserialized.getAlignmentX(), DELTA);
		assertEquals(original.getAlignmentY(), deserialized.getAlignmentY(), DELTA);
		assertEquals(original.getGap(), deserialized.getGap());
		assertEquals(original.getSymbolSize(), deserialized.getSymbolSize());
    }
}
