package de.erichseifert.gral.uml;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawableContainer;
import de.erichseifert.gral.navigation.AbstractNavigator;
import de.erichseifert.gral.navigation.Navigable;
import de.erichseifert.gral.util.MathUtils;
import de.erichseifert.gral.util.PointND;

public class DrawableContainerNavigator<T extends DrawableContainer> extends AbstractNavigator {
	private final T drawableContainer;
	private Dimension2D initialSize;
	private double zoom;

	public DrawableContainerNavigator(T drawableContainer) {
		this.drawableContainer = drawableContainer;
		Rectangle2D bounds = drawableContainer.getBounds();
		zoom = 1.0;
		setZoomFactor(1.05);
	}

	@Override
	public double getZoom() {
		return zoom;
	}

	@Override
	public void setZoom(double zoom) {
		if (!isZoomable()) {
			return;
		}
		this.zoom = MathUtils.limit(zoom, getZoomMin(), getZoomMax());
		Rectangle2D bounds = drawableContainer.getBounds();
		if (initialSize == null) {
			initialSize = new de.erichseifert.gral.util.Dimension2D.Double(bounds.getWidth(), bounds.getHeight());;
		}
		double width = initialSize.getWidth()*zoom;
		double height  = initialSize.getHeight()*zoom;
		double widthDelta = width - bounds.getWidth();
		double heightDelta = height - bounds.getHeight();
		double x = bounds.getX() - widthDelta/2;
		double y = bounds.getY() - heightDelta/2;
		drawableContainer.setBounds(new Rectangle2D.Double(x, y, width, height));
	}

	@Override
	public void zoomIn() {
		super.zoomIn();
		for (Drawable drawable : drawableContainer.getDrawables()) {
			if (drawable instanceof Navigable) {
				((Navigable) drawable).getNavigator().zoomIn();
			}
		}
	}

	@Override
	public void zoomOut() {
		super.zoomOut();
		for (Drawable drawable : drawableContainer.getDrawables()) {
			if (drawable instanceof Navigable) {
				((Navigable) drawable).getNavigator().zoomOut();
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
		Rectangle2D bounds = drawableContainer.getBounds();
		drawableContainer.setPosition(bounds.getX() + deltaX, bounds.getY() + deltaY);
		for (Drawable drawable : drawableContainer.getDrawables()) {
			if (drawable instanceof Navigable) {
				((Navigable) drawable).getNavigator().pan(deltas);
			}
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

	public T getDrawableContainer() {
		return drawableContainer;
	}
}
