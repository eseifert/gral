package openjchart.plots.lines;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.GeneralPath;

import openjchart.AbstractDrawable;
import openjchart.Drawable;
import openjchart.plots.DataPoint2D;

public class DiscreteLineRenderer2D extends AbstractLineRenderer2D {
	public static final String KEY_ASCENDING_POINT = "line.discrete.ascending";

	public DiscreteLineRenderer2D() {
		setSettingDefault(KEY_ASCENDING_POINT, 1.0);
	}

	@Override
	public Drawable getLine(final DataPoint2D p1, final DataPoint2D p2) {
		Drawable d = new AbstractDrawable() {
			@Override
			public void draw(Graphics2D g2d) {
				double ascendingPoint = DiscreteLineRenderer2D.this.<Double>getSetting(KEY_ASCENDING_POINT);
				double ascendingX = p1.getX() + (p2.getX() - p1.getX()) * ascendingPoint;

				// Create path
				GeneralPath line = new GeneralPath();
				line.moveTo(p1.getX(), p1.getY());
				line.lineTo(ascendingX, p1.getY());
				line.lineTo(ascendingX, p2.getY());
				line.lineTo(p2.getX(), p2.getY());

				Shape lineShape = punchShapes(line, p1, p2);

				// Draw path
				Paint paintOld = g2d.getPaint();
				g2d.setPaint(DiscreteLineRenderer2D.this.<Paint>getSetting(LineRenderer2D.KEY_LINE_COLOR));
				g2d.fill(lineShape);
				g2d.setPaint(paintOld);
			}
		};
		return d;
	}

}
