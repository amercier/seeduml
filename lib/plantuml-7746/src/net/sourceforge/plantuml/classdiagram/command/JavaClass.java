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
package net.sourceforge.plantuml.classdiagram.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import net.sourceforge.plantuml.cucadiagram.EntityType;

class JavaClass {

	private final String name;
	private final String javaPackage;
	private final List<String> parents = new ArrayList<String>();
	private final EntityType type;
	private final EntityType parentType;

	public JavaClass(String javaPackage, String name, String p, EntityType type, EntityType parentType) {
		this.name = name;
		this.javaPackage = javaPackage;
		if (p == null) {
			p = "";
		}
		final StringTokenizer st = new StringTokenizer(p.trim(), ",");
		while (st.hasMoreTokens()) {
			this.parents.add(st.nextToken().trim().replaceAll("\\<.*", ""));
		}
		this.type = type;
		this.parentType = parentType;
	}

	public final String getName() {
		return name;
	}

	public final EntityType getType() {
		return type;
	}

	public final List<String> getParents() {
		return Collections.unmodifiableList(parents);
	}

	public final EntityType getParentType() {
		return parentType;
	}

	public final String getJavaPackage() {
		return javaPackage;
	}

}
