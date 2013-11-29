package de.erichseifert.gral;

import java.awt.Font;
import java.awt.geom.Dimension2D;

public interface SymbolSettingProvider {
	Font getFont();
	void setFont(Font font);

	Dimension2D getSymbolSize();
	void setSymbolSize(Dimension2D symbolSize);
}
