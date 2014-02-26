package de.erichseifert.gral.examples.uml;

import javax.swing.JFrame;

import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.ui.DrawablePanel;
import de.erichseifert.gral.uml.ClassRenderer;
import metamodel.classes.kernel.Package;
import metamodel.examples.WindowClass;

public class ClassExample extends JFrame {

	public ClassExample() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(800, 600);

		// Example taken from UML 2.4.1 superstructure
		Package defaultPackage = new Package("Default");
		WindowClass windowClass = new WindowClass(defaultPackage);

		ClassRenderer classRenderer = new ClassRenderer();
		Drawable classDrawable = classRenderer.getRendererComponent(windowClass);
		getContentPane().add(new DrawablePanel(classDrawable));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ClassExample().setVisible(true);
	}
}
