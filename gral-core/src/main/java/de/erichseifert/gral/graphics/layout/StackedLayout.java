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
import java.io.Serializable;

import de.erichseifert.gral.graphics.Container;
import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.Insets2D;
import de.erichseifert.gral.graphics.Orientation;


/**
 * Class that represents a layout manager which arranges its components
 * as horizontal or vertical stacks.
 */
public class StackedLayout extends AbstractOrientedLayout {
	/** Version id for serialization. */
	private static final long serialVersionUID = -3183337606556363756L;

	/** Default layout behaviour for components. */
	private final Constraints defaultConstraints;

	public static class Constraints implements Serializable {
		private static final long serialVersionUID = -3375316557720116460L;
		/**
		 * Whether the component is stretched to the container's width (vertical layout)
		 * or height (horizontal layout).
		 */
		private final boolean stretched;
		/** Horizontal alignment of the component. */
		private final double alignmentX;
		/** Vertical alignment of the component. */
		private final double alignmentY;

		public Constraints(boolean stretched, double alignmentX, double alignmentY) {
			this.stretched = stretched;
			this.alignmentX = alignmentX;
			this.alignmentY = alignmentY;
		}

		/**
		 * Returns whether the component is stretched to the container's width (vertical layout)
		 * or height (horizontal orientation).
		 * @return {@code true} if the layed out component should be stretched, {@code false} otherwise.
		 */
		public boolean isStretched() {
			return stretched;
		}

		/**
		 * Returns the relative horizontal position of the component within the container.
		 * This value only has effect, if the components do not fill the width of the container.
		 * @return Relative position of layed out components.
		 */
		public double getAlignmentX() {
			return alignmentX;
		}

		/**
		 * Returns the relative vertical position of the components within the container.
		 * This value only has effect, if the components do not fill the height of the container.
		 * @return Relative position of layed out components.
		 */
		public double getAlignmentY() {
			return alignmentY;
		}
	}

	/**
	 * Creates a new StackedLayout object with the specified orientation
	 * and default gap between the components.
	 * @param orientation Orientation in which components are stacked.
	 */
	public StackedLayout(Orientation orientation) {
		this(orientation, 0.0, 0.0);
	}

	/**
	 * Creates a new StackedLayout object with the specified orientation
	 * and gap between the components.
	 * @param orientation Orientation in which components are stacked.
	 * @param gapX Horizontal gap between the components.
	 * @param gapY Vertical gap between the components.
	 */
	public StackedLayout(Orientation orientation, double gapX, double gapY) {
		super(orientation, gapX, gapY);
		defaultConstraints = new Constraints(true, 0.5, 0.5);
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

		double xMin = bounds.getMinX() + insets.getLeft();
		double yMin = bounds.getMinY() + insets.getTop();
		double width = bounds.getWidth() - insets.getLeft() - insets.getRight();
		double height = bounds.getHeight() - insets.getTop() - insets.getBottom();
		int count = 0;
		if (getOrientation() == Orientation.HORIZONTAL) {
			xMin += Math.max(bounds.getWidth() - size.getWidth(), 0.0)*defaultConstraints.getAlignmentX();
			for (Drawable component : container) {
				if (count++ > 0) {
					xMin += getGapX();
				}
				Dimension2D compBounds = component.getPreferredSize();
				Constraints constraints = getConstraints(component, container);
				double componentHeight;
				double componentY;
				if (constraints.isStretched()) {
					componentHeight = height;
					componentY = yMin;
				} else {
					componentHeight = Math.min(compBounds.getHeight(), height);
					componentY = yMin + (height - componentHeight)*constraints.getAlignmentY();
				}
				component.setBounds(xMin, componentY, compBounds.getWidth(), componentHeight);
				xMin += compBounds.getWidth();
			}
		} else if (getOrientation() == Orientation.VERTICAL) {
			yMin += Math.max(bounds.getHeight() - size.getHeight(), 0.0)*defaultConstraints.getAlignmentY();
			for (Drawable component : container) {
				if (count++ > 0) {
					yMin += getGapY();
				}
				Dimension2D compBounds = component.getPreferredSize();
				Constraints constraints = getConstraints(component, container);
				double componentWidth;
				double componentX;
				if (constraints.isStretched()) {
					componentWidth = width;
					componentX = xMin;
				} else {
					componentWidth = Math.min(compBounds.getWidth(), width);
					componentX = xMin + (width - componentWidth)*constraints.getAlignmentX();
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

		double width = insets.getLeft();
		double height = insets.getTop();
		int count = 0;
		if (getOrientation() == Orientation.HORIZONTAL) {
			double h = 0.0;
			for (Drawable component : container) {
				if (count++ > 0) {
					width += getGapX();
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
					height += getGapY();
				}
				Dimension2D itemBounds = component.getPreferredSize();
				w = Math.max(w, itemBounds.getWidth());
				height += itemBounds.getHeight();
			}
			width += w;
		}
		width += insets.getRight();
		height += insets.getBottom();

		return new de.erichseifert.gral.graphics.Dimension2D.Double(width, height);
	}

	private Constraints getConstraints(Drawable component, Container container) {
		Object constraints = container.getConstraints(component);
		if (constraints == null || !(constraints instanceof Constraints)) {
			constraints = defaultConstraints;
		}
		return (Constraints) constraints;
	}
}
