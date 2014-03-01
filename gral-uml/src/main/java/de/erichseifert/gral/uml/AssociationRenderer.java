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
