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
package net.sourceforge.plantuml.command;

import java.util.List;
import java.util.regex.Matcher;

import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.UmlDiagram;
import net.sourceforge.plantuml.graphic.HorizontalAlignement;

public class CommandMultilinesFooter extends CommandMultilines<UmlDiagram> {

	public CommandMultilinesFooter(final UmlDiagram diagram) {
		super(diagram, "(?i)^(?:(left|right|center)?\\s*)footer$");
	}
	
	@Override
	public String getPatternEnd() {
		return "(?i)^end ?footer$";
	}


	public CommandExecutionResult execute(List<String> lines) {
		StringUtils.trim(lines, false);
		final Matcher m = getStartingPattern().matcher(lines.get(0).trim());
		if (m.find() == false) {
			throw new IllegalStateException();
		}
		final String align = m.group(1);
		if (align != null) {
			getSystem().setFooterAlignement(HorizontalAlignement.valueOf(align.toUpperCase()));
		}
		final List<String> strings = lines.subList(1, lines.size() - 1);
		if (strings.size() > 0) {
			getSystem().setFooter(strings);
			return CommandExecutionResult.ok();
		}
		return CommandExecutionResult.error("Empty footer");
	}

}
