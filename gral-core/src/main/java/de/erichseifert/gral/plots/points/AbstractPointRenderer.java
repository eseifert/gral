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
package de.erichseifert.gral.plots.points;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.Format;

import de.erichseifert.gral.plots.colors.ColorMapper;
import de.erichseifert.gral.plots.colors.SingleColor;
import de.erichseifert.gral.plots.settings.BasicSettingsStorage;
import de.erichseifert.gral.plots.settings.Key;
import de.erichseifert.gral.plots.settings.SettingChangeEvent;
import de.erichseifert.gral.plots.settings.SettingsListener;
import de.erichseifert.gral.util.Location;


/**
 * Abstract class implementing functions for the administration of settings.
 */
public abstract class AbstractPointRenderer extends BasicSettingsStorage
		implements PointRenderer, SettingsListener {
	/** Version id for serialization. */
	private static final long serialVersionUID = -408976260196287753L;

	private Shape shape;
	private ColorMapper color;

	private boolean valueDisplayed;
	private int valueColumn;
	private Format valueFormat;
	private Location valueLocation;
	private double valueAlignmentX;
	private double valueAlignmentY;
	private double valueRotation;
	private double valueDistance;

	/**
	 * Creates a new AbstractPointRenderer object with default shape and
	 * color.
	 */
	public AbstractPointRenderer() {
		addSettingsListener(this);

		shape = new Rectangle2D.Double(-2.5, -2.5, 5.0, 5.0);
		color = new SingleColor(Color.BLACK);

		valueDisplayed = false;
		valueColumn = 1;
		valueLocation = Location.CENTER;
		valueAlignmentX = 0.5;
		valueAlignmentY = 0.5;
		valueRotation = 0.0;
		valueDistance = 1.0;
		setSettingDefault(VALUE_COLOR, new SingleColor(Color.BLACK));
		setSettingDefault(VALUE_FONT, Font.decode(null));

		setSettingDefault(ERROR_DISPLAYED, Boolean.FALSE);
		setSettingDefault(ERROR_COLUMN_TOP, 2);
		setSettingDefault(ERROR_COLUMN_BOTTOM, 3);
		setSettingDefault(ERROR_COLOR, new SingleColor(Color.BLACK));
		setSettingDefault(ERROR_SHAPE, new Line2D.Double(-2.0, 0.0, 2.0, 0.0));
		setSettingDefault(ERROR_STROKE, new BasicStroke(1f));
	}

	/**
	 * Invoked if a setting has changed.
	 * @param event Event containing information about the changed setting.
	 */
	public void settingChanged(SettingChangeEvent event) {
	}

	@Override
	protected <T> void setSetting(Key key, T value, boolean isDefault) {
		// Be nice and automatically convert colors to color mappers
		if (value instanceof Paint && (VALUE_COLOR.equals(key)
				|| ERROR_COLOR.equals(key))) {
			super.setSetting(key, new SingleColor((Paint) value), isDefault);
		} else {
			super.setSetting(key, value, isDefault);
		}
	};

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
	public Shape getShape() {
		return shape;
	}

	@Override
	public void setShape(Shape shape) {
		// TODO Store clone of shape to prevent external modification
		this.shape = shape;
	}

	@Override
	public ColorMapper getColor() {
		return color;
	}

	@Override
	public void setColor(ColorMapper color) {
		this.color = color;
	}

	@Override
	public void setColor(Paint color) {
		setColor(new SingleColor(color));
	}

	@Override
	public boolean isValueDisplayed() {
		return valueDisplayed;
	}

	@Override
	public void setValueDisplayed(boolean valueDisplayed) {
		this.valueDisplayed = valueDisplayed;
	}

	@Override
	public int getValueColumn() {
		return valueColumn;
	}

	@Override
	public void setValueColumn(int columnIndex) {
		this.valueColumn = columnIndex;
	}

	@Override
	public Format getValueFormat() {
		return valueFormat;
	}

	@Override
	public void setValueFormat(Format format) {
		this.valueFormat = format;
	}

	@Override
	public Location getValueLocation() {
		return valueLocation;
	}

	@Override
	public void setValueLocation(Location location) {
		this.valueLocation = location;
	}

	@Override
	public double getValueAlignmentX() {
		return valueAlignmentX;
	}

	@Override
	public void setValueAlignmentX(double alignmentX) {
		this.valueAlignmentX = alignmentX;
	}

	@Override
	public double getValueAlignmentY() {
		return valueAlignmentY;
	}

	@Override
	public void setValueAlignmentY(double alignmentY) {
		this.valueAlignmentY = alignmentY;
	}

	@Override
	public double getValueRotation() {
		return valueRotation;
	}

	@Override
	public void setValueRotation(double angle) {
		this.valueRotation = angle;
	}

	@Override
	public double getValueDistance() {
		return valueRotation;
	}

	@Override
	public void setValueDistance(double distance) {
		this.valueDistance = distance;
	}
}
