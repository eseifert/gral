/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2010 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael.seifert[at]gmx.net>
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.erichseifert.gral.Drawable;
import de.erichseifert.gral.DrawableContainer;

public class InteractivePanelTest {
	private static Drawable drawable;
	private InteractivePanel panel;

	@BeforeClass
	public static void setUpBeforeClass() {
		drawable = new DrawableContainer();
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
		BufferedImage image = new BufferedImage(320, 240, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = (Graphics2D) image.getGraphics();
		PageFormat page = new PageFormat();
		int ret;

		// Test valid page
		ret = panel.print(graphics, page, 0);
		assertEquals(Printable.PAGE_EXISTS, ret);

		// Test invalid page
		ret = panel.print(graphics, page, 1);
		assertEquals(Printable.NO_SUCH_PAGE, ret);
	}

}
