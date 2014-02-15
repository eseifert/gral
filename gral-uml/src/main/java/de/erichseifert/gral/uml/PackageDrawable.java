package de.erichseifert.gral.uml;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Dimension2D;

import de.erichseifert.gral.graphics.DrawableContainer;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.graphics.StackedLayout;
import de.erichseifert.gral.navigation.Navigable;
import de.erichseifert.gral.navigation.Navigator;
import de.erichseifert.gral.plots.Label;
import de.erichseifert.gral.util.Insets2D;
import de.erichseifert.gral.util.Orientation;
import metamodel.classes.kernel.Class;
import metamodel.classes.kernel.NamedElement;
import metamodel.classes.kernel.Package;

/**
 * Represents a drawable that displays a package in UML class diagrams.
 */
public class PackageDrawable extends DrawableContainer implements Navigable {
	private final Package pkg;

	private final Tab tab;
	private final Body body;

	private final Navigator navigator;

	public static class Tab extends NamedElementDrawable {
		private final Insets2D insets;
		// TODO: Make stroke serializable
		private Stroke borderStroke;

		public Tab(Package pkg) {
			super(pkg);
			float fontSize = getName().getFont().getSize2D();
			insets = new Insets2D.Double(fontSize/2f, fontSize, fontSize/2f, fontSize);
			borderStroke = new BasicStroke(1f);
		}

		@Override
		public void draw(DrawingContext context) {
			Graphics2D g2d = context.getGraphics();
			Stroke strokeOld = g2d.getStroke();
			g2d.setStroke(getBorderStroke());
			g2d.draw(getBounds());
			g2d.setStroke(strokeOld);

			super.draw(context);
		}

		public Insets2D getInsets() {
			Insets2D insetsCopy = new Insets2D.Double();
			insetsCopy.setInsets(this.insets);
			return insetsCopy;
		}

		public void setInsets(Insets2D insets) {
			this.insets.setInsets(insets);
		}

		@Override
		public Dimension2D getPreferredSize() {
			Dimension2D preferredSize = super.getPreferredSize();
			preferredSize.setSize(
				preferredSize.getWidth() + insets.getHorizontal(),
				preferredSize.getHeight() + insets.getVertical()
			);
			return preferredSize;
		}

		public Stroke getBorderStroke() {
			return borderStroke;
		}

		public void setBorderStroke(Stroke borderStroke) {
			this.borderStroke = borderStroke;
		}
	}

	public static class Body extends DrawableContainer {
		private final Label name;
		// TODO: Make stroke serializable
		private Stroke borderStroke;
		private boolean membersDisplayed;

		public Body(Package pkg) {
			super(new StackedLayout(Orientation.VERTICAL));
			name = new Label(pkg.getName());
			borderStroke = new BasicStroke(1f);

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
			Stroke strokeOld = g2d.getStroke();
			g2d.setStroke(getBorderStroke());
			g2d.draw(getBounds());
			g2d.setStroke(strokeOld);

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

		public Stroke getBorderStroke() {
			return borderStroke;
		}

		public void setBorderStroke(Stroke borderStroke) {
			this.borderStroke = borderStroke;
		}
	}

	/**
	 * Creates a drawable used to display the specified package.
	 * @param pkg Package to be displayed.
	 */
	public PackageDrawable(Package pkg) {
		super(new StackedLayout(Orientation.VERTICAL));

		this.pkg = pkg;
		navigator = new PackageDrawableNavigator(this);

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

	public Body getBody() {
		return body;
	}

	public void setBorderStroke(Stroke borderStroke) {
		getTab().setBorderStroke(borderStroke);
		getBody().setBorderStroke(borderStroke);
	}
}
