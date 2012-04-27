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
 * Revision $Revision: 7715 $
 *
 */
package net.sourceforge.plantuml.componentdiagram.command;

import java.util.Map;

import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexPartialMatch;
import net.sourceforge.plantuml.componentdiagram.ComponentDiagram;
import net.sourceforge.plantuml.cucadiagram.Entity;
import net.sourceforge.plantuml.cucadiagram.EntityType;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.HtmlColor;

public class CommandCreateComponent2 extends SingleLineCommand2<ComponentDiagram> {

	public CommandCreateComponent2(ComponentDiagram diagram) {
		super(
				diagram, getRegexConcat());
				// "(?i)^(?:component\\s+)?([\\p{L}0-9_.]+|\\[[^\\]*]+[^\\]]*\\]|\"[^\"]+\")\\s*(?:as\\s+\\[?([\\p{L}0-9_.]+)\\]?)?(?:\\s*([\\<\\[]{2}.*[\\>\\]]{2}))?$");
	}

	private static RegexConcat getRegexConcat() {
		return new RegexConcat(new RegexLeaf("^"), //
				new RegexLeaf("(?:component\\s+)?"), //
				new RegexLeaf("CODE", "([\\p{L}0-9_.]+|\\[[^\\]*]+[^\\]]*\\]|\"[^\"]+\")\\s*"), //
				new RegexLeaf("AS", "(?:as\\s+\\[?([\\p{L}0-9_.]+)\\]?)?"), //
				new RegexLeaf("STEREOTYPE", "(?:\\s*([\\<\\[]{2}.*[\\>\\]]{2}))?"), //
				new RegexLeaf("COLOR", "\\s*(#\\w+)?"), //
				new RegexLeaf("$"));
	}


	@Override
	protected boolean isForbidden(String line) {
		if (line.matches("^[\\p{L}0-9_.]+$")) {
			return true;
		}
		return false;
	}

	@Override
	protected CommandExecutionResult executeArg(Map<String, RegexPartialMatch> arg) {
		final EntityType type = EntityType.COMPONENT;
		final String code;
		final String display;
		if (arg.get("AS").get(0) == null) {
			code = StringUtils.eventuallyRemoveStartingAndEndingDoubleQuote(arg.get("CODE").get(0));
			display = code;
		} else {
			display = StringUtils.eventuallyRemoveStartingAndEndingDoubleQuote(arg.get("CODE").get(0));
			code = StringUtils.eventuallyRemoveStartingAndEndingDoubleQuote(arg.get("AS").get(0));
		}
		final String stereotype = arg.get("STEREOTYPE").get(0);
		final Entity entity = (Entity) getSystem().getOrCreateEntity(code, type);
		entity.setDisplay2(display);
		if (stereotype != null) {
			entity.setStereotype(new Stereotype(stereotype, getSystem().getSkinParam().getCircledCharacterRadius(),
					getSystem().getSkinParam().getFont(FontParam.CIRCLED_CHARACTER, null)));
		}
		entity.setSpecificBackcolor(HtmlColor.getColorIfValid(arg.get("COLOR").get(0)));
		return CommandExecutionResult.ok();
	}

}
