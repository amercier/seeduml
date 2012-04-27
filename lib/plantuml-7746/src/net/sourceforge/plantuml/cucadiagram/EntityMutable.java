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
 * Revision $Revision: 4749 $
 *
 */
package net.sourceforge.plantuml.cucadiagram;

import java.io.File;
import java.io.IOException;
import java.util.List;

import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.cucadiagram.dot.DrawFile;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.svek.IEntityImage;

public class EntityMutable implements IEntity {

	private final IEntity entity;

	public EntityMutable(IEntity entity) {
		this.entity = entity;
	}

	public DrawFile getImageFile() {
		return entity.getImageFile();
	}

	public HtmlColor getSpecificBackColor() {
		return entity.getSpecificBackColor();
	}

	public void setSpecificBackcolor(HtmlColor specificBackcolor) {
		entity.setSpecificBackcolor(specificBackcolor);
	}

	public int compareTo(IEntity arg0) {
		return entity.compareTo(arg0);
	}

	public Group getParent() {
		return entity.getParent();
	}

	public List<? extends CharSequence> getDisplay2() {
		return entity.getDisplay2();
	}

	public EntityType getType() {
		return entity.getType();
	}

	public String getUid() {
		return entity.getUid();
	}

	public Url getUrl() {
		return entity.getUrl();
	}

	public Stereotype getStereotype() {
		return entity.getStereotype();
	}

	public void setStereotype(Stereotype stereotype) {
		entity.setStereotype(stereotype);
	}

	public List<Member> getFieldsToDisplay() {
		return entity.getFieldsToDisplay();
	}

	public List<Member> getMethodsToDisplay() {
		return entity.getMethodsToDisplay();
	}

	public BlockMember getBody(PortionShower portionShower) {
		return entity.getBody(portionShower);
	}

	public String getCode() {
		return entity.getCode();
	}

	public DrawFile getImageFile(File searched) throws IOException {
		return entity.getImageFile(searched);
	}

	public boolean isTop() {
		return entity.isTop();
	}

	public void setTop(boolean top) {
		entity.setTop(top);
	}

	public boolean hasNearDecoration() {
		return entity.hasNearDecoration();
	}

	public void setNearDecoration(boolean nearDecoration) {
		entity.setNearDecoration(nearDecoration);
	}

	public int getXposition() {
		return entity.getXposition();
	}

	public void setXposition(int pos) {
		entity.setXposition(pos);
	}

	public IEntityImage getSvekImage() {
		return entity.getSvekImage();
	}

	public String getGeneric() {
		return entity.getGeneric();
	}

	public BlockMember getMouseOver() {
		return entity.getMouseOver();
	}

}
