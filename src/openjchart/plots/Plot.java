package openjchart.plots;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.border.Border;

import openjchart.Drawable;
import openjchart.data.DataListener;
import openjchart.data.DataSource;
import openjchart.plots.axes.Axis;
import openjchart.util.SettingChangeEvent;
import openjchart.util.Settings;
import openjchart.util.SettingsListener;
import openjchart.util.SettingsStorage;

public abstract class Plot extends JPanel implements SettingsStorage, DataListener, SettingsListener {
	public static final String KEY_TITLE = "plot.title";
	public static final String KEY_BACKGROUND = "plot.background";
	public static final String KEY_BORDER = "plot.border";
	public static final String KEY_ANTIALISING = "plot.antialiasing";
	public static final String KEY_PLOTAREA_BACKGROUND = "plot.plotarea.background";
	public static final String KEY_PLOTAREA_BORDER = "plot.plotarea.border";

	private final Settings settings;

	private final Map<String, Axis> axes;
	private final Map<String, Drawable> axisDrawables;
	private final List<Drawable> components;
	private PlotLayout layout;

	private Label title;

	public Plot() {
		axes = new HashMap<String, Axis>();
		axisDrawables = new HashMap<String, Drawable>();
		components = new ArrayList<Drawable>();
		settings = new Settings(this);
		layout = new PlotLayout();
		title = new Label("");
		title.setSetting(Label.KEY_FONT, new Font("Arial", Font.BOLD, 18));
		add(title, PlotLayout.NORTH);
		setSettingDefault(KEY_TITLE, null);
		setSettingDefault(KEY_BACKGROUND, null);
		setSettingDefault(KEY_BORDER, null);
		setSettingDefault(KEY_ANTIALISING, true);
		setSettingDefault(KEY_PLOTAREA_BACKGROUND, Color.WHITE);
		setSettingDefault(KEY_PLOTAREA_BORDER, new BasicStroke(1f));
		setOpaque(false);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				this.<Boolean>getSetting(KEY_ANTIALISING) ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);

		Paint bg = getSetting(KEY_BACKGROUND);
		if (bg != null) {
			AffineTransform txOrig = g2d.getTransform();
			g2d.translate(getX(), getY());
			g2d.scale(getWidth(), getHeight());

			Paint paintOld = g2d.getPaint();
			g2d.setPaint(bg);
			g2d.fill(new Rectangle2D.Double(0.0, 0.0, 1.0, 1.0));
			g2d.setPaint(paintOld);

			g2d.setTransform(txOrig);
		}

		Stroke borderStroke = getSetting(KEY_BORDER);
		if (borderStroke != null) {
			Stroke strokeOld = g2d.getStroke();
			g2d.setStroke(borderStroke);
			g2d.draw(getBounds());
			g2d.setStroke(strokeOld);
		}
		
		drawComponents(g2d);
	}

	protected void drawComponents(Graphics2D g2d) {
		for (Drawable component : components) {
			component.draw(g2d);
		}
	}

	protected void drawAxes(Graphics2D g2d) {
		for (Drawable d : axisDrawables.values()) {
			d.draw(g2d);
		}
	}

	@Override
	public Insets getInsets() {
		Border border = getBorder();
		if (border != null) {
			return border.getBorderInsets(this);
		}

		return new Insets(0, 0, 0, 0);
	}

	public Axis getAxis(String name) {
		return axes.get(name);
	}

	public void setAxis(String name, Axis axis, Drawable drawable) {
		if (axis == null) {
			removeAxis(name);
		}
		axes.put(name, axis);
		axisDrawables.put(name, drawable);
	}

	public void add(Drawable drawable, String anchor) {
		components.add(drawable);
		layout.add(drawable, anchor);
	}

	public void remove(Drawable drawable) {
		components.remove(drawable);
		layout.remove(drawable);
	}

	public void removeAxis(String name) {
		axes.remove(name);
	}

	@Override
	public <T> T getSetting(String key) {
		return settings.<T>get(key);
	}

	@Override
	public <T> void setSetting(String key, T value) {
		settings.<T>set(key, value);
		if (KEY_TITLE.equals(key) && value != null) {
			title.setText((String) value);
		}
	}

	@Override
	public <T> void removeSetting(String key) {
		settings.remove(key);
	}

	@Override
	public <T> void setSettingDefault(String key, T value) {
		settings.set(key, value);
		if (KEY_TITLE.equals(key) && value != null) {
			title.setText((String) value);
		}
	}

	@Override
	public <T> void removeSettingDefault(String key) {
		settings.removeDefault(key);
	}

	@Override
	public void dataChanged(DataSource data) {
	}

	@Override
	public void settingChanged(SettingChangeEvent event) {
	}

	public Label getTitle() {
		return title;
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		layout.layout(getBounds(), getInsets());
	}
}
