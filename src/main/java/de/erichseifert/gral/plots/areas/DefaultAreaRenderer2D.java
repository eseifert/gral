package de.erichseifert.gral.plots.areas;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.GeneralPath;

import de.erichseifert.gral.AbstractDrawable;
import de.erichseifert.gral.Drawable;
import de.erichseifert.gral.plots.DataPoint2D;
import de.erichseifert.gral.plots.axes.Axis;
import de.erichseifert.gral.plots.axes.AxisRenderer2D;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.MathUtils;

public class DefaultAreaRenderer2D extends AbstractAreaRenderer2D {

	@Override
	public Drawable getArea(Axis axis, AxisRenderer2D axisRenderer, Iterable<DataPoint2D> points) {
		Shape path = getAreaShape(axis, axisRenderer, points);
		final Shape area = punch(path, points);

		return new AbstractDrawable() {
			@Override
			public void draw(Graphics2D g2d) {
				Paint paint = getSetting(KEY_COLOR);
				GraphicsUtils.fillPaintedShape(g2d, area, paint, area.getBounds2D());
			}
		};
	}

	private Shape getAreaShape(Axis axis, AxisRenderer2D axisRenderer, Iterable<DataPoint2D> points) {
		double axisYMin = axis.getMin().doubleValue();
		double axisYMax = axis.getMax().doubleValue();
		double axisYOrigin = MathUtils.limit(0.0, axisYMin, axisYMax);
		double posYOrigin = axisRenderer.getPosition(axis, axisYOrigin, true, false).getY();

		GeneralPath path = new GeneralPath();

		float x = 0f;
		float y = 0f;
		boolean isFirst = true;
		for (DataPoint2D p: points) {
			x = (float)p.getPosition().getX();
			y = (float)p.getPosition().getY();
			if (isFirst) {
				path.moveTo(x, (float)posYOrigin);
				isFirst = false;
			}
			path.lineTo(x, y);
		}
		path.lineTo(x, (float)posYOrigin);
		path.closePath();

		return path;
	}
}
