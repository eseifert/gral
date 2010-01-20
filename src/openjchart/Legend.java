/* OpenJChart : a free plotting library for the Java(tm) platform
 *
 * (C) Copyright 2009, by Erich Seifert and Michael Seifert.
 *
 * This file is part of OpenJChart.
 *
 * OpenJChart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenJChart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenJChart.  If not, see <http://www.gnu.org/licenses/>.
 */

package openjchart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Dimension2D;
import java.util.HashMap;
import java.util.Map;

import openjchart.DrawableConstants.Location;
import openjchart.DrawableConstants.Orientation;
import openjchart.data.DataSource;
import openjchart.plots.Label;
import openjchart.util.GraphicsUtils;
import openjchart.util.Insets2D;
import openjchart.util.SettingChangeEvent;
import openjchart.util.Settings;
import openjchart.util.SettingsListener;
import openjchart.util.SettingsStorage;

/**
 * Abstract class that serves as a basic for any legend in a plot.
 * It provides an inner Item class which is responsible for
 * displaying a specific DataSource.
 * The functionality includes:
 * <ul>
 * <li>Storing and retrieving settings</li>
 * <li>Adding and removing DataSources</li>
 * </ul>
 */
public abstract class Legend extends DrawableContainer implements SettingsStorage, SettingsListener {
	/** Key for specifying the {@link java.awt.Paint} instance to be used to paint the background. */
	public static final String KEY_BACKGROUND = "legend.background";
	/** Key for specifying the {@link java.awt.Stroke} instance to be used to paint the border of the legend. */
	public static final String KEY_BORDER = "legend.border";
	/** Key for specifying the orientation of the legend using a {@link openjchart.DrawableConstants.Orientation} value. */
	public static final String KEY_ORIENTATION = "legend.orientation";
	/** Key for specifying the gap between items. */
	public static final String KEY_GAP = "legend.gap";
	/** Key for specifying the gap between items. */
	public static final String KEY_SYMBOL_SIZE = "legend.symbol.size";

	private final Settings settings;

	private final Map<DataSource, Drawable> components;

	/**
	 * Class that displays a specific DataSource as an item of a Legend.
	 */
	protected class Item extends DrawableContainer {
		private final DataSource data;
		private final Drawable symbol;
		private final Label label;

		/**
		 * Creates a new Item object with the specified DataSource and text.
		 * @param data DataSource to be displayed.
		 * @param labelText Description text.
		 */
		public Item(final DataSource data, final String labelText) {
			super(new EdgeLayout(10.0, 0.0));
			this.data = data;

			symbol = new AbstractDrawable() {
				@Override
				public void draw(Graphics2D g2d) {
					drawSymbol(g2d, this, Item.this.data);
				}

				@Override
				public Dimension2D getPreferredSize() {
					final double fontSize = 10.0;  // TODO: Use real font size
					Dimension2D symbolSize = Legend.this.getSetting(KEY_SYMBOL_SIZE);
					Dimension2D size = super.getPreferredSize();
					size.setSize(symbolSize.getWidth()*fontSize, symbolSize.getHeight()*fontSize);
					return size;
				}
			};
			label = new Label(labelText);
			label.setSetting(Label.KEY_ALIGNMENT_X, 0.0);
			label.setSetting(Label.KEY_ALIGNMENT_Y, 0.5);

			add(symbol, Location.WEST);
			add(label, Location.CENTER);
		}

		@Override
		public Dimension2D getPreferredSize() {
			return getLayout().getPreferredSize(this);
		}

		/**
		 * Returns the displayed DataSource.
		 * @return Displayed DataSource
		 */
		public DataSource getData() {
			return data;
		}
	}

	/**
	 * Creates a new Legend object with default background color, border,
	 * orientation and gap between the Items.
	 */
	public Legend() {
		components = new HashMap<DataSource, Drawable>();
		setInsets(new Insets2D.Double(10.0));

		settings = new Settings(this);
		setSettingDefault(KEY_BACKGROUND, Color.WHITE);
		setSettingDefault(KEY_BORDER, new BasicStroke(1f));
		setSettingDefault(KEY_ORIENTATION, Orientation.VERTICAL);
		setSettingDefault(KEY_GAP, new openjchart.util.Dimension2D.Double(20.0, 5.0));
		setSettingDefault(KEY_SYMBOL_SIZE, new openjchart.util.Dimension2D.Double(2.0, 2.0));
	}

	@Override
	public void draw(Graphics2D g2d) {
		drawBackground(g2d);
		drawBorder(g2d);
		drawComponents(g2d);
	}

	/**
	 * Draws the background of this Legend with the specified Graphics2D
	 * object.
	 * @param g2d Graphics object to draw with.
	 */
	protected void drawBackground(Graphics2D g2d) {
		Paint bg = getSetting(KEY_BACKGROUND);
		if (bg != null) {
			GraphicsUtils.fillPaintedShape(g2d, getBounds(), bg, null);
		}
	}

	/**
	 * Draws the border of this Legend with the specified Graphics2D
	 * object.
	 * @param g2d Graphics object to draw with.
	 */
	protected void drawBorder(Graphics2D g2d) {
		Stroke borderStroke = getSetting(KEY_BORDER);
		if (borderStroke != null) {
			Stroke strokeOld = g2d.getStroke();
			g2d.setStroke(borderStroke);
			g2d.draw(getBounds());
			g2d.setStroke(strokeOld);
		}
	}

	protected abstract void drawSymbol(Graphics2D g2d, Drawable symbol, DataSource data);

	/**
	 * Adds the specified DataSource in order to display it.
	 * @param source DataSource to be added.
	 */
	public void add(DataSource source) {
		Item item = new Item(source, source.toString());
		add(item);
		components.put(source, item);
	}

	/**
	 * Returns whether the specified DataSource was added to the legend.
	 * @param source Data source
	 * @return <code>true</code> if legend contains the data source, otherwise <code>false</code>
	 */
	public boolean contains(DataSource source) {
		return components.containsKey(source);
	}

	/**
	 * Removes the specified DataSource.
	 * @param source DataSource to be removed.
	 */
	public void remove(DataSource source) {
		Drawable removeItem = components.get(source);
		if (removeItem != null) {
			remove(removeItem);
		}
		components.remove(source);
	}

	/**
	 * Invoked if data has changed.
	 */
	protected void notifyDataChanged() {
		// FIXME: is this function needed?
		layout();
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
			Dimension2D gap = getSetting(KEY_GAP);
			Layout layout = new StackedLayout(orientation, gap);
			setLayout(layout);
		}
	}

}
