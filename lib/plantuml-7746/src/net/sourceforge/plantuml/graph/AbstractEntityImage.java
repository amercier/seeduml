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
 * Revision $Revision: 7715 $
 *
 */
package net.sourceforge.plantuml.graph;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.cucadiagram.Entity;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.ColorMapper;
import net.sourceforge.plantuml.ugraphic.UFont;

abstract class AbstractEntityImage {

	private final Entity entity;

	final private HtmlColor red = HtmlColor.getColorIfValid("#A80036");
	
	final private HtmlColor yellow = HtmlColor.getColorIfValid("#FEFECE");
	private final HtmlColor yellowNote = HtmlColor.getColorIfValid("#FBFB77");

	final private UFont font14 = new UFont("SansSerif", Font.PLAIN, 14);
	final private UFont font17 = new UFont("Courier", Font.BOLD, 17);
	final private HtmlColor green = HtmlColor.getColorIfValid("#ADD1B2");
	final private HtmlColor violet = HtmlColor.getColorIfValid("#B4A7E5");
	final private HtmlColor blue = HtmlColor.getColorIfValid("#A9DCDF");
	final private HtmlColor rose = HtmlColor.getColorIfValid("#EB937F");

	public AbstractEntityImage(Entity entity) {
		if (entity == null) {
			throw new IllegalArgumentException("entity null");
		}
		this.entity = entity;
	}

	public abstract Dimension2D getDimension(StringBounder stringBounder);

	public abstract void draw(ColorMapper colorMapper, Graphics2D g2d);

	protected final Entity getEntity() {
		return entity;
	}

	protected final HtmlColor getRed() {
		return red;
	}

	protected final HtmlColor getYellow() {
		return yellow;
	}

	protected final UFont getFont17() {
		return font17;
	}

	protected final UFont getFont14() {
		return font14;
	}

	protected final HtmlColor getGreen() {
		return green;
	}

	protected final HtmlColor getViolet() {
		return violet;
	}

	protected final HtmlColor getBlue() {
		return blue;
	}

	protected final HtmlColor getRose() {
		return rose;
	}

	protected final HtmlColor getYellowNote() {
		return yellowNote;
	}
}
