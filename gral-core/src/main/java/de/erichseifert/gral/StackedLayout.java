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
import de.erichseifert.gral.util.Orientation;


/**
 * Class that represents a layout manager which arranges its components
 * as horizontal or vertical stacks.
 */
public class StackedLayout implements Layout {
	/** Orientation in which elements should be laid out. */
	private final Orientation orientation;
	/** Spacing of components. */
	private final Dimension2D gap;
	/** Alignment of smaller components. */
	private final double alignment;

	/**
	 * Creates a new StackedLayout object with the specified orientation
	 * and default gap between the components.
	 * @param orientation Orientation in which components are stacked.
	 */
	public StackedLayout(Orientation orientation) {
		this(orientation, null);
	}

	/**
	 * Creates a new StackedLayout object with the specified orientation
	 * gap between the components.
	 * @param orientation Orientation in which components are stacked.
	 * @param gap Gap between the components.
	 */
	public StackedLayout(Orientation orientation, Dimension2D gap) {
		this.orientation = orientation;
		this.gap = new de.erichseifert.gral.util.Dimension2D.Double();
		if (gap != null) {
			this.gap.setSize(gap);
		}
		this.alignment = 0.5;
	}

	@Override
	public Dimension2D getPreferredSize(Container container) {
		Insets2D insets = container.getInsets();

		double width = insets.getLeft();
		double height = insets.getTop();
		int count = 0;
		if (Orientation.HORIZONTAL.equals(orientation)) {
			double h = 0.0;
			for (Drawable component : container) {
				if (count++ > 0) {
					width += gap.getWidth();
				}
				Dimension2D itemBounds = component.getPreferredSize();
				width += itemBounds.getWidth();
				h = Math.max(height, itemBounds.getHeight());
			}
			height += h;
		} else if (Orientation.VERTICAL.equals(orientation)) {
			double w = 0.0;
			for (Drawable component : container) {
				if (count++ > 0) {
					height += gap.getHeight();
				}
				Dimension2D itemBounds = component.getPreferredSize();
				w = Math.max(w, itemBounds.getWidth());
				height += itemBounds.getHeight();
			}
			width += w;
		}
		width += insets.getRight();
		height += insets.getBottom();

		Dimension2D bounds =
			new de.erichseifert.gral.util.Dimension2D.Double(width, height);
		return bounds;
	}

	@Override
	public void layout(Container container) {
		Dimension2D size = getPreferredSize(container);
		Rectangle2D bounds = container.getBounds();
		Insets2D insets = container.getInsets();

		double x = bounds.getMinX() + insets.getLeft();
		double y = bounds.getMinY() + insets.getTop();
		double width = bounds.getWidth() - insets.getLeft() - insets.getRight();
		double height = bounds.getHeight() - insets.getTop() - insets.getBottom();
		int count = 0;
		if (Orientation.HORIZONTAL.equals(orientation)) {
			x += Math.max(bounds.getWidth() - size.getWidth(), 0.0)*alignment;
			for (Drawable component : container) {
				if (count++ > 0) {
					x += gap.getWidth();
				}
				Dimension2D compBounds = component.getPreferredSize();
				component.setBounds(x, y, compBounds.getWidth(), height);
				x += compBounds.getWidth();
			}
		} else if (Orientation.VERTICAL.equals(orientation)) {
			y += Math.max(bounds.getHeight() - size.getHeight(), 0.0)*alignment;
			for (Drawable component : container) {
				if (count++ > 0) {
					y += gap.getHeight();
				}
				Dimension2D compBounds = component.getPreferredSize();
				component.setBounds(x, y, width, compBounds.getHeight());
				y += compBounds.getHeight();
			}
		}
	}

	/**
	 * Returns whether the components will be laid out horizontally or
	 * vertically.
	 * @return Orientation constant
	 */
	public Orientation getOrientation() {
		return orientation;
	}

	/**
	 * Returns the minimal space between components. No space will be allocated
	 * if there are no components.
	 * @return Horizontal and vertical gaps
	 */
	public Dimension2D getGap() {
		Dimension2D gap =
			new de.erichseifert.gral.util.Dimension2D.Double();
		gap.setSize(this.gap);
		return gap;
	}

}
