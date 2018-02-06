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
package de.erichseifert.gral.graphics.layout;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import de.erichseifert.gral.graphics.Container;
import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.Insets2D;


/**
 * Implementation of Layout that arranges a {@link Container}'s components
 * according to a tabular grid with a fixed number of columns. This is similar
 * to Java's {@link java.awt.GridLayout}, but the cells in the grid may have
 * different dimensions.
 */
public class TableLayout extends AbstractLayout {
	/** Version id for serialization. */
	private static final long serialVersionUID = -6738742507926295041L;

	/** Number of columns. */
	private final int cols;

	/** Index of the column values in the array that is returned by
	{@link #getInfo(Container)}. */
	private static final int COLS = 0;
	/** Index of the row values in the array that is returned by
	{@link #getInfo(Container)}. */
	private static final int ROWS = 1;

	/**
	 * Internal data class to store layout related values.
	 */
	private static final class Info {
		/** Map of column/row index and maximal preferred size. */
		public final Map<Integer, Double> sizes;
		/** Number of columns/rows */
		public int size;
		/** Sum of preferred sizes in horizontal/vertical direction. */
		public double sizeSum;
		/** Sum of insets in horizontal/vertical direction. */
		public double insetsSum;
		/** Sum of gaps in horizontal/vertical direction. */
		public double gapSum;
		/** Mean preferred size in horizontal/vertical direction. */
		public double sizeMean;
		/** Space in horizontal/vertical direction which couldn't be resized
		because the size limits of some components have been reached. */
		public double unsizeableSpace;

		/**
		 * Initializes a new instance for storing several variables.
		 */
		public Info() {
			sizes = new HashMap<>();
		}
	}

	/**
	 * Initializes a layout manager object with the specified number of columns
	 * and the distances between the components.
	 * @param cols Number of columns
	 * @param gapH Horizontal gap.
	 * @param gapV Vertical gap.
	 */
	public TableLayout(int cols, double gapH, double gapV) {
		super(gapH, gapV);
		if (cols <= 0) {
			throw new IllegalArgumentException("Invalid number of columns.");
		}
		this.cols = cols;
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
	 * Calculates the preferred dimensions for all columns and rows.
	 * @param container The container for which the dimension should be
	 *        calculated.
	 * @see #COLS
	 * @see #ROWS
	 */
	private Info[] getInfo(Container container) {
		Info[] infos = new Info[2];
		infos[COLS] = new Info();
		infos[ROWS] = new Info();

		infos[COLS].size = cols;
		infos[ROWS].size = (int) Math.ceil(container.size() / (double) cols);

		// Find out the preferred dimensions for each columns and row
		int compIndex = 0;
		for (Drawable component : container) {
			Integer col = compIndex%infos[COLS].size;
			Integer row = compIndex/infos[COLS].size;

			Double colWidth = infos[COLS].sizes.get(col);
			Double rowHeight = infos[ROWS].sizes.get(row);

			Dimension2D size = component.getPreferredSize();

			infos[COLS].sizes.put(col, max(size.getWidth(), colWidth));
			infos[ROWS].sizes.put(row, max(size.getHeight(), rowHeight));

			compIndex++;
		}

		// Calculate container specific variables
		Rectangle2D bounds = container.getBounds();
		Insets2D insets = container.getInsets();
		if (insets == null) {
			insets = new Insets2D.Double();
		}
		infos[COLS].insetsSum = insets.getLeft() + insets.getRight();
		infos[ROWS].insetsSum = insets.getTop() + insets.getBottom();
		infos[COLS].gapSum = Math.max((infos[COLS].size - 1)*getGapX(), 0.0);
		infos[ROWS].gapSum = Math.max((infos[ROWS].size - 1)*getGapY(), 0.0);
		double containerWidth =
			Math.max(bounds.getWidth() - infos[COLS].insetsSum - infos[COLS].gapSum, 0.0);
		double containerHeight =
			Math.max(bounds.getHeight() - infos[ROWS].insetsSum - infos[ROWS].gapSum, 0.0);
		infos[COLS].sizeMean = (infos[COLS].size > 0) ? containerWidth/infos[COLS].size : 0.0;
		infos[ROWS].sizeMean = (infos[ROWS].size > 0) ? containerHeight/infos[ROWS].size : 0.0;

		// Values for columns and rows
		for (Info info : infos) {
			info.sizeSum = 0.0;
			info.unsizeableSpace = 0.0;
			int sizeable = 0;
			for (double size : info.sizes.values()) {
				info.sizeSum += size;
				if (size >= info.sizeMean) {
					info.unsizeableSpace += size - info.sizeMean;
				} else {
					sizeable++;
				}
			}
			if (sizeable > 0) {
				info.unsizeableSpace /= sizeable;
			}
		}

		return infos;
	}

	/**
	 * Arranges the components of the specified container according to this
	 * layout.
	 * @param container Container to be laid out.
	 */
	public void layout(Container container) {
		Info[] infos = getInfo(container);

		Rectangle2D bounds = container.getBounds();
		Insets2D insets = container.getInsets();
		if (insets == null) {
			insets = new Insets2D.Double();
		}
		Integer lastCol = infos[COLS].size - 1;

		int compIndex = 0;
		double x = bounds.getX() + insets.getLeft();
		double y = bounds.getY() + insets.getTop();
		for (Drawable component : container) {
			Integer col = compIndex%infos[COLS].size;
			Integer row = compIndex/infos[COLS].size;

			double colWidth = infos[COLS].sizes.get(col);
			double rowHeight = infos[ROWS].sizes.get(row);

			double w = Math.max(infos[COLS].sizeMean - infos[COLS].unsizeableSpace, colWidth);
			double h = Math.max(infos[ROWS].sizeMean - infos[ROWS].unsizeableSpace, rowHeight);

			if (component != null) {
				component.setBounds(x, y, w, h);
			}

			if (col.equals(lastCol)) {
				x = bounds.getX() + insets.getLeft();
				y += h + getGapY();
			} else {
				x += w + getGapX();
			}

			compIndex++;
		}
	}

	/**
	 * Returns the preferred size of the specified container using this layout.
	 * @param container Container whose preferred size is to be returned.
	 * @return Preferred extent of the specified container.
	 */
	public Dimension2D getPreferredSize(Container container) {
		Info[] infos = getInfo(container);

		return new de.erichseifert.gral.graphics.Dimension2D.Double(
			infos[COLS].sizeSum + infos[COLS].gapSum + infos[COLS].insetsSum,
			infos[ROWS].sizeSum + infos[ROWS].gapSum + infos[ROWS].insetsSum
		);
	}

	/**
	 * Returns the number of desired columns.
	 * @return Number of desired columns.
	 */
	public int getColumns() {
		return cols;
	}

	/**
	 * Returns the value that is larger. If both are equal the first value will
	 * be returned.
	 * @param <T> Data type for the values.
	 * @param a First value.
	 * @param b Second value.
	 * @return Larger value.
	 */
	private static <T extends Comparable<T>> T max(T a, T b) {
		if (a == null || b == null) {
			if (a == null) {
				return b;
			} else {
				return a;
			}
		}
		if (a.compareTo(b) >= 0) {
			return a;
		}
		return b;
	}
}
