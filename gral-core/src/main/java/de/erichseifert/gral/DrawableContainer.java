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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.erichseifert.gral.util.Insets2D;


/**
 * Implementation of <code>Container</code> that is a <code>Drawable</code>
 * itself and stores instances of <code>Drawable</code> as components.
 * It takes care of laying out, managing insets for and painting the
 * components.
 *
 * @see Drawable
 * @see Container
 */
public class DrawableContainer extends AbstractDrawable implements Container {
	/** Empty margins that should be preserved around the contents of this
	    container. */
	private final Insets2D insets = new Insets2D.Double();
	/** Object that manages the layout of all container components. */
	private Layout layout;
	/** Elements stored in this container. */
	private final List<Drawable> components;
	/** Supplemental information for components, like layout constraints. */
	private final Map<Drawable, Object> constraints;

	/**
	 * Creates a new container for <code>Drawable</code>s without layout
	 * manager.
	 */
	public DrawableContainer() {
		this(null);
	}

	/**
	 * Creates a new container for <code>Drawable</code>s with the specified
	 * layout manager.
	 * @param layout Layout manager to be set.
	 */
	public DrawableContainer(Layout layout) {
		components = new LinkedList<Drawable>();
		constraints = new HashMap<Drawable, Object>();
		this.layout = layout;
	}

	@Override
	public void draw(DrawingContext context) {
		drawComponents(context);
	}

	/**
	 * Invokes the draw method of each <code>Drawable</code>.
	 * @param context Environment used for drawing.
	 */
	protected void drawComponents(DrawingContext context) {
		for (Drawable d : this) {
			d.draw(context);
		}
	}

	@Override
	public void add(Drawable drawable) {
		add(drawable, null);
	}

	@Override
	public void add(Drawable drawable, Object constraints) {
		components.add(drawable);
		this.constraints.put(drawable, constraints);
		layout();
	}

	@Override
	public void remove(Drawable drawable) {
		components.remove(drawable);
		constraints.remove(drawable);
		layout();
	}

	@Override
	public Object getConstraints(Drawable drawable) {
		return constraints.get(drawable);
	}

	@Override
	public Insets2D getInsets() {
		Insets2D insets = new Insets2D.Double();
		insets.setInsets(this.insets);
		return insets;
	}

	@Override
	public void setInsets(Insets2D insets) {
		if (insets == this.insets) {
			return;
		}
		this.insets.setInsets(insets);
		layout();
	}

	@Override
	public Layout getLayout() {
		return layout;
	}

	@Override
	public void setLayout(Layout layout) {
		this.layout = layout;
		layout();
	}

	/**
	 * Recalculates this container's layout.
	 */
	protected void layout() {
		if (layout != null) {
			layout.layout(this);
		}
	}

	@Override
	public Iterator<Drawable> iterator() {
		return components.iterator();
	}

	@Override
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
		if (getLayout() != null) {
			return getLayout().getPreferredSize(this);
		}
		return super.getPreferredSize();
	}

}
