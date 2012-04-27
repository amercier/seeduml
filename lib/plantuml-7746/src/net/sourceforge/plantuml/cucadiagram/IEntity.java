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

import net.sourceforge.plantuml.SpecificBackcolorable;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.cucadiagram.dot.DrawFile;
import net.sourceforge.plantuml.svek.IEntityImage;

public interface IEntity extends Imaged, SpecificBackcolorable, Comparable<IEntity> {

	public Group getParent();

	public List<? extends CharSequence> getDisplay2();

	public EntityType getType();

	public String getUid();

	public Url getUrl();

	public Stereotype getStereotype();

	public void setStereotype(Stereotype stereotype);

	public List<Member> getFieldsToDisplay();
	
	public List<Member> getMethodsToDisplay();

	public BlockMember getBody(PortionShower portionShower);

	public String getCode();

	public DrawFile getImageFile(File searched) throws IOException;

	public boolean isTop();

	public void setTop(boolean top);

	public boolean hasNearDecoration();

	public void setNearDecoration(boolean nearDecoration);

	public int getXposition();

	public void setXposition(int pos);

	public IEntityImage getSvekImage();
	
	public String getGeneric();
	
	public BlockMember getMouseOver();


}
