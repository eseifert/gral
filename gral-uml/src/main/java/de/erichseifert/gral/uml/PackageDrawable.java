package de.erichseifert.gral.uml;

import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;

import de.erichseifert.gral.navigation.Navigable;
import de.erichseifert.gral.navigation.Navigator;
import metamodel.classes.kernel.Class;
import metamodel.classes.kernel.NamedElement;
import metamodel.classes.kernel.Package;
import de.erichseifert.gral.graphics.DrawableContainer;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.graphics.Layout;
import de.erichseifert.gral.graphics.StackedLayout;
import de.erichseifert.gral.plots.Label;
import de.erichseifert.gral.util.Insets2D;
import de.erichseifert.gral.util.Orientation;

/**
 * Represents a drawable that displays a package in UML class diagrams.
 */
public class PackageDrawable extends DrawableContainer implements Navigable {
	private final Package pkg;

	private final Tab tab;
	private final Body body;

	private final Navigator navigator;

	public static class Tab extends NamedElementDrawable {

		public Tab(Package pkg) {
			super(pkg);
		}

		@Override
		public void draw(DrawingContext context) {
			Graphics2D g2d = context.getGraphics();
			g2d.draw(getBounds());

			super.draw(context);
		}
	}

	public static class Body extends DrawableContainer {
		private final Label name;
		private boolean membersDisplayed;

		public Body(Package pkg) {
			super(new StackedLayout(Orientation.VERTICAL));
			name = new Label(pkg.getName());

			for (NamedElement member : pkg.getOwnedMembers()) {
				if (member instanceof metamodel.classes.kernel.Class) {
					add(new ClassDrawable((Class) member));
				} else if (member instanceof Package) {
					add(new PackageDrawable((Package) member));
				}
			}

			double textHeight = name.getTextRectangle().getHeight();
			setInsets(new Insets2D.Double(textHeight));
		}

		@Override
		public void draw(DrawingContext context) {
			Graphics2D g2d = context.getGraphics();
			g2d.draw(getBounds());

			if (membersDisplayed) {
				drawComponents(context);
			} else {
				name.setBounds(getBounds());
				name.draw(context);
			}
		}

		@Override
		public Dimension2D getPreferredSize() {
			if (membersDisplayed) {
				return super.getPreferredSize();
			}
			Dimension2D preferredSize = name.getPreferredSize();
			Insets2D insets = getInsets();
			preferredSize.setSize(
				preferredSize.getWidth() + insets.getLeft() + insets.getRight(),
				preferredSize.getHeight() + insets.getTop() + insets.getBottom()
			);
			return preferredSize;
		}

		public boolean isMembersDisplayed() {
			return membersDisplayed;
		}

		public void setMembersDisplayed(boolean membersDisplayed) {
			this.membersDisplayed = membersDisplayed;
		}
	}

	/**
	 * Creates a drawable used to display the specified package.
	 * @param pkg Package to be displayed.
	 */
	public PackageDrawable(Package pkg) {
		super(new StackedLayout(Orientation.VERTICAL));

		this.pkg = pkg;
		navigator = new DrawableContainerNavigator(this);

		tab = new Tab(pkg);
		tab.setNameVisible(false);
		StackedLayout.Constraints layoutConstraints = new StackedLayout.Constraints(false, 0.0, 0.5);
		add(tab, layoutConstraints);
		body = new Body(pkg);
		add(body);
		// TODO Add support for package URI
	}

	public Navigator getNavigator() {
		return navigator;
	}

	/**
	 * Returns the displayed package.
	 * @return Displayed Package.
	 */
	public Package getPackage() {
		return pkg;
	}

	/**
	 * Returns whether or not the members of the package are displayed.
	 * @return {@code true} if the members are shown, {@code false} otherwise.
	 */
	public boolean isMembersDisplayed() {
		return body.isMembersDisplayed();
	}

	/**
	 * Sets the display behaviour for package members to the specified value.
	 * @param membersDisplayed Tells whether or not the package members should be displayed.
	 */
	public void setMembersDisplayed(boolean membersDisplayed) {
		body.setMembersDisplayed(membersDisplayed);
	}

	public Tab getTab() {
		return tab;
	}

	@Override
	public void setLayout(Layout layout) {
		body.setLayout(layout);
	}

	@Override
	public Layout getLayout() {
		if (body != null) {
			return body.getLayout();
		}
		return null;
	}
}
