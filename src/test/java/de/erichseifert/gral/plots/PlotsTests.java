/*
 * GRAL: Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2009-2010 Erich Seifert <info[at]erichseifert.de>, Michael Seifert <michael.seifert[at]gmx.net>
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

package de.erichseifert.gral.plots;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import de.erichseifert.gral.plots.areas.AreasTests;
import de.erichseifert.gral.plots.axes.AxesTests;
import de.erichseifert.gral.plots.colors.ColorsTests;
import de.erichseifert.gral.plots.lines.LinesTests;
import de.erichseifert.gral.plots.points.PointsTests;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	AxesTests.class,
	ColorsTests.class,
	AreasTests.class,
	LinesTests.class,
	PointsTests.class,
	PlotArea2DTest.class,
	LegendTest.class,
	PlotTest.class,
	XYPlotTest.class,
	PiePlotTest.class,
	BarPlotTest.class
})
public class PlotsTests {
}
