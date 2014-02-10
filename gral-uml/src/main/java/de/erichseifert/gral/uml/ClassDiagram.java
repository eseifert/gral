package de.erichseifert.gral.uml;

import de.erichseifert.gral.graphics.DrawableContainer;
import de.erichseifert.gral.graphics.TableLayout;
import de.erichseifert.gral.navigation.Navigable;
import de.erichseifert.gral.navigation.Navigator;

public class ClassDiagram extends DrawableContainer implements Navigable {
	private final Navigator navigator;

	public ClassDiagram() {
		super(new TableLayout(4));
		navigator = new ClassDiagramNavigator(this);
	}

	@Override
	public Navigator getNavigator() {
		return navigator;
	}
}
