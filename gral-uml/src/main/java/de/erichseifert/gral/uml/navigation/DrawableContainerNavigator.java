package de.erichseifert.gral.uml.navigation;

import java.awt.Font;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

import de.erichseifert.gral.graphics.DrawableContainer;
import de.erichseifert.gral.graphics.Label;
import de.erichseifert.gral.navigation.AbstractNavigator;
import de.erichseifert.gral.util.MathUtils;
import de.erichseifert.gral.util.PointND;

public class DrawableContainerNavigator<T extends DrawableContainer> extends AbstractNavigator {
	private final Map<Label, Font> defaultFontSizesByLabel;
	private final T drawableContainer;
	private double zoom;
	private final PointND<? extends Number> center;

	public DrawableContainerNavigator(T drawableContainer) {
		this.drawableContainer = drawableContainer;
		defaultFontSizesByLabel = new HashMap<Label, Font>();
		zoom = 1.0;
		setZoomFactor(1.05);
		center = new PointND<Number>(0.0, 0.0);
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
	}

	// TODO: Coordinate transformation is the responsibility of the renderer
	private Point2D toViewCoordinates(PointND<? extends Number> point, double zoom) {
		return new Point2D.Double(
				(point.get(0).doubleValue() - center.get(0).doubleValue())*zoom,
				(point.get(1).doubleValue() - center.get(1).doubleValue())*zoom
		);
	}

	@Override
	public PointND<? extends Number> getCenter() {
		return new PointND<Number>(center.get(0), center.get(1));
	}

	@Override
	public void zoomAt(double zoom, PointND<? extends Number> zoomPoint) {
		if (!isZoomable()) {
			return;
		}
		if (isPannable() && zoomPoint != null) {
			Point2D centerView = toViewCoordinates(center, zoom);
			// TODO: zoomPoint should be passed in world coordinates
			Point2D absoluteZoomPointView = new Point2D.Double(
					centerView.getX() - zoomPoint.get(0).doubleValue(),
					centerView.getY() - zoomPoint.get(1).doubleValue()
			);
			Point2D absoluteZoomPointScaledView = new Point2D.Double(
				absoluteZoomPointView.getX()/getZoom()*zoom,
				absoluteZoomPointView.getY()/getZoom()*zoom
			);
			Point2D combinedViewportMovement = new Point2D.Double(
					-absoluteZoomPointScaledView.getX() + absoluteZoomPointView.getX(),
					-absoluteZoomPointScaledView.getY() + absoluteZoomPointView.getY()
			);
			setCenter(toWorldCoordinates(combinedViewportMovement, zoom));
		}
		setZoom(zoom);
	}

	@Override
	public void setCenter(PointND<? extends Number> center) {
		this.center.setLocation(center.get(0), center.get(1));
	}

	private PointND<? extends Number> toWorldCoordinates(Point2D point, double zoom) {
		return new PointND<Double>(
			point.getX()/zoom + center.get(0).doubleValue(),
			point.getY()/zoom + center.get(1).doubleValue()
		);
	}

	@Override
	public void pan(PointND<? extends Number> deltas) {
		Point2D deltasView = deltas.getPoint2D();
		PointND<? extends Number> deltasWorld = new PointND<Double>(
				deltasView.getX()/getZoom(),
				deltasView.getY()/getZoom()
		);
		PointND<? extends Number> centerOld = getCenter();
		PointND<? extends Number> centerNew = new PointND<Double>(
				centerOld.get(0).doubleValue() - deltasWorld.get(0).doubleValue(),
				centerOld.get(1).doubleValue() - deltasWorld.get(1).doubleValue()
		);
		setCenter(centerNew);
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
