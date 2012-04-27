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
 * Revision $Revision: 5183 $
 *
 */
package net.sourceforge.plantuml.svek;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.cucadiagram.IEntity;
import net.sourceforge.plantuml.cucadiagram.PortionShower;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlockWidth;
import net.sourceforge.plantuml.ugraphic.Shadowable;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UGroup;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UStroke;

public class EntityImageClass extends AbstractEntityImage {

	final private TextBlockWidth body;
	final private int shield;
	final private EntityImageClassHeader2 header;
	final private Url url;
	final private TextBlockWidth mouseOver;

	public EntityImageClass(IEntity entity, ISkinParam skinParam, PortionShower portionShower) {
		super(entity, skinParam);

		this.shield = entity.hasNearDecoration() ? 16 : 0;
		this.body = entity.getBody(portionShower).asTextBlock(FontParam.CLASS_ATTRIBUTE, skinParam);

		header = new EntityImageClassHeader2(entity, skinParam, portionShower);
		this.url = entity.getUrl();
		if (entity.getMouseOver() == null) {
			this.mouseOver = null;
		} else {
			this.mouseOver = entity.getMouseOver().asTextBlock(FontParam.CLASS_ATTRIBUTE, skinParam);
		}

	}

	// private int marginEmptyFieldsOrMethod = 13;

	@Override
	public Dimension2D getDimension(StringBounder stringBounder) {
		final Dimension2D dimHeader = header.getDimension(stringBounder);
		final Dimension2D dimBody = body == null ? new Dimension2DDouble(0, 0) : body.calculateDimension(stringBounder);
		final double width = Math.max(dimBody.getWidth(), dimHeader.getWidth());
		final double height = dimBody.getHeight() + dimHeader.getHeight();
		return new Dimension2DDouble(width, height);
	}

	public void drawU(UGraphic ug, double xTheoricalPosition, double yTheoricalPosition) {
		if (url != null) {
			ug.startUrl(url.getUrl(), url.getTooltip());
		}
		drawInternal(ug, xTheoricalPosition, yTheoricalPosition);
		if (mouseOver != null) {
			final UGroup g = ug.createGroup();
			ug.getParam().setBackcolor(getColor(ColorParam.classBackground, getStereo()));
			final Dimension2D dim = mouseOver.calculateDimension(ug.getStringBounder());
			final Shadowable rect = new URectangle(dim.getWidth(), dim.getHeight());
			if (getSkinParam().shadowing()) {
				rect.setDeltaShadow(4);
			}

			final HtmlColor classBorder = getColor(ColorParam.classBorder, getStereo());
			ug.getParam().setColor(classBorder);
			ug.getParam().setBackcolor(getColor(ColorParam.classBackground, getStereo()));

			double x = xTheoricalPosition + 30;
			double y = yTheoricalPosition + 30;
			ug.getParam().setStroke(new UStroke(1.5));
			g.draw(x, y, rect);
			ug.getParam().setStroke(new UStroke());
			mouseOver.drawU(ug, x, y, dim.getWidth());
			g.close();
		}

		if (url != null) {
			ug.closeAction();
		}
	}

	private void drawInternal(UGraphic ug, double xTheoricalPosition, double yTheoricalPosition) {
		final StringBounder stringBounder = ug.getStringBounder();
		final Dimension2D dimTotal = getDimension(stringBounder);
		final Dimension2D dimHeader = header.getDimension(stringBounder);

		final double widthTotal = dimTotal.getWidth();
		final double heightTotal = dimTotal.getHeight();
		final Shadowable rect = new URectangle(widthTotal, heightTotal);
		if (getSkinParam().shadowing()) {
			rect.setDeltaShadow(4);
		}

		final HtmlColor classBorder = getColor(ColorParam.classBorder, getStereo());
		ug.getParam().setColor(classBorder);
		ug.getParam().setBackcolor(getColor(ColorParam.classBackground, getStereo()));

		double x = xTheoricalPosition;
		double y = yTheoricalPosition;
		ug.getParam().setStroke(new UStroke(1.5));
		ug.draw(x, y, rect);
		ug.getParam().setStroke(new UStroke());

		ug.getParam().setBackcolor(getColor(ColorParam.classBackground, getStereo()));
		header.drawU(ug, x, y, dimTotal.getWidth(), dimHeader.getHeight());

		y += dimHeader.getHeight();

		x = xTheoricalPosition;
		if (body != null) {
			ug.getParam().setBackcolor(getColor(ColorParam.classBackground, getStereo()));
			ug.getParam().setColor(classBorder);
			body.drawU(ug, x, y, widthTotal);
		}
	}

	public ShapeType getShapeType() {
		return ShapeType.RECTANGLE;
	}

	public int getShield() {
		return shield;
	}

}
