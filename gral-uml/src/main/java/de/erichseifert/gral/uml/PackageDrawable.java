package de.erichseifert.gral.uml;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

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
	private final Label name;

	private final Tab tab;
	private final Rectangle2D frame;

	private boolean membersDisplayed;

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

	/**
	 * Creates a drawable used to display the specified package.
	 * @param pkg Package to be displayed.
	 */
	public PackageDrawable(Package pkg) {
		super(new StackedLayout(Orientation.VERTICAL));

		this.pkg = pkg;
		name = new Label(pkg.getName());

		for (NamedElement member : pkg.getOwnedMembers()) {
			if (member instanceof metamodel.classes.kernel.Class) {
				add(new ClassDrawable((Class) member));
			}
		}

		Font font = name.getFont();
		double fontHeight = font.getSize2D();
		double textWidth = name.getPreferredSize().getWidth();
		double textHeight = name.getPreferredSize().getHeight();

		double frameWidth = textWidth + fontHeight*2.0;

		tab = new Tab(pkg);
		calculateTabSize();
		frame = new Rectangle2D.Double(
			0, tab.getHeight(),
			frameWidth, textHeight + fontHeight*2.0
		);

		// TODO Add support for package URI
	}

	protected final void calculateTabSize() {
		Font font = name.getFont();
		double fontHeight = font.getSize2D();
		double textWidth = name.getPreferredSize().getWidth();

		double frameWidth = textWidth + fontHeight*2.0;
		double tabWidth = 1.0/3.0*frameWidth;
		if (isMembersDisplayed()) {
			tabWidth = Math.min(textWidth, frameWidth);
		}
		tab.setBounds(
			0.0, 0.0,
			tabWidth, fontHeight*1.0
		);
	}

	@Override
	public void draw(DrawingContext context) {
		Graphics2D g2d = context.getGraphics();

		g2d.translate(getX(), getY());
		// Draw tab
		tab.draw(context);

		// Draw outer frame
		g2d.draw(frame);

		// Draw package name
		if (!isMembersDisplayed()) {
			name.setBounds(frame);
			name.draw(context);
		}
		g2d.translate(-getX(), -getY());
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
		return membersDisplayed;
	}

	/**
	 * Sets the display behaviour for package members to the specified value.
	 * @param membersDisplayed Tells whether or not the package members should be displayed.
	 */
	public void setMembersDisplayed(boolean membersDisplayed) {
		this.membersDisplayed = membersDisplayed;
		calculateTabSize();
	}

	@Override
	public Dimension2D getPreferredSize() {
		Dimension2D preferredSize = new de.erichseifert.gral.util.Dimension2D.Double(frame.getWidth(), tab.getHeight() + frame.getHeight());
		return preferredSize;
	}

	public Tab getTab() {
		return tab;
	}
}
