/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2014 Erich Seifert <dev[at]erichseifert.de>,
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
package de.erichseifert.gral.uml;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import de.erichseifert.gral.graphics.AbstractDrawable;
import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.util.GeometryUtils;

public class AssociationRenderer {
	public AssociationRenderer() {
	}

	public Drawable getRendererComponent(final Drawable destination, final Drawable source) {
		return new AbstractDrawable() {
			@Override
			public void draw(DrawingContext context) {
				Rectangle2D boundsSource = source.getBounds();
				Rectangle2D boundsDestination = destination.getBounds();
				if (boundsDestination.intersects(boundsSource)) {
					return;
				}

				Line2D centerConnection = new Line2D.Double(
					boundsDestination.getCenterX(), boundsDestination.getCenterY(),
					boundsSource.getCenterX(), boundsSource.getCenterY()
				);
				Point2D startingPoint = GeometryUtils.intersection(centerConnection, boundsSource).get(0);
				Point2D endPoint = GeometryUtils.intersection(centerConnection, boundsDestination).get(0);
				Line2D connection = new Line2D.Double(startingPoint, endPoint);

				Graphics2D g2d = context.getGraphics();
				g2d.draw(connection);
			}
		};
	}
}
