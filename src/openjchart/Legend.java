package openjchart;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import openjchart.DrawableConstants.Location;
import openjchart.DrawableConstants.Orientation;
import openjchart.data.DataSeries;
import openjchart.data.DummyData;
import openjchart.data.Row;
import openjchart.plots.DataPoint2D;
import openjchart.plots.Label;
import openjchart.plots.lines.LineRenderer2D;
import openjchart.plots.shapes.ShapeRenderer;
import openjchart.util.GraphicsUtils;
import openjchart.util.SettingChangeEvent;
import openjchart.util.Settings;
import openjchart.util.SettingsListener;
import openjchart.util.SettingsStorage;

public class Legend extends DrawableContainer implements SettingsStorage, SettingsListener {
	public static final String KEY_BACKGROUND = "legend.background";
	public static final String KEY_BORDER = "legend.border";
	public static final String KEY_ORIENTATION = "legend.orientation";
	public static final String KEY_GAP = "legend.gap";

	private final Settings settings;

	private final Map<DataSeries, Drawable> dataToComponent;

	private static class Item extends DrawableContainer {
		private static final DummyData DUMMY_DATA = new DummyData(1, 1, 1.0);
		private final Drawable symbol;
		private final Label label;

		public Item(final String labelText, final ShapeRenderer shapeRenderer, final LineRenderer2D lineRenderer) {
			super(new EdgeLayout(10.0, 0.0));

			symbol = new AbstractDrawable() {
				@Override
				public void draw(Graphics2D g2d) {
					Row row = new Row(DUMMY_DATA, 0);
					Rectangle2D bounds = getBounds();

					DataPoint2D p1 = new DataPoint2D(
						new Point2D.Double(bounds.getMinX(), bounds.getCenterY()), null,
						null, null
					);
					DataPoint2D p2 = new DataPoint2D(
						new Point2D.Double(bounds.getCenterX(), bounds.getCenterY()), null,
						(shapeRenderer != null) ? shapeRenderer.getShapePath(row) : null, null
					);
					DataPoint2D p3 = new DataPoint2D(
						new Point2D.Double(bounds.getMaxX(), bounds.getCenterY()), null,
						null, null
					);

					if (lineRenderer != null) {
						lineRenderer.getLine(p1, p2).draw(g2d);
						lineRenderer.getLine(p2, p3).draw(g2d);
					}
					if (shapeRenderer != null) {
						Point2D pos = p2.getPosition();
						AffineTransform txOrig = g2d.getTransform();
						g2d.translate(pos.getX(), pos.getY());
						shapeRenderer.getShape(row).draw(g2d);
						g2d.setTransform(txOrig);
					}
				}

				@Override
				public Dimension2D getPreferredSize() {
					Dimension2D size = super.getPreferredSize();
					size.setSize(20.0, 20.0);
					return size;
				}
			};
			label = new Label(labelText);

			add(symbol, Location.CENTER);
			add(label, Location.EAST);
		}

		@Override
		public Dimension2D getPreferredSize() {
			return getLayout().getPreferredSize(this);
		}

	}
	
	public Legend() {
		dataToComponent = new HashMap<DataSeries, Drawable>();
		settings = new Settings(this);

		setSettingDefault(KEY_ORIENTATION, Orientation.VERTICAL);
		setSettingDefault(KEY_GAP, 10.0);
	}

	@Override
	public void draw(Graphics2D g2d) {
		drawBackground(g2d);
		drawBorder(g2d);
		drawComponents(g2d);
	}

	protected void drawBackground(Graphics2D g2d) {
		Paint bg = getSetting(KEY_BACKGROUND);
		if (bg != null) {
			GraphicsUtils.fillPaintedShape(g2d, getBounds(), bg, null);
		}
	}

	protected void drawBorder(Graphics2D g2d) {
		Stroke borderStroke = getSetting(KEY_BORDER);
		if (borderStroke != null) {
			Stroke strokeOld = g2d.getStroke();
			g2d.setStroke(borderStroke);
			g2d.draw(getBounds());
			g2d.setStroke(strokeOld);
		}
	}

	public void add(DataSeries series, String label, ShapeRenderer shapeRenderer, LineRenderer2D lineRenderer) {
		Item item = new Item(label, shapeRenderer, lineRenderer);
		add(item);
		dataToComponent.put(series, item);
	}

	public void remove(DataSeries series) {
		Drawable removeItem = dataToComponent.get(series);
		if (removeItem != null) {
			remove(removeItem);
		}
		dataToComponent.remove(series);
	}

	protected void notifyDataChanged() {
		doLayout();
	}

	@Override
	public <T> T getSetting(String key) {
		return settings.get(key);
	}

	@Override
	public <T> void removeSetting(String key) {
		settings.remove(key);
	}

	@Override
	public <T> void removeSettingDefault(String key) {
		settings.removeDefault(key);
	}

	@Override
	public <T> void setSetting(String key, T value) {
		settings.set(key, value);
	}

	@Override
	public <T> void setSettingDefault(String key, T value) {
		settings.setDefault(key, value);
	}

	@Override
	public void settingChanged(SettingChangeEvent event) {
		String key = event.getKey();
		if (KEY_ORIENTATION.equals(key) || KEY_GAP.equals(key)) {
			Orientation orientation = getSetting(KEY_ORIENTATION);
			Double gap = getSetting(KEY_GAP);
			Layout layout = new StackedLayout(orientation, (gap != null) ? gap : 0.0);
			setLayout(layout);
		}
	}

}
