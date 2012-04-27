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

public abstract class EntityUtils {

	private static IEntity withNoParent(final IEntity ent) {
		if (ent.getType() == EntityType.GROUP) {
			throw new IllegalArgumentException();
		}
		return new IEntity() {
			public List<Member> getFieldsToDisplay() {
				return ent.getFieldsToDisplay();
			}

			public List<? extends CharSequence> getDisplay2() {
				return ent.getDisplay2();
			}

			public Group getParent() {
				return null;
			}

			public Stereotype getStereotype() {
				return ent.getStereotype();
			}

			public void setStereotype(Stereotype stereotype) {
				ent.setStereotype(stereotype);
			}

			public EntityType getType() {
				return ent.getType();
			}

			public String getUid() {
				return ent.getUid();
			}

			public Url getUrl() {
				return ent.getUrl();
			}

			public List<Member> getMethodsToDisplay() {
				return ent.getMethodsToDisplay();
			}

			public DrawFile getImageFile() {
				return ent.getImageFile();
			}

			public HtmlColor getSpecificBackColor() {
				return ent.getSpecificBackColor();
			}

			public void setSpecificBackcolor(HtmlColor specificBackcolor) {
				throw new UnsupportedOperationException();
			}

			@Override
			public int hashCode() {
				return ent.hashCode();
			}

			@Override
			public boolean equals(Object obj) {
				return ent.equals(obj);
			}

			@Override
			public String toString() {
				return "NoParent " + ent.toString();
			}

			public String getCode() {
				return ent.getCode();
			}

			public DrawFile getImageFile(File searched) throws IOException {
				return ent.getImageFile(searched);
			}

			public boolean isTop() {
				return ent.isTop();
			}

			public void setTop(boolean top) {
				ent.setTop(top);
			}

			public boolean hasNearDecoration() {
				return ent.hasNearDecoration();
			}

			public void setNearDecoration(boolean nearDecoration) {
				ent.setNearDecoration(nearDecoration);
			}

			public int compareTo(IEntity other) {
				return ent.compareTo(other);
			}

			public int getXposition() {
				return ent.getXposition();
			}

			public void setXposition(int pos) {
				ent.setXposition(pos);
			}

			public IEntityImage getSvekImage() {
				return ent.getSvekImage();
			}

			public String getGeneric() {
				return ent.getGeneric();
			}

			public BlockMember getBody(PortionShower portionShower) {
				return ent.getBody(portionShower);
			}

			public BlockMember getMouseOver() {
				return ent.getMouseOver();
			}

		};
	}

}
