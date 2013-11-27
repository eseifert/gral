/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2013 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael[at]erichseifert.de>
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
import java.io.IOException;
import java.io.ObjectInputStream;

import de.erichseifert.gral.plots.DataPoint;
import de.erichseifert.gral.plots.settings.BasicSettingsStorage;
import de.erichseifert.gral.plots.settings.SettingChangeEvent;
import de.erichseifert.gral.plots.settings.SettingsListener;
import de.erichseifert.gral.util.DataUtils;
import de.erichseifert.gral.util.GeometryUtils;
import de.erichseifert.gral.util.MathUtils;


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
	/** Version id for serialization. */
	private static final long serialVersionUID = -4172505541305453796L;

	private Stroke stroke;

	/**
	 * Initializes a new {@code AbstractLineRenderer2D} instance with
	 * default settings.
	 */
	public AbstractLineRenderer2D() {
		addSettingsListener(this);

		stroke = new BasicStroke(1.5f);
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
		if (line == null) {
			return null;
		}
		Stroke stroke = getStroke();
		Shape lineShape = stroke.createStrokedShape(line);

		double size = DataUtils.getValueOrDefault(
			this.<Number>getSetting(GAP), 0.0);
		if (!MathUtils.isCalculatable(size) || size == 0.0) {
			return lineShape;
		}

		boolean rounded = this.<Boolean>getSetting(GAP_ROUNDED);

		// Subtract shapes of data points from the line to yield gaps.
		Area punched = new Area(lineShape);
		for (DataPoint p : dataPoints) {
			punched = GeometryUtils.punch(punched, size, rounded,
				p.position.getPoint2D(), p.shape);
		}
		return punched;
	}

	/**
	 * Invoked if a setting has changed.
	 * @param event Event containing information about the changed setting.
	 */
	public void settingChanged(SettingChangeEvent event) {
	}

	/**
	 * Custom deserialization method.
	 * @param in Input stream.
	 * @throws ClassNotFoundException if a serialized class doesn't exist anymore.
	 * @throws IOException if there is an error while reading data from the
	 *         input stream.
	 */
	private void readObject(ObjectInputStream in)
			throws ClassNotFoundException, IOException {
		// Normal deserialization
		in.defaultReadObject();

		// Restore listeners
		addSettingsListener(this);
	}

	@Override
	public Stroke getStroke() {
		return stroke;
	}

	@Override
	public void setStroke(Stroke stroke) {
		this.stroke = stroke;
	}
}
