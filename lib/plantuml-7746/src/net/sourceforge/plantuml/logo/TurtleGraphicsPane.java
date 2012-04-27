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
 * Revision $Revision: 4041 $
 *
 */
package net.sourceforge.plantuml.logo;

import java.awt.Font;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.plantuml.SpriteContainerEmpty;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignement;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UPolygon;

class TurtleGraphicsPane {
	final private double width;
	final private double height;
	private double x;
	private double y;
	private double turtleDirection = 90;
	private boolean penIsDown = true;
	private boolean showTurtle = true;
	private HtmlColor penColor = HtmlColor.BLACK;
	private List<Rectangle2D.Double> lines = new ArrayList<Rectangle2D.Double>();
	private List<HtmlColor> colors = new ArrayList<HtmlColor>();

	private String message;

	public TurtleGraphicsPane(int width, int height) {
		this.width = width;
		this.height = height;
		clearScreen();
	}

	public void clearScreen() {
		x = width / 2;
		y = -height / 2;
		turtleDirection = 90;
		lines.clear();
		colors.clear();
	}

	private double dtor(double degrees) {
		return degrees * Math.PI / 180.0;
	}

	private void drawTurtle(UGraphic ug) {
		if (showTurtle == false) {
			return;
		}
		final UPolygon poly = new UPolygon();
		double size = 2;
		double deltax = 4.5 * size;
		poly.addPoint(0 * size - deltax, 0);
		poly.addPoint(0 * size - deltax, -2 * size);
		poly.addPoint(1 * size - deltax, -2 * size);
		poly.addPoint(1 * size - deltax, -4 * size);
		poly.addPoint(2 * size - deltax, -4 * size);
		poly.addPoint(2 * size - deltax, -6 * size);
		poly.addPoint(3 * size - deltax, -6 * size);
		poly.addPoint(3 * size - deltax, -8 * size);
		poly.addPoint(4 * size - deltax, -8 * size);
		poly.addPoint(4 * size - deltax, -9 * size);
		poly.addPoint(5 * size - deltax, -9 * size);
		poly.addPoint(5 * size - deltax, -8 * size);
		poly.addPoint(6 * size - deltax, -8 * size);
		poly.addPoint(6 * size - deltax, -6 * size);
		poly.addPoint(7 * size - deltax, -6 * size);
		poly.addPoint(7 * size - deltax, -4 * size);
		poly.addPoint(8 * size - deltax, -4 * size);
		poly.addPoint(8 * size - deltax, -2 * size);
		poly.addPoint(9 * size - deltax, -2 * size);
		poly.addPoint(9 * size - deltax, 0);
		poly.addPoint(0 * size - deltax, 0);
		final double angle = -dtor(turtleDirection - 90);
		poly.rotate(angle);
		// ug.setAntiAliasing(false);
		final HtmlColor turtleColor1 = HtmlColor.getColorIfValid("OliveDrab");
		final HtmlColor turtleColor2 = HtmlColor.getColorIfValid("MediumSpringGreen");

		ug.getParam().setColor(turtleColor1);
		ug.getParam().setBackcolor(turtleColor2);
		ug.draw(x, -y, poly);
		// ug.setAntiAliasing(true);
	}

	public void showTurtle() {
		showTurtle = true;
	}

	public void hideTurtle() {
		showTurtle = false;
	}

	public void setPenColor(HtmlColor newPenColor) {
		penColor = newPenColor;
	}

	void addLine(double x1, double y1, double x2, double y2) {
		lines.add(new Rectangle2D.Double(x1, y1, x2, y2));
		colors.add(penColor);
	}

	public void forward(double distance) {
		double angle = dtor(turtleDirection);
		double newX = x + distance * Math.cos(angle);
		double newY = y + distance * Math.sin(angle);
		if (penIsDown) {
			addLine(x, y, newX, newY);
			x = newX;
			y = newY;
		} else {
			x = newX;
			y = newY;
		}
	}

	public void back(double distance) {
		forward(-distance);
	}

	public void left(double turnAngle) {
		turtleDirection += turnAngle;
		while (turtleDirection > 360) {
			turtleDirection -= 360;
		}
		while (turtleDirection < 0) {
			turtleDirection += 360;
		}
	}

	public void right(double turnAngle) {
		left(-turnAngle);
	}

	public void penUp() {
		penIsDown = false;
	}

	public void penDown() {
		penIsDown = true;
	}

	public void paint(UGraphic ug) {
		int n = lines.size();

		for (int i = 0; i < n; i++) {
			final HtmlColor color = colors.get(i);
			final Rectangle2D.Double r = lines.get(i);
			ug.getParam().setColor(color);
			final ULine line = new ULine(r.width - r.x, -r.height + r.y);
			ug.draw(r.x, -r.y, line);

		}
		drawTurtle(ug);
		if (message != null) {
			final FontConfiguration font = new FontConfiguration(new UFont("", Font.PLAIN, 14), HtmlColor.BLACK);
			final TextBlock text = TextBlockUtils.create(Arrays.asList(message), font, HorizontalAlignement.LEFT, new SpriteContainerEmpty());
			final Dimension2D dim = text.calculateDimension(ug.getStringBounder());
			final double textHeight = dim.getHeight();
			text.drawU(ug, 0, height - textHeight);
		}
	}

	public void message(String messageText) {
		this.message = messageText;
	}

}
