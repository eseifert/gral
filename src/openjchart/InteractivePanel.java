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

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

import openjchart.plots.io.DrawableWriter;
import openjchart.plots.io.DrawableWriterFactory;
import openjchart.plots.io.WriterCapabilities;


/**
 * A class that displays a <code>Drawable</code> instance as a rich Swing component.
 */
public class InteractivePanel extends DrawablePanel {
	private static final long serialVersionUID = 1L;
	
	private JPopupMenu menu;
	private JMenu exportMenu;
	private JFileChooser exportChooser;

	public InteractivePanel(Drawable drawable) {
		super(drawable);
		
		exportChooser = new JFileChooser();

		menu = new JPopupMenu();

		exportMenu = new JMenu("Export");
		menu.add(exportMenu);
		String[] exportFormats = DrawableWriterFactory.getInstance().getSupportedFormats();
		for (final String format : exportFormats) {
			WriterCapabilities capabilities = DrawableWriterFactory.getInstance().getCapabilities(format);
			String exporterLabel = String.format("%s (%s)...", capabilities.getName(), capabilities.getFormat());
			Action a = new AbstractAction(exporterLabel) {
				@Override
				public void actionPerformed(ActionEvent e) {
					int ret = exportChooser.showSaveDialog(InteractivePanel.this);
					if (ret == JFileChooser.APPROVE_OPTION) {
						File f = exportChooser.getSelectedFile();
						FileOutputStream destination;
						try {
							destination = new FileOutputStream(f);
						} catch (FileNotFoundException ex) {
							// TODO Auto-generated catch block
							ex.printStackTrace();
							return;
						}
						DrawableWriter w = DrawableWriterFactory.getInstance().getDrawableWriter(destination, format);
						Drawable d = getDrawable();
						try {
							w.write(d, d.getWidth(), d.getHeight());
						} catch (IOException ex) {
							// TODO Auto-generated catch block
							ex.printStackTrace();
						} finally {
							try {
								destination.close();
							} catch (IOException ex2) {
								// TODO Auto-generated catch block
								ex2.printStackTrace();
							}
						}
					}
				}
			};
			exportMenu.add(a);
		}

		addMouseListener(new PopupListener());
	}

	private class PopupListener extends MouseAdapter {
	    public void mousePressed(MouseEvent e) {
	        showPopup(e);
	    }

	    public void mouseReleased(MouseEvent e) {
	        showPopup(e);
	    }

	    private void showPopup(MouseEvent e) {
	        if (e.isPopupTrigger()) {
	            menu.show(e.getComponent(), e.getX(), e.getY());
	        }
	    }
	}
}
