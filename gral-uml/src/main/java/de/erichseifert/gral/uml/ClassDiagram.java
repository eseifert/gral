package de.erichseifert.gral.uml;

import de.erichseifert.gral.graphics.DrawableContainer;
import de.erichseifert.gral.navigation.Navigable;
import de.erichseifert.gral.navigation.Navigator;

public class ClassDiagram extends DrawableContainer implements Navigable {
	private final Navigator navigator;

	public ClassDiagram() {
		navigator = new DrawableContainerNavigator(this);
	}

	@Override
	public Navigator getNavigator() {
		return navigator;
	}
}
