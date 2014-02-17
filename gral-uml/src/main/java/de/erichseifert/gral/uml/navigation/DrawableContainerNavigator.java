package de.erichseifert.gral.uml.navigation;

import java.awt.Font;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawableContainer;
import de.erichseifert.gral.graphics.Label;
import de.erichseifert.gral.navigation.AbstractNavigator;
import de.erichseifert.gral.navigation.Navigable;
import de.erichseifert.gral.util.MathUtils;
import de.erichseifert.gral.util.PointND;

public class DrawableContainerNavigator<T extends DrawableContainer> extends AbstractNavigator {
	private final Map<Label, Font> defaultFontSizesByLabel;
	private final T drawableContainer;
	private Rectangle2D initialBounds;
	private double zoom;
	private final Point2D center;

	public DrawableContainerNavigator(T drawableContainer) {
		this.drawableContainer = drawableContainer;
		defaultFontSizesByLabel = new HashMap<Label, Font>();
		Rectangle2D bounds = drawableContainer.getBounds();
		zoom = 1.0;
		setZoomFactor(1.05);
		center = new Point2D.Double();
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
		if (initialBounds == null) {
			initialBounds = new Rectangle2D.Double();
			//center.setLocation(initialBounds.getCenterX(), initialBounds.getCenterY());
			initialBounds.setFrame(bounds);
		}
		double width = initialBounds.getWidth()*zoom;
		double height  = initialBounds.getHeight()*zoom;
		double x = (bounds.getX() - center.getX())*zoom + center.getX();
		double y = (bounds.getY() - center.getY())*zoom + center.getY();
		drawableContainer.setBounds(new Rectangle2D.Double(x, y, width, height));
	}

	@Override
	public void zoomInAt(PointND<? extends Number> zoomPoint) {
		super.zoomInAt(zoomPoint);
		for (Drawable drawable : drawableContainer.getDrawables()) {
			if (drawable instanceof Navigable) {
				((Navigable) drawable).getNavigator().zoomInAt(zoomPoint);
			}
		}
	}

	@Override
	public void zoomOutAt(PointND<? extends Number> zoomPoint) {
		super.zoomOutAt(zoomPoint);
		for (Drawable drawable : drawableContainer.getDrawables()) {
			if (drawable instanceof Navigable) {
				((Navigable) drawable).getNavigator().zoomOutAt(zoomPoint);
			}
		}
	}

	@Override
	public PointND<? extends Number> getCenter() {
		return new PointND<Double>(center.getX(), center.getY());
	}

	@Override
	public void setCenter(PointND<? extends Number> center) {
		double centerX = center.get(0).doubleValue();
		double centerY = center.get(1).doubleValue();
		this.center.setLocation(centerX, centerY);
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
		getDrawableContainer().layout();
		// TODO: reset center
	}

	public T getDrawableContainer() {
		return drawableContainer;
	}

	protected void zoomLabel(Label label, double zoom) {
		Font classLabelFont = defaultFontSizesByLabel.get(label);
		if (classLabelFont == null) {
			classLabelFont = label.getFont();
			defaultFontSizesByLabel.put(label, classLabelFont);
		}
		label.setFont(classLabelFont.deriveFont((float) (classLabelFont.getSize2D()*zoom)));
	}
}
