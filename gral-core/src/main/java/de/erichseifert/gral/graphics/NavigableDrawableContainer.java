package de.erichseifert.gral.graphics;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.List;

import de.erichseifert.gral.navigation.DrawableContainerNavigator;
import de.erichseifert.gral.navigation.Navigable;
import de.erichseifert.gral.navigation.Navigator;
import de.erichseifert.gral.util.PointND;

public class NavigableDrawableContainer extends DrawableContainer implements Navigable {
	private final Navigator navigator;

	public NavigableDrawableContainer() {
		navigator = new DrawableContainerNavigator<DrawableContainer>(this);
	}

	@Override
	protected void drawComponents(DrawingContext context) {
		Graphics2D g2d = context.getGraphics();
		AffineTransform txOld = g2d.getTransform();
		double zoom = navigator.getZoom();

		Point2D origin = navigator.getCenter().getPoint2D();
		g2d.scale(zoom, zoom);
		g2d.translate(-origin.getX(), -origin.getY());
		super.drawComponents(context);
		g2d.setTransform(txOld);
	}

	@Override
	public Navigator getNavigator() {
		return navigator;
	}

	@Override
	public List<Drawable> getDrawablesAt(Point2D point) {
		DrawableContainerNavigator navigator = (DrawableContainerNavigator) getNavigator();
		PointND<? extends Number> pointZoomed = navigator.toWorldCoordinates(point, navigator.getZoom());
		return super.getDrawablesAt(pointZoomed.getPoint2D());
	}
}
