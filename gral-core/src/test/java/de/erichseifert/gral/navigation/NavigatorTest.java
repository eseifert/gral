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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import de.erichseifert.gral.TestUtils;
import de.erichseifert.gral.util.PointND;

public class NavigatorTest {
	private static final double DELTA = TestUtils.DELTA;

	private MockNavigator navigator;

	@Before
	public void setUp() {
		navigator = new MockNavigator();
	}

	@Test
	public void testCreate() {
		assertEquals(AbstractNavigator.DEFAULT_ZOOM_FACTOR, navigator.getZoomFactor(), DELTA);
		assertEquals(AbstractNavigator.DEFAULT_ZOOM_MIN, navigator.getZoomMin(), DELTA);
		assertEquals(AbstractNavigator.DEFAULT_ZOOM_MAX, navigator.getZoomMax(), DELTA);
		assertNull(navigator.getDirection());

		assertEquals(new PointND<>(0.0, 0.0), navigator.getCenter());
		assertEquals(1.0, navigator.getZoom(), DELTA);
	}

	@Test
	public void testZoom() {
		navigator.setZoom(2.0);
		assertEquals(2.0, navigator.getZoom(), DELTA);
	}

	@Test
	public void testZoomIn() {
		double zoomOld = navigator.getZoom();
		navigator.zoomIn();
		assertEquals(zoomOld*navigator.getZoomFactor(), navigator.getZoom(), DELTA);
	}

	@Test
	public void testZoomOut() {
		double zoomOld = navigator.getZoom();
		navigator.zoomOut();
		assertEquals(zoomOld/navigator.getZoomFactor(), navigator.getZoom(), DELTA);
	}

	@Test
	public void testZoomable() {
		double zoomOld = navigator.getZoom();
		navigator.setZoomable(false);

		navigator.setZoom(zoomOld + 2.0);
		assertEquals(zoomOld, navigator.getZoom(), DELTA);

		navigator.zoomIn();
		assertEquals(zoomOld, navigator.getZoom(), DELTA);

		navigator.zoomOut();
		assertEquals(zoomOld, navigator.getZoom(), DELTA);
	}

	@Test
	public void testCenter() {
		PointND<Double> centerNew = new PointND<>(1.2, 3.4);
		navigator.setCenter(centerNew);
		assertEquals(centerNew, navigator.getCenter());
	}

	@Test
	public void testPan() {
		PointND<? extends Number> centerOld = navigator.getCenter();
		PointND<Double> deltas = new PointND<>(-3.2, -1.0);
		PointND<Double> expected = new PointND<>(
				centerOld.get(0).doubleValue() + deltas.get(0),
				centerOld.get(1).doubleValue() + deltas.get(1)
		);

		navigator.pan(deltas);
		assertEquals(expected, navigator.getCenter());
	}

	@Test
	public void testPannable() {
		PointND<? extends Number> centerOld = navigator.getCenter();
		PointND<Double> centerNew = new PointND<>(1.2, 3.4);
		PointND<Double> deltas = new PointND<>(-3.2, -1.0);

		navigator.setPannable(false);

		navigator.setCenter(centerNew);
		assertEquals(centerOld, navigator.getCenter());

		navigator.pan(deltas);
		assertEquals(centerOld, navigator.getCenter());
	}

	@Test
	public void testZoomFactor() {
		double zoomFactorNew = AbstractNavigator.DEFAULT_ZOOM_FACTOR + 2.0;
		navigator.setZoomFactor(zoomFactorNew);
		assertEquals(zoomFactorNew, navigator.getZoomFactor(), DELTA);
	}

	@Test
	public void testZoomMin() {
		double zoomMinNew = AbstractNavigator.DEFAULT_ZOOM_MIN + 2.0;
		navigator.setZoomMin(zoomMinNew);
		assertEquals(zoomMinNew, navigator.getZoomMin(), DELTA);
	}

	@Test
	public void testZoomMax() {
		double zoomMaxNew = AbstractNavigator.DEFAULT_ZOOM_MAX + 2.0;
		navigator.setZoomMax(zoomMaxNew);
		assertEquals(zoomMaxNew, navigator.getZoomMax(), DELTA);
	}

	@Test
	public void testDirection() {
		NavigationDirection directionNew = MockNavigationDirection.BAR;
		navigator.setDirection(directionNew);
		assertEquals(directionNew, navigator.getDirection());
	}

	@Test
	public void testConnect() {
		Navigator navigator2 = new MockNavigator();

		navigator.connect(navigator2);

		PointND<Double> centerNew = new PointND<>(1.2, 3.4);
		navigator.setCenter(centerNew);
		assertEquals(navigator.getCenter(), navigator2.getCenter());

		navigator.setZoom(2.0);
		assertEquals(navigator.getZoom(), navigator2.getZoom(), DELTA);
	}

	@Test
	public void testDisconnect() {
		Navigator navigator2 = new MockNavigator();

		navigator.connect(navigator2);

		navigator.setCenter(new PointND<>(1.2, 3.4));
		assertEquals(navigator.getCenter(), navigator2.getCenter());

		navigator.setZoom(2.0);
		assertEquals(navigator.getZoom(), navigator2.getZoom(), DELTA);

		navigator2.disconnect(navigator);

		navigator.setCenter(new PointND<>(3.2, -1.0));
		assertFalse(navigator.getCenter().equals(navigator2.getCenter()));

		navigator.setZoom(3.0);
		assertFalse(navigator.getZoom() == navigator2.getZoom());
	}
}
