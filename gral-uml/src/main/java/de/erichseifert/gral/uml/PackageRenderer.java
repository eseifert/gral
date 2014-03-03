package de.erichseifert.gral.uml;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawableContainer;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.graphics.NavigableDrawableContainer;
import de.erichseifert.gral.graphics.StackedLayout;
import de.erichseifert.gral.navigation.DrawableContainerNavigator;
import de.erichseifert.gral.util.Insets2D;
import de.erichseifert.gral.util.Orientation;
import de.erichseifert.gral.util.PointND;
import metamodel.classes.kernel.Class;
import metamodel.classes.kernel.NamedElement;
import metamodel.classes.kernel.Package;

public class PackageRenderer {
	protected static class Tab extends NamedElementDrawable {
		// TODO: Stroke should be serializable
		private final Stroke borderStroke;
		private final Insets2D insets;

		public Tab(metamodel.classes.kernel.Package pkg, Stroke borderStroke) {
			super(pkg);
			float fontSize = getName().getFont().getSize2D();
			insets = new Insets2D.Double(fontSize/2f, fontSize, fontSize/2f, fontSize);
			this.borderStroke = borderStroke;
		}

		@Override
		public void draw(DrawingContext context) {
			Graphics2D g2d = context.getGraphics();
			Stroke strokeOld = g2d.getStroke();
			g2d.setStroke(borderStroke);
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
	}

	protected static class Body extends NavigableDrawableContainer {
		private final Stroke borderStroke;

		public Body(Package pkg, PackageRenderer packageRenderer) {
			this.borderStroke = packageRenderer.getBorderStroke();
			ClassRenderer classRenderer = packageRenderer.getClassRenderer();
			if (packageRenderer.isMembersVisible()) {
				for (NamedElement member : pkg.getOwnedMembers()) {
					Drawable drawable = null;
					if (member instanceof metamodel.classes.kernel.Class) {
						drawable = classRenderer.getRendererComponent((Class) member);
					} else if (member instanceof Package) {
						drawable = packageRenderer.getRendererComponent((Package) member);
					}
					if (drawable != null) {
						Dimension2D preferredSize = drawable.getPreferredSize();
						Point2D bodyPos = new Point2D.Double(getX(), getY());
						PointND<? extends Number> posNew = ((DrawableContainerNavigator) getNavigator()).toWorldCoordinates(bodyPos, getNavigator().getZoom());
						drawable.setBounds(posNew.get(0).doubleValue(), posNew.get(1).doubleValue(), preferredSize.getWidth(), preferredSize.getHeight());
						add(drawable);
					}
				}
			}

			// TODO: Use proper reference size
			double textHeight = 12.0;
			setInsets(new Insets2D.Double(textHeight));
		}

		@Override
		public void draw(DrawingContext context) {
			Graphics2D g2d = context.getGraphics();
			Stroke strokeOld = g2d.getStroke();
			g2d.setStroke(borderStroke);
			g2d.draw(getBounds());
			g2d.setStroke(strokeOld);

			drawComponents(context);
		}

		@Override
		public Dimension2D getPreferredSize() {
			Insets2D insets = getInsets();
			double width = 0.0;
			double height = 0.0;
			for (Drawable drawable : getDrawables()) {
				Rectangle2D bounds = drawable.getBounds();
				width += bounds.getWidth();
				height += bounds.getHeight();
			}
			// Scale the preferred size according to the current zoom
			double zoom = getNavigator().getZoom();
			width *= zoom;
			height *= zoom;
			return new de.erichseifert.gral.util.Dimension2D.Double(
				Math.max(width, insets.getHorizontal()),
				Math.max(height, insets.getVertical())
			);
		}

		@Override
		public void setBounds(double x, double y, double width, double height) {
			double deltaX = x - getX();
			double deltaY = y - getY();
			super.setBounds(x, y, width, height);
			for (Drawable drawable : getDrawables()) {
				// TODO: Body should not be dependent on a Navigator of type DrawableContainerNavigator
				DrawableContainerNavigator navigator = (DrawableContainerNavigator) getNavigator();
				// TODO: Coordinate transformation should use DrawableContainerNavigator.toWorldCoordinates()
				double drawableXNew = drawable.getX() + deltaX/navigator.getZoom();
				double drawableYNew = drawable.getY() + deltaY/navigator.getZoom();
				drawable.setPosition(drawableXNew, drawableYNew);
			}
		}
	}

	protected static class PackageDrawable extends DrawableContainer {
		private final Package pkg;
		private final PackageRenderer packageRenderer;
		private final Tab tab;
		private final Body body;

		public PackageDrawable(metamodel.classes.kernel.Package pkg, PackageRenderer packageRenderer) {
			super(new StackedLayout(Orientation.VERTICAL));
			this.pkg = pkg;
			this.packageRenderer = packageRenderer;
			tab = new Tab(pkg, packageRenderer.getBorderStroke());
			tab.setNameVisible(packageRenderer.isNameVisible());
			StackedLayout.Constraints layoutConstraints = new StackedLayout.Constraints(false, 0.0, 0.5);
			add(tab, layoutConstraints);
			body = new Body(pkg, packageRenderer);
			add(body);
			// TODO Add support for package URI
		}

		public Tab getTab() {
			return tab;
		}

		public Body getBody() {
			return body;
		}

		public Package getPackage() {
			return pkg;
		}

		public PackageRenderer getPackageRenderer() {
			return packageRenderer;
		}
	}

	private boolean membersVisible;
	private boolean nameVisible;
	// TODO: Make stroke serializable
	private Stroke borderStroke;
	private ClassRenderer classRenderer;

	public PackageRenderer() {
		borderStroke = new BasicStroke(1f);
		classRenderer = new ClassRenderer();
	}

	public Drawable getRendererComponent(metamodel.classes.kernel.Package pkg) {
		return new PackageDrawable(pkg, this);
	}

	/**
	 * Returns whether or not the members of the package are displayed.
	 * @return {@code true} if the members are shown, {@code false} otherwise.
	 */
	public boolean isMembersVisible() {
		return membersVisible;
	}

	/**
	 * Sets the display behaviour for package members to the specified value.
	 * @param membersVisible Tells whether or not the package members should be displayed.
	 */
	public void setMembersVisible(boolean membersVisible) {
		this.membersVisible = membersVisible;
	}

	public boolean isNameVisible() {
		return nameVisible;
	}

	public void setNameVisible(boolean nameVisible) {
		this.nameVisible = nameVisible;
	}

	public Stroke getBorderStroke() {
		return borderStroke;
	}

	public void setBorderStroke(Stroke borderStroke) {
		this.borderStroke = borderStroke;
	}

	public ClassRenderer getClassRenderer() {
		return classRenderer;
	}

	public void setClassRenderer(ClassRenderer classRenderer) {
		this.classRenderer = classRenderer;
	}
}
