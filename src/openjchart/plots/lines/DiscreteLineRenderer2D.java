package openjchart.plots.lines;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import openjchart.AbstractDrawable;
import openjchart.Drawable;
import openjchart.plots.DataPoint2D;
import openjchart.util.GraphicsUtils;

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
				Point2D pos1 = p1.getPosition();
				Point2D pos2 = p2.getPosition();
				double ascendingPoint = DiscreteLineRenderer2D.this.<Double>getSetting(KEY_ASCENDING_POINT);
				double ascendingX = pos1.getX() + (pos2.getX() - pos1.getX()) * ascendingPoint;

				// Create path
				GeneralPath line = new GeneralPath();
				line.moveTo(pos1.getX(), pos1.getY());
				line.lineTo(ascendingX,  pos1.getY());
				line.lineTo(ascendingX,  pos2.getY());
				line.lineTo(pos2.getX(), pos2.getY());

				Shape lineShape = punchShapes(line, p1, p2);

				// Draw path
				Paint paint = getSetting(LineRenderer2D.KEY_LINE_COLOR);
				GraphicsUtils.fillPaintedShape(g2d, lineShape, paint, null);
			}
		};
		return d;
	}

}
