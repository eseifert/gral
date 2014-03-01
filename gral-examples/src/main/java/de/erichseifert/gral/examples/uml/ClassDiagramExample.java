package de.erichseifert.gral.examples.uml;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawableContainer;
import de.erichseifert.gral.ui.DrawablePanel;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.uml.AssociationRenderer;
import de.erichseifert.gral.uml.ClassDiagramRenderer;
import de.erichseifert.gral.uml.ClassRenderer;
import de.erichseifert.gral.uml.PackageRenderer;
import metamodel.classes.kernel.Package;
import metamodel.examples.TypesPackage;
import metamodel.examples.WindowClass;

public class ClassDiagramExample extends JFrame {
	private final ClassDiagramRenderer classDiagramRenderer;

	public ClassDiagramExample() {
		super("Class diagram");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setSize(1024, 768);

		classDiagramRenderer = new ClassDiagramRenderer();
		DrawableContainer classDiagram = (DrawableContainer) classDiagramRenderer.getRendererComponent();
		classDiagram.setBounds(0, 0, 1000, 700);
		metamodel.classes.kernel.Package defaultPackage = new Package("Default");
		metamodel.classes.kernel.Class abstractWindow = defaultPackage.addClass("AbstractWindow");
		WindowClass windowClass = new WindowClass(defaultPackage);
		windowClass.specializes(abstractWindow);
		ClassRenderer classRenderer = new ClassRenderer();
		Drawable abstractWindowDrawable = classRenderer.getRendererComponent(abstractWindow);
		Dimension2D preferredSize = abstractWindowDrawable.getPreferredSize();
		abstractWindowDrawable.setBounds(new Rectangle2D.Double(0.0, 0.0, preferredSize.getWidth(), preferredSize.getHeight()));
		classDiagram.add(abstractWindowDrawable);

		Drawable windowDrawable = classRenderer.getRendererComponent(windowClass);
		windowDrawable.setBounds(new Rectangle2D.Double(50.0, 300.0, 150.0, 200.0));
		classDiagram.add(windowDrawable);

		AssociationRenderer associationRenderer = classDiagramRenderer.getAssociationRenderer();
		classDiagram.add(associationRenderer.getRendererComponent(abstractWindowDrawable, windowDrawable));

		Package typesPackage = new TypesPackage();
		PackageRenderer packageRenderer = new PackageRenderer();
		packageRenderer.setMembersVisible(true);
		packageRenderer.setNameVisible(true);;
		Drawable typesPacakgeDrawable = packageRenderer.getRendererComponent(typesPackage);
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
