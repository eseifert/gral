/**
 * GRAL : Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2010 Erich Seifert <info[at]erichseifert.de>, Michael Seifert <michael.seifert[at]gmx.net>
 *
 * This file is part of GRAL.
 *
 * GRAL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GRAL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GRAL.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.erichseifert.gral.plots.lines;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;

import de.erichseifert.gral.plots.DataPoint2D;
import de.erichseifert.gral.util.GeometryUtils;
import de.erichseifert.gral.util.MathUtils;
import de.erichseifert.gral.util.SettingChangeEvent;
import de.erichseifert.gral.util.Settings;
import de.erichseifert.gral.util.SettingsListener;


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
	public Shape punchPoints(Shape line, DataPoint2D... dataPoints) {
		Stroke stroke = getSetting(LineRenderer2D.KEY_STROKE);
		Area lineShape = new Area(stroke.createStrokedShape(line));

		// Subtract shapes of data points from line to yield gaps.
		double gapSize = this.<Double>getSetting(KEY_GAP);
		if (!MathUtils.almostEqual(gapSize, 0.0, 1e-10)) {
			boolean isGapRounded = this.<Boolean>getSetting(KEY_GAP_ROUNDED);
			int gapJoin = (isGapRounded) ? BasicStroke.JOIN_ROUND : BasicStroke.JOIN_MITER;
			for (DataPoint2D p : dataPoints) {
				Shape point = p.getPoint();
				if (point == null) {
					continue;
				}
				Point2D pos = p.getPosition();
				AffineTransform tx = AffineTransform.getTranslateInstance(pos.getX(), pos.getY());
				Area gapShape = GeometryUtils.grow(tx.createTransformedShape(point), gapSize, gapJoin, 10f);
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
