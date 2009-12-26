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

package openjchart;

import java.awt.geom.Dimension2D;

/**
 * Interface that provides basic functions for arranging a layout.
 * Functionality includes the arrangement of the layout itself and
 * returning the preferred size of a container.
 */
public interface Layout {

	/**
	 * Arranges the components of this Container according to this Layout.
	 * @param container Container to be laid out.
	 */
	void layout(Container container);

	/**
	 * Returns the preferred size of the specified Container using this Layout.
	 * @param container Container whose preferred size is to be returned.
	 * @return Preferred extent of the specified Container.
	 */
	Dimension2D getPreferredSize(Container container);
}