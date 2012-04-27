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
package net.sourceforge.plantuml.componentdiagram;

import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.UmlDiagramType;
import net.sourceforge.plantuml.classdiagram.AbstractEntityDiagram;
import net.sourceforge.plantuml.cucadiagram.Entity;
import net.sourceforge.plantuml.cucadiagram.EntityType;
import net.sourceforge.plantuml.cucadiagram.Group;
import net.sourceforge.plantuml.cucadiagram.GroupType;
import net.sourceforge.plantuml.cucadiagram.IEntity;

public class ComponentDiagram extends AbstractEntityDiagram {

	@Override
	public IEntity getOrCreateClass(String code) {
		if (code.startsWith("[") && code.endsWith("]")) {
			return getOrCreateEntity(StringUtils.eventuallyRemoveStartingAndEndingDoubleQuote(code),
					EntityType.COMPONENT);
		}
		if (code.startsWith(":") && code.endsWith(":")) {
			return getOrCreateEntity(StringUtils.eventuallyRemoveStartingAndEndingDoubleQuote(code), EntityType.ACTOR);
		}
		if (code.startsWith("()")) {
			code = code.substring(2).trim();
			code = StringUtils.eventuallyRemoveStartingAndEndingDoubleQuote(code);
			return getOrCreateEntity(code, EntityType.CIRCLE_INTERFACE);
		}
		code = StringUtils.eventuallyRemoveStartingAndEndingDoubleQuote(code);
		return getOrCreateEntity(code, EntityType.CIRCLE_INTERFACE);
	}
	
	@Override
	public IEntity getOrCreateEntity(String code, EntityType defaultType) {
		code = getFullyQualifiedCode(code);
		if (super.entityExist(code)) {
			return super.getOrCreateEntity(code, defaultType);
		}
		return createEntityWithNamespace(code, getShortName(code), defaultType);
	}
	
	@Override
	public Entity createEntity(String code, String display, EntityType type) {
		if (type != EntityType.COMPONENT) {
			return super.createEntity(code, display, type);
		}
		code = getFullyQualifiedCode(code);
		if (super.entityExist(code)) {
			throw new IllegalArgumentException("Already known: " + code);
		}
		return createEntityWithNamespace(code, display, type);
	}

	private Entity createEntityWithNamespace(String fullyCode, String display, EntityType type) {
		Group group = getCurrentGroup();
		final String namespace = getNamespace(fullyCode);
		if (namespace != null && (group == null || group.getCode().equals(namespace) == false)) {
			group = getOrCreateGroupInternal(namespace, namespace, namespace, GroupType.PACKAGE, null);
			group.setBold(true);
		}
		return createEntityInternal(fullyCode, display == null ? getShortName(fullyCode) : display, type, group);
	}

	@Override
	public final boolean entityExist(String code) {
		return super.entityExist(getFullyQualifiedCode(code));
	}





	@Override
	public UmlDiagramType getUmlDiagramType() {
		return UmlDiagramType.COMPONENT;
	}

}
