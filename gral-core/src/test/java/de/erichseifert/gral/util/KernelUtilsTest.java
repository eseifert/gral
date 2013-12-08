/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2013 Erich Seifert <dev[at]erichseifert.de>,
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
package de.erichseifert.gral.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.erichseifert.gral.data.filters.Kernel;
import de.erichseifert.gral.data.filters.KernelUtils;

public class KernelUtilsTest {
	public static final double DELTA = 1e-15;

	@Test
	public void testBinomial() {
		Kernel kernel;

		// Small kernel
		kernel = KernelUtils.getBinomial(2);
		assertEquals(2, kernel.size());
		assertEquals(1, kernel.getOffset());
		assertEquals(0.00, kernel.get(-2), DELTA);
		assertEquals(0.50, kernel.get(-1), DELTA);
		assertEquals(0.50, kernel.get( 0), DELTA);
		assertEquals(0.00, kernel.get( 1), DELTA);
		assertEquals(0.00, kernel.get( 2), DELTA);

		// Large kernel
		kernel = KernelUtils.getBinomial(13);
		assertEquals(13, kernel.size());
		assertEquals(6, kernel.getOffset());
		assertEquals(0.1933593750, kernel.get(-1), DELTA);
		assertEquals(0.2255859375, kernel.get( 0), DELTA);
		assertEquals(0.1933593750, kernel.get( 1), DELTA);

		// Kernel with specified variance
		kernel = KernelUtils.getBinomial(1.0);
		assertEquals(5, kernel.size());
		assertEquals(2, kernel.getOffset());
	}

	@Test
	public void testUniform() {
		Kernel kernel = KernelUtils.getUniform(13, 0, 1.0);
		assertEquals(13, kernel.size());
		assertEquals(0, kernel.getOffset());
		assertEquals(0.0, kernel.get(-1), DELTA);
		assertEquals(1.0, kernel.get( 0), DELTA);
		assertEquals(1.0, kernel.get(12), DELTA);
		assertEquals(0.0, kernel.get(13), DELTA);
	}

}
