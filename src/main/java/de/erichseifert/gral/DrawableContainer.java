/* GRAL : a free graphing library for the Java(tm) platform
 *
 * (C) Copyright 2009-2010, by Erich Seifert and Michael Seifert.
 *
 * This file is part of GRAL.
 *
 * GRAL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GRAL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GRAL.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.erichseifert.gral;
import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.erichseifert.gral.util.Insets2D;


/**
 * Implementation of Container that is a Drawable itself.
 * It takes care of the layout and the insets and stores Drawables.
 */
public class DrawableContainer extends AbstractDrawable implements Container {
	private Insets2D insets = new Insets2D.Double();
	private Layout layout;
	private final List<Drawable> components;
	private final Map<Drawable, Object> constraints;

	/**
	 * Creates a new container for Drawables without layout manager.
	 */
	public DrawableContainer() {
		this(null);
	}

	/**
	 * Creates a new container for Drawables with the specified layout manager.
	 * @param layout Layout manager to be set.
	 */
	public DrawableContainer(Layout layout) {
		components = new LinkedList<Drawable>();
		constraints = new HashMap<Drawable, Object>();
		this.layout = layout;
	}

	@Override
	public void draw(Graphics2D g2d) {
		drawComponents(g2d);
	}

	/**
	 * Invokes the draw method of each Drawable.
	 * @param g2d Graphics object used for drawing.
	 */
	protected void drawComponents(Graphics2D g2d) {
		for (Drawable d : this) {
			d.draw(g2d);
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
