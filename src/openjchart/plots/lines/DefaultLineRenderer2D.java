package openjchart.plots.lines;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import openjchart.AbstractDrawable;
import openjchart.Drawable;
import openjchart.util.GeometryUtils;

public class DefaultLineRenderer2D extends AbstractLineRenderer2D {

	public DefaultLineRenderer2D() {
	}

	@Override
	public Drawable getLine(final double x1, final double y1, final double x2, final double y2) {
		Drawable d = new AbstractDrawable() {
			@Override
			public void draw(Graphics2D g2d) {
				Insets insets = getSetting(KEY_POINT_INSETS);
				double width = insets.left + insets.right;
				double height = insets.top + insets.bottom;
				Line2D line = new Line2D.Double(x1, y1, x2, y2);

				// Calculate intersections with start point
				double x = x1 - insets.left;
				double y = y1 - insets.top;
				Rectangle2D bounds = new Rectangle2D.Double(x, y, width, height);
				List<Point2D> intersections = GeometryUtils.intersection(bounds, line);
				Point2D intersection1 = !intersections.isEmpty() ? intersections.get(0): new Point2D.Double(x1, y1);

				// Calculate intersections with end point
				x = x2 - insets.left;
				y = y2 - insets.top;
				bounds.setFrame(x, y, width, height);
				intersections = GeometryUtils.intersection(bounds, line);
				Point2D intersection2 = !intersections.isEmpty() ? intersections.get(0): new Point2D.Double(x2, y2);

				// Draw line
				Color colorOld = g2d.getColor();
				Stroke strokeOld = g2d.getStroke();
				g2d.setColor(DefaultLineRenderer2D.this.<Color>getSetting(LineRenderer2D.KEY_LINE_COLOR));
				g2d.setStroke(DefaultLineRenderer2D.this.<Stroke>getSetting(LineRenderer2D.KEY_LINE_STROKE));
				line.setLine(
						intersection1.getX(), intersection1.getY(),
						intersection2.getX(), intersection2.getY());
				g2d.draw(line);
				g2d.setColor(colorOld);
				g2d.setStroke(strokeOld);
			}
		};
		return d;
	}

}
