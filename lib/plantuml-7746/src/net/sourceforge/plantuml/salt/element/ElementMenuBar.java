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
 * Revision $Revision: 3835 $
 *
 */
package net.sourceforge.plantuml.salt.element;

import java.awt.geom.Dimension2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.SpriteContainer;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.URectangle;

public class ElementMenuBar implements Element {

	private final Collection<ElementMenuEntry> entries = new ArrayList<ElementMenuEntry>();
	private final Map<ElementMenuEntry, ElementMenuPopup> popups = new HashMap<ElementMenuEntry, ElementMenuPopup>();
	private final UFont font;
	private final SpriteContainer spriteContainer;

	public ElementMenuBar(UFont font, SpriteContainer spriteContainer) {
		this.font = font;
		this.spriteContainer = spriteContainer;
	}

	public void addEntry(String s) {
		entries.add(new ElementMenuEntry(s, font, spriteContainer));
	}

	public void addSubEntry(String s, String sub) {
		final ElementMenuPopup popup = getPopup(getElementMenuEntry(s));
		popup.addEntry(sub);
	}

	private ElementMenuPopup getPopup(ElementMenuEntry s) {
		ElementMenuPopup popup = popups.get(s);
		if (popup == null) {
			popup = new ElementMenuPopup(font, spriteContainer);
			popups.put(s, popup);
		}
		return popup;
	}

	private ElementMenuEntry getElementMenuEntry(String n) {
		for (ElementMenuEntry entry : entries) {
			if (entry.getText().equals(n)) {
				return entry;
			}
		}
		throw new IllegalArgumentException();
	}

	public Dimension2D getPreferredDimension(StringBounder stringBounder, double x, double y) {
		double w = 0;
		double h = 0;
		for (ElementMenuEntry entry : entries) {
			final Dimension2D dim = entry.getPreferredDimension(stringBounder, x, y);
			w += dim.getWidth() + 10;
			h = Math.max(h, dim.getHeight());
		}
		return new Dimension2DDouble(w, h);
	}

	public void drawU(UGraphic ug, final double x, final double y, int zIndex, Dimension2D dimToUse) {
		final Dimension2D preferred = getPreferredDimension(ug.getStringBounder(), x, y);

		double x1 = x;
		if (zIndex == 0) {
			ug.getParam().setBackcolor(HtmlColor.getColorIfValid("#DDDDDD"));
			ug.draw(x, y, new URectangle(dimToUse.getWidth(), dimToUse.getHeight()));
			ug.getParam().setBackcolor(null);
			for (ElementMenuEntry entry : entries) {
				entry.drawU(ug, x1, y, zIndex, dimToUse);
				final double w = entry.getPreferredDimension(ug.getStringBounder(), x1, y).getWidth();
				entry.setX(x1);
				x1 += w + 10;
			}
			return;
		}

		if (zIndex == 1) {
			for (ElementMenuEntry entry : popups.keySet()) {
				entry.setBackground(HtmlColor.getColorIfValid("#BBBBBB"));
			}

			final double y1 = y + preferred.getHeight();
			for (Map.Entry<ElementMenuEntry, ElementMenuPopup> ent : popups.entrySet()) {
				final ElementMenuPopup p = ent.getValue();
				final double xpopup = ent.getKey().getX();
				p.drawU(ug, xpopup, y1, zIndex, p.getPreferredDimension(ug.getStringBounder(), xpopup, y1));
			}
		}
	}

}
