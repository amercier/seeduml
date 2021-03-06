/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2012, Arnaud Roques
 *
 * Project Info:  http://plantuml.sourceforge.net
 * 
 * This file is part of PlantUML.
 *
 * PlantUML is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PlantUML distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 *
 * Original Author:  Arnaud Roques
 * 
 * Revision $Revision: 4041 $
 *
 */
package net.sourceforge.plantuml.font;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.AbstractPSystem;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.graphic.GraphicStrings;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.ugraphic.UFont;

public class PSystemListFonts extends AbstractPSystem {

	private final List<String> strings = new ArrayList<String>();

	public PSystemListFonts(String text) {
		strings.add("   <b><size:16>Fonts available:");
		strings.add(" ");
		// final Font fonts[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
		// for (Font f : fonts) {
		// strings.add("f=" + f + "/" + f.getPSName() + "/" + f.getName() + "/" + f.getFontName() + "/"
		// + f.getFamily());
		// }
		final String name[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		for (String n : name) {
			strings.add(n + " : <font:" + n + ">" + text);
		}

	}

	public void exportDiagram(OutputStream os, StringBuilder cmap, int index, FileFormatOption fileFormat)
			throws IOException {
		getGraphicStrings().writeImage(os, fileFormat);
	}

	private GraphicStrings getGraphicStrings() throws IOException {
		final UFont font = new UFont("SansSerif", Font.PLAIN, 12);
		final GraphicStrings result = new GraphicStrings(strings, font, HtmlColor.BLACK, HtmlColor.WHITE, false);
		return result;
	}

	public String getDescription() {
		return "(List fonts)";
	}

}
