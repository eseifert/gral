package openjchart.plots.lines;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;

import openjchart.plots.DataPoint2D;
import openjchart.util.GeometryUtils;
import openjchart.util.Settings;

public abstract class AbstractLineRenderer2D implements LineRenderer2D {
	private final Settings settings;

	public AbstractLineRenderer2D() {
		this.settings = new Settings();

		setSettingDefault(KEY_LINE_STROKE, new BasicStroke(1.5f));
		setSettingDefault(KEY_LINE_GAP, 0.0);
		setSettingDefault(KEY_LINE_COLOR, Color.BLACK);
	}

	@Override
	public Shape punchShapes(Shape line, DataPoint2D... points) {
		Stroke stroke = getSetting(LineRenderer2D.KEY_LINE_STROKE);
		Area lineShape = new Area(stroke.createStrokedShape(line));

		// Subtract shape of data points from line to yield gaps.
		double gap = getSetting(KEY_LINE_GAP);
		if (gap > 1e-10) {
			for (DataPoint2D p : points) {
				AffineTransform tx = AffineTransform.getTranslateInstance(p.getX(), p.getY());
				Shape shape = tx.createTransformedShape(p.getShape());
				Area gapShape = GeometryUtils.grow(shape, gap);
				lineShape.subtract(gapShape);
			}
		}
		return lineShape;
	}

	@Override
	public <T> T getSetting(String key) {
		return settings.<T>get(key);
	}

	@Override
	public <T> void setSetting(String key, T value) {
		settings.<T>set(key, value);
	}

	@Override
	public <T> void removeSetting(String key) {
		settings.remove(key);
	}

	@Override
	public <T> void setSettingDefault(String key, T value) {
		settings.<T>setDefault(key, value);
	}

	@Override
	public <T> void removeSettingDefault(String key) {
		settings.removeDefault(key);
	}

}
