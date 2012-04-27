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
package net.sourceforge.plantuml.sequencediagram;

import net.sourceforge.plantuml.command.AbstractUmlSystemCommandFactory;
import net.sourceforge.plantuml.command.note.sequence.FactorySequenceNoteCommand;
import net.sourceforge.plantuml.command.note.sequence.FactorySequenceNoteOnArrowCommand;
import net.sourceforge.plantuml.command.note.sequence.FactorySequenceNoteOverSeveralCommand;
import net.sourceforge.plantuml.sequencediagram.command.CommandActivate;
import net.sourceforge.plantuml.sequencediagram.command.CommandActivate2;
import net.sourceforge.plantuml.sequencediagram.command.CommandArrow;
import net.sourceforge.plantuml.sequencediagram.command.CommandAutoNewpage;
import net.sourceforge.plantuml.sequencediagram.command.CommandAutoactivate;
import net.sourceforge.plantuml.sequencediagram.command.CommandAutonumber;
import net.sourceforge.plantuml.sequencediagram.command.CommandBoxEnd;
import net.sourceforge.plantuml.sequencediagram.command.CommandBoxStart;
import net.sourceforge.plantuml.sequencediagram.command.CommandDelay;
import net.sourceforge.plantuml.sequencediagram.command.CommandDivider;
import net.sourceforge.plantuml.sequencediagram.command.CommandExoArrowLeft;
import net.sourceforge.plantuml.sequencediagram.command.CommandExoArrowRight;
import net.sourceforge.plantuml.sequencediagram.command.CommandFootbox;
import net.sourceforge.plantuml.sequencediagram.command.CommandFootboxOld;
import net.sourceforge.plantuml.sequencediagram.command.CommandGrouping;
import net.sourceforge.plantuml.sequencediagram.command.CommandIgnoreNewpage;
import net.sourceforge.plantuml.sequencediagram.command.CommandNewpage;
import net.sourceforge.plantuml.sequencediagram.command.CommandParticipantA;
import net.sourceforge.plantuml.sequencediagram.command.CommandParticipantA2;
import net.sourceforge.plantuml.sequencediagram.command.CommandParticipantA3;
import net.sourceforge.plantuml.sequencediagram.command.CommandParticipantA4;
import net.sourceforge.plantuml.sequencediagram.command.CommandReferenceMultilinesOverSeveral;
import net.sourceforge.plantuml.sequencediagram.command.CommandReferenceOverSeveral;
import net.sourceforge.plantuml.sequencediagram.command.CommandReturn;
import net.sourceforge.plantuml.sequencediagram.command.CommandSkin;
import net.sourceforge.plantuml.sequencediagram.command.CommandUrl;

public class SequenceDiagramFactory extends AbstractUmlSystemCommandFactory {

	private SequenceDiagram system;

	@Override
	protected void initCommands() {
		system = new SequenceDiagram();

		addCommonCommands(system);

		addCommand(new CommandActivate(system));
		
		addCommand(new CommandParticipantA(system));
		addCommand(new CommandParticipantA2(system));
		addCommand(new CommandParticipantA3(system));
		addCommand(new CommandParticipantA4(system));
		addCommand(new CommandArrow(system));
		// addCommand(new CommandArrowCrossX(system));
		addCommand(new CommandExoArrowLeft(system));
		addCommand(new CommandExoArrowRight(system));
		
		final FactorySequenceNoteCommand factorySequenceNoteCommand = new FactorySequenceNoteCommand();
		addCommand(factorySequenceNoteCommand.createSingleLine(system));
		
		final FactorySequenceNoteOverSeveralCommand factorySequenceNoteOverSeveralCommand = new FactorySequenceNoteOverSeveralCommand();
		addCommand(factorySequenceNoteOverSeveralCommand.createSingleLine(system));

		addCommand(new CommandBoxStart(system));
		addCommand(new CommandBoxEnd(system));
		addCommand(new CommandGrouping(system));

		addCommand(new CommandActivate2(system));
		addCommand(new CommandReturn(system));

		final FactorySequenceNoteOnArrowCommand factorySequenceNoteOnArrowCommand = new FactorySequenceNoteOnArrowCommand();
		addCommand(factorySequenceNoteOnArrowCommand.createSingleLine(system));

		addCommand(factorySequenceNoteCommand.createMultiLine(system));
		addCommand(factorySequenceNoteOverSeveralCommand.createMultiLine(system));
		addCommand(factorySequenceNoteOnArrowCommand.createMultiLine(system));

		addCommand(new CommandNewpage(system));
		addCommand(new CommandIgnoreNewpage(system));
		addCommand(new CommandAutoNewpage(system));
		addCommand(new CommandDivider(system));
		addCommand(new CommandReferenceOverSeveral(system));
		addCommand(new CommandReferenceMultilinesOverSeveral(system));
		addCommand(new CommandSkin(system));
		addCommand(new CommandAutonumber(system));
		addCommand(new CommandAutoactivate(system));
		addCommand(new CommandFootbox(system));
		addCommand(new CommandDelay(system));
		addCommand(new CommandFootboxOld(system));
		addCommand(new CommandUrl(system));

	}

	public SequenceDiagram getSystem() {
		return system;
	}

	@Override
	public String checkFinalError() {
		if (system.isHideUnlinkedData()) {
			system.removeHiddenParticipants();
		}
		return super.checkFinalError();
	}

}
