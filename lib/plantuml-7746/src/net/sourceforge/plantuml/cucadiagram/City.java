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
 * Revision $Revision: 7738 $
 *
 */
package net.sourceforge.plantuml.cucadiagram;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sourceforge.plantuml.graphic.HtmlColor;

public class City {

	private final Map<String, IEntity> entities = new LinkedHashMap<String, IEntity>();

	private HtmlColor backColor;
	private Group parent;

//	private boolean dashed;
//	private boolean rounded;
//	private boolean bold;

	public City(Group parent) {
		this.parent = parent;
	}

	@Override
	public String toString() {
		return super.toString()+" "+entities;
	}

	public void addEntity(IEntity entity) {
		if (entities.containsValue(entity)) {
			throw new IllegalArgumentException();
		}
		if (entities.containsKey(entity.getCode())) {
			throw new IllegalArgumentException(entity.getCode());
		}
		entities.put(entity.getCode(), entity);
	}

	public boolean contains(IEntity entity) {
		if (entity == null) {
			throw new IllegalArgumentException();
		}
		if (entities.containsValue(entity)) {
			return true;
		}
		return false;
	}

	public Map<String, IEntity> entities() {
		return Collections.unmodifiableMap(entities);
	}

	public final HtmlColor getBackColor() {
		return backColor;
	}

	public final void setBackColor(HtmlColor backColor) {
		this.backColor = backColor;
	}

	public final Group getParent() {
		return parent;
	}

//	public final boolean isDashed() {
//		return dashed;
//	}
//
//	public final void setDashed(boolean dashed) {
//		this.dashed = dashed;
//	}
//
//	public final boolean isRounded() {
//		return rounded;
//	}
//
//	public final void setRounded(boolean rounded) {
//		this.rounded = rounded;
//	}


//	public boolean isBold() {
//		return bold;
//	}
//
//	public void setBold(boolean bold) {
//		this.bold = bold;
//	}

}
