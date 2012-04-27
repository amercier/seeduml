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
 * Revision $Revision: 6104 $
 *
 */
package net.sourceforge.plantuml.project.graphic;

import java.awt.Font;
import java.awt.geom.Dimension2D;
import java.util.Arrays;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

import net.sourceforge.plantuml.SpriteContainerEmpty;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignement;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.project.Day;
import net.sourceforge.plantuml.project.Instant;
import net.sourceforge.plantuml.project.Month;
import net.sourceforge.plantuml.project.Project;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.URectangle;

class TimeScale {

	private final UFont font = new UFont("Serif", Font.PLAIN, 9);
	private final Project project;
	private final FontConfiguration fontConfig = new FontConfiguration(font, HtmlColor.BLACK);

	public TimeScale(Project project) {
		this.project = project;
	}

	public void draw(UGraphic ug, final double x, double y) {
		final StringBounder stringBounder = ug.getStringBounder();
		final double monthHeight = getMonthHeight(stringBounder);
		final double caseWidth = getCaseWidth(stringBounder);
		final double caseHeight = getCaseHeight(stringBounder);
		final int nb = getNbCase();

		ug.getParam().setColor(HtmlColor.BLACK);
		ug.draw(x, y, new URectangle(nb * caseWidth, monthHeight));
		final Instant end = project.getEnd();

		Month printed = null;

		double curx = x;
		for (Instant cur = project.getStart(); cur.compareTo(end) <= 0; cur = cur.next(project.getDayClose())) {
			final Day d = cur.getDay();
			if (printed == null || d.getMonth() != printed) {
				ug.draw(curx, y, new ULine(0, monthHeight));
				printed = d.getMonth();
				final TextBlock b = TextBlockUtils.create(Arrays.asList(printed.name()), fontConfig,
						HorizontalAlignement.LEFT, new SpriteContainerEmpty());
				final Dimension2D dim = b.calculateDimension(stringBounder);
				b.drawU(ug, curx, y + (monthHeight - dim.getHeight()) / 2);
			}
			curx += caseWidth;
		}

		curx = x;
		y += monthHeight;
		ug.draw(x, y, new URectangle(nb * caseWidth, caseHeight));

		for (Instant cur = project.getStart(); cur.compareTo(end) <= 0; cur = cur.next(project.getDayClose())) {
			final Day d = cur.getDay();
			final TextBlock b = TextBlockUtils.create(Arrays.asList("" + d.getNumDay()), fontConfig,
					HorizontalAlignement.LEFT, new SpriteContainerEmpty());
			final Dimension2D dim = b.calculateDimension(stringBounder);
			b.drawU(ug, curx + (caseWidth - dim.getWidth()) / 2, y + (caseHeight - dim.getHeight()) / 2);
			curx += caseWidth;
			ug.draw(curx, y, new ULine(0, caseHeight));
		}
	}

	public SortedMap<Instant, Double> getAbscisse(StringBounder stringBounder) {
		final SortedMap<Instant, Double> pos = new TreeMap<Instant, Double>();
		final double caseWidth = getCaseWidth(stringBounder);
		final Instant end = project.getEnd();
		double x = 0;
		for (Instant cur = project.getStart(); cur.compareTo(end) <= 0; cur = cur.next(project.getDayClose())) {
			pos.put(cur, x);
			x += caseWidth;
		}
		return Collections.unmodifiableSortedMap(pos);
	}

	private int getNbCase() {
		int result = 0;
		final Instant end = project.getEnd();
		for (Instant cur = project.getStart(); cur.compareTo(end) <= 0; cur = cur.next(project.getDayClose())) {
			result++;
		}
		return result;
	}

	private double getCaseWidth(StringBounder stringBounder) {
		final Dimension2D dim00 = stringBounder.calculateDimension(font, "00");
		return dim00.getWidth() + 3;
	}

	private double getCaseHeight(StringBounder stringBounder) {
		final Dimension2D dim00 = stringBounder.calculateDimension(font, "00");
		return dim00.getHeight() + 3;
	}

	private double getMonthHeight(StringBounder stringBounder) {
		final Dimension2D dimZZ = stringBounder.calculateDimension(font, "ZZ");
		return dimZZ.getHeight() + 3;
	}

	public double getWidth(StringBounder stringBounder) {
		return getCaseWidth(stringBounder) * getNbCase();
	}

	public double getHeight(StringBounder stringBounder) {
		return getCaseHeight(stringBounder) + getMonthHeight(stringBounder);
	}

}