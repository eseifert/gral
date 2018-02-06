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
package de.erichseifert.gral.ui;

import static de.erichseifert.gral.TestUtils.assertNotEmpty;
import static de.erichseifert.gral.TestUtils.createTestImage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.erichseifert.gral.graphics.AbstractDrawable;
import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawingContext;

public class InteractivePanelTest {
	private static Drawable drawable;
	private InteractivePanel panel;

	@BeforeClass
	public static void setUpBeforeClass() {
		drawable = new AbstractDrawable() {
			/** Version id for serialization. */
			private static final long serialVersionUID = 243601807224242703L;

			public void draw(DrawingContext context) {
				Graphics2D g = context.getGraphics();
				g.draw(new Line2D.Double(0.0, 0.0, 0.0, 0.0));
			}
		};
	}

	@Before
	public void setUp() {
		panel = new InteractivePanel(drawable);
	}

	@Test
	public void testCreation() {
		assertNotNull(panel.getMinimumSize());
		assertSame(drawable, panel.getDrawable());
	}

	@Test
	public void testPrint() throws PrinterException {
		BufferedImage image;
		PageFormat page = new PageFormat();
		int ret;

		// Test valid page
		image = createTestImage();
		ret = panel.print(image.getGraphics(), page, 0);
		assertEquals(Printable.PAGE_EXISTS, ret);
		assertNotEmpty(image);

		// Test invalid page
		image = createTestImage();
		ret = panel.print(image.getGraphics(), page, 1);
		assertEquals(Printable.NO_SUCH_PAGE, ret);
		try {
			assertNotEmpty(image);
			fail();
		} catch (AssertionError e) {
		}
	}

}
