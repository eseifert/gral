/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2026 Erich Seifert <dev[at]erichseifert.de>,
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
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import de.erichseifert.gral.graphics.layout.Layout;


/**
 * <p>A {@link Drawable} that holds other drawables and arranges them with a
 * {@link Layout}. Drawing the container draws all of its components, in the
 * order they were added. This is the base class of
 * {@link de.erichseifert.gral.plots.AbstractPlot}, which is why a plot is a
 * container of a plot area, a title, a legend and the axis components.</p>
 *
 * <pre>
 * DrawableContainer container = new DrawableContainer(new EdgeLayout(5.0, 5.0));
 * container.setInsets(new Insets2D.Double(10.0));
 * // The kind of constraint depends on the layout; EdgeLayout expects a Location.
 * container.add(plot, Location.CENTER);
 * container.add(caption, Location.SOUTH);
 * container.setBounds(0.0, 0.0, 800.0, 600.0);
 * </pre>
 *
 * <p>The layout runs immediately whenever something that affects it changes: a
 * component is added or removed, the layout or the insets are replaced, or the
 * bounds change. Nothing is deferred, so components have valid bounds as soon
 * as they are added to a container that has a size. Without a layout,
 * components keep whatever bounds they were given.</p>
 *
 * <p>The {@link Insets2D} of the container are space the layout must keep free
 * at its edges. A container without a layout ignores them, and so does its
 * preferred size, which then falls back to the empty default of
 * {@link AbstractDrawable}.</p>
 *
 * @see Drawable
 * @see Container
 * @see Layout
 */
public class DrawableContainer extends AbstractDrawable implements Container {
	/** Version id for serialization. */
	private static final long serialVersionUID = 3741045651357559308L;

	/** Empty margins that should be preserved around the contents of this
	    container. */
	private final Insets2D insets;
	/** Object that manages the layout of all container components. */
	private Layout layout;
	/** Elements stored in this container. */
	private final Queue<Drawable> components;
	/** Supplemental information for components, like layout constraints. */
	private final Map<Drawable, Object> constraints;

	/**
	 * Creates an empty container without a layout manager. Components added to
	 * it keep the bounds they are given.
	 */
	public DrawableContainer() {
		this(null);
	}

	/**
	 * Creates an empty container that arranges its components with the
	 * specified layout manager.
	 * @param layout Layout manager to be set, or {@code null} for none.
	 */
	public DrawableContainer(Layout layout) {
		insets = new Insets2D.Double();
		components = new ConcurrentLinkedQueue<>();
		constraints = new HashMap<>();
		this.layout = layout;
	}

	/**
	 * Draws the {@code Drawable} with the specified drawing context.
	 * @param context Environment used for drawing.
	 */
	public void draw(DrawingContext context) {
		drawComponents(context);
	}

	/**
	 * Invokes the draw method of each {@code Drawable}.
	 * @param context Environment used for drawing.
	 */
	protected void drawComponents(DrawingContext context) {
		for (Drawable d : this) {
			d.draw(context);
		}
	}

	/**
	 * Adds a component without layout constraints, and lays the container out
	 * again.
	 * @param drawable Component
	 * @throws IllegalArgumentException if the container is added to itself.
	 */
	public void add(Drawable drawable) {
		add(drawable, null);
	}

	/**
	 * Adds a component with the specified layout constraints, and lays the
	 * container out again. What kind of object the constraints have to be is
	 * decided by the layout: {@link de.erichseifert.gral.graphics.layout.EdgeLayout}
	 * expects a {@link Location}, {@link de.erichseifert.gral.graphics.layout.StackedLayout}
	 * its own {@code Constraints} class, and others none at all.
	 * @param drawable Component
	 * @param constraints Additional information (e.g. for layout)
	 * @throws IllegalArgumentException if the container is added to itself.
	 */
	public void add(Drawable drawable, Object constraints) {
		if (drawable == this) {
			throw new IllegalArgumentException(
				"A container cannot be added to itself."); //$NON-NLS-1$
		}
		this.constraints.put(drawable, constraints);
		components.add(drawable);
		layout();
	}

	@Override
	public boolean contains(Drawable drawable) {
		return components.contains(drawable);
	}

	@Override
	public List<Drawable> getDrawablesAt(Point2D point) {
		return getDrawablesAt(this, point, new LinkedList<Drawable>());
	}

	@Override
	public List<Drawable> getDrawables() {
		/*
		 * TODO: Size of ArrayList can be different from the number of added components
		 * in concurrent environments.
		 */
		var drawableList = new ArrayList<Drawable>(components.size());
		drawableList.addAll(components);
		return drawableList;
	}

	private static List<Drawable> getDrawablesAt(Container container, Point2D point, LinkedList<Drawable> previousResults) {
		if (container instanceof Drawable && container.getBounds().contains(point)) {
			previousResults.addFirst((Drawable) container);
		}
		for (Drawable component : container) {
			// Check whether the point is in one of the child elements of the container
			if (component instanceof Container) {
				getDrawablesAt((Container) component, point, previousResults);
			} else if (component != null && component.getBounds().contains(point)) {
				previousResults.addFirst(component);
			}
		}
		return previousResults;
	}

	/**
	 * Return additional information on component
	 * @param drawable Component
	 * @return Information object or {@code null}
	 */
	public Object getConstraints(Drawable drawable) {
		return constraints.get(drawable);
	}

	/**
	 * Removes a component from this container.
	 * @param drawable Component
	 */
	public void remove(Drawable drawable) {
		components.remove(drawable);
		constraints.remove(drawable);
		layout();
	}

	/**
	 * Returns the space that this container must preserve at each of its
	 * edges.
	 * @return The insets of this DrawableContainer
	 */
	public Insets2D getInsets() {
		var insets = new Insets2D.Double();
		insets.setInsets(this.insets);
		return insets;
	}

	/**
	 * Sets the space that this container must preserve at each of its
	 * edges.
	 * @param insets Insets to be set.
	 */
	public void setInsets(Insets2D insets) {
		if (insets == this.insets || this.insets.equals(insets)) {
			return;
		}
		this.insets.setInsets(insets);
		layout();
	}

	/**
	 * Returns the layout associated with this container.
	 * @return Layout manager
	 */
	public Layout getLayout() {
		return layout;
	}

	/**
	 * Sets the layout associated with this container.
	 * @param layout Layout to be set.
	 */
	public void setLayout(Layout layout) {
		this.layout = layout;
		layout();
	}

	/**
	 * Recalculates this container's layout.
	 */
	public void layout() {
		Layout layout = getLayout();
		if (layout != null) {
			layout.layout(this);
		}
	}

    /**
     * Returns an iterator over the container's elements.
     *
     * @return an Iterator.
     */
	public Iterator<Drawable> iterator() {
		return components.iterator();
	}

	/**
	 * Returns the number of components that are stored in this container.
	 * @return total number of components
	 */
	public int size() {
		return components.size();
	}

	@Override
	public void setBounds(Rectangle2D bounds) {
		super.setBounds(bounds);
		layout();
	}

	@Override
	public void setBounds(double x, double y, double width, double height) {
		super.setBounds(x, y, width, height);
		layout();
	}

	@Override
	public Dimension2D getPreferredSize() {
		Layout layout = getLayout();
		if (layout != null) {
			return layout.getPreferredSize(this);
		}
		return super.getPreferredSize();
	}

}
