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
package net.sourceforge.plantuml.statediagram;

import net.sourceforge.plantuml.classdiagram.command.CommandUrl;
import net.sourceforge.plantuml.command.AbstractUmlSystemCommandFactory;
import net.sourceforge.plantuml.command.note.FactoryNoteOnEntityCommand;
import net.sourceforge.plantuml.command.note.FactoryNoteOnLinkCommand;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexOr;
import net.sourceforge.plantuml.statediagram.command.CommandAddField;
import net.sourceforge.plantuml.statediagram.command.CommandConcurrentState;
import net.sourceforge.plantuml.statediagram.command.CommandCreatePackageState;
import net.sourceforge.plantuml.statediagram.command.CommandCreatePackageState2;
import net.sourceforge.plantuml.statediagram.command.CommandCreateState;
import net.sourceforge.plantuml.statediagram.command.CommandCreateState2;
import net.sourceforge.plantuml.statediagram.command.CommandEndState;
import net.sourceforge.plantuml.statediagram.command.CommandHideEmptyDescription;
import net.sourceforge.plantuml.statediagram.command.CommandLinkState2;
import net.sourceforge.plantuml.usecasediagram.command.CommandRankDirUsecase;

public class StateDiagramFactory extends AbstractUmlSystemCommandFactory {

	private StateDiagram system;

	public StateDiagram getSystem() {
		return system;
	}

	@Override
	protected void initCommands() {
		system = new StateDiagram();

		addCommand(new CommandRankDirUsecase(system));
		addCommand(new CommandCreateState(system));
		addCommand(new CommandCreateState2(system));
		// addCommand(new CommandLinkState(system));
		addCommand(new CommandLinkState2(system));
		addCommand(new CommandCreatePackageState(system));
		addCommand(new CommandCreatePackageState2(system));
		addCommand(new CommandEndState(system));
		addCommand(new CommandAddField(system));
		addCommand(new CommandConcurrentState(system));

		final FactoryNoteOnEntityCommand factoryNoteCommand = new FactoryNoteOnEntityCommand(new RegexOr("ENTITY",
				new RegexLeaf("[\\p{L}0-9_.]+"), //
				new RegexLeaf("\"[^\"]+\"") //
				));
		addCommand(factoryNoteCommand.createMultiLine(system));

		addCommand(new CommandHideEmptyDescription(system));

		addCommand(factoryNoteCommand.createSingleLine(system));
		final FactoryNoteOnLinkCommand factoryNoteOnLinkCommand = new FactoryNoteOnLinkCommand();
		addCommand(factoryNoteOnLinkCommand.createSingleLine(system));
		addCommand(factoryNoteOnLinkCommand.createMultiLine(system));
		addCommand(new CommandUrl(system));
		addCommonCommands(system);
	}
}
