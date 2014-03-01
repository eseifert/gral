package de.erichseifert.gral.uml;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawableContainer;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.navigation.Navigable;
import de.erichseifert.gral.navigation.Navigator;
import de.erichseifert.gral.uml.navigation.DrawableContainerNavigator;

public class ClassDiagramRenderer {
	protected static class ClassDiagram extends DrawableContainer implements Navigable {
		private final Navigator navigator;

		public ClassDiagram() {
			navigator = new DrawableContainerNavigator(this);
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
	}

	public ClassDiagramRenderer() {
	}

	public Drawable getRendererComponent() {
		return new ClassDiagram();
	}
}
