/* OpenJChart : a free plotting library for the Java(tm) platform
 *
 * (C) Copyright 2009, by Erich Seifert and Michael Seifert.
 *
 * This file is part of OpenJChart.
 *
 * OpenJChart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenJChart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenJChart.  If not, see <http://www.gnu.org/licenses/>.
 */

package openjchart.plots.lines;

import java.awt.Shape;

import openjchart.Drawable;
import openjchart.plots.DataPoint2D;
import openjchart.util.SettingsStorage;

public interface LineRenderer2D extends SettingsStorage {
	public static final String KEY_LINE_STROKE = "line.stroke";
	public static final String KEY_LINE_GAP = "line.gap.size";
	public static final String KEY_LINE_GAP_ROUNDED = "line.gap.rounded";
	public static final String KEY_LINE_COLOR = "line.color";

	Drawable getLine(DataPoint2D... points);

	Shape punchShapes(Shape lineShape, DataPoint2D... points);
}
