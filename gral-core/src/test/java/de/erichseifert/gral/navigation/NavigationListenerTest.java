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
package de.erichseifert.gral.navigation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import de.erichseifert.gral.TestUtils;
import de.erichseifert.gral.util.PointND;

public class NavigationListenerTest {
	private static final double DELTA = TestUtils.DELTA;

	private static final class MockNavigationListener implements NavigationListener {
		PointND<? extends Number> center;
		Double zoom;

		public void centerChanged(
				NavigationEvent<PointND<? extends Number>> event) {
			center = event.getValueNew();
		}

		public void zoomChanged(NavigationEvent<Double> event) {
			zoom = event.getValueNew();
		}
	}

	private Navigator navigator;
	private MockNavigationListener listener;

	@Before
	public void setUp() {
		navigator = new MockNavigator();
		listener = new MockNavigationListener();
		navigator.addNavigationListener(listener);
	}

	@Test
	public void testCenterChanged() {
		assertNull(listener.center);
		PointND<Double> centerNew = new PointND<>(1.2, 3.4);
		navigator.setCenter(centerNew);
		assertEquals(centerNew, listener.center);
	}

	@Test
	public void testZoomChanged() {
		assertNull(listener.zoom);
		double zoomNew = 2.0;
		navigator.setZoom(zoomNew);
		assertEquals(zoomNew, listener.zoom, DELTA);
	}

	@Test
	public void testRemove() {
		navigator.removeNavigationListener(listener);

		PointND<Double> centerNew = new PointND<>(1.2, 3.4);
		navigator.setCenter(centerNew);
		assertNull(listener.zoom);

		double zoomNew = 2.0;
		navigator.setZoom(zoomNew);
		assertNull(listener.zoom);
	}
}
