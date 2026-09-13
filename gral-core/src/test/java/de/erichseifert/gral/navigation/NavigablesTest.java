/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2026 Erich Seifert <dev[at]erichseifert.de>,
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
package de.erichseifert.gral.navigation;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.awt.geom.Point2D;

import org.junit.Before;
import org.junit.Test;

import de.erichseifert.gral.graphics.AbstractDrawable;
import de.erichseifert.gral.graphics.DrawableContainer;
import de.erichseifert.gral.graphics.DrawingContext;

public class NavigablesTest {
	private static class MockDrawable extends AbstractDrawable {
		@Override
		public void draw(DrawingContext context) {
		}
	}

	private static class MockNavigable extends MockDrawable implements Navigable {
		private final Navigator navigator = new MockNavigator();

		@Override
		public Navigator getNavigator() {
			return navigator;
		}
	}

	private MockNavigable navigable;

	@Before
	public void setUp() {
		navigable = new MockNavigable();
		navigable.setBounds(0.0, 0.0, 10.0, 10.0);
	}

	@Test
	public void testNavigableAtPoint() {
		assertSame(navigable, Navigables.getNavigableAt(navigable, new Point2D.Double(5.0, 5.0)));
	}

	@Test
	public void testNavigableOutsideBounds() {
		assertNull(Navigables.getNavigableAt(navigable, new Point2D.Double(20.0, 20.0)));
	}

	@Test
	public void testDrawableIsNotNavigable() {
		var drawable = new MockDrawable();
		drawable.setBounds(0.0, 0.0, 10.0, 10.0);
		assertNull(Navigables.getNavigableAt(drawable, new Point2D.Double(5.0, 5.0)));
	}

	@Test
	public void testNavigableChildOfContainer() {
		var container = new DrawableContainer();
		container.setBounds(0.0, 0.0, 10.0, 10.0);
		container.add(navigable);
		assertSame(navigable, Navigables.getNavigableAt(container, new Point2D.Double(5.0, 5.0)));
	}

	@Test
	public void testContainerWithoutNavigableChild() {
		var container = new DrawableContainer();
		container.setBounds(0.0, 0.0, 10.0, 10.0);
		container.add(new MockDrawable());
		assertNull(Navigables.getNavigableAt(container, new Point2D.Double(5.0, 5.0)));
	}
}
