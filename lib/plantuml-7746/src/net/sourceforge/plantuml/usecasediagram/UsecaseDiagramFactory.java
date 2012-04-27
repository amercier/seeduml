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
 */
package net.sourceforge.plantuml.usecasediagram;

import net.sourceforge.plantuml.classdiagram.command.CommandUrl;
import net.sourceforge.plantuml.command.AbstractUmlSystemCommandFactory;
import net.sourceforge.plantuml.command.CommandEndPackage;
import net.sourceforge.plantuml.command.CommandPackage;
import net.sourceforge.plantuml.command.CommandPage;
import net.sourceforge.plantuml.command.note.FactoryNoteCommand;
import net.sourceforge.plantuml.command.note.FactoryNoteOnEntityCommand;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexOr;
import net.sourceforge.plantuml.usecasediagram.command.CommandCreateActor;
import net.sourceforge.plantuml.usecasediagram.command.CommandCreateActor2;
import net.sourceforge.plantuml.usecasediagram.command.CommandCreateUsecase;
import net.sourceforge.plantuml.usecasediagram.command.CommandCreateUsecase2;
import net.sourceforge.plantuml.usecasediagram.command.CommandLinkUsecase2;
import net.sourceforge.plantuml.usecasediagram.command.CommandRankDirUsecase;

public class UsecaseDiagramFactory extends AbstractUmlSystemCommandFactory {

	private UsecaseDiagram system;

	public UsecaseDiagram getSystem() {
		return system;
	}

	@Override
	protected void initCommands() {
		system = new UsecaseDiagram();

		addCommand(new CommandRankDirUsecase(system));
		addCommonCommands(system);

		addCommand(new CommandPage(system));
		// addCommand(new CommandLinkUsecase(system));
		addCommand(new CommandLinkUsecase2(system));

		addCommand(new CommandPackage(system));
		addCommand(new CommandEndPackage(system));
		final FactoryNoteOnEntityCommand factoryNoteOnEntityCommand = new FactoryNoteOnEntityCommand(new RegexOr(
				"ENTITY", new RegexLeaf("[\\p{L}0-9_.]+"), //
				new RegexLeaf("\\((?!\\*\\))[^\\)]+\\)"), //
				new RegexLeaf(":[^:]+:"), //
				new RegexLeaf("\"[^\"]+\"") //
				));
		addCommand(factoryNoteOnEntityCommand.createSingleLine(system));
		addCommand(new CommandUrl(system));

		final FactoryNoteCommand factoryNoteCommand = new FactoryNoteCommand();
		addCommand(factoryNoteCommand.createSingleLine(system));
		addCommand(new CommandCreateActor(system));
		addCommand(new CommandCreateActor2(system));
		addCommand(new CommandCreateUsecase(system));
		addCommand(new CommandCreateUsecase2(system));

		addCommand(factoryNoteOnEntityCommand.createMultiLine(system));
		addCommand(factoryNoteCommand.createMultiLine(system));
	}
}
