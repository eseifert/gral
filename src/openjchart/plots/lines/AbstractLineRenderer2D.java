/* OpenJChart : a free plotting library for the Java(tm) platform
 *
 * (C) Copyright 2009-2010, by Erich Seifert and Michael Seifert.
 *
 * This file is part of OpenJChart.
 *
 * OpenJChart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenJChart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenJChart.  If not, see <http://www.gnu.org/licenses/>.
 */

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

/**
 * Abstract class that renders a line in 2-dimensional space.
 * Functionality includes:
 * <ul>
 * <li>Punching data points out of the line's shape</li>
 * <li>Administration of settings</li>
 * </ul>
 */
public abstract class AbstractLineRenderer2D implements LineRenderer2D, SettingsListener {
	private final Settings settings;

	/**
	 * Creates a new AbstractLineRenderer2D with default settings.
	 */
	public AbstractLineRenderer2D() {
		this.settings = new Settings(this);

		setSettingDefault(KEY_STROKE, new BasicStroke(1.5f));
		setSettingDefault(KEY_GAP, 0.0);
		setSettingDefault(KEY_GAP_ROUNDED, false);
		setSettingDefault(KEY_COLOR, Color.BLACK);
	}

	@Override
	public Shape punchShapes(Shape line, DataPoint2D... points) {
		Stroke stroke = getSetting(LineRenderer2D.KEY_STROKE);
		Area lineShape = new Area(stroke.createStrokedShape(line));

		// Subtract shape of data points from line to yield gaps.
		double gapSize = getSetting(KEY_GAP);
		if (!MathUtils.almostEqual(gapSize, 0.0, 1e-10)) {
			boolean isGapRounded = getSetting(KEY_GAP_ROUNDED);
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
