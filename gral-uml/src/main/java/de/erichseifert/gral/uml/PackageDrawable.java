package de.erichseifert.gral.uml;

import java.awt.Graphics2D;

import metamodel.classes.kernel.Class;
import metamodel.classes.kernel.NamedElement;
import metamodel.classes.kernel.Package;
import de.erichseifert.gral.graphics.DrawableContainer;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.graphics.StackedLayout;
import de.erichseifert.gral.plots.Label;
import de.erichseifert.gral.util.Orientation;

/**
 * Represents a drawable that displays a package in UML class diagrams.
 */
public class PackageDrawable extends DrawableContainer {
	private final Package pkg;

	private final Tab tab;
	private final Body body;

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
				}
			}
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

		tab = new Tab(pkg);
		tab.setNameVisible(false);
		add(tab);
		body = new Body(pkg);
		add(body);
		// TODO Add support for package URI
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
}
