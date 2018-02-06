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

import java.awt.Font;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.text.Format;
import java.text.NumberFormat;

import de.erichseifert.gral.data.Row;
import de.erichseifert.gral.util.GraphicsUtils;

/**
 * Class that provides {@code Drawable}s, which display specified data
 * values as labels.
 */
public class LabelPointRenderer extends DefaultPointRenderer2D {
	/** Version id for serialization. */
	private static final long serialVersionUID = -2612520977245369774L;

	/** Index of the column for the label content. */
	private int column;
	/** Format for the label content. */
	private Format format;
	/** Font for the label content. */
	private Font font;
	/** Horizontal alignment of the label content. */
	private double alignmentX;
	/** Vertical alignment of the label content. */
	private double alignmentY;

	/**
	 * Initializes a new renderer.
	 */
	public LabelPointRenderer() {
		column = 1;
		format = NumberFormat.getInstance();
		font = Font.decode(null);
		alignmentX = 0.5;
		alignmentY = 0.5;
	}

	/**
	 * Returns the index of the column which is used for the label.
	 * @return Index of the column which is used for the label.
	 */
	public int getColumn() {
		return column;
	}

	/**
	 * Sets the index of the column which will be used for the label.
	 * @param column Index of the column which will be used for the label.
	 */
	public void setColumn(int column) {
		this.column = column;
	}

	/**
	 * Returns the format which specifies how the labels are displayed.
	 * @return {@code Format} instance which specifies how the labels are
	 * displayed.
	 */
	public Format getFormat() {
		return format;
	}

	/**
	 * Sets the format which specifies how the labels will be displayed.
	 * @param format {@code Format} instance which specifies how the labels will
	 * be displayed.
	 */
	public void setFormat(Format format) {
		this.format = format;
	}

	/**
	 * Returns the font of this label.
	 * @return Font of this label.
	 */
	public Font getFont() {
		return font;
	}

	/**
	 * Sets font of this label.
	 * @param font Font of this label.
	 */
	public void setFont(Font font) {
		this.font = font;
	}

	/**
	 * Returns the horizontal alignment relative to the data point.
	 * 0 means left, 1 means right.
	 * @return Horizontal alignment relative to the data point.
	 */
	public double getAlignmentX() {
		return alignmentX;
	}

	/**
	 * Sets the horizontal alignment relative to the data point.
	 * 0 means left, 1 means right.
	 * @param alignmentX Horizontal alignment relative to the data point.
	 */
	public void setAlignmentX(double alignmentX) {
		this.alignmentX = alignmentX;
	}

	/**
	 * Returns the vertical alignment relative to the data point.
	 * 0 means top, 1 means bottom.
	 * @return Vertical alignment relative to the data point.
	 */
	public double getAlignmentY() {
		return alignmentY;
	}

	/**
	 * Sets the vertical alignment relative to the data point.
	 * 0 means top, 1 means bottom.
	 * @param alignmentY Vertical alignment relative to the data point.
	 */
	public void setAlignmentY(double alignmentY) {
		this.alignmentY = alignmentY;
	}

	@Override
	public Shape getPointShape(PointData data) {
		Row row = data.row;
		int colLabel = getColumn();
		if (colLabel >= row.size()) {
			return null;
		}

		Comparable<?> labelValue = row.get(colLabel);
		if (labelValue == null) {
			return null;
		}

		Format format = getFormat();
		Font font = getFont();
		String text = format.format(labelValue);
		double alignment = getAlignmentX();
		Shape shape = GraphicsUtils.getOutline(text, font, 0f, alignment);

		double alignX = getAlignmentX();
		double alignY = getAlignmentY();
		Rectangle2D bounds = shape.getBounds2D();
		AffineTransform tx = AffineTransform.getTranslateInstance(
			-alignX*bounds.getWidth(), alignY*bounds.getHeight());
		shape = tx.createTransformedShape(shape);

		return shape;
	}
}
