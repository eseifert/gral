package de.erichseifert.gral.plots.legends;

import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.graphics.Container;
import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.plots.settings.Key;
import de.erichseifert.gral.plots.settings.SettingsStorage;

/**
 * Interface for a legend that display visual examples of the variables used in
 * a plot.
 */
public interface Legend extends Container, Drawable, SettingsStorage {
	/** Key for specifying the {@link java.awt.Paint} instance to be used to
	 paint the background. */
	Key BACKGROUND = new Key("legend.background"); //$NON-NLS-1$
	/** Key for specifying the {@link java.awt.Stroke} instance to be used to
	 paint the border of the legend. */
	Key BORDER = new Key("legend.border"); //$NON-NLS-1$
	/** Key for specifying the {@link java.awt.Font} instance to be used to
	 display the legend labels. */
	Key FONT = new Key("legend.font"); //$NON-NLS-1$
	/** Key for specifying the {@link java.awt.Paint} instance to be used to
	 fill the border of the legend. */
	Key COLOR = new Key("legend.color"); //$NON-NLS-1$
	/** Key for specifying a {@link de.erichseifert.gral.util.Orientation}
	instance defining the direction of the legend's items. */
	Key ORIENTATION = new Key("legend.orientation"); //$NON-NLS-1$
	/** Key for specifying a {@link Number} value describing the horizontal
	alignment of the legend relative to the plot area. {@code 0.0} means left,
	{@code 0.5} means centered, and {@code 1.0} means right. */
	Key ALIGNMENT_X = new Key("legend.alignment.x"); //$NON-NLS-1$
	/** Key for specifying a {@link Number} value describing the vertical
	alignment of the legend relative to the plot area. {@code 0.0} means top,
	{@code 0.5} means centered, and {@code 1.0} means bottom. */
	Key ALIGNMENT_Y = new Key("legend.alignment.y"); //$NON-NLS-1$
	/** Key for specifying a {@link java.awt.Insets2D} instance defining the
	horizontal and vertical gap between items. The gap size is defined
	relative to the font height of the legend. */
	Key GAP = new Key("legend.gap"); //$NON-NLS-1$
	/** Key for specifying a {@link java.awt.Insets2D} instance defining the
	size of the legend's symbols. The symbol size is defined relative to the
	font height of the legend. */
	Key SYMBOL_SIZE = new Key("legend.symbol.size"); //$NON-NLS-1$

	/**
	 * Adds the specified data source in order to display it.
	 * @param source data source to be added.
	 */
	void add(DataSource source);

	/**
	 * Returns whether the specified data source was added to the legend.
	 * @param source Data source
	 * @return {@code true} if legend contains the data source, otherwise {@code false}
	 */
	boolean contains(DataSource source);

	/**
	 * Removes the specified data source.
	 * @param source Data source to be removed.
	 */
	void remove(DataSource source);

	/**
	 * Removes all data sources from the legend.
	 */
	void clear();

	/**
	 * Updates the items for all data sources stored in this legend.
	 */
	void refresh();
}
