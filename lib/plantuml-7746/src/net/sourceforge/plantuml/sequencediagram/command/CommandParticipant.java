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
 * Revision $Revision: 6109 $
 *
 */
package net.sourceforge.plantuml.sequencediagram.command;

import java.util.List;
import java.util.Map;

import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexPartialMatch;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.sequencediagram.LifeEventType;
import net.sourceforge.plantuml.sequencediagram.Participant;
import net.sourceforge.plantuml.sequencediagram.ParticipantType;
import net.sourceforge.plantuml.sequencediagram.SequenceDiagram;
import net.sourceforge.plantuml.ugraphic.UFont;

public abstract class CommandParticipant extends SingleLineCommand2<SequenceDiagram> {

	public CommandParticipant(SequenceDiagram sequenceDiagram, RegexConcat pattern) {
		super(sequenceDiagram, pattern);
	}

	@Override
	final protected CommandExecutionResult executeArg(Map<String, RegexPartialMatch> arg2) {
		final String code = arg2.get("CODE").get(0);
		if (getSystem().participants().containsKey(code)) {
			getSystem().putParticipantInLast(code);
			return CommandExecutionResult.ok();
		}

		List<String> strings = null;
		if (arg2.get("FULL") != null && arg2.get("FULL").get(0) != null) {
			strings = StringUtils.getWithNewlines(arg2.get("FULL").get(0));
		}

		final String typeString = arg2.get("TYPE").get(0).toUpperCase();
		final ParticipantType type;
		final boolean create;
		if (typeString.equals("CREATE")) {
			type = ParticipantType.PARTICIPANT;
			create = true;
		} else {
			type = ParticipantType.valueOf(typeString);
			create = false;
		}
		final Participant participant = getSystem().createNewParticipant(type, code, strings);

		final String stereotype = arg2.get("STEREO").get(0);

		if (stereotype != null) {
			final ISkinParam skinParam = getSystem().getSkinParam();
			final boolean stereotypePositionTop = skinParam.stereotypePositionTop();
			final UFont font = skinParam.getFont(FontParam.CIRCLED_CHARACTER, null);
			participant.setStereotype(new Stereotype(stereotype, skinParam.getCircledCharacterRadius(), font),
					stereotypePositionTop);
		}
		participant.setSpecificBackcolor(HtmlColor.getColorIfValid(arg2.get("COLOR").get(0)));

		if (create) {
			final String error = getSystem().activate(participant, LifeEventType.CREATE, null);
			if (error != null) {
				return CommandExecutionResult.error(error);
			}

		}

		return CommandExecutionResult.ok();
	}

}
