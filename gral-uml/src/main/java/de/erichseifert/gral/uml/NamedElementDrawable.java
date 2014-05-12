/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2014 Erich Seifert <dev[at]erichseifert.de>,
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
package de.erichseifert.gral.uml;

import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawableContainer;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.graphics.EditableLabel;
import de.erichseifert.gral.graphics.Label;
import de.erichseifert.gral.graphics.StackedLayout;
import de.erichseifert.gral.util.Orientation;
import metamodel.classes.kernel.NamedElement;

public abstract class NamedElementDrawable extends DrawableContainer {
	private final EditableLabel name;
	private boolean nameVisible;

	public NamedElementDrawable(NamedElement namedElement, boolean nameVisible) {
		super(new StackedLayout(Orientation.HORIZONTAL, 0, 0));
		name = new EditableLabel(namedElement.getName());
		add(name);
		this.nameVisible = nameVisible;
	}

	public NamedElementDrawable(NamedElement namedElement) {
		this(namedElement, true);
	}

	@Override
	public void drawComponents(DrawingContext context) {
		for (Drawable d : this) {
			if (d.equals(name) && !isNameVisible()) {
				continue;
			}
			d.draw(context);
		}
	}

	public boolean isNameVisible() {
		return nameVisible;
	}

	public void setNameVisible(boolean nameVisible) {
		this.nameVisible = nameVisible;
	}

	public Label getName() {
		return name;
	}
}
