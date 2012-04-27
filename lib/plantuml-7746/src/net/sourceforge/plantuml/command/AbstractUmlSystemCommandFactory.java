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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.DiagramType;
import net.sourceforge.plantuml.UmlDiagram;

public abstract class AbstractUmlSystemCommandFactory implements PSystemCommandFactory {

	private final DiagramType type;
	private List<Command> cmds;

	protected AbstractUmlSystemCommandFactory() {
		this(DiagramType.UML);
	}

	protected AbstractUmlSystemCommandFactory(DiagramType type) {
		this.type = type;
	}

	public String checkFinalError() {
		return null;
	}

	final public CommandControl isValid(List<String> lines) {
		for (Command cmd : cmds) {
			final CommandControl result = cmd.isValid(lines);
			if (result == CommandControl.OK || result == CommandControl.OK_PARTIAL) {
				return result;
			}
		}
		return CommandControl.NOT_OK;

	}

	final public Command createCommand(List<String> lines) {
		for (Command cmd : cmds) {
			final CommandControl result = cmd.isValid(lines);
			if (result == CommandControl.OK) {
				return cmd;
			} else if (result == CommandControl.OK_PARTIAL) {
				throw new IllegalArgumentException();
			}
		}
		throw new IllegalArgumentException();
	}

	final public void init(String startLine) {
		cmds = new ArrayList<Command>();
		initCommands();
	}

	protected abstract void initCommands();

	final protected void addCommonCommands(UmlDiagram system) {
		addCommand(new CommandNope(system));
		addCommand(new CommandComment(system));
		addCommand(new CommandMultilinesComment(system));
		addCommand(new CommandPragma(system));
		addCommand(new CommandTitle(system));
		addCommand(new CommandMultilinesTitle(system));

		addCommand(new CommandFooter(system));
		addCommand(new CommandMultilinesFooter(system));

		addCommand(new CommandHeader(system));
		addCommand(new CommandMultilinesHeader(system));

		addCommand(new CommandSkinParam(system));
		addCommand(new CommandSkinParamMultilines(system));
		addCommand(new CommandMinwidth(system));
		addCommand(new CommandRotate(system));
		addCommand(new CommandScale(system));
		addCommand(new CommandScaleWidthAndHeight(system));
		addCommand(new CommandScaleWidthOrHeight(system));
		addCommand(new CommandHideUnlinked(system));
		final FactorySpriteCommand factorySpriteCommand = new FactorySpriteCommand();
		addCommand(factorySpriteCommand.createMultiLine(system));
		addCommand(factorySpriteCommand.createSingleLine(system));

	}

	protected final void addCommand(Command cmd) {
		cmds.add(cmd);
	}

	final public List<String> getDescription() {
		final List<String> result = new ArrayList<String>();
		for (Command cmd : cmds) {
			result.addAll(Arrays.asList(cmd.getDescription()));
		}
		return Collections.unmodifiableList(result);

	}

	final public DiagramType getDiagramType() {
		return type;
	}

}
