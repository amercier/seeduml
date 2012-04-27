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
 * Revision $Revision: 3979 $
 *
 */
package net.sourceforge.plantuml.command;

import java.util.List;

import net.sourceforge.plantuml.classdiagram.AbstractEntityDiagram;
import net.sourceforge.plantuml.cucadiagram.Group;
import net.sourceforge.plantuml.cucadiagram.GroupType;
import net.sourceforge.plantuml.graphic.HtmlColor;

public class CommandNamespace extends SingleLineCommand<AbstractEntityDiagram> {

	public CommandNamespace(AbstractEntityDiagram diagram) {
		super(diagram, "(?i)^namespace\\s+([\\p{L}0-9_][\\p{L}0-9_.]*)\\s*(#[0-9a-fA-F]{6}|\\w+)?\\s*\\{?$");
	}

	@Override
	protected CommandExecutionResult executeArg(List<String> arg) {
		final String code = arg.get(0);
		final Group currentPackage = getSystem().getCurrentGroup();
		final Group p = getSystem().getOrCreateGroup(code, code, code, GroupType.PACKAGE, currentPackage);
		p.setBold(true);
		final String color = arg.get(1);
		if (color != null) {
			p.setBackColor(HtmlColor.getColorIfValid(color));
		}
		return CommandExecutionResult.ok();
	}

}
