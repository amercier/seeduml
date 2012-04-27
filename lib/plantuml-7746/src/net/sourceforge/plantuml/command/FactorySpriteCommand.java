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
 * Revision $Revision: 7558 $
 *
 */
package net.sourceforge.plantuml.command;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.UmlDiagram;
import net.sourceforge.plantuml.command.note.SingleMultiFactoryCommand;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexPartialMatch;
import net.sourceforge.plantuml.ugraphic.Sprite;
import net.sourceforge.plantuml.ugraphic.SpriteGrayLevel;

public final class FactorySpriteCommand implements SingleMultiFactoryCommand<UmlDiagram> {

	private RegexConcat getRegexConcatMultiLine() {
		return new RegexConcat(new RegexLeaf("^"), //
				new RegexLeaf("sprite\\s+\\$?"), //
				new RegexLeaf("NAME", "([\\p{L}0-9_]+)\\s*"), //
				new RegexLeaf("DIM", "(?:\\[(\\d+)x(\\d+)/(\\d+)(z)?\\])?"), //
				new RegexLeaf("\\s*\\{"), //
				new RegexLeaf("$"));
	}

	private RegexConcat getRegexConcatSingleLine() {
		return new RegexConcat(new RegexLeaf("^"), //
				new RegexLeaf("sprite\\s+\\$?"), //
				new RegexLeaf("NAME", "([\\p{L}0-9_]+)\\s*"), //
				new RegexLeaf("DIM", "(?:\\[(\\d+)x(\\d+)/(\\d+)(z)\\])?"), //
				new RegexLeaf("\\s+"), //
				new RegexLeaf("DATA", "([-_A-Za-z0-9]+)"), //
				new RegexLeaf("$"));
	}

	public Command createSingleLine(final UmlDiagram system) {
		return new SingleLineCommand2<UmlDiagram>(system, getRegexConcatSingleLine()) {

			@Override
			protected CommandExecutionResult executeArg(Map<String, RegexPartialMatch> arg) {
				return executeInternal(getSystem(), arg, Arrays.asList(arg.get("DATA").get(0)));
			}

		};
	}

	public Command createMultiLine(final UmlDiagram system) {
		return new CommandMultilines2<UmlDiagram>(system, getRegexConcatMultiLine()) {

			@Override
			public String getPatternEnd() {
				return "(?i)^end ?sprite|\\}$";
			}

			public CommandExecutionResult execute(List<String> lines) {
				StringUtils.trim(lines, true);
				final Map<String, RegexPartialMatch> line0 = getStartingPattern().matcher(lines.get(0).trim());

				final List<String> strings = StringUtils.removeEmptyColumns(lines.subList(1, lines.size() - 1));
				if (strings.size() == 0) {
					return CommandExecutionResult.error("No sprite defined.");
				}
				return executeInternal(getSystem(), line0, strings);
			}

		};
	}

	private CommandExecutionResult executeInternal(UmlDiagram system, Map<String, RegexPartialMatch> line0,
			final List<String> strings) {
		try {
			final Sprite sprite;
			if (line0.get("DIM").get(0) == null) {
				sprite = SpriteGrayLevel.GRAY_16.buildSprite(-1, -1, strings);
			} else {
				final int width = Integer.parseInt(line0.get("DIM").get(0));
				final int height = Integer.parseInt(line0.get("DIM").get(1));
				final int nbColor = Integer.parseInt(line0.get("DIM").get(2));
				if (nbColor != 4 && nbColor != 8 && nbColor != 16) {
					return CommandExecutionResult.error("Only 4, 8 or 16 graylevel are allowed.");
				}
				final SpriteGrayLevel level = SpriteGrayLevel.get(nbColor);
				if (line0.get("DIM").get(3) != null) {
					sprite = level.buildSpriteZ(width, height, concat(strings));
				} else {
					sprite = level.buildSprite(width, height, strings);
				}
			}
			system.addSprite(line0.get("NAME").get(0), sprite);
			return CommandExecutionResult.ok();
		} catch (IOException e) {
			return CommandExecutionResult.error("Cannot decode sprite.");
		}
	}

	private String concat(final List<String> strings) {
		final StringBuilder sb = new StringBuilder();
		for (String s : strings) {
			sb.append(s.trim());
		}
		return sb.toString();
	}

}
