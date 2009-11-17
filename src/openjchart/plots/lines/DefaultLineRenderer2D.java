package openjchart.plots.lines;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import openjchart.AbstractDrawable;
import openjchart.Drawable;
import openjchart.plots.DataPoint2D;

public class DefaultLineRenderer2D extends AbstractLineRenderer2D {

	public DefaultLineRenderer2D() {
	}

	@Override
	public Drawable getLine(final DataPoint2D p1, final DataPoint2D p2) {
		Drawable d = new AbstractDrawable() {
			@Override
			public void draw(Graphics2D g2d) {
				Point2D pos1 = p1.getPosition();
				Point2D pos2 = p2.getPosition();
				Line2D line = new Line2D.Double(pos1, pos2);
				Shape lineShape = punchShapes(line, p1, p2);

				// Draw line
				Color colorOld = g2d.getColor();

				g2d.setColor(DefaultLineRenderer2D.this.<Color>getSetting(LineRenderer2D.KEY_LINE_COLOR));
				g2d.fill(lineShape);

				g2d.setColor(colorOld);
			}
		};
		return d;
	}

}
