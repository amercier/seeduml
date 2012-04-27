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
package net.sourceforge.plantuml.skin;

import java.awt.BasicStroke;
import java.awt.Graphics2D;

import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UStroke;

public abstract class AbstractComponent implements Component {

	final protected void stroke(Graphics2D g2d, float dash, float thickness) {
		final float[] style = { dash, dash };
		g2d.setStroke(new BasicStroke(thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, style, 0));
	}

	final protected void stroke(UGraphic ug, double dashVisible, double dashSpace, double thickness) {
		ug.getParam().setStroke(new UStroke(dashVisible, dashSpace, thickness));
	}

	final protected void stroke(Graphics2D g2d, float dash) {
		stroke(g2d, dash, 1);
	}

	final protected void stroke(UGraphic ug, double dashVisible, double dashSpace) {
		stroke(ug, dashVisible, dashSpace, 1);
	}

	abstract protected void drawInternalU(UGraphic ug, Area area, boolean withShadow);

	protected void drawBackgroundInternalU(UGraphic ug, Area area) {
	}

	public final void drawU(UGraphic ug, Area area, Context2D context) {
		final double dx = ug.getTranslateX();
		final double dy = ug.getTranslateY();
		ug.translate(getPaddingX(), getPaddingY());
		if (context.isBackground()) {
			drawBackgroundInternalU(ug, area);
		} else {
			drawInternalU(ug, area, context.withShadow());
		}
		ug.setTranslate(dx, dy);
	}

	public double getPaddingX() {
		return 0;
	}

	public double getPaddingY() {
		return 0;
	}

	public abstract double getPreferredWidth(StringBounder stringBounder);

	public abstract double getPreferredHeight(StringBounder stringBounder);

}
