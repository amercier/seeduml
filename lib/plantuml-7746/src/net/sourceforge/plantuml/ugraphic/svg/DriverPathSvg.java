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
 */
package net.sourceforge.plantuml.ugraphic.svg;

import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.svg.SvgGraphics;
import net.sourceforge.plantuml.ugraphic.ClipContainer;
import net.sourceforge.plantuml.ugraphic.ColorMapper;
import net.sourceforge.plantuml.ugraphic.UDriver;
import net.sourceforge.plantuml.ugraphic.UParam;
import net.sourceforge.plantuml.ugraphic.UPath;
import net.sourceforge.plantuml.ugraphic.USegment;
import net.sourceforge.plantuml.ugraphic.USegmentType;
import net.sourceforge.plantuml.ugraphic.UShape;
import net.sourceforge.plantuml.ugraphic.g2d.DriverShadowedG2d;

public class DriverPathSvg extends DriverShadowedG2d implements UDriver<SvgGraphics> {

	private final ClipContainer clipContainer;

	public DriverPathSvg(ClipContainer clipContainer) {
		this.clipContainer = clipContainer;
	}

	public void draw(UShape ushape, double x, double y, ColorMapper mapper, UParam param, SvgGraphics svg) {
		final UPath shape = (UPath) ushape;

		final String color = param.getColor() == null ? "none" : StringUtils.getAsHtml(mapper.getMappedColor(param
				.getColor()));
		final String backcolor = param.getBackcolor() == null ? "none" : StringUtils.getAsHtml(mapper
				.getMappedColor(param.getBackcolor()));
		
//		// Shadow
//		if (shape.getDeltaShadow() != 0) {
//			double lastX = 0;
//			double lastY = 0;
//			for (USegment seg : shape) {
//				final USegmentType type = seg.getSegmentType();
//				final double coord[] = seg.getCoord();
//				if (type == USegmentType.SEG_MOVETO) {
//					lastX = x + coord[0];
//					lastY = y + coord[1];
//				} else if (type == USegmentType.SEG_LINETO) {
//					svg.svgLineShadow(lastX, lastY, x + coord[0], y + coord[1], shape.getDeltaShadow());
//					lastX = x + coord[0];
//					lastY = y + coord[1];
//				} else {
//					throw new UnsupportedOperationException();
//				}
//			}
//		}


		svg.setFillColor(backcolor);
		svg.setStrokeColor(color);
		svg.setStrokeWidth("" + param.getStroke().getThickness(), param.getStroke().getDasharraySvg());

		svg.svgPath(x, y, shape, shape.getDeltaShadow());

	}
}
