/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2018 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <mseifert[at]error-reports.org>
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
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.Format;

import de.erichseifert.gral.plots.colors.ColorMapper;
import de.erichseifert.gral.plots.colors.SingleColor;
import de.erichseifert.gral.graphics.Location;
import de.erichseifert.gral.util.SerializationUtils;


/**
 * Abstract class implementing functions for the administration of settings.
 */
public abstract class AbstractPointRenderer
		implements PointRenderer, Serializable {
	/** Version id for serialization. */
	private static final long serialVersionUID = -408976260196287753L;

	/** Shape to draw for the points. */
	private Shape shape;
	/** Color mapping used to fill the points. */
	private ColorMapper color;

	/** Decides whether a value label are drawn at the point. */
	private boolean valueVisible;
	/** Index of the column for the value label content. */
	private int valueColumn;
	/** Format of the value label content. */
	private Format valueFormat;
	/** Position of the value label relative to the point position. */
	private Location valueLocation;
	/** Horizontal alignment of the value label. */
	private double valueAlignmentX;
	/** Vertical alignment of the value label. */
	private double valueAlignmentY;
	/** Rotation angle of the value label in degrees. */
	private double valueRotation;
	/** Distance of the value label to the shape of the point. */
	private double valueDistance;
	/** Color mapping to fill the value label. */
	private ColorMapper valueColor;
	/** Font to draw the value label contents. */
	private Font valueFont;

	/** Decides whether error indicators are drawn for the point. */
	private boolean errorVisible;
	/** Index of the column for the upper error bounds. */
	private int errorColumnTop;
	/** Index of the column for the lower error bounds. */
	private int errorColumnBottom;
	/** Color mapping to fill the error indicators. */
	private ColorMapper errorColor;
	/** Shape to draw the error indicators. */
	private Shape errorShape;
	/** Stroke to the shapes of the error indicators. */
	private transient Stroke errorStroke;

	/**
	 * Creates a new AbstractPointRenderer object with default shape and
	 * color.
	 */
	public AbstractPointRenderer() {
		shape = new Rectangle2D.Double(-2.5, -2.5, 5.0, 5.0);
		color = new SingleColor(Color.BLACK);

		valueVisible = false;
		valueColumn = 1;
		valueLocation = Location.CENTER;
		valueAlignmentX = 0.5;
		valueAlignmentY = 0.5;
		valueRotation = 0.0;
		valueDistance = 1.0;
		valueColor = new SingleColor(Color.BLACK);
		valueFont = Font.decode(null);

		errorVisible = false;
		errorColumnTop = 2;
		errorColumnBottom = 3;
		errorColor = new SingleColor(Color.BLACK);
		errorShape = new Line2D.Double(-2.0, 0.0, 2.0, 0.0);
		errorStroke = new BasicStroke(1f);
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
		// Default deserialization
		in.defaultReadObject();
		// Custom deserialization
		errorStroke = (Stroke) SerializationUtils.unwrap(
				(Serializable) in.readObject());
	}

	/**
	 * Custom serialization method.
	 * @param out Output stream.
	 * @throws ClassNotFoundException if a serialized class doesn't exist anymore.
	 * @throws IOException if there is an error while writing data to the
	 *         output stream.
	 */
	private void writeObject(ObjectOutputStream out)
			throws ClassNotFoundException, IOException {
		// Default serialization
		out.defaultWriteObject();
		// Custom serialization
		out.writeObject(SerializationUtils.wrap(errorStroke));
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
	public boolean isValueVisible() {
		return valueVisible;
	}

	@Override
	public void setValueVisible(boolean valueVisible) {
		this.valueVisible = valueVisible;
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
		return valueDistance;
	}

	@Override
	public void setValueDistance(double distance) {
		this.valueDistance = distance;
	}

	@Override
	public ColorMapper getValueColor() {
		return valueColor;
	}

	@Override
	public void setValueColor(ColorMapper color) {
		this.valueColor = color;
	}

	@Override
	public void setValueColor(Paint color) {
		setValueColor(new SingleColor(color));
	}

	@Override
	public Font getValueFont() {
		return valueFont;
	}

	@Override
	public void setValueFont(Font font) {
		this.valueFont = font;
	}

	@Override
	public boolean isErrorVisible() {
		return errorVisible;
	}

	@Override
	public void setErrorVisible(boolean errorVisible) {
		this.errorVisible = errorVisible;
	}

	@Override
	public int getErrorColumnTop() {
		return errorColumnTop;
	}

	@Override
	public void setErrorColumnTop(int columnIndex) {
		this.errorColumnTop = columnIndex;
	}

	@Override
	public int getErrorColumnBottom() {
		return errorColumnBottom;
	}

	@Override
	public void setErrorColumnBottom(int columnIndex) {
		this.errorColumnBottom = columnIndex;
	}

	@Override
	public ColorMapper getErrorColor() {
		return errorColor;
	}

	@Override
	public void setErrorColor(ColorMapper color) {
		this.errorColor = color;
	}

	@Override
	public void setErrorColor(Paint color) {
		setErrorColor(new SingleColor(color));
	}

	@Override
	public Shape getErrorShape() {
		return errorShape;
	}

	@Override
	public void setErrorShape(Shape shape) {
		// TODO Store clone of shape to prevent external modification
		this.errorShape = shape;
	}

	@Override
	public Stroke getErrorStroke() {
		return errorStroke;
	}

	@Override
	public void setErrorStroke(Stroke stroke) {
		this.errorStroke = stroke;
	}
}
