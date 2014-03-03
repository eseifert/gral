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
