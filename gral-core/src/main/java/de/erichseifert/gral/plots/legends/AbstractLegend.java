/*
 * GRAL: GRAphing Library for Java(R)
 *
 * (C) Copyright 2009-2013 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <mseifert[at]error-reports.org>
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
package de.erichseifert.gral.plots.legends;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Dimension2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.Row;
import de.erichseifert.gral.graphics.AbstractDrawable;
import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawableContainer;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.graphics.EdgeLayout;
import de.erichseifert.gral.graphics.Layout;
import de.erichseifert.gral.graphics.StackedLayout;
import de.erichseifert.gral.plots.Label;
import de.erichseifert.gral.util.GraphicsUtils;
import de.erichseifert.gral.util.Insets2D;
import de.erichseifert.gral.util.Location;
import de.erichseifert.gral.util.Orientation;
import de.erichseifert.gral.util.SerializationUtils;


/**
 * <p>Abstract class that serves as a base for legends in plots.
 * It stores a list of of items that are used to display a symbol and label for
 * each (visible) data source.</p>
 * <p>Like other elements legends can be styled using various settings. The
 * settings are used to control to control how the legend, and its items
 * are displayed. The actual rendering of symbols has to be implemented by
 * derived classes.</p>
 */
public abstract class AbstractLegend extends DrawableContainer
		implements Legend, LegendSymbolRenderer {
	/** Version id for serialization. */
	private static final long serialVersionUID = -1561976879958765700L;

	/** List of data sources displayed in this legend. */
	private final Set<DataSource> sources;
	/** Mapping of data rows to drawable components. */
	private final Map<Row, Drawable> components;
	/** Flag that tells whether the data in the legend is up-to-date. */
	private transient boolean valid;

	/** Paint used to draw the background. */
	private Paint background;
	/** Stroke used to draw the border of the legend. */
	// Property will be serialized using a wrapper
	private transient Stroke borderStroke;
	/** Font used to display the labels. */
	private Font font;
	/** Paint used to fill the border of the legend. */
	private Paint borderColor;
	/** Direction of the legend's items. */
	private Orientation orientation;
	/** Horizontal alignment of the legend relative to the plot area. */
	private double alignmentX;
	/** Vertical alignment of the legend relative to the plot area. */
	private double alignmentY;
	/** Gap size relative to the font height. */
	private Dimension2D gap;
	/** Symbol size relative to the font height. */
	private Dimension2D symbolSize;

	/**
	 * An abstract base class for drawable symbols.
	 */
	protected static abstract class AbstractSymbol extends AbstractDrawable {
		/** Version id for serialization. */
		private static final long serialVersionUID = 7475404103140652668L;

		/** Settings for determining the visual of the symbol. */
		private final Legend legend;

		/**
		 * Initializes a new instances.
		 * @param legend Legend that determines the appearance of the symbol.
		 */
		public AbstractSymbol(Legend legend) {
			this.legend = legend;
		}

		@Override
		public Dimension2D getPreferredSize() {
			double fontSize = legend.getFont().getSize2D();
			Dimension2D symbolSize = legend.getSymbolSize();
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
			label.setFont(font);
			label.setAlignmentX(0.0);
			label.setAlignmentY(0.5);
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

		sources = new LinkedHashSet<DataSource>();
		components = new HashMap<Row, Drawable>();

		background = Color.WHITE;
		borderStroke = new BasicStroke(1f);
		font = Font.decode(null);
		setDrawableFonts(font);
		borderColor = Color.BLACK;
		orientation = Orientation.VERTICAL;
		alignmentX = 0.0;
		alignmentY = 0.0;
		// TODO: Replace setter call in constructor
		setGap(new de.erichseifert.gral.util.Dimension2D.Double(2.0, 0.5));
		symbolSize = new de.erichseifert.gral.util.Dimension2D.Double(2.0, 2.0);
		refreshLayout();
	}

	/**
	 * Draws the {@code Drawable} with the specified drawing context.
	 * @param context Environment used for drawing.
	 */
	@Override
	public void draw(DrawingContext context) {
		if (components.isEmpty()) {
			return;
		}
		drawBackground(context);
		drawBorder(context);
		drawComponents(context);
	}

	/**
	 * Draws the background of this legend with the specified drawing context.
	 * @param context Environment used for drawing.
	 */
	protected void drawBackground(DrawingContext context) {
		Paint background = getBackground();
		if (background != null) {
			GraphicsUtils.fillPaintedShape(
				context.getGraphics(), getBounds(), background, null);
		}
	}

	/**
	 * Draws the border of this legend with the specified drawing context.
	 * @param context Environment used for drawing.
	 */
	protected void drawBorder(DrawingContext context) {
		Stroke stroke = getBorderStroke();
		if (stroke != null) {
			Paint borderColor = getBorderColor();
			GraphicsUtils.drawPaintedShape(
				context.getGraphics(), getBounds(), borderColor, null, stroke);
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
		sources.add(source);
		for (Row row : getEntries(source)) {
			String label = getLabel(row);
			Font font = getFont();
			Item item = new Item(row, this, label, font);
			add(item);
			components.put(row, item);
		}
		invalidate();
	}

	/**
	 * Returns whether the specified data source was added to the legend.
	 * @param source Data source.
	 * @return {@code true} if legend contains the data source,
	 *         otherwise {@code false}.
	 */
	public boolean contains(DataSource source) {
		return sources.contains(source);
	}

	/**
	 * Removes the specified data source.
	 * @param source Data source to be removed.
	 */
	public void remove(DataSource source) {
		sources.remove(source);
		Set<Row> rows = new HashSet<Row>(components.keySet());
		for (Row row : rows) {
			if (row.getSource() != source) {
				continue;
			}
			Drawable item = components.remove(row);
			if (item != null) {
				remove(item);
			}
		}
		invalidate();
	}

	/**
	 * Removes all data sources from the legend.
	 */
	public void clear() {
		Set<DataSource> sources = new HashSet<DataSource>(this.sources);
		for (DataSource source : sources) {
			remove(source);
		}
	}

	/**
	 * Updates the items for all data sources stored in this legend. The update
	 * is only performed if the legend is invalid.
	 * @see #invalidate()
	 */
	public void refresh() {
		if (isValid()) {
			return;
		}
		Set<DataSource> sources = new LinkedHashSet<DataSource>(this.sources);
		clear();
		for (DataSource source : sources) {
			add(source);
		}
		valid = true;
	}

	/**
	 * Refreshes the layout of the legend. It's currently used to handle new
	 * gap values.
	 */
	protected final void refreshLayout() {
		Orientation orientation = getOrientation();
		Dimension2D gap = getGap();
		Layout layout = new StackedLayout(orientation, gap);
		setLayout(layout);
	}

	@Override
	public void setBounds(double x, double y, double width, double height) {
		Dimension2D size = getPreferredSize();
		double alignX = getAlignmentX();
		double alignY = getAlignmentY();
		super.setBounds(
			x + alignX*(width - size.getWidth()),
			y + alignY*(height - size.getHeight()),
			size.getWidth(),
			size.getHeight()
		);
	}

	/**
	 * Returns whether this legend's values and layout are valid.
	 * @return {@code true} if the values and the layout are valid,
	 *         otherwise {@code false}.
	 * @see #invalidate()
	 */
	protected boolean isValid() {
		return valid;
	}

	/**
	 * Marks this legend's values and layout as invalid. The legend will only
	 * be refreshed if it's invalid.
	 * @see #refresh()
	 */
	protected void invalidate() {
		valid = false;
	}

	/**
	 * Sets the font of the contained drawables.
	 * @param font Font to be set.
	 */
	protected final void setDrawableFonts(Font font) {
		for (Drawable drawable : components.values()) {
			if (drawable instanceof Item) {
				Item item = (Item) drawable;
				item.label.setFont(font);
			}
		}
	}

	/**
	 * Custom deserialization method.
	 * @param in Input stream.
	 * @throws ClassNotFoundException if a serialized class doesn't exist anymore.
	 * @throws IOException if there is an error while reading data from the input stream.
	 */
	private void readObject(ObjectInputStream in)
			throws ClassNotFoundException, IOException {
		in.defaultReadObject();
		borderStroke = (Stroke) SerializationUtils.unwrap((Serializable) in.readObject());
	}

	/**
	 * Custom serialization method.
	 * @param out Output stream.
	 * @throws ClassNotFoundException if a deserialized class does not exist.
	 * @throws IOException if there is an error while writing data to the
	 *         output stream.
	 */
	private void writeObject(ObjectOutputStream out)
			throws ClassNotFoundException, IOException {
		out.defaultWriteObject();
		out.writeObject(SerializationUtils.wrap(borderStroke));
	}

	@Override
	public Paint getBackground() {
		return background;
	}

	@Override
	public void setBackground(Paint background) {
		this.background = background;
	}

	@Override
	public Stroke getBorderStroke() {
		return borderStroke;
	}

	@Override
	public void setBorderStroke(Stroke borderStroke) {
		this.borderStroke = borderStroke;
	}

	@Override
	public Font getFont() {
		return font;
	}

	@Override
	public void setFont(Font font) {
		this.font = font;
		setDrawableFonts(font);
	}

	@Override
	public Paint getBorderColor() {
		return borderColor;
	}

	@Override
	public void setBorderColor(Paint borderColor) {
		this.borderColor = borderColor;
	}

	@Override
	public Orientation getOrientation() {
		return orientation;
	}

	@Override
	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
		refreshLayout();
	}

	@Override
	public double getAlignmentX() {
		return alignmentX;
	}

	@Override
	public void setAlignmentX(double alignmentX) {
		this.alignmentX = alignmentX;
	}

	@Override
	public double getAlignmentY() {
		return alignmentY;
	}

	@Override
	public void setAlignmentY(double alignmentY) {
		this.alignmentY = alignmentY;
	}

	@Override
	public Dimension2D getGap() {
		return gap;
	}

	@Override
	public void setGap(Dimension2D gap) {
		this.gap = gap;
		if (this.gap != null) {
			double fontSize = getFont().getSize2D();
			this.gap.setSize(this.gap.getWidth()*fontSize, this.gap.getHeight()*fontSize);
		}
	}

	@Override
	public Dimension2D getSymbolSize() {
		return symbolSize;
	}

	@Override
	public void setSymbolSize(Dimension2D symbolSize) {
		this.symbolSize = symbolSize;
	}
}
