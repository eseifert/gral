package de.erichseifert.gral.uml;

import de.erichseifert.gral.graphics.Drawable;

public class ClassDrawableRenderer {
	public Drawable getRendererComponent(metamodel.classes.kernel.Class clazz) {
		return new ClassDrawable(clazz);
	}
}
