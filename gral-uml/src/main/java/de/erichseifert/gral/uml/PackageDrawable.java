package de.erichseifert.gral.uml;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawableContainer;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.graphics.Label;
import de.erichseifert.gral.graphics.StackedLayout;
import de.erichseifert.gral.navigation.Navigable;
import de.erichseifert.gral.navigation.Navigator;
import de.erichseifert.gral.uml.navigation.DrawableContainerNavigator;
import de.erichseifert.gral.util.Insets2D;
import de.erichseifert.gral.util.Orientation;
import metamodel.classes.kernel.Class;
import metamodel.classes.kernel.NamedElement;
import metamodel.classes.kernel.Package;

/**
 * Represents a drawable that displays a package in UML class diagrams.
 */
public class PackageDrawable extends DrawableContainer {
	private final Package pkg;

	private final Tab tab;
	private final Body body;

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

	public static class Body extends DrawableContainer implements Navigable {
		private final Label name;
		// TODO: Make stroke serializable
		private Stroke borderStroke;
		private boolean membersDisplayed;
		private final Navigator navigator;

		public Body(Package pkg) {
			name = new Label(pkg.getName());
			borderStroke = new BasicStroke(1f);

			navigator = new DrawableContainerNavigator(this);
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
		protected void drawComponents(DrawingContext context) {
			Graphics2D g2d = context.getGraphics();
			AffineTransform txOld = g2d.getTransform();
			double zoom = navigator.getZoom();

			Point2D origin = navigator.getCenter().getPoint2D();
			g2d.scale(zoom, zoom);
			g2d.translate(-origin.getX(), -origin.getY());
			super.drawComponents(context);
			g2d.setTransform(txOld);
		}

		@Override
		public Dimension2D getPreferredSize() {
			Dimension2D preferredSize = name.getPreferredSize();
			Insets2D insets = getInsets();
			preferredSize.setSize(
					preferredSize.getWidth() + insets.getLeft() + insets.getRight(),
					preferredSize.getHeight() + insets.getTop() + insets.getBottom()
			);

			if (membersDisplayed) {
				double width = 0.0;
				double height = 0.0;
				for (Drawable drawable : getDrawables()) {
					Rectangle2D bounds = drawable.getBounds();
					width += bounds.getWidth();
					height += bounds.getHeight();
				}
				return new de.erichseifert.gral.util.Dimension2D.Double(
					Math.max(width, preferredSize.getWidth()),
					Math.max(height, preferredSize.getHeight())
				);
			}
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

		@Override
		public Navigator getNavigator() {
			return navigator;
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
		StackedLayout.Constraints layoutConstraints = new StackedLayout.Constraints(false, 0.0, 0.5);
		add(tab, layoutConstraints);
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

	public Body getBody() {
		return body;
	}

	public void setBorderStroke(Stroke borderStroke) {
		getTab().setBorderStroke(borderStroke);
		getBody().setBorderStroke(borderStroke);
	}
}
