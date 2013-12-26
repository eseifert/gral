package de.erichseifert.gral.examples.uml;

import javax.swing.JFrame;

import metamodel.classes.kernel.Package;
import de.erichseifert.gral.ui.DrawablePanel;
import de.erichseifert.gral.uml.PackageDrawable;

public class ClassExample extends JFrame {

	public ClassExample() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(800, 600);

		// Example taken from UML 2.4.1 superstructure
		Package defaultPackage = new Package("Default");
		defaultPackage.addClass("Window");

		PackageDrawable packageDrawable = new PackageDrawable(defaultPackage);
		getContentPane().add(new DrawablePanel(packageDrawable));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ClassExample().setVisible(true);
	}
}
