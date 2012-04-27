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
 * Revision $Revision: 5751 $
 *
 */
package net.sourceforge.plantuml.activitydiagram2.command;

import java.util.List;

import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.UniqueSequence;
import net.sourceforge.plantuml.activitydiagram2.ActivityDiagram2;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.CommandMultilines;
import net.sourceforge.plantuml.command.Position;
import net.sourceforge.plantuml.command.note.CommandNote;
import net.sourceforge.plantuml.cucadiagram.Entity;
import net.sourceforge.plantuml.cucadiagram.EntityType;
import net.sourceforge.plantuml.cucadiagram.IEntity;
import net.sourceforge.plantuml.cucadiagram.Link;
import net.sourceforge.plantuml.cucadiagram.LinkDecor;
import net.sourceforge.plantuml.cucadiagram.LinkType;
import net.sourceforge.plantuml.graphic.HtmlColor;

public class CommandMultilinesNoteActivity2 extends CommandMultilines<ActivityDiagram2> implements CommandNote {

	public CommandMultilinesNoteActivity2(final ActivityDiagram2 system) {
		super(system, "(?i)^note\\s+(right|left|top|bottom)\\s*(#\\w+)?\\s*$");
	}
	
	@Override
	public String getPatternEnd() {
		return "(?i)^end ?note$";
	}


	public final CommandExecutionResult execute(List<String> lines) {

		final List<String> line0 = StringUtils.getSplit(getStartingPattern(), lines.get(0).trim());
		final String pos = line0.get(0);

		IEntity activity = getSystem().getLastEntityConsulted();
		if (activity == null) {
			// activity = getSystem().getStart();
			return CommandExecutionResult.error("No activity defined");
		}

		final List<String> strings = StringUtils.removeEmptyColumns(lines.subList(1, lines.size() - 1));
		final String s = StringUtils.getMergedLines(strings);

		final Entity note = getSystem().createEntity("GMN" + UniqueSequence.getValue(), s, EntityType.NOTE);
		note.setSpecificBackcolor(HtmlColor.getColorIfValid(line0.get(1)));

		final Link link;

		final Position position = Position.valueOf(pos.toUpperCase()).withRankdir(getSystem().getRankdir());

		final LinkType type = new LinkType(LinkDecor.NONE, LinkDecor.NONE).getDashed();

		if (position == Position.RIGHT) {
			link = new Link(activity, note, type, null, 1);
		} else if (position == Position.LEFT) {
			link = new Link(note, activity, type, null, 1);
		} else if (position == Position.BOTTOM) {
			link = new Link(activity, note, type, null, 2);
		} else if (position == Position.TOP) {
			link = new Link(note, activity, type, null, 2);
		} else {
			throw new IllegalArgumentException();
		}
		getSystem().addLink(link);
		return CommandExecutionResult.ok();
	}

}
