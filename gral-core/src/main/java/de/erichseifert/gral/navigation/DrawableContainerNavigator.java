package de.erichseifert.gral.navigation;

import java.awt.geom.Point2D;

import de.erichseifert.gral.graphics.DrawableContainer;
import de.erichseifert.gral.util.MathUtils;
import de.erichseifert.gral.util.PointND;

public class DrawableContainerNavigator<T extends DrawableContainer> extends AbstractNavigator {
	private final T drawableContainer;
	private double zoom;
	private final PointND<? extends Number> center;

	public DrawableContainerNavigator(T drawableContainer) {
		this.drawableContainer = drawableContainer;
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
	public Point2D toViewCoordinates(PointND<? extends Number> point, double zoom) {
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
		PointND<? extends Number> centerOld = new PointND<Number>(this.center.get(0), this.center.get(1));
		this.center.setLocation(center.get(0), center.get(1));
		NavigationEvent centerChangeEvent = new NavigationEvent(this, centerOld, center);
		fireCenterChanged(centerChangeEvent);
	}

	public PointND<? extends Number> toWorldCoordinates(Point2D point, double zoom) {
		return new PointND<Double>(
			point.getX()/zoom + center.get(0).doubleValue(),
			point.getY()/zoom + center.get(1).doubleValue()
		);
	}

	@Override
	public void pan(PointND<? extends Number> deltas) {
		Point2D centerView = toViewCoordinates(getCenter(), getZoom());
		Point2D centerNewView = new Point2D.Double(
				centerView.getX() - deltas.get(0).doubleValue(),
				centerView.getY() - deltas.get(1).doubleValue()
		);
		PointND<? extends Number> centerNewWorld = toWorldCoordinates(centerNewView, getZoom());
		setCenter(centerNewWorld);
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
}
