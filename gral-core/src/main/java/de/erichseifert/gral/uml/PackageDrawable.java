package de.erichseifert.gral.uml;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import metamodel.classes.Package;
import de.erichseifert.gral.graphics.DrawableContainer;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.plots.Label;

/**
 * Represents a drawable that displays a package in UML class diagrams.
 */
public class PackageDrawable extends DrawableContainer {
	private final Package p;
	private final Label name;

	private boolean membersDisplayed;

	/**
	 * Creates a drawable used to display the specified package.
	 * @param pkg Package to be displayed.
	 */
	public PackageDrawable(Package pkg) {
		this.p = pkg;
		name = new Label(pkg.getName());
		// TODO Add support for package URI
	}

	@Override
	public void draw(DrawingContext context) {
		Graphics2D g2d = context.getGraphics();

		double textHeight = name.getTextRectangle().getHeight();
		double textWidth = name.getTextRectangle().getWidth();

		// Draw tab
		Rectangle2D tab = new Rectangle2D.Double(
			0.0, 0.0,
			textWidth + textHeight*2.0, textHeight*2.0
		);
		g2d.draw(tab);

		// Draw outer frame
		Rectangle2D frame = new Rectangle2D.Double(
			0, tab.getHeight(),
			textWidth + textHeight*5.0, textHeight*5.0
		);
		g2d.draw(frame);

		// Draw package name
		if (membersDisplayed) {
			name.setBounds(tab);
		}
		name.setBounds(frame);
		name.draw(context);
	}

	/**
	 * Returns the displayed package.
	 * @return Displayed Package.
	 */
	public Package getPackage() {
		return p;
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
	}
}
