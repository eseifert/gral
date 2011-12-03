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
package de.erichseifert.gral;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import de.erichseifert.gral.util.Insets2D;


/**
 * Implementation of Layout that arranges a {@link Container}'s components
 * according to a tabular grid with a fixed number of columns. This is similar
 * to Java's {@link java.awt.GridLayout}, but the cells in the grid may have
 * different dimensions.
 */
public class TableLayout implements Layout {
	/** Number of columns. */
	private final int cols;
	/** Horizontal spacing. */
	private final double hgap;
	/** Vertical spacing. */
	private final double vgap;

	private double[] colWidths;
	private double[] rowHeights;
	private double colWidthsSum;
	private double rowHeightsSum;

	/**
	 * Initializes a layout manager object with the specified number of columns
	 * and the distances between the components.
	 * @param cols Number of columns
	 * @param hgap Horizontal gap.
	 * @param vgap Vertical gap.
	 */
	public TableLayout(int cols, double hgap, double vgap) {
		this.cols = cols;
		this.hgap = hgap;
		this.vgap = vgap;
	}

	/**
	 * Initializes a layout manager object with the specified number of columns
	 * and no gap between the components.
	 * @param cols Number of columns.
	 */
	public TableLayout(int cols) {
		this(cols, 0.0, 0.0);
	}

	/**
	 * Arranges the components of the specified container according to this
	 * layout.
	 * @param container Container to be laid out.
	 */
	public void layout(Container container) {
		Insets2D insets = container.getInsets();
		if (insets == null) {
			insets = new Insets2D.Double();
		}
		Rectangle2D bounds = container.getBounds();
		double containerWidth =
			bounds.getWidth() - insets.getLeft() - insets.getRight();
		double containerHeight =
			bounds.getHeight() - insets.getTop() - insets.getBottom();

		updateDimensions(container);
		double remainderH =
			Math.max(containerWidth - colWidthsSum, 0.0)/colWidths.length;
		double remainderV =
			Math.max(containerHeight - rowHeightsSum, 0.0)/rowHeights.length;

		int compIndex = 0;
		double x = insets.getLeft(), y = insets.getTop();
		for (Drawable component : container) {
			int col = compIndex%cols;
			int row = compIndex/cols;

			Dimension2D size = component.getPreferredSize();

			layoutComponent(component,
				x, y,
				Math.max(size.getWidth(), colWidths[col] + remainderH),
				Math.max(size.getHeight(), rowHeights[row] + remainderV)
			);

			if (col < cols - 1) {
				x += colWidths[col] + remainderH + hgap;
			} else {
				x = insets.getLeft();
				y += rowHeights[row] + remainderV + vgap;
			}

			compIndex++;
		}
	}

	/**
	 * Calculates the preferred dimensions for all columns and rows.
	 * @param container The container for which the dimension should be
	 *        calculated.
	 */
	private void updateDimensions(Container container) {
		int rows = (int) Math.ceil(container.size() / (double) cols);

		colWidths = new double[cols];
		rowHeights = new double[rows];

		// Find out the preferred dimensions for each columns and row
		int i = 0;
		for (Drawable component : container) {
			int col = i%cols;
			int row = i/cols;

			Dimension2D size = component.getPreferredSize();

			colWidths[col] = Math.max(size.getWidth(), colWidths[col]);
			rowHeights[row] = Math.max(size.getHeight(), rowHeights[row]);

			i++;
		}

		colWidthsSum = sum(colWidths) + (colWidths.length - 1)*hgap;
		rowHeightsSum = sum(rowHeights) + (rowHeights.length - 1)*vgap;
	}

	/**
	 * Returns the preferred size of the specified container using this layout.
	 * @param container Container whose preferred size is to be returned.
	 * @return Preferred extent of the specified container.
	 */
	public Dimension2D getPreferredSize(Container container) {
		updateDimensions(container);

		return new de.erichseifert.gral.util.Dimension2D.Double(
			colWidthsSum, rowHeightsSum
		);
	}

	/**
	 * Returns the sum of all values in the specified array.
	 * @param values Values to sum.
	 * @return Sum of all values.
	 */
	private static final double sum(double[] values) {
		double sum = 0.0;
		for (double v : values) {
			sum += v;
		}
		return sum;
	}

	/**
	 * Sets the bounds of the specified {@code Drawable} to the specified
	 * values.
	 * @param component {@code Drawable} that should be resized.
	 * @param x X coordinate.
	 * @param y Y coordinate.
	 * @param w Width.
	 * @param h Height.
	 */
	private static void layoutComponent(Drawable component,
			double x, double y, double w, double h) {
		if (component == null) {
			return;
		}
		component.setBounds(x, y, w, h);
	}
}
