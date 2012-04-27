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
 * Revision $Revision: 4762 $
 *
 */
package net.sourceforge.plantuml.activitydiagram2.command;

import java.util.Map;

import net.sourceforge.plantuml.activitydiagram2.ActivityDiagram2;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexPartialMatch;

public class CommandIf2 extends SingleLineCommand2<ActivityDiagram2> {

	public CommandIf2(ActivityDiagram2 diagram) {
		super(diagram, getRegexConcat());
	}

	static RegexConcat getRegexConcat() {
		return new RegexConcat(new RegexLeaf("^"),
					new RegexLeaf("if"),
					new RegexLeaf("\\s*"),
					new RegexLeaf("TEST", "[\"(](.+)[\")]"),
					new RegexLeaf("\\s*"),
					new RegexLeaf("WHEN", "(?:then\\s*(?:when\\s+(.*))?)?"),
					new RegexLeaf("$"));
	}


	@Override
	protected CommandExecutionResult executeArg(Map<String, RegexPartialMatch> arg) {
//
		getSystem().startIf(arg.get("TEST").get(0), arg.get("WHEN").get(0));
//
//		int lenght = 2;
//
//		if (arg.get("ARROW").get(0) != null) {
//			final String arrow = StringUtils.manageArrowForCuca(arg.get("ARROW").get(0));
//			lenght = arrow.length() - 1;
//		}
//
//		final IEntity branch = getSystem().getCurrentContext().getBranch();
//
//		Link link = new Link(entity1, branch, new LinkType(LinkDecor.ARROW, LinkDecor.NONE), arg.get("BRACKET").get(0),
//				lenght, null, arg.get("IF").get(0), getSystem().getLabeldistance(), getSystem().getLabelangle());
//		if (arg.get("ARROW").get(0) != null) {
//			final Direction direction = StringUtils.getArrowDirection(arg.get("ARROW").get(0));
//			if (direction == Direction.LEFT || direction == Direction.UP) {
//				link = link.getInv();
//			}
//		}
//
//		getSystem().addLink(link);

		return CommandExecutionResult.ok();
	}

}
