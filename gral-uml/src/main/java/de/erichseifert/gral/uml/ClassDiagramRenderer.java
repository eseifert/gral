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
import de.erichseifert.gral.graphics.NavigableDrawableContainer;

public class ClassDiagramRenderer {
	protected static class ClassDiagram extends NavigableDrawableContainer {
		public ClassDiagram() {
		}
	}

	private AssociationRenderer associationRenderer;

	public ClassDiagramRenderer() {
		associationRenderer = new AssociationRenderer();
	}

	public Drawable getRendererComponent() {
		return new ClassDiagram();
	}

	public AssociationRenderer getAssociationRenderer() {
		return associationRenderer;
	}

	public void setAssociationRenderer(AssociationRenderer associationRenderer) {
		this.associationRenderer = associationRenderer;
	}
}
