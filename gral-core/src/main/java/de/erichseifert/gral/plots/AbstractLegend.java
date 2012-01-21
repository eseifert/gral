/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2012 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael[at]erichseifert.de>
 *
 * This file is part of GRAL.
 *
 * GRAL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GRAL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GRAL.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.erichseifert.gral.plots;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Dimension2D;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import de.erichseifert.gral.data.DataChangeEvent;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.Row;
import de.erichseifert.gral.graphics.AbstractDrawable;
import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawableContainer;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.graphics.EdgeLayout;
import de.erichseifert.gral.graphics.Layout;
import de.erichseifert.gral.graphics.StackedLayout;
import de.erichseifert.gral.plots.settings.Key;
import de.erichseifert.gral.plots.settings.SettingChangeEvent;
import de.erichseifert.gral.plots.settings.SettingsStorage;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.Insets2D;
import de.erichseifert.gral.util.Location;
import de.erichseifert.gral.util.Orientation;


/**
 * <p>Abstract class that serves as a base for legends in plots.
 * It stores a list of of items that are used to display a symbol and label for
 * each (visible) data source.</p>
 * <p>Like other elements legends can be styled using various settings. The
 * settings are used to control to control how the legend, and its items
 * are displayed. The actual rendering of symbols has to be implemented by
 * derived classes.</p>
 */
public abstract class AbstractLegend extends StylableContainer
		implements Legend, LegendSymbolRenderer {
	/** Version id for serialization. */
	private static final long serialVersionUID = -1561976879958765700L;

	/** Mapping of data sources to drawable components. */
	private final Map<Row, Drawable> components;

	/**
	 * An abstract base class for drawable symbols.
	 */
	protected static abstract class AbstractSymbol extends AbstractDrawable {
		/** Version id for serialization. */
		private static final long serialVersionUID = 7475404103140652668L;

		/** Settings for determining the visual of the symbol. */
		private final SettingsStorage settings;

		/**
		 * Initializes a new instances.
		 * @param settings Settings for determining the appearance of the symbol.
		 */
		public AbstractSymbol(SettingsStorage settings) {
			this.settings = settings;
		}

		@Override
		public Dimension2D getPreferredSize() {
			double fontSize = settings.<Font>getSetting(FONT).getSize2D();
			Dimension2D symbolSize = settings.getSetting(SYMBOL_SIZE);
			Dimension2D size = super.getPreferredSize();
			size.setSize(symbolSize.getWidth()*fontSize,
				symbolSize.getHeight()*fontSize);
			return size;
		}
	}

	/**
	 * Class that displays a specific data source as an item of a legend.
	 */
	protected static class Item extends DrawableContainer {
		/** Version id for serialization. */
		private static final long serialVersionUID = 3401141040936913098L;

		/** Data source that is related to this item. */
		private final Row row;
		/** Symbol that should be drawn. */
		private final Drawable symbol;
		/** Label string that should be drawn. */
		private final Label label;

		/**
		 * Creates a new Item object with the specified data source and text.
		 * @param row Data row to be displayed.
		 * @param symbolRenderer Renderer for the symbol.
		 * @param labelText Description text.
		 * @param font Font for the description text.
		 */
		public Item(Row row, LegendSymbolRenderer symbolRenderer,
				String labelText, Font font) {
			double fontSize = font.getSize2D();
			setLayout(new EdgeLayout(fontSize, 0.0));

			this.row = row;

			symbol = symbolRenderer.getSymbol(row);
			add(symbol, Location.WEST);

			label = new Label(labelText);
			label.setSetting(Label.FONT, font);
			label.setSetting(Label.ALIGNMENT_X, 0.0);
			label.setSetting(Label.ALIGNMENT_Y, 0.5);
			add(label, Location.CENTER);
		}

		/**
		 * Returns the row that is displayed by this item.
		 * @return Displayed data row.
		 */
		public Row getRow() {
			return row;
		}
	}

	/**
	 * Initializes a new instance with a default background color, a border,
	 * vertical orientation and a gap between the items. The default alignment
	 * is set to top-left.
	 */
	public AbstractLegend() {
		setInsets(new Insets2D.Double(10.0));

		components = new HashMap<Row, Drawable>();

		setSettingDefault(BACKGROUND, Color.WHITE);
		setSettingDefault(BORDER, new BasicStroke(1f));
		setSettingDefault(FONT, Font.decode(null));
		setSettingDefault(COLOR, Color.BLACK);
		setSettingDefault(ORIENTATION, Orientation.VERTICAL);
		setSettingDefault(ALIGNMENT_X, 0.0);
		setSettingDefault(ALIGNMENT_Y, 0.0);
		setSettingDefault(GAP, new de.erichseifert.gral.util.Dimension2D.Double(2.0, 0.5));
		setSettingDefault(SYMBOL_SIZE, new de.erichseifert.gral.util.Dimension2D.Double(2.0, 2.0));
	}

	/**
	 * Draws the {@code Drawable} with the specified drawing context.
	 * @param context Environment used for drawing.
	 */
	@Override
	public void draw(DrawingContext context) {
		drawBackground(context);
		drawBorder(context);
		drawComponents(context);
	}

	/**
	 * Draws the background of this legend with the specified drawing context.
	 * @param context Environment used for drawing.
	 */
	protected void drawBackground(DrawingContext context) {
		Paint bg = getSetting(BACKGROUND);
		if (bg != null) {
			GraphicsUtils.fillPaintedShape(
				context.getGraphics(), getBounds(), bg, null);
		}
	}

	/**
	 * Draws the border of this legend with the specified drawing context.
	 * @param context Environment used for drawing.
	 */
	protected void drawBorder(DrawingContext context) {
		Stroke stroke = getSetting(BORDER);
		if (stroke != null) {
			Paint fg = getSetting(COLOR);
			GraphicsUtils.drawPaintedShape(
				context.getGraphics(), getBounds(), fg, null, stroke);
		}
	}

	/**
	 * Returns a sequence of items for the specified data source that should be
	 * added to the legend.
	 * @param source Data source.
	 * @return A sequence of items for the specified data source.
	 */
	protected abstract Iterable<Row> getEntries(DataSource source);

	/**
	 * Returns the label text for the specified row.
	 * @param row Data row.
	 * @return Label text.
	 */
	protected abstract String getLabel(Row row);

	/**
	 * Adds the specified data source in order to display it.
	 * @param source data source to be added.
	 */
	public void add(DataSource source) {
		for (Row row : getEntries(source)) {
			String label = getLabel(row);
			Font font = this.<Font>getSetting(FONT);
			Item item = new Item(row, this, label, font);
			add(item);
			components.put(row, item);
		}
	}

	/**
	 * Returns whether the specified data source was added to the legend.
	 * @param source Data source
	 * @return {@code true} if legend contains the data source, otherwise {@code false}
	 */
	public boolean contains(DataSource source) {
		for (Row row : getEntries(source)) {
			if (components.containsKey(row)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Removes the specified data source.
	 * @param source Data source to be removed.
	 */
	public void remove(DataSource source) {
		for (Row row : getEntries(source)) {
			Drawable item = components.remove(row);
			if (item != null) {
				remove(item);
			}
		}
	}

	/**
	 * Removes all data sources from the legend.
	 */
	public void clear() {
		for (Drawable item : components.values()) {
			remove(item);
		}
		components.clear();
	}

	/**
	 * Re-creates the items for all data sources stored  in this legend.
	 */
	protected void rebuildItems() {
		Set<DataSource> sources = new LinkedHashSet<DataSource>();
		for (Row row : components.keySet()) {
			sources.add(row.getSource());
		}
		clear();
		for (DataSource source : sources) {
			add(source);
		}
	}

	/**
	 * Notify all listeners that data has changed.
	 * @param events Event objects describing the values that have changed.
	 */
	protected void notifyDataChanged(DataChangeEvent... events) {
		// FIXME Is refreshing layout really necessary?
		layout();
	}

	/**
	 * Invoked if a setting has changed.
	 * @param event Event containing information about the changed setting.
	 */
	@Override
	public void settingChanged(SettingChangeEvent event) {
		Key key = event.getKey();
		if (ORIENTATION.equals(key) || GAP.equals(key)) {
			Orientation orientation = getSetting(ORIENTATION);
			Dimension2D gap = getSetting(GAP);
			if (GAP.equals(key) && gap != null) {
				double fontSize = this.<Font>getSetting(FONT).getSize2D();
				gap.setSize(gap.getWidth()*fontSize, gap.getHeight()*fontSize);
			}
			Layout layout = new StackedLayout(orientation, gap);
			setLayout(layout);
		} else if (FONT.equals(key)) {
			for (Drawable item : components.values()) {
				if (!(item instanceof Item)) {
					continue;
				}
				((Item) item).label.setSetting(Label.FONT,
					this.<Font>getSetting(FONT));
			}
		}
	}

	@Override
	public void setBounds(double x, double y, double width, double height) {
		Dimension2D size = getPreferredSize();
		double alignX = this.<Number>getSetting(ALIGNMENT_X).doubleValue();
		double alignY = this.<Number>getSetting(ALIGNMENT_Y).doubleValue();
		super.setBounds(
			x + alignX*(width - size.getWidth()),
			y + alignY*(height - size.getHeight()),
			size.getWidth(),
			size.getHeight()
		);
	}
}
