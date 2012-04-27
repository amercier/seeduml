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
 * Revision $Revision: 7743 $
 *
 */
package net.sourceforge.plantuml.cucadiagram;

import java.util.Collection;
import java.util.Map;

import net.sourceforge.plantuml.graphic.HtmlColor;

public interface Group {

	public void addEntity(IEntity entity);

	public boolean contains(IEntity entity);

	public CrossingType getCrossingType(Link link);

	public Map<String, IEntity> entities();

	public String getCode();

	public String getUid();

	public String getUid1();

	public int getUid2();

	public HtmlColor getBackColor();

	public void setBackColor(HtmlColor backColor);

	public Group getParent();

	public boolean isDashed();

	public void setDashed(boolean dashed);

	public boolean isRounded();

	public void setRounded(boolean rounded);

	public GroupType getType();

	public IEntity getEntityCluster();

	public void setEntityCluster(IEntity entityCluster);

	public String getDisplay();

	public boolean isBold();

	public void setBold(boolean bold);

	public void moveEntitiesTo(Group dest);

	public String getNamespace();

	public Collection<Group> getChildren();

	public boolean isAutonom();

	public void setAutonom(boolean autonom);

	public Rankdir getRankdir();

	public void setRankdir(Rankdir rankdir);

	public void setStereotype(Stereotype stereotype);

	public Stereotype getStereotype();

	void removeInternal(Entity entity);

	public void removeInternal(Group group);
}
