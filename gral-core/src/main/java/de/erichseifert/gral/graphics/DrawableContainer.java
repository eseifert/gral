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
 * Implementation of {@code Container} that is a {@code Drawable}
 * itself and stores instances of {@code Drawable} as components.
 * It takes care of laying out, managing insets for and painting the
 * components.
 *
 * @see Drawable
 * @see Container
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
	 * Creates a new container for {@code Drawable}s without layout
	 * manager.
	 */
	public DrawableContainer() {
		this(null);
	}

	/**
	 * Creates a new container for {@code Drawable}s with the specified
	 * layout manager.
	 * @param layout Layout manager to be set.
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
	 * Adds a new component to this container.
	 * @param drawable Component
	 */
	public void add(Drawable drawable) {
		add(drawable, null);
	}

	/**
	 * Adds a new component to this container.
	 * @param drawable Component
	 * @param constraints Additional information (e.g. for layout)
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
		List<Drawable> drawableList = new ArrayList<>(components.size());
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
		Insets2D insets = new Insets2D.Double();
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
