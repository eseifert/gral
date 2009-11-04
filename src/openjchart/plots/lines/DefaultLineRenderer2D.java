package openjchart.plots.lines;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;

import openjchart.AbstractDrawable;
import openjchart.Drawable;

public class DefaultLineRenderer2D extends AbstractLineRenderer2D {

	public DefaultLineRenderer2D() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Drawable getLine(final double x1, final double y1, final double x2, final double y2) {
		Drawable d = new AbstractDrawable() {
			@Override
			public void draw(Graphics2D g2d) {
				Color colorOld = g2d.getColor();
				Stroke strokeOld = g2d.getStroke();

				g2d.setColor(DefaultLineRenderer2D.this.<Color>getSetting(LineRenderer2D.KEY_LINE_COLOR));
				g2d.setStroke(DefaultLineRenderer2D.this.<Stroke>getSetting(LineRenderer2D.KEY_LINE_STROKE));
				Line2D line = new Line2D.Double(x1, y1, x2, y2);
				g2d.draw(line);
				g2d.setColor(colorOld);
				g2d.setStroke(strokeOld);
			}
		};
		return d;
	}

}
