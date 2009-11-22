package openjchart.plots;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
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
	public static final String KEY_BACKGROUND_COLOR = "plot.background.color";
	public static final String KEY_ANTIALISING = "plot.antialiasing";

	private final Settings settings;

	private final Map<String, Axis> axes;
	private final Map<String, Drawable> axisDrawables;
	private final List<Drawable> components;
	private PlotLayout layout;

	private Label title;

	public Plot() {
		this.axes = new HashMap<String, Axis>();
		this.axisDrawables = new HashMap<String, Drawable>();
		this.components = new ArrayList<Drawable>();
		this.settings = new Settings(this);
		this.layout = new PlotLayout();
		this.title = new Label("");
		this.title.setSetting(Label.KEY_FONT, new Font("Arial", Font.BOLD, 18));
		add(title, PlotLayout.NORTH);
		setSettingDefault(KEY_TITLE, null);
		setSettingDefault(KEY_BACKGROUND_COLOR, Color.WHITE);
		setSettingDefault(KEY_ANTIALISING, true);
		setBackground(this.<Color>getSetting(KEY_BACKGROUND_COLOR));
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				this.<Boolean>getSetting(KEY_ANTIALISING) ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);

		drawAxes(g2d);
		drawComponents(g2d);
	}

	protected void drawComponents(Graphics2D g2d) {
		// Draw components
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
