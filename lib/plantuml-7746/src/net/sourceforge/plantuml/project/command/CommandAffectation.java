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
 * Revision $Revision: 5884 $
 *
 */
package net.sourceforge.plantuml.project.command;

import java.util.List;

import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand;
import net.sourceforge.plantuml.project.Expression;
import net.sourceforge.plantuml.project.PSystemProject;

public class CommandAffectation extends SingleLineCommand<PSystemProject> {

	public CommandAffectation(PSystemProject diagram) {
		super(diagram, "(?i)^\\s*([~\\^]?[\\w$/]+)\\s*:=\\s*(.+)$");
	}

	@Override
	protected CommandExecutionResult executeArg(List<String> arg) {
		final Expression exp = getSystem().getProject().getExpression(arg.get(1).trim());
		final boolean ok = getSystem().getProject().affectation(arg.get(0).trim(), exp);
		if (ok) {
			return CommandExecutionResult.ok();
		}
		return CommandExecutionResult.error("Cannot execute");
	}
}
