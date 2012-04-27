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
 * Revision $Revision: 4636 $
 *
 */
package net.sourceforge.plantuml.sequencediagram.command;

import java.util.Arrays;
import java.util.List;

import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand;
import net.sourceforge.plantuml.sequencediagram.MessageExo;
import net.sourceforge.plantuml.sequencediagram.MessageExoType;
import net.sourceforge.plantuml.sequencediagram.Participant;
import net.sourceforge.plantuml.sequencediagram.SequenceDiagram;
import net.sourceforge.plantuml.skin.ArrowConfiguration;
import net.sourceforge.plantuml.skin.ArrowDirection;
import net.sourceforge.plantuml.skin.ArrowHead;
import net.sourceforge.plantuml.skin.ArrowPart;

abstract class CommandExoArrowAny extends SingleLineCommand<SequenceDiagram> {

	private final int posArrow;
	private final int posParticipant;

	public CommandExoArrowAny(SequenceDiagram sequenceDiagram, String pattern, int posArrow, int posParticipant) {
		super(sequenceDiagram, pattern);
		this.posArrow = posArrow;
		this.posParticipant = posParticipant;
	}

	@Override
	final protected CommandExecutionResult executeArg(List<String> arg) {
		final String arrow = StringUtils.manageArrowForSequence(arg.get(posArrow));
		final Participant p = getSystem().getOrCreateParticipant(
				StringUtils.eventuallyRemoveStartingAndEndingDoubleQuote(arg.get(posParticipant)));

		final boolean sync = arrow.contains(">>") || arrow.contains("<<") || arrow.contains("//")
				|| arrow.contains("\\\\");
		final boolean dotted = arrow.contains("--");

		final List<String> labels;
		if (arg.get(2) == null) {
			labels = Arrays.asList("");
		} else {
			labels = StringUtils.getWithNewlines(arg.get(2));
		}

		ArrowConfiguration config = ArrowConfiguration.withDirection(ArrowDirection.LEFT_TO_RIGHT_NORMAL);
		if (dotted) {
			config = config.withDotted();
		}
		if (sync) {
			config = config.withHead(ArrowHead.ASYNC);
		}
		config = config.withPart(getArrowPart(arrow));

		final String error = getSystem().addMessage(
				new MessageExo(p, getMessageExoType(arrow), labels, config, getSystem().getNextMessageNumber()));
		if (error != null) {
			return CommandExecutionResult.error(error);
		}
		return CommandExecutionResult.ok();
	}

	private ArrowPart getArrowPart(String arrow) {
		if (arrow.contains("/")) {
			return ArrowPart.BOTTOM_PART;
		}
		if (arrow.contains("\\")) {
			return ArrowPart.TOP_PART;
		}
		return ArrowPart.FULL;
	}

	abstract MessageExoType getMessageExoType(String arrow);

}
