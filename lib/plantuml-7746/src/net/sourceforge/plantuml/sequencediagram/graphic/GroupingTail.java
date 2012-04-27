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
package net.sourceforge.plantuml.sequencediagram.graphic;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.sequencediagram.InGroupableList;
import net.sourceforge.plantuml.skin.Area;
import net.sourceforge.plantuml.skin.Component;
import net.sourceforge.plantuml.skin.Context2D;
import net.sourceforge.plantuml.ugraphic.UGraphic;

class GroupingTail extends GroupingGraphicalElement {

	private final double initY;
	private final Component body;
	private final Component tail;

	public GroupingTail(double currentY, double initY, Component body, Component tail, InGroupableList inGroupableList,
			boolean parallel) {
		super(currentY, inGroupableList);
		if (currentY < initY) {
			throw new IllegalArgumentException("currentY=" + currentY + " initY=" + initY);
			// System.err.println("currentY=" + currentY + " initY=" + initY);
		}
		if (inGroupableList == null) {
			throw new IllegalArgumentException();
		}
		this.body = body;
		this.tail = tail;
		this.initY = initY;
	}

	@Override
	protected void drawInternalU(UGraphic ug, double maxX, Context2D context) {
		final StringBounder stringBounder = ug.getStringBounder();
		final double x1 = getInGroupableList().getMinX(stringBounder);
		final double x2 = getInGroupableList().getMaxX(stringBounder);
		ug.translate(x1, initY);
		final Dimension2D dimBody = new Dimension2DDouble(x2 - x1, getPreferredHeight(stringBounder));
		body.drawU(ug, new Area(dimBody), context);
		tail.drawU(ug, new Area(dimBody), context);
	}

	@Override
	public double getPreferredHeight(StringBounder stringBounder) {
		return getStartingY() - initY;
	}

	@Override
	public double getPreferredWidth(StringBounder stringBounder) {
		return 0;
	}

}
