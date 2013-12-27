package de.erichseifert.gral.examples.uml;

import javax.swing.JFrame;

import metamodel.classes.kernel.Package;
import metamodel.examples.WindowClass;
import de.erichseifert.gral.ui.DrawablePanel;
import de.erichseifert.gral.uml.ClassDrawable;

public class ClassExample extends JFrame {

	public ClassExample() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(800, 600);

		// Example taken from UML 2.4.1 superstructure
		Package defaultPackage = new Package("Default");
		WindowClass windowClass = new WindowClass(defaultPackage);

		ClassDrawable classDrawable = new ClassDrawable(windowClass);
		getContentPane().add(new DrawablePanel(classDrawable));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ClassExample().setVisible(true);
	}
}
