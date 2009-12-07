package openjchart.plots;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import openjchart.Container;
import openjchart.Drawable;
import openjchart.DrawableContainer;
import openjchart.EdgeLayout;
import openjchart.Legend;
import openjchart.PlotArea2D;
import openjchart.DrawableConstants.Location;
import openjchart.data.DataSource;
import openjchart.plots.axes.Axis;
import openjchart.util.GraphicsUtils;
import openjchart.util.Insets2D;
import openjchart.util.SettingChangeEvent;
import openjchart.util.Settings;
import openjchart.util.SettingsListener;
import openjchart.util.SettingsStorage;

public abstract class Plot extends DrawableContainer implements SettingsStorage, SettingsListener {
	public static final String KEY_TITLE = "plot.title";
	public static final String KEY_BACKGROUND = "plot.background";
	public static final String KEY_BORDER = "plot.border";
	public static final String KEY_ANTIALISING = "plot.antialiasing";
	public static final String KEY_PLOTAREA_BACKGROUND = "plot.plotarea.background";
	public static final String KEY_PLOTAREA_BORDER = "plot.plotarea.border";
	public static final String KEY_LEGEND = "plot.legend";
	public static final String KEY_LEGEND_POSITION = "plot.legend.position";
	public static final String KEY_LEGEND_MARGIN = "plot.legend.margin";

	private final Settings settings;

	protected final List<DataSource> data;

	private final Map<String, Axis> axes;
	private final Map<String, Drawable> axisDrawables;

	private Label title;
	private PlotArea2D plotArea;
	private final Container legendContainer;
	private Legend legend;

	public Plot(DataSource... data) {
		super(new EdgeLayout(20.0, 20.0));

		title = new Label("");
		title.setSetting(Label.KEY_FONT, new Font("Arial", Font.BOLD, 18));

		legendContainer = new DrawableContainer(new EdgeLayout(0.0, 0.0));

		this.data = new LinkedList<DataSource>();
		axes = new HashMap<String, Axis>();
		axisDrawables = new HashMap<String, Drawable>();
		for (DataSource source : data) {
			this.data.add(source);
		}

		settings = new Settings(this);
		setSettingDefault(KEY_TITLE, null);
		setSettingDefault(KEY_BACKGROUND, null);
		setSettingDefault(KEY_BORDER, null);
		setSettingDefault(KEY_ANTIALISING, true);
		setSettingDefault(KEY_PLOTAREA_BACKGROUND, Color.WHITE);
		setSettingDefault(KEY_PLOTAREA_BORDER, new BasicStroke(1f));
		setSettingDefault(KEY_LEGEND, false);
		setSettingDefault(KEY_LEGEND_POSITION, Location.NORTH_WEST);
		setSettingDefault(KEY_LEGEND_MARGIN, new Insets2D.Double(20.0));

		add(title, Location.NORTH);
	}

	@Override
	public void draw(Graphics2D g2d) {
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				this.<Boolean>getSetting(KEY_ANTIALISING) ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);

		Paint bg = getSetting(KEY_BACKGROUND);
		if (bg != null) {
			GraphicsUtils.fillPaintedShape(g2d, getBounds(), bg, null);
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

	protected void drawAxes(Graphics2D g2d) {
		for (Drawable d : axisDrawables.values()) {
			d.draw(g2d);
		}
	}

	protected void drawLegend(Graphics2D g2d) {
		boolean isVisible = getSetting(KEY_LEGEND);
		if (!isVisible || legend == null) {
			return;
		}
		legend.draw(g2d);
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

	public void removeAxis(String name) {
		axes.remove(name);
	}

	protected PlotArea2D getPlotArea() {
		return plotArea;
	}

	protected void setPlotArea(PlotArea2D plotArea) {
		if (this.plotArea != null) {
			remove(this.plotArea);
		}
		this.plotArea = plotArea;
		if (this.plotArea != null) {
			add(this.plotArea, Location.CENTER);
		}
	}

	@Override
	public <T> T getSetting(String key) {
		return settings.<T>get(key);
	}

	@Override
	public <T> void setSetting(String key, T value) {
		settings.<T>set(key, value);
	}

	@Override
	public <T> void removeSetting(String key) {
		settings.remove(key);
	}

	@Override
	public <T> void setSettingDefault(String key, T value) {
		settings.set(key, value);
	}

	@Override
	public <T> void removeSettingDefault(String key) {
		settings.removeDefault(key);
	}

	@Override
	public void settingChanged(SettingChangeEvent event) {
		String key = event.getKey();
		if (KEY_TITLE.equals(key)) {
			String text = getSetting(KEY_TITLE);
			title.setText((text != null) ? text : "");
		} else if (KEY_PLOTAREA_BACKGROUND.equals(key)) {
			Paint bg = getSetting(KEY_PLOTAREA_BACKGROUND);
			if (plotArea != null) {
				plotArea.setSetting(PlotArea2D.KEY_BACKGROUND, bg);
			}
		} else if (KEY_PLOTAREA_BORDER.equals(key)) {
			Stroke border = getSetting(KEY_PLOTAREA_BORDER);
			if (plotArea != null) {
				plotArea.setSetting(PlotArea2D.KEY_BORDER, border);
			}
		} else if (KEY_LEGEND_POSITION.equals(key)) {
			Location constraints = getSetting(KEY_LEGEND_POSITION);
			if (legend != null) {
				legendContainer.remove(legend);
				legendContainer.add(legend, constraints);
			}
		} else if (KEY_LEGEND_MARGIN.equals(key)) {
			Insets2D margin = getSetting(KEY_LEGEND_MARGIN);
			legendContainer.setInsets(margin);
		}
	}

	public Label getTitle() {
		return title;
	}

	protected Container getLegendContainer() {
		return legendContainer;
	}

	public Legend getLegend() {
		return legend;
	}

	protected void setLegend(Legend legend) {
		if (this.legend != null) {
			legendContainer.remove(this.legend);
		}
		this.legend = legend;
		if (this.legend != null) {
			Location constraints = getSetting(KEY_LEGEND_POSITION);
			legendContainer.add(legend, constraints);
		}
	}
}
