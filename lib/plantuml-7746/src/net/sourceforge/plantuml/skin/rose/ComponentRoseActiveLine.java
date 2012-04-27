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
package net.sourceforge.plantuml.skin.rose;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.skin.AbstractComponent;
import net.sourceforge.plantuml.skin.Area;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.URectangle;

public class ComponentRoseActiveLine extends AbstractComponent {

	private final HtmlColor foregroundColor;
	private final HtmlColor lifeLineBackground;
	private final boolean closeUp;
	private final boolean closeDown;

	public ComponentRoseActiveLine(HtmlColor foregroundColor, HtmlColor lifeLineBackground, boolean closeUp, boolean closeDown) {
		this.foregroundColor = foregroundColor;
		this.lifeLineBackground = lifeLineBackground;
		this.closeUp = closeUp;
		this.closeDown = closeDown;
	}

	protected void drawInternalU(UGraphic ug, Area area, boolean withShadow) {
		final Dimension2D dimensionToUse = area.getDimensionToUse();
		final StringBounder stringBounder = ug.getStringBounder();
		final int x = (int) (dimensionToUse.getWidth() - getPreferredWidth(stringBounder)) / 2;

		final URectangle rect = new URectangle(getPreferredWidth(stringBounder), dimensionToUse.getHeight());
		if (closeUp && closeDown) {
			ug.getParam().setBackcolor(lifeLineBackground);
			ug.getParam().setColor(foregroundColor);
			ug.draw(x, 0, rect);
			return;
		}
		ug.getParam().setBackcolor(lifeLineBackground);
		ug.getParam().setColor(lifeLineBackground);
		ug.draw(x, 0, rect);
		ug.getParam().setColor(foregroundColor);

		final ULine vline = new ULine(0, dimensionToUse.getHeight());
		ug.draw(x, 0, vline);
		ug.draw(x + getPreferredWidth(stringBounder), 0, vline);
		
		final ULine hline = new ULine(getPreferredWidth(stringBounder), 0);
		if (closeUp) {
			ug.draw(x, 0, hline);
		}
		if (closeDown) {
			ug.draw(x, dimensionToUse.getHeight(), hline);
		}
	}

	@Override
	public double getPreferredHeight(StringBounder stringBounder) {
		return 0;
	}

	@Override
	public double getPreferredWidth(StringBounder stringBounder) {
		return 10;
	}

}
