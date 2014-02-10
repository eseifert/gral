package de.erichseifert.gral.examples.uml;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import de.erichseifert.gral.ui.DrawablePanel;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.uml.ClassDiagram;
import de.erichseifert.gral.uml.ClassDrawable;
import metamodel.classes.kernel.Package;
import metamodel.examples.WindowClass;

public class ClassDiagramExample extends JFrame {
	private final ClassDiagram classDiagram;

	public ClassDiagramExample() {
		super("Class diagram");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setSize(1024, 768);

		classDiagram = new ClassDiagram();
		metamodel.classes.kernel.Package defaultPackage = new Package("Default");
		WindowClass windowClass = new WindowClass(defaultPackage);
		classDiagram.add(new ClassDrawable(windowClass));
		classDiagram.setLayout(null);

		DrawablePanel panel = new InteractivePanel(classDiagram);
		getContentPane().add(panel);
	}

	public static void main(String[] args) {
		ClassDiagramExample example = new ClassDiagramExample();
		example.setVisible(true);
	}
}
