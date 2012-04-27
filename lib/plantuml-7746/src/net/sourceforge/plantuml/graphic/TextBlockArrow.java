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
 * Revision $Revision: 7163 $
 *
 */
package net.sourceforge.plantuml.graphic;

import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.cucadiagram.LinkArrow;
import net.sourceforge.plantuml.ugraphic.ColorMapper;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UPolygon;

public class TextBlockArrow implements TextBlock {

	private final double size;
	private final LinkArrow arrow;
	private final HtmlColor color;

	public TextBlockArrow(LinkArrow arrow, FontConfiguration fontConfiguration) {
		if (arrow != LinkArrow.DIRECT_NORMAL && arrow != LinkArrow.BACKWARD) {
			throw new IllegalArgumentException();
		}
		this.arrow = arrow;
		// this.size = fontConfiguration.getFont().getSize2D() * 0 + 30;
		this.size = fontConfiguration.getFont().getSize2D();
		this.color = fontConfiguration.getColor();

	}

	public void drawU(UGraphic ug, double x, double y) {
		// ug.draw(x, y, new URectangle(size, size));
		ug.getParam().setBackcolor(color);
		ug.getParam().setColor(color);
		int triSize = (int) (size * .8 - 3);
		if (triSize % 2 == 1) {
			triSize--;
		}
		final UPolygon triangle = getTriangle(triSize);
		ug.draw(x + 2, y + (size - triSize) - 2, triangle);
	}

	private UPolygon getTriangle(int triSize) {
		final UPolygon triangle = new UPolygon();
		if (arrow == LinkArrow.DIRECT_NORMAL) {
			triangle.addPoint(0, 0);
			triangle.addPoint(triSize, triSize / 2);
			triangle.addPoint(0, triSize);
			triangle.addPoint(0, 0);
		} else {
			triangle.addPoint(triSize, 0);
			triangle.addPoint(0, triSize / 2);
			triangle.addPoint(triSize, triSize);
			triangle.addPoint(triSize, 0);
		}
		return triangle;
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		return new Dimension2DDouble(size, size);
	}

	public void drawTOBEREMOVED(ColorMapper colorMapper, Graphics2D g2d, double x, double y) {
		throw new UnsupportedOperationException();
	}

}