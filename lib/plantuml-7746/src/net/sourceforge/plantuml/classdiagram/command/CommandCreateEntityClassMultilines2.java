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
 * Revision $Revision: 4161 $
 *
 */
package net.sourceforge.plantuml.classdiagram.command;

import java.util.List;
import java.util.Map;

import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.classdiagram.ClassDiagram;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.CommandMultilines2;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexOr;
import net.sourceforge.plantuml.command.regex.RegexPartialMatch;
import net.sourceforge.plantuml.cucadiagram.Entity;
import net.sourceforge.plantuml.cucadiagram.EntityType;
import net.sourceforge.plantuml.cucadiagram.IEntity;
import net.sourceforge.plantuml.cucadiagram.Link;
import net.sourceforge.plantuml.cucadiagram.LinkDecor;
import net.sourceforge.plantuml.cucadiagram.LinkType;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.skin.VisibilityModifier;

public class CommandCreateEntityClassMultilines2 extends CommandMultilines2<ClassDiagram> {

	enum Mode {
		EXTENDS, IMPLEMENTS
	};
	public CommandCreateEntityClassMultilines2(ClassDiagram diagram) {
		super(diagram, getRegexConcat());
	}

	@Override
	public String getPatternEnd() {
		return "(?i)^\\s*\\}\\s*$";
	}

	private static RegexConcat getRegexConcat() {
		return new RegexConcat(new RegexLeaf("^"), //
				new RegexLeaf("TYPE", "(interface|enum|abstract\\s+class|abstract|class)\\s+"), //
				new RegexOr( //
						new RegexLeaf("NAME1", "(?:\"([^\"]+)\"\\s+as\\s+)?(\\.?[\\p{L}0-9_]+(?:\\.[\\p{L}0-9_]+)*)"), //
						new RegexLeaf("NAME2", "(\\.?[\\p{L}0-9_]+(?:\\.[\\p{L}0-9_]+)*)\\s+as\\s+\"([^\"]+)\""), //
						new RegexLeaf("NAME3", "\"([^\"]+)\"")), //
				new RegexLeaf("GENERIC", "(?:\\s*\\<(" + GenericRegexProducer.PATTERN + ")\\>)?"), //
				new RegexLeaf("STEREO", "(?:\\s*([\\<\\[]{2}.*[\\>\\]]{2}))?"), //
				new RegexLeaf("EXTENDS", "(\\s+(extends|implements)\\s+(\\.?[\\p{L}0-9_]+(?:\\.[\\p{L}0-9_]+)*))?"), //
				new RegexLeaf("\\s*\\{\\s*$"));
	}

	public CommandExecutionResult execute(List<String> lines) {
		StringUtils.trim(lines, false);
		final Map<String, RegexPartialMatch> line0 = getStartingPattern().matcher(lines.get(0).trim());
		final Entity entity = executeArg0(line0);
		if (entity == null) {
			return CommandExecutionResult.error("No such entity");
		}
		for (String s : lines.subList(1, lines.size() - 1)) {
			if (s.length() > 0 && VisibilityModifier.isVisibilityCharacter(s.charAt(0))) {
				getSystem().setVisibilityModifierPresent(true);
			}
			entity.addFieldOrMethod(s);
		}

		manageExtends(getSystem(), line0, entity);

		return CommandExecutionResult.ok();
	}
	
	private static void manageExtends(ClassDiagram system, Map<String, RegexPartialMatch> arg, final Entity entity) {
		if (arg.get("EXTENDS").get(1) != null) {
			final Mode mode = arg.get("EXTENDS").get(1).equalsIgnoreCase("extends") ? Mode.EXTENDS : Mode.IMPLEMENTS;
			final String other = arg.get("EXTENDS").get(2);
			EntityType type2 = EntityType.CLASS;
			if (mode == Mode.IMPLEMENTS) {
				type2 = EntityType.INTERFACE;
			}
			if (mode == Mode.EXTENDS && entity.getType() == EntityType.INTERFACE) {
				type2 = EntityType.INTERFACE;
			}
			final IEntity cl2 = system.getOrCreateClass(other, type2);
			LinkType typeLink = new LinkType(LinkDecor.NONE, LinkDecor.EXTENDS);
			if (type2 == EntityType.INTERFACE && entity.getType() != EntityType.INTERFACE) {
				typeLink = typeLink.getDashed();
			}
			final Link link = new Link(cl2, entity, typeLink, null, 2, null, null, system.getLabeldistance(),
					system.getLabelangle());
			system.addLink(link);
		}
	}



	private Entity executeArg0(Map<String, RegexPartialMatch> arg) {

		final EntityType type = EntityType.getEntityType(arg.get("TYPE").get(0).toUpperCase());
		final String code;
		final String display;
		if (arg.get("NAME1").get(1) != null) {
			code = arg.get("NAME1").get(1);
			display = arg.get("NAME1").get(0);
		} else if (arg.get("NAME3").get(0) != null) {
			code = arg.get("NAME3").get(0);
			display = arg.get("NAME3").get(0);
		} else {
			code = arg.get("NAME2").get(0);
			display = arg.get("NAME2").get(1);
		}
		final String stereotype = arg.get("STEREO").get(0);
		final String generic = arg.get("GENERIC").get(0);

		if (getSystem().entityExist(code)) {
			final Entity result = (Entity) getSystem().getOrCreateClass(code);
			result.muteToType(type);
			return result;
		}
		final Entity entity = getSystem().createEntity(code, display, type);
		if (stereotype != null) {
			entity.setStereotype(new Stereotype(stereotype, getSystem().getSkinParam().getCircledCharacterRadius(),
					getSystem().getSkinParam().getFont(FontParam.CIRCLED_CHARACTER, null)));
		}
		if (generic != null) {
			entity.setGeneric(generic);
		}
		return entity;
	}

}
