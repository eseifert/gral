/**
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

package de.erichseifert.gral.plots.points;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;

import de.erichseifert.gral.AbstractDrawable;
import de.erichseifert.gral.Drawable;
import de.erichseifert.gral.data.Row;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.axes.AxisRenderer2D;
import de.erichseifert.gral.util.GraphicsUtils;


/**
 * Class that creates Drawables for a row of data.
 */
public class DefaultPointRenderer extends AbstractPointRenderer {

	@Override
	public Drawable getPoint(final Axis axisY, final AxisRenderer2D axisYRenderer, final Row row) {
		Drawable drawable = new AbstractDrawable() {
			@Override
			public void draw(Graphics2D g2d) {
				Paint paint = getSetting(COLOR);
				Shape point = getPointPath(row);
				GraphicsUtils.fillPaintedShape(g2d, point, paint, null);

				if (DefaultPointRenderer.this.<Boolean>getSetting(VALUE_DISPLAYED)) {
					drawValue(g2d, point, row.get(1).doubleValue());
				}
				if (DefaultPointRenderer.this.<Boolean>getSetting(ERROR_DISPLAYED)) {
					int columnIndex = row.size()-1;
					drawError(g2d, point, row.get(1).doubleValue(), row.get(columnIndex-1).doubleValue(), row.get(columnIndex).doubleValue(), axisY, axisYRenderer);
				}
			}
		};

		return drawable;
	}

	@Override
	public Shape getPointPath(Row row) {
		Shape shape = getSetting(SHAPE);
		return shape;
	}
}
