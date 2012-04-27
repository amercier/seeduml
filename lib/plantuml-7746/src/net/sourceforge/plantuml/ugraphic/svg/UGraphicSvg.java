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

import java.awt.Graphics2D;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.transform.TransformerException;

import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.StringBounderUtils;
import net.sourceforge.plantuml.graphic.UnusedSpace;
import net.sourceforge.plantuml.posimo.DotPath;
import net.sourceforge.plantuml.svg.SvgGraphics;
import net.sourceforge.plantuml.ugraphic.AbstractUGraphic;
import net.sourceforge.plantuml.ugraphic.ClipContainer;
import net.sourceforge.plantuml.ugraphic.ColorMapper;
import net.sourceforge.plantuml.ugraphic.UClip;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGroup;
import net.sourceforge.plantuml.ugraphic.UImage;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UPath;
import net.sourceforge.plantuml.ugraphic.UPolygon;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UShape;
import net.sourceforge.plantuml.ugraphic.UText;

public class UGraphicSvg extends AbstractUGraphic<SvgGraphics> implements ClipContainer {

	final static Graphics2D imDummy = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB).createGraphics();
	private UClip clip;

	private final StringBounder stringBounder;

	public UGraphicSvg(ColorMapper colorMapper, String backcolor, boolean textAsPath) {
		this(colorMapper, new SvgGraphics(backcolor), textAsPath);
	}

	public UGraphicSvg(ColorMapper colorMapper, boolean textAsPath) {
		this(colorMapper, new SvgGraphics(), textAsPath);
	}

	private UGraphicSvg(ColorMapper colorMapper, SvgGraphics svg, boolean textAsPath) {
		super(colorMapper, svg);
		stringBounder = StringBounderUtils.asStringBounder(imDummy);
		registerDriver(URectangle.class, new DriverRectangleSvg(this));
		textAsPath = false;
		if (textAsPath) {
			registerDriver(UText.class, new DriverTextAsPathSvg(imDummy.getFontRenderContext(), this));
		} else {
			registerDriver(UText.class, new DriverTextSvg(getStringBounder(), this));
		}
		registerDriver(ULine.class, new DriverLineSvg(this));
		registerDriver(UPolygon.class, new DriverPolygonSvg(this));
		registerDriver(UEllipse.class, new DriverEllipseSvg());
		registerDriver(UImage.class, new DriverImageSvg());
		registerDriver(UPath.class, new DriverPathSvg(this));
		registerDriver(DotPath.class, new DriverDotPathSvg());
	}

	public SvgGraphics getSvgGraphics() {
		return this.getGraphicObject();
	}

	public StringBounder getStringBounder() {
		return stringBounder;
	}

	public void createXml(OutputStream os) throws IOException {
		try {
			getGraphicObject().createXml(os);
		} catch (TransformerException e) {
			throw new IOException(e.toString());
		}
	}

	public void setClip(UClip clip) {
		this.clip = clip == null ? null : clip.translate(getTranslateX(), getTranslateY());
	}

	public UClip getClip() {
		return clip;
	}

	// public void centerCharOld(double x, double y, char c, Font font) {
	// final UText uText = new UText("" + c, new FontConfiguration(font,
	// getParam().getColor()));
	// final UnusedSpace unusedSpace = UnusedSpace.getUnusedSpace(font, c);
	// draw(x - unusedSpace.getCenterX() + getTranslateX(), y -
	// unusedSpace.getCenterY() + getTranslateY(), uText);
	// }

	public void centerChar(double x, double y, char c, UFont font) {
		final UnusedSpace unusedSpace = UnusedSpace.getUnusedSpace(font, c);

		final double xpos = x - unusedSpace.getCenterX() - 0.5;
		final double ypos = y - unusedSpace.getCenterY() - 0.5;

		final TextLayout t = new TextLayout("" + c, font.getFont(), imDummy.getFontRenderContext());
		getGraphicObject()
				.setStrokeColor(StringUtils.getAsHtml(getColorMapper().getMappedColor(getParam().getColor())));
		DriverTextAsPathSvg.drawPathIterator(getGraphicObject(), xpos + getTranslateX(), ypos + getTranslateY(), t
				.getOutline(null).getPathIterator(null));
	}

	public void setAntiAliasing(boolean trueForOn) {
	}

	public void startUrl(String url, String tooltip) {
		getGraphicObject().openLink(url, tooltip);
	}

	public void closeAction() {
		getGraphicObject().closeLink();
	}
	
	class SvgGroup implements UGroup {
		public void draw(double x, double y, UShape shape) {
		}

		public void close() {
		}

		public void centerChar(double x, double y, char c, UFont font) {
		}
	}
	
	@Override
	public UGroup createGroup() {
		return new SvgGroup();
	}

//	@Override
//	public String startHiddenGroup() {
//		getGraphicObject().startHiddenGroup();
//		return null;
//	}
//
//	@Override
//	public String closeHiddenGroup() {
//		getGraphicObject().closeHiddenGroup();
//		return null;
//	}

}
