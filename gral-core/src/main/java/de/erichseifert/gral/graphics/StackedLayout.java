/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2013 Erich Seifert <dev[at]erichseifert.de>,
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
package de.erichseifert.gral.graphics;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import de.erichseifert.gral.util.Insets2D;
import de.erichseifert.gral.util.Orientation;


/**
 * Class that represents a layout manager which arranges its components
 * as horizontal or vertical stacks.
 */
public class StackedLayout implements Layout {
	/** Version id for serialization. */
	private static final long serialVersionUID = -3183337606556363756L;

	/** Orientation in which elements should be laid out. */
	private final Orientation orientation;
	/** Spacing of components. */
	private final Dimension2D gap;
	/** Horizontal alignment of smaller components. */
	private double alignmentX;
	/** Vertical alignment of smaller components. */
	private double alignmentY;
	/**
	 * Whether components are strechted to the container's width (vertical layout)
	 * or height (horizontal layout).
	 */
	private boolean componentsStrechted;

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
	 * and gap between the components.
	 * @param orientation Orientation in which components are stacked.
	 * @param gap Gap between the components.
	 */
	public StackedLayout(Orientation orientation, Dimension2D gap) {
		this(orientation, gap, true);
	}

	/**
	 * Creates a new StackedLayout object with the specified orientation,
	 * gap between the components, and stretching behaviour.
	 * @param orientation Orientation in which components are stacked.
	 * @param gap Gap between the components.
	 * @param componentsStrechted Whether components should be strechted to the size of the container.
	 */
	public StackedLayout(Orientation orientation, Dimension2D gap, boolean componentsStrechted) {
		this.orientation = orientation;
		this.gap = new de.erichseifert.gral.util.Dimension2D.Double();
		if (gap != null) {
			this.gap.setSize(gap);
		}
		this.alignmentX = 0.5;
		this.alignmentY = 0.5;
		this.componentsStrechted = componentsStrechted;
	}

	/**
	 * Arranges the components of the specified container according to this
	 * layout.
	 * @param container Container to be laid out.
	 */
	public void layout(Container container) {
		Dimension2D size = getPreferredSize(container);
		Rectangle2D bounds = container.getBounds();
		Insets2D insets = container.getInsets();
		Dimension2D gap = getGap();

		double xMin = bounds.getMinX() + insets.getLeft();
		double yMin = bounds.getMinY() + insets.getTop();
		double width = bounds.getWidth() - insets.getLeft() - insets.getRight();
		double height = bounds.getHeight() - insets.getTop() - insets.getBottom();
		int count = 0;
		if (getOrientation() == Orientation.HORIZONTAL) {
			xMin += Math.max(bounds.getWidth() - size.getWidth(), 0.0)*getAlignmentX();
			for (Drawable component : container) {
				if (count++ > 0) {
					xMin += gap.getWidth();
				}
				Dimension2D compBounds = component.getPreferredSize();
				double componentHeight;
				double componentY;
				if (isComponentsStrechted()) {
					componentHeight = height;
					componentY = yMin;
				} else {
					componentHeight = Math.min(compBounds.getHeight(), height);
					componentY = yMin + (height - componentHeight)*getAlignmentY();
				}
				component.setBounds(xMin, componentY, compBounds.getWidth(), componentHeight);
				xMin += compBounds.getWidth();
			}
		} else if (getOrientation() == Orientation.VERTICAL) {
			yMin += Math.max(bounds.getHeight() - size.getHeight(), 0.0)*getAlignmentY();
			for (Drawable component : container) {
				if (count++ > 0) {
					yMin += gap.getHeight();
				}
				Dimension2D compBounds = component.getPreferredSize();
				double componentWidth;
				double componentX;
				if (isComponentsStrechted()) {
					componentWidth = width;
					componentX = xMin;
				} else {
					componentWidth = Math.min(compBounds.getWidth(), width);
					componentX = xMin + (width - componentWidth)*getAlignmentX();
				}
				component.setBounds(componentX, yMin, componentWidth, compBounds.getHeight());
				yMin += compBounds.getHeight();
			}
		}
	}

	/**
	 * Returns the preferred size of the specified container using this layout.
	 * @param container Container whose preferred size is to be returned.
	 * @return Preferred extent of the specified container.
	 */
	public Dimension2D getPreferredSize(Container container) {
		Insets2D insets = container.getInsets();
		Dimension2D gap = getGap();

		double width = insets.getLeft();
		double height = insets.getTop();
		int count = 0;
		if (getOrientation() == Orientation.HORIZONTAL) {
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
		} else if (getOrientation() == Orientation.VERTICAL) {
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

	/**
	 * Returns whether components are strechted to the container's width (vertical layout)
	 * or height (horizontal orientation).
	 * @return {@code true} if the layed out components should be strechted, {@code false} otherwise.
	 */
	public boolean isComponentsStrechted() {
		return componentsStrechted;
	}

	/**
	 * Sets whether the components should be stretched to the container's width (vertical layout)
	 * or height (horizontal layout).
	 * @param componentsStrechted {@code true} if the layed out components should be strechted, {@code false} otherwise.
	 */
	public void setComponentsStrechted(boolean componentsStrechted) {
		this.componentsStrechted = componentsStrechted;
	}

	/**
	 * Returns the relative horizontal position of the components within the container.
	 * This value only has effect, if the components do not fill the width of the container.
	 * @return Relative position of layed out components.
	 */
	public double getAlignmentX() {
		return alignmentX;
	}

	/**
	 * Sets the relative horizontal position of the components within the container.
	 * This value only has effect, if the components do not fill the width of the container.
	 * @param alignmentX Relative position of layed out components.
	 */
	public void setAlignmentX(double alignmentX) {
		this.alignmentX = alignmentX;
	}

	/**
	 * Returns the relative vertical position of the components within the container.
	 * This value only has effect, if the components do not fill the height of the container.
	 * @return Relative position of layed out components.
	 */
	public double getAlignmentY() {
		return alignmentY;
	}

	/**
	 * Sets the relative vertical position of the components within the container.
	 * This value only has effect, if the components do not fill the height of the container.
	 * @param alignmentY Relative position of layed out components.
	 */
	public void setAlignmentY(double alignmentY) {
		this.alignmentY = alignmentY;
	}

}
