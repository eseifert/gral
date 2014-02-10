package de.erichseifert.gral.uml;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawableContainer;
import de.erichseifert.gral.navigation.AbstractNavigator;
import de.erichseifert.gral.navigation.Navigable;
import de.erichseifert.gral.util.PointND;

public class DrawableContainerNavigator extends AbstractNavigator {
	private final DrawableContainer drawableContainer;
	private final Map<Drawable, Dimension2D> initialSizeByDrawable;
	private double zoom;

	public DrawableContainerNavigator(DrawableContainer drawableContainer) {
		this.drawableContainer = drawableContainer;
		initialSizeByDrawable = new HashMap<Drawable, Dimension2D>();
		zoom = 1.0;
		setZoomFactor(1.05);
	}

	@Override
	public double getZoom() {
		return zoom;
	}

	@Override
	public void setZoom(double zoom) {
		this.zoom = zoom;
		for (Drawable drawable : drawableContainer.getDrawables()) {
			Rectangle2D bounds = drawable.getBounds();
			Dimension2D baseSize = initialSizeByDrawable.get(drawable);
			if (baseSize == null) {
				baseSize = new de.erichseifert.gral.util.Dimension2D.Double(bounds.getWidth(), bounds.getHeight());
				initialSizeByDrawable.put(drawable, baseSize);
			}
			double width = baseSize.getWidth()*zoom;
			double height  = baseSize.getHeight()*zoom;
			double widthDelta = width - bounds.getWidth();
			double heightDelta = height - bounds.getHeight();
			double x = bounds.getX() - widthDelta/2;
			double y = bounds.getY() - heightDelta/2;
			drawable.setBounds(new Rectangle2D.Double(x, y, width, height));
			if (drawable instanceof Navigable) {
				((Navigable) drawable).getNavigator().setZoom(zoom);
			}
		}
	}

	@Override
	public PointND<? extends Number> getCenter() {
		Rectangle2D bounds = drawableContainer.getBounds();
		PointND<Double> center = new PointND<Double>(bounds.getCenterX(), bounds.getCenterY());
		return center;
	}

	@Override
	public void setCenter(PointND<? extends Number> center) {
		Rectangle2D bounds = drawableContainer.getBounds();
		double centerX = center.get(0).doubleValue();
		double centerY = center.get(1).doubleValue();
		bounds.setFrameFromCenter(centerX, centerY, bounds.getX(), bounds.getY());
		drawableContainer.setBounds(bounds);
	}

	@Override
	public void pan(PointND<? extends Number> deltas) {
		double deltaX = deltas.get(0).doubleValue();
		double deltaY = deltas.get(1).doubleValue();
		for (Drawable drawable : drawableContainer.getDrawables()) {
			Rectangle2D bounds = drawable.getBounds();
			drawable.setPosition(bounds.getX() + deltaX, bounds.getY() + deltaY);
		}
	}

	@Override
	public void setDefaultState() {
		// TODO
	}

	@Override
	public void reset() {
		setZoom(1.0);
		// TODO: reset center
	}
}
