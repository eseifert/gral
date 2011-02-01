/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2011 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael.seifert[at]gmx.net>
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
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GRAL.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.erichseifert.gral.plots.lines;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;

import de.erichseifert.gral.plots.DataPoint;
import de.erichseifert.gral.util.BasicSettingsStorage;
import de.erichseifert.gral.util.GeometryUtils;
import de.erichseifert.gral.util.SettingChangeEvent;
import de.erichseifert.gral.util.SettingsListener;


/**
 * <p>Abstract class that renders a line in two-dimensional space.</p>
 * <p>Functionality includes:</p>
 * <ul>
 *   <li>Punching data points out of the line's shape</li>
 *   <li>Administration of settings</li>
 * </ul>
 */
public abstract class AbstractLineRenderer2D extends BasicSettingsStorage
		implements LineRenderer, SettingsListener {
	/**
	 * Initializes a new <code>AbstractLineRenderer2D</code> instance with
	 * default settings.
	 */
	public AbstractLineRenderer2D() {
		addSettingsListener(this);

		setSettingDefault(STROKE, new BasicStroke(1.5f));
		setSettingDefault(GAP, 0.0);
		setSettingDefault(GAP_ROUNDED, false);
		setSettingDefault(COLOR, Color.BLACK);
	}

	/**
	 * Returns the shape of a line from which the shapes of the specified
	 * points are subtracted.
	 * @param line Shape of the line.
	 * @param dataPoints Data points on the line.
	 * @return Punched shape.
	 */
	protected Shape punch(Shape line, Iterable<DataPoint> dataPoints) {
		Stroke stroke = getSetting(LineRenderer.STROKE);
		Shape lineShape = stroke.createStrokedShape(line);

		// Subtract shapes of data points from line to yield gaps.
		double gapSize = this.<Number>getSetting(GAP).doubleValue();
		if (gapSize > 0.0) {
			boolean isGapRounded = this.<Boolean>getSetting(GAP_ROUNDED);
			Area punched = GeometryUtils.punch(
					lineShape, gapSize, isGapRounded, dataPoints);
			return punched;
		}
		return lineShape;
	}

	@Override
	public void settingChanged(SettingChangeEvent event) {
	}
}
