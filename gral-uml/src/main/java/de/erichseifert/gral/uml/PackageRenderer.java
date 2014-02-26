package de.erichseifert.gral.uml;

import de.erichseifert.gral.graphics.Drawable;

public class PackageRenderer {
	public Drawable getRendererComponent(metamodel.classes.kernel.Package pkg) {
		return new PackageDrawable(pkg);
	}
}
