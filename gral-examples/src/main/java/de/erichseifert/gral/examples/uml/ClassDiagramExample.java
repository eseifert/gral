package de.erichseifert.gral.examples.uml;

import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import de.erichseifert.gral.ui.DrawablePanel;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.uml.ClassDiagram;
import de.erichseifert.gral.uml.ClassDrawable;
import de.erichseifert.gral.uml.PackageDrawable;
import metamodel.classes.kernel.Package;
import metamodel.examples.TypesPackage;
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
		ClassDrawable windowClassDrawable1 = new ClassDrawable(windowClass);
		windowClassDrawable1.setBounds(new Rectangle2D.Double(0.0, 0.0, 100.0, 100.0));
		classDiagram.add(windowClassDrawable1);

		ClassDrawable windowClassDrawable2 = new ClassDrawable(windowClass);
		windowClassDrawable2.setBounds(new Rectangle2D.Double(50.0, 300.0, 150.0, 200.0));
		classDiagram.add(windowClassDrawable2);

		Package typesPackage = new TypesPackage();
		PackageDrawable typesPacakgeDrawable = new PackageDrawable(typesPackage);
		typesPacakgeDrawable.setBounds(new Rectangle2D.Double(300.0, 100.0, 150.0, 80.0));
		classDiagram.add(typesPacakgeDrawable);

		DrawablePanel panel = new InteractivePanel(classDiagram);
		getContentPane().add(panel);
	}

	public static void main(String[] args) {
		ClassDiagramExample example = new ClassDiagramExample();
		example.setVisible(true);
	}
}
