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
import de.erichseifert.gral.util.Location;
import de.erichseifert.gral.util.SerializationUtils;


/**
 * Abstract class implementing functions for the administration of settings.
 */
public abstract class AbstractPointRenderer
		implements PointRenderer, Serializable {
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
	private ColorMapper valueColor;
	private Font valueFont;

	private boolean errorDisplayed;
	private int errorColumnTop;
	private int errorColumnBottom;
	private ColorMapper errorColor;
	private Shape errorShape;
	private transient Stroke errorStroke;

	/**
	 * Creates a new AbstractPointRenderer object with default shape and
	 * color.
	 */
	public AbstractPointRenderer() {
		shape = new Rectangle2D.Double(-2.5, -2.5, 5.0, 5.0);
		color = new SingleColor(Color.BLACK);

		valueDisplayed = false;
		valueColumn = 1;
		valueLocation = Location.CENTER;
		valueAlignmentX = 0.5;
		valueAlignmentY = 0.5;
		valueRotation = 0.0;
		valueDistance = 1.0;
		valueColor = new SingleColor(Color.BLACK);
		valueFont = Font.decode(null);

		errorDisplayed = false;
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
	public boolean isErrorDisplayed() {
		return errorDisplayed;
	}

	@Override
	public void setErrorDisplayed(boolean errorDisplayed) {
		this.errorDisplayed = errorDisplayed;
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
