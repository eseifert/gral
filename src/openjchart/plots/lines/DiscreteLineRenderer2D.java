package openjchart.plots.lines;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import openjchart.AbstractDrawable;
import openjchart.Drawable;
import openjchart.util.GeometryUtils;

public class DiscreteLineRenderer2D extends AbstractLineRenderer2D {
	public static final String KEY_ASCENDING_POINT = "line.discrete.ascending";

	public DiscreteLineRenderer2D() {
		setSettingDefault(KEY_ASCENDING_POINT, 1.0);
	}

	@Override
	public Drawable getLine(final double x1, final double y1, final double x2, final double y2) {
		Drawable d = new AbstractDrawable() {
			@Override
			public void draw(Graphics2D g2d) {
				Insets insets = getSetting(KEY_POINT_INSETS);
				double width = insets.left + insets.right;
				double height = insets.top + insets.bottom;

				double ascendingPoint = DiscreteLineRenderer2D.this.<Double>getSetting(KEY_ASCENDING_POINT);
				double ascedingX = x1 + (x2 - x1) * ascendingPoint;

				// Calculate intersections with start point
				double startX = x1;
				double startY = y1;
				double x = x1 - insets.left;
				double y = y1 - insets.top;
				Rectangle2D bounds = new Rectangle2D.Double(x, y, width, height);
				List<Point2D> intersections = GeometryUtils.intersection(bounds, new Line2D.Double(x1, y1, ascedingX, y1));
				if (!intersections.isEmpty()) {
					startX = intersections.get(0).getX();
					startY = intersections.get(0).getY();
				}

				// Calculate intersections with end point
				double endX = x2;
				double endY = y2;
				x = x2 - insets.left;
				y = y2 - insets.top;
				bounds.setFrame(x, y, width, height);
				intersections = GeometryUtils.intersection(bounds, new Line2D.Double(ascedingX, y2, x2, y2));
				if (!intersections.isEmpty()) {
					endX = intersections.get(0).getX();
					endY = intersections.get(0).getY();
				}

				// Create path
				GeneralPath path = new GeneralPath();
				path.moveTo(startX, startY);
				path.lineTo(ascedingX, y1);
				path.lineTo(ascedingX, y2);
				path.lineTo(endX, endY);

				// Draw path
				Color colorOld = g2d.getColor();
				Stroke strokeOld = g2d.getStroke();
				g2d.setColor(DiscreteLineRenderer2D.this.<Color>getSetting(LineRenderer2D.KEY_LINE_COLOR));
				g2d.setStroke(DiscreteLineRenderer2D.this.<Stroke>getSetting(LineRenderer2D.KEY_LINE_STROKE));
				g2d.draw(path);
				g2d.setColor(colorOld);
				g2d.setStroke(strokeOld);
			}
		};
		return d;
	}

}
