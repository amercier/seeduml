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
package net.sourceforge.plantuml.command.note.sequence;

import java.util.List;
import java.util.Map;

import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.command.Command;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.CommandMultilines2;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.command.note.SingleMultiFactoryCommand;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexPartialMatch;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.sequencediagram.Note;
import net.sourceforge.plantuml.sequencediagram.NotePosition;
import net.sourceforge.plantuml.sequencediagram.NoteStyle;
import net.sourceforge.plantuml.sequencediagram.Participant;
import net.sourceforge.plantuml.sequencediagram.SequenceDiagram;

public final class FactorySequenceNoteCommand implements SingleMultiFactoryCommand<SequenceDiagram> {

	private RegexConcat getRegexConcatMultiLine() {
		return new RegexConcat(new RegexLeaf("STYLE", "^(note|hnote|rnote)\\s+"), //
				new RegexLeaf("POSITION", "(right|left|over)\\s+"), //
				new RegexLeaf("PARTICIPANT", "(?:of\\s+)?([\\p{L}0-9_.@]+|\"[^\"]+\")\\s*"), //
				new RegexLeaf("COLOR", "(#\\w+)?"), //
				new RegexLeaf("$"));
	}

	private RegexConcat getRegexConcatSingleLine() {
		return new RegexConcat(new RegexLeaf("STYLE", "^(note|hnote|rnote)\\s+"), //
				new RegexLeaf("POSITION", "(right|left|over)\\s+"), //
				new RegexLeaf("PARTICIPANT", "(?:of\\s+)?([\\p{L}0-9_.@]+|\"[^\"]+\")\\s*"), //
				new RegexLeaf("COLOR", "(#\\w+)?\\s*:\\s*"), //
				new RegexLeaf("NOTE", "(.*)"), //
				new RegexLeaf("$"));
	}

	public Command createMultiLine(final SequenceDiagram system) {
		return new CommandMultilines2<SequenceDiagram>(system, getRegexConcatMultiLine()) {

			@Override
			public String getPatternEnd() {
				return "(?i)^end ?(note|hnote|rnote)$";
			}

			public CommandExecutionResult execute(List<String> lines) {
				final Map<String, RegexPartialMatch> line0 = getStartingPattern().matcher(lines.get(0).trim());
				final List<String> strings = StringUtils.removeEmptyColumns(lines.subList(1, lines.size() - 1));
				return executeInternal(system, line0, strings);
			}
		};
	}

	public Command createSingleLine(final SequenceDiagram system) {
		return new SingleLineCommand2<SequenceDiagram>(system, getRegexConcatSingleLine()) {

			@Override
			protected CommandExecutionResult executeArg(Map<String, RegexPartialMatch> arg) {
				final List<String> strings = StringUtils.getWithNewlines(arg.get("NOTE").get(0));
				return executeInternal(system, arg, strings);
			}

		};
	}

	private CommandExecutionResult executeInternal(SequenceDiagram system, Map<String, RegexPartialMatch> arg,
			final List<String> strings) {
		final Participant p = system.getOrCreateParticipant(StringUtils
				.eventuallyRemoveStartingAndEndingDoubleQuote(arg.get("PARTICIPANT").get(0)));

		final NotePosition position = NotePosition.valueOf(arg.get("POSITION").get(0).toUpperCase());

		if (strings.size() > 0) {
			final Note note = new Note(p, position, strings);
			note.setSpecificBackcolor(HtmlColor.getColorIfValid(arg.get("COLOR").get(0)));
			note.setStyle(NoteStyle.getNoteStyle(arg.get("STYLE").get(0)));
			system.addNote(note);
		}
		return CommandExecutionResult.ok();
	}

}
