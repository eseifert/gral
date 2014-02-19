package de.erichseifert.gral.uml;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import de.erichseifert.gral.graphics.DrawableContainer;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.navigation.Navigable;
import de.erichseifert.gral.navigation.Navigator;
import de.erichseifert.gral.uml.navigation.DrawableContainerNavigator;
import de.erichseifert.gral.util.PointND;

public class ClassDiagram extends DrawableContainer implements Navigable {
	private final Navigator navigator;

	public ClassDiagram() {
		navigator = new DrawableContainerNavigator(this);
	}

	@Override
	protected void drawComponents(DrawingContext context) {
		Graphics2D g2d = context.getGraphics();
		AffineTransform txOld = g2d.getTransform();
		double zoom = navigator.getZoom();

		Point2D origin = toViewCoordinates(new PointND<Double>(getX(), getY()), zoom);
		g2d.translate(origin.getX(), origin.getY());
		g2d.scale(zoom, zoom);
		super.drawComponents(context);
		g2d.setTransform(txOld);
	}

	@Override
	public Navigator getNavigator() {
		return navigator;
	}

	private Point2D toViewCoordinates(PointND<? extends Number> point, double zoom) {
		PointND<? extends Number> center = navigator.getCenter();
		return new Point2D.Double(
				(point.get(0).doubleValue() - center.get(0).doubleValue())*zoom,
				(point.get(1).doubleValue() - center.get(1).doubleValue())*zoom
		);
	}
}
