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
 * Revision $Revision: 4749 $
 *
 */
package net.sourceforge.plantuml.cucadiagram;

import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignement;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.graphic.TextBlockWidth;
import net.sourceforge.plantuml.skin.VisibilityModifier;
import net.sourceforge.plantuml.skin.rose.Rose;
import net.sourceforge.plantuml.ugraphic.ColorMapper;
import net.sourceforge.plantuml.ugraphic.PlacementStrategyVisibility;
import net.sourceforge.plantuml.ugraphic.PlacementStrategyY1Y2Left;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULayoutGroup;

public class MethodsOrFieldsArea implements TextBlockWidth {

	private final UFont font;
	private final ISkinParam skinParam;
	private final HtmlColor color;
	private final Rose rose = new Rose();
	private final List<Member> members = new ArrayList<Member>();

	public MethodsOrFieldsArea(List<Member> members, FontParam fontParam, ISkinParam skinParam) {
		this.skinParam = skinParam;
		this.font = skinParam.getFont(fontParam, null);
		this.color = rose.getFontColor(skinParam, fontParam);
		this.members.addAll(members);
	}

	private boolean hasSmallIcon() {
		if (skinParam.classAttributeIconSize() == 0) {
			return false;
		}
		for (Member m : members) {
			if (m.getVisibilityModifier() != null) {
				return true;
			}
		}
		return false;
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		double x = 0;
		double y = 0;
		for (Member m : members) {
			final TextBlock bloc = createTextBlock(m);
			final Dimension2D dim = bloc.calculateDimension(stringBounder);
			y += dim.getHeight();
			x = Math.max(dim.getWidth(), x);
		}
		if (hasSmallIcon()) {
			x += skinParam.getCircledCharacterRadius() + 3;
		}
		return new Dimension2DDouble(x, y);
	}

	private TextBlock createTextBlock(Member m) {
		final boolean withVisibilityChar = skinParam.classAttributeIconSize() == 0;
		final String s = m.getDisplay(withVisibilityChar);
		FontConfiguration config = new FontConfiguration(font, color);
		if (m.isAbstract()) {
			config = config.italic();
		}
		if (m.isStatic()) {
			config = config.underline();
		}
		final TextBlock bloc = TextBlockUtils.create(StringUtils.getWithNewlines(s), config, HorizontalAlignement.LEFT, skinParam);
		return bloc;
	}

	public void drawTOBEREMOVED(ColorMapper colorMapper, Graphics2D g2d, double x, double y) {
		throw new UnsupportedOperationException();
	}

	public void drawU(UGraphic ug, double x, double y, double widthToUse) {
		final Dimension2D dim = calculateDimension(ug.getStringBounder());
		final ULayoutGroup group;
		if (hasSmallIcon()) {
			group = new ULayoutGroup(new PlacementStrategyVisibility(ug.getStringBounder(),
					skinParam.getCircledCharacterRadius() + 3));
			for (Member att : members) {
				final TextBlock bloc = createTextBlock(att);
				final VisibilityModifier modifier = att.getVisibilityModifier();
				group.add(getUBlock(modifier));
				group.add(bloc);
			}
		} else {
			group = new ULayoutGroup(new PlacementStrategyY1Y2Left(ug.getStringBounder()));
			for (Member att : members) {
				final TextBlock bloc = createTextBlock(att);
				group.add(bloc);
			}
		}
		group.drawU(ug, x, y, dim.getWidth(), dim.getHeight());

	}

	private TextBlock getUBlock(final VisibilityModifier modifier) {
		if (modifier == null) {
			return new TextBlock() {

				public void drawU(UGraphic ug, double x, double y) {
				}

				public void drawTOBEREMOVED(ColorMapper colorMapper, Graphics2D g2d, double x, double y) {
					throw new UnsupportedOperationException();
				}

				public Dimension2D calculateDimension(StringBounder stringBounder) {
					return new Dimension2DDouble(1, 1);
				}
			};
		}
		final HtmlColor back = modifier.getBackground() == null ? null : rose.getHtmlColor(skinParam,
				modifier.getBackground());
		final HtmlColor fore = rose.getHtmlColor(skinParam, modifier.getForeground());

		final TextBlock uBlock = modifier.getUBlock(skinParam.classAttributeIconSize(), fore, back);
		return uBlock;
	}

}
