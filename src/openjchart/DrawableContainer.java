package openjchart;
import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import openjchart.util.Insets2D;


public class DrawableContainer extends AbstractDrawable implements Container {
	private Insets2D insets = new Insets2D.Double();
	private Layout layout;
	private final List<Drawable> components;
	private final Map<Drawable, Object> constraints;

	public DrawableContainer() {
		this(null);
	}

	public DrawableContainer(Layout layout) {
		components = new ArrayList<Drawable>();
		constraints = new HashMap<Drawable, Object>();
		this.layout = layout;
	}

	@Override
	public void draw(Graphics2D g2d) {
		drawComponents(g2d);
	}
	
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
		doLayout();
	}

	@Override
	public void remove(Drawable drawable) {
		components.remove(drawable);
		constraints.remove(drawable);
		doLayout();
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
	
	public void setInsets(Insets2D insets) {
		if (insets == this.insets) {
			return;
		}
		this.insets.setInsets(insets);
		doLayout();
	}

	@Override
	public Layout getLayout() {
		return layout;
	}

	public void setLayout(Layout layout) {
		this.layout = layout;
		doLayout();
	}
	
	protected void doLayout() {
		if (layout != null) {
			layout.layout(this);
		}
	}

	@Override
	public Iterator<Drawable> iterator() {
		return components.iterator();
	}

	@Override
	public void setBounds(double x, double y, double width, double height) {
		super.setBounds(x, y, width, height);
		doLayout();
	}

	@Override
	public Dimension2D getPreferredSize() {
		if (getLayout() != null) {
			return getLayout().getPreferredSize(this);
		}
		return super.getPreferredSize();
	}
}
