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

import org.junit.Before;
import org.junit.Test;

import de.erichseifert.gral.TestUtils;

public class NavigationEventTest {
	private static final double DELTA = TestUtils.DELTA;

	private static final Navigator SOURCE = new MockNavigator();

	private NavigationEvent<Double> event;

	@Before
	public void setUp() {
		event = new NavigationEvent<>(SOURCE, 1.2, 3.4);
	}

	@Test
	public void testCreate() {
		assertEquals(SOURCE, event.getSource());
		assertEquals(1.2, event.getValueOld(), DELTA);
		assertEquals(3.4, event.getValueNew(), DELTA);
	}
}
