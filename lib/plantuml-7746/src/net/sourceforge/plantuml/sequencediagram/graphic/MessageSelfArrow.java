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
 * Revision $Revision: 7718 $
 *
 */
package net.sourceforge.plantuml.sequencediagram.graphic;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.sequencediagram.NotePosition;
import net.sourceforge.plantuml.skin.Area;
import net.sourceforge.plantuml.skin.ArrowComponent;
import net.sourceforge.plantuml.skin.Component;
import net.sourceforge.plantuml.skin.Context2D;
import net.sourceforge.plantuml.skin.Skin;
import net.sourceforge.plantuml.ugraphic.UGraphic;

class MessageSelfArrow extends Arrow {

	private final LivingParticipantBox p1;
	private final double deltaY;

	public MessageSelfArrow(double startingY, Skin skin, Component arrow, LivingParticipantBox p1, double deltaY) {
		super(startingY, skin, arrow);
		this.p1 = p1;
		this.deltaY = deltaY;
	}

	@Override
	public double getPreferredHeight(StringBounder stringBounder) {
		return getArrowComponent().getPreferredHeight(stringBounder);
	}

	@Override
	public double getPreferredWidth(StringBounder stringBounder) {
		return getArrowComponent().getPreferredWidth(stringBounder);
	}

	@Override
	protected void drawInternalU(UGraphic ug, double maxX, Context2D context) {
		final StringBounder stringBounder = ug.getStringBounder();
		ug.translate(getStartingX(stringBounder), getStartingY() + deltaY);
		final Area area = new Area(new Dimension2DDouble(getPreferredWidth(stringBounder),
				getPreferredHeight(stringBounder)));
		// System.err.println("AZERTY deltaY="+deltaY);
		area.setDeltaX1(deltaY);
		getArrowComponent().drawU(ug, area, context);
	}

	@Override
	public double getStartingX(StringBounder stringBounder) {
		final double pos2 = p1.getLiveThicknessAt(stringBounder, getArrowYStartLevel(stringBounder)).getSegment().getPos2();
		// System.err.println("AZERTY pos2="+pos2);
		return pos2;
	}

	@Override
	public int getDirection(StringBounder stringBounder) {
		return 1;
	}

	@Override
	public double getArrowYStartLevel(StringBounder stringBounder) {
		if (getArrowComponent() instanceof ArrowComponent) {
			final ArrowComponent arrowComponent = (ArrowComponent) getArrowComponent();
			final Dimension2D dim = new Dimension2DDouble(arrowComponent.getPreferredWidth(stringBounder),
					arrowComponent.getPreferredHeight(stringBounder));
			return getStartingY() + arrowComponent.getStartPoint(stringBounder, dim).getY();
		}
		return getStartingY();
	}

	@Override
	public double getArrowYEndLevel(StringBounder stringBounder) {
		if (getArrowComponent() instanceof ArrowComponent) {
			final ArrowComponent arrowComponent = (ArrowComponent) getArrowComponent();
			final Dimension2D dim = new Dimension2DDouble(arrowComponent.getPreferredWidth(stringBounder),
					arrowComponent.getPreferredHeight(stringBounder));
			return getStartingY() + arrowComponent.getEndPoint(stringBounder, dim).getY();
		}
		return getStartingY() + getArrowComponent().getPreferredHeight(stringBounder);
	}

	public double getMaxX(StringBounder stringBounder) {
		return getStartingX(stringBounder) + getPreferredWidth(stringBounder);
	}

	public double getMinX(StringBounder stringBounder) {
		return getStartingX(stringBounder);
	}

	public String toString(StringBounder stringBounder) {
		return super.toString();
	}

	@Override
	public LivingParticipantBox getParticipantAt(StringBounder stringBounder, NotePosition position) {
		return p1;
	}

	@Override
	public double getActualWidth(StringBounder stringBounder) {
		return getPreferredWidth(stringBounder);
	}
}
