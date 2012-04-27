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
 * Revision $Revision: 4192 $
 *
 */
package net.sourceforge.plantuml.objectdiagram;

import net.sourceforge.plantuml.classdiagram.command.CommandLinkClass3;
import net.sourceforge.plantuml.classdiagram.command.CommandUrl;
import net.sourceforge.plantuml.command.AbstractUmlSystemCommandFactory;
import net.sourceforge.plantuml.command.CommandEndPackage;
import net.sourceforge.plantuml.command.CommandPackage;
import net.sourceforge.plantuml.command.CommandPage;
import net.sourceforge.plantuml.command.note.FactoryNoteCommand;
import net.sourceforge.plantuml.command.note.FactoryNoteOnEntityCommand;
import net.sourceforge.plantuml.command.note.FactoryNoteOnLinkCommand;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.objectdiagram.command.CommandAddData;
import net.sourceforge.plantuml.objectdiagram.command.CommandCreateEntityObject;
import net.sourceforge.plantuml.objectdiagram.command.CommandCreateEntityObjectMultilines;

public class ObjectDiagramFactory extends AbstractUmlSystemCommandFactory {

	private ObjectDiagram system;

	public ObjectDiagram getSystem() {
		return system;
	}

	@Override
	protected void initCommands() {
		system = new ObjectDiagram();

		addCommonCommands(system);

		addCommand(new CommandPage(system));
		addCommand(new CommandAddData(system));
		addCommand(new CommandLinkClass3(system));
		//
		addCommand(new CommandCreateEntityObject(system));
		final FactoryNoteCommand factoryNoteCommand = new FactoryNoteCommand();

		addCommand(factoryNoteCommand.createSingleLine(system));
		addCommand(new CommandPackage(system));
		addCommand(new CommandEndPackage(system));
		// addCommand(new CommandNamespace(system));
		// addCommand(new CommandEndNamespace(system));
		// addCommand(new CommandStereotype(system));
		//
		// addCommand(new CommandImport(system));
		final FactoryNoteOnEntityCommand factoryNoteOnEntityCommand = new FactoryNoteOnEntityCommand(new RegexLeaf(
				"ENTITY", "([\\p{L}0-9_.]+|\"[^\"]+\")"));
		addCommand(factoryNoteOnEntityCommand.createSingleLine(system));

		addCommand(new CommandUrl(system));

		addCommand(factoryNoteCommand.createMultiLine(system));
		addCommand(factoryNoteOnEntityCommand.createMultiLine(system));
		addCommand(new CommandCreateEntityObjectMultilines(system));
		
		final FactoryNoteOnLinkCommand factoryNoteOnLinkCommand = new FactoryNoteOnLinkCommand();
		addCommand(factoryNoteOnLinkCommand.createSingleLine(system));
		addCommand(factoryNoteOnLinkCommand.createMultiLine(system));


		// addCommand(new CommandNoopClass(system));

	}
}
