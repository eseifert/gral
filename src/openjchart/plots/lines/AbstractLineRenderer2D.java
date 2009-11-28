package openjchart.plots.lines;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;

import openjchart.plots.DataPoint2D;
import openjchart.util.GeometryUtils;
import openjchart.util.MathUtils;
import openjchart.util.SettingChangeEvent;
import openjchart.util.Settings;
import openjchart.util.SettingsListener;

public abstract class AbstractLineRenderer2D implements LineRenderer2D, SettingsListener {
	private final Settings settings;

	public AbstractLineRenderer2D() {
		this.settings = new Settings(this);

		setSettingDefault(KEY_LINE_STROKE, new BasicStroke(1.5f));
		setSettingDefault(KEY_LINE_GAP, 0.0);
		setSettingDefault(KEY_LINE_GAP_ROUNDED, false);
		setSettingDefault(KEY_LINE_COLOR, Color.BLACK);
	}

	@Override
	public Shape punchShapes(Shape line, DataPoint2D... points) {
		Stroke stroke = getSetting(LineRenderer2D.KEY_LINE_STROKE);
		Area lineShape = new Area(stroke.createStrokedShape(line));

		// Subtract shape of data points from line to yield gaps.
		double gapSize = getSetting(KEY_LINE_GAP);
		if (!MathUtils.almostEqual(gapSize, 0.0, 1e-10)) {
			boolean isGapRounded = getSetting(KEY_LINE_GAP_ROUNDED);
			int gapJoin = (isGapRounded) ? BasicStroke.JOIN_ROUND : BasicStroke.JOIN_MITER;
			for (DataPoint2D p : points) {
				Shape shape = p.getShape();
				if (shape == null) {
					continue;
				}
				Point2D pos = p.getPosition();
				AffineTransform tx = AffineTransform.getTranslateInstance(pos.getX(), pos.getY());
				Area gapShape = GeometryUtils.grow(tx.createTransformedShape(shape), gapSize, gapJoin, 10f);
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

	@Override
	public void settingChanged(SettingChangeEvent event) {
	}
}
