package openjchart.plots.lines;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import openjchart.AbstractDrawable;
import openjchart.Drawable;
import openjchart.plots.DataPoint2D;
import openjchart.util.GraphicsUtils;

public class DefaultLineRenderer2D extends AbstractLineRenderer2D {

	public DefaultLineRenderer2D() {
	}

	@Override
	public Drawable getLine(final DataPoint2D p1, final DataPoint2D p2) {
		Drawable d = new AbstractDrawable() {
			@Override
			public void draw(Graphics2D g2d) {
				if (p1 == null || p2 == null) {
					return;
				}
				Point2D pos1 = p1.getPosition();
				Point2D pos2 = p2.getPosition();
				Line2D line = new Line2D.Double(pos1, pos2);
				Shape lineShape = punchShapes(line, p1, p2);

				// Draw line
				Paint paint = getSetting(LineRenderer2D.KEY_LINE_COLOR);
				GraphicsUtils.fillPaintedShape(g2d, lineShape, paint, null);
			}
		};
		return d;
	}

}
