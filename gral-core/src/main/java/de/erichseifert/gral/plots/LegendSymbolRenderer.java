package de.erichseifert.gral.plots;

import de.erichseifert.gral.data.Row;
import de.erichseifert.gral.graphics.Drawable;

/**
 * A renderer for symbols that are used in legend items.
 */
public interface LegendSymbolRenderer {
	/**
	 * Returns a symbol for rendering a legend item.
	 * @param row Data row.
	 * @return A drawable object that can be used to display the symbol.
	 */
	Drawable getSymbol(Row row);
}
