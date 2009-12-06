package openjchart;
import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import openjchart.util.Insets2D;

/**
 * Implementation of Container that is a Drawable itself.
 * It takes care of the layout and the insets and stores Drawables.
 */
public class DrawableContainer extends AbstractDrawable implements Container {
	private Insets2D insets = new Insets2D.Double();
	private Layout layout;
	private final List<Drawable> components;
	private final Map<Drawable, Object> constraints;

	/**
	 * Creates a new container for Drawables without layout manager.
	 */
	public DrawableContainer() {
		this(null);
	}

	/**
	 * Creates a new container for Drawables with the specified layout manager.
	 * @param layout Layout manager to be set.
	 */
	public DrawableContainer(Layout layout) {
		components = new LinkedList<Drawable>();
		constraints = new HashMap<Drawable, Object>();
		this.layout = layout;
	}

	@Override
	public void draw(Graphics2D g2d) {
		drawComponents(g2d);
	}

	/**
	 * Invokes the draw method of each Drawable.
	 * @param g2d Graphics object used for drawing.
	 */
	protected void drawComponents(Graphics2D g2d) {
		for (Drawable d : this) {
			d.draw(g2d);
		}
	}

	@Override
	public void add(Drawable drawable) {
		add(drawable, null);
	}

	@Override
	public void add(Drawable drawable, Object constraints) {
		components.add(drawable);
		this.constraints.put(drawable, constraints);
		layout();
	}

	@Override
	public void remove(Drawable drawable) {
		components.remove(drawable);
		constraints.remove(drawable);
		layout();
	}

	@Override
	public Object getConstraints(Drawable drawable) {
		return constraints.get(drawable);
	}

	@Override
	public Insets2D getInsets() {
		Insets2D insets = new Insets2D.Double();
		insets.setInsets(this.insets);
		return insets;
	}

	@Override
	public void setInsets(Insets2D insets) {
		if (insets == this.insets) {
			return;
		}
		this.insets.setInsets(insets);
		layout();
	}

	@Override
	public Layout getLayout() {
		return layout;
	}

	@Override
	public void setLayout(Layout layout) {
		this.layout = layout;
		layout();
	}

	/**
	 * Recalculates this container's layout
	 */
	protected void layout() {
		if (layout != null) {
			layout.layout(this);
		}
	}

	@Override
	public Iterator<Drawable> iterator() {
		return components.iterator();
	}

	@Override
	public int size() {
		return components.size();
	}

	@Override
	public void setBounds(Rectangle2D bounds) {
		super.setBounds(bounds);
		layout();
	}

	@Override
	public void setBounds(double x, double y, double width, double height) {
		super.setBounds(x, y, width, height);
		layout();
	}

	@Override
	public Dimension2D getPreferredSize() {
		if (getLayout() != null) {
			return getLayout().getPreferredSize(this);
		}
		return super.getPreferredSize();
	}

}
