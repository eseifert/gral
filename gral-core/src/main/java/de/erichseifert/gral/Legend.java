/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2011 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <michael.seifert[at]gmx.net>
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
package de.erichseifert.gral;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Dimension2D;
import java.util.HashMap;
import java.util.Map;

import de.erichseifert.gral.data.DataChangeEvent;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.plots.Label;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.Insets2D;
import de.erichseifert.gral.util.Orientation;
import de.erichseifert.gral.util.SettingChangeEvent;
import de.erichseifert.gral.util.SettingsListener;


/**
 * <p>Abstract class that serves as a base for legends in plots.
 * It stores a list of of items of type <code>Item</code> which are used to
 * display a symbol and label for each (visible) data source.</p>
 * <p>Like other elements legends can be styled using various settings. The
 * settings are used to control to control how the legend, and its items
 * are displayed. The actual rendering of symbols has to be implemented by
 * derived classes.</p>
 */
public abstract class Legend extends DrawableContainer
		implements SettingsListener {
	/** Key for specifying the {@link java.awt.Paint} instance to be used to
	 paint the background. */
	public static final Key BACKGROUND =
		new Key("legend.background"); //$NON-NLS-1$
	/** Key for specifying the {@link java.awt.Stroke} instance to be used to
	 paint the border of the legend. */
	public static final Key BORDER =
		new Key("legend.border"); //$NON-NLS-1$
	/** Key for specifying the {@link java.awt.Paint} instance to be used to
	 fill the border of the legend. */
	public static final Key COLOR =
		new Key("legend.color"); //$NON-NLS-1$
	/** Key for specifying the orientation of the legend using a
	 {@link de.erichseifert.gral.util.Orientation} value. */
	public static final Key ORIENTATION =
		new Key("legend.orientation"); //$NON-NLS-1$
	/** Key for specifying the gap between items. */
	public static final Key GAP =
		new Key("legend.gap"); //$NON-NLS-1$
	/** Key for specifying the gap between items. */
	public static final Key SYMBOL_SIZE =
		new Key("legend.symbol.size"); //$NON-NLS-1$

	/** Mapping of data sources to drawable components. */
	private final Map<DataSource, Drawable> components;

	/**
	 * Class that displays a specific data source as an item of a Legend.
	 */
	protected class Item extends DrawableContainer {
		/** Data source that is related to this item. */
		private final DataSource data;
		/** Symbol that should be drawn. */
		private final Drawable symbol;
		/** Label string that should be drawn. */
		private final Label label;

		/**
		 * Creates a new Item object with the specified data source and text.
		 * @param data Data source to be displayed.
		 * @param labelText Description text.
		 */
		public Item(final DataSource data, final String labelText) {
			super(new EdgeLayout(10.0, 0.0));
			this.data = data;

			symbol = new AbstractDrawable() {
				@Override
				public void draw(DrawingContext context) {
					drawSymbol(context, this, Item.this.data);
				}

				@Override
				public Dimension2D getPreferredSize() {
					// TODO Use real font size instead of fixed value
					final double fontSize = 10.0;
					Dimension2D symbolSize =
						Legend.this.getSetting(SYMBOL_SIZE);
					Dimension2D size = super.getPreferredSize();
					size.setSize(symbolSize.getWidth()*fontSize,
							symbolSize.getHeight()*fontSize);
					return size;
				}
			};
			label = new Label(labelText);
			label.setSetting(Label.ALIGNMENT_X, 0.0);
			label.setSetting(Label.ALIGNMENT_Y, 0.5);

			add(symbol, Location.WEST);
			add(label, Location.CENTER);
		}

		@Override
		public Dimension2D getPreferredSize() {
			return getLayout().getPreferredSize(this);
		}

		/**
		 * Returns the displayed data source.
		 * @return Displayed data source
		 */
		public DataSource getData() {
			return data;
		}
	}

	/**
	 * Initializes a new <code>Legend</code> instance with default background
	 * color, border, orientation and gap between the items.
	 */
	public Legend() {
		components = new HashMap<DataSource, Drawable>();
		setInsets(new Insets2D.Double(10.0));

		addSettingsListener(this);
		setSettingDefault(BACKGROUND, Color.WHITE);
		setSettingDefault(BORDER, new BasicStroke(1f));
		setSettingDefault(COLOR, Color.BLACK);
		setSettingDefault(ORIENTATION, Orientation.VERTICAL);
		setSettingDefault(GAP, new de.erichseifert.gral.util.Dimension2D.Double(20.0, 5.0));
		setSettingDefault(SYMBOL_SIZE, new de.erichseifert.gral.util.Dimension2D.Double(2.0, 2.0));
	}

	@Override
	public void draw(DrawingContext context) {
		drawBackground(context);
		drawBorder(context);
		drawComponents(context);
	}

	/**
	 * Draws the background of this Legend with the specified <code>Graphics2D</code>
	 * object.
	 * @param context Environment used for drawing.
	 */
	protected void drawBackground(DrawingContext context) {
		Paint bg = getSetting(BACKGROUND);
		if (bg != null) {
			GraphicsUtils.fillPaintedShape(context.getGraphics(), getBounds(), bg, null);
		}
	}

	/**
	 * Draws the border of this Legend with the specified <code>Graphics2D</code>
	 * object.
	 * @param context Environment used for drawing.
	 */
	protected void drawBorder(DrawingContext context) {
		Stroke stroke = getSetting(BORDER);
		if (stroke != null) {
			Paint fg = getSetting(COLOR);
			GraphicsUtils.drawPaintedShape(context.getGraphics(), getBounds(), fg, null, stroke);
		}
	}

	/**
	 * Draws the symbol of a certain data source.
	 * @param context Settings for drawing.
	 * @param symbol symbol to draw.
	 * @param data Data source.
	 */
	protected abstract void drawSymbol(
			DrawingContext context,
			Drawable symbol, DataSource data);

	/**
	 * Adds the specified data source in order to display it.
	 * @param source data source to be added.
	 */
	public void add(DataSource source) {
		Item item = new Item(source, source.toString());
		add(item);
		components.put(source, item);
	}

	/**
	 * Returns whether the specified data source was added to the legend.
	 * @param source Data source
	 * @return <code>true</code> if legend contains the data source, otherwise <code>false</code>
	 */
	public boolean contains(DataSource source) {
		return components.containsKey(source);
	}

	/**
	 * Removes the specified data source.
	 * @param source Data source to be removed.
	 */
	public void remove(DataSource source) {
		Drawable removeItem = components.get(source);
		if (removeItem != null) {
			remove(removeItem);
		}
		components.remove(source);
	}

	/**
	 * Removes all data sources from the legend.
	 */
	public void clear() {
		for (DataSource source : components.keySet()) {
			remove(source);
		}
	}

	/**
	 * Notify all listeners that data has changed.
	 * @param events Event objects describing the values that have changed.
	 */
	protected void notifyDataChanged(DataChangeEvent... events) {
		// FIXME Is this function really necessary?
		layout();
	}

	@Override
	public void settingChanged(SettingChangeEvent event) {
		Key key = event.getKey();
		if (ORIENTATION.equals(key) || GAP.equals(key)) {
			Orientation orientation = getSetting(ORIENTATION);
			Dimension2D gap = getSetting(GAP);
			Layout layout = new StackedLayout(orientation, gap);
			setLayout(layout);
		}
	}

}
