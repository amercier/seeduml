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
import java.util.Arrays;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.SpriteContainer;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignement;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;

abstract class AbstractElementText implements Element {

	private final TextBlock block;
	private final FontConfiguration config;
	private final int charLength;

	public AbstractElementText(String text, UFont font, boolean manageLength, SpriteContainer spriteContainer) {
		config = new FontConfiguration(font, HtmlColor.BLACK);
		if (manageLength) {
			this.charLength = text.length();
			text = text.trim();
		} else {
			this.charLength = 0;
		}
		this.block = TextBlockUtils.create(Arrays.asList(text), config, HorizontalAlignement.LEFT, spriteContainer);
	}

	protected void drawText(UGraphic ug, double x, double y) {
		block.drawU(ug, x, y);
	}

	protected Dimension2D getPureTextDimension(StringBounder stringBounder) {
		return block.calculateDimension(stringBounder);
	}

	protected Dimension2D getTextDimensionAt(StringBounder stringBounder, double x) {
		final Dimension2D result = block.calculateDimension(stringBounder);
		if (charLength == 0) {
			return result;
		}
		final double dimSpace = getSingleSpace(stringBounder);
		// final double endx = x + result.getWidth();
		// final double mod = endx % CHAR_SIZE;
		// final double delta = charLength * CHAR_SIZE - mod;
		// return Dimension2DDouble.delta(result, delta, 0);
		return new Dimension2DDouble(Math.max(result.getWidth(), charLength * dimSpace), result.getHeight());
	}

	private double getSingleSpace(StringBounder stringBounder) {
//		double max = 0;
//		for (int i = 32; i < 127; i++) {
//			final char c = (char) i;
//			final double w = TextBlockUtils.create(Arrays.asList("" + c), config, HorizontalAlignement.LEFT)
//					.calculateDimension(stringBounder).getWidth();
//			if (w > max) {
//				System.err.println("c="+c+" "+max);
//				max = w;
//			}
//		}
//		return max;
		return 8;
	}

}
