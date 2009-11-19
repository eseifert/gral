package openjchart.util;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;

/**
 * Class that contains utility functions for working with graphics.
 * For example, This includes font handling.
 * @author Erich Seifert
 */
public abstract class GraphicsUtils {
	private static final FontRenderContext frc = new FontRenderContext(null, true, true);

	public static TextLayout getLayout(String text, Font font) {
		TextLayout layout = new TextLayout(text, font, frc);
		return layout;
	}

}
