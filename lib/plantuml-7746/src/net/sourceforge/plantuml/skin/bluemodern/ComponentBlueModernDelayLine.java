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
 * Revision $Revision: 4169 $
 *
 */
package net.sourceforge.plantuml.skin.bluemodern;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.skin.AbstractComponent;
import net.sourceforge.plantuml.skin.Area;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UStroke;

public class ComponentBlueModernDelayLine extends AbstractComponent {

	private final HtmlColor color;

	public ComponentBlueModernDelayLine(HtmlColor color) {
		this.color = color;
	}

	@Override
	protected void drawInternalU(UGraphic ug, Area area, boolean withShadow) {
		final Dimension2D dimensionToUse = area.getDimensionToUse();
		ug.getParam().setColor(color);
		ug.getParam().setBackcolor(color);
		stroke(ug, 1, 4);
		final int x = (int) (dimensionToUse.getWidth() / 2);
		ug.setAntiAliasing(false);
		ug.draw(x + 1, 0, new ULine(0, dimensionToUse.getHeight()));
		ug.setAntiAliasing(true);
		ug.getParam().setStroke(new UStroke());
	}

	@Override
	public double getPreferredHeight(StringBounder stringBounder) {
		return 20;
	}

	@Override
	public double getPreferredWidth(StringBounder stringBounder) {
		return 2;
	}

}
