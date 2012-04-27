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
 * Revision $Revision: 3829 $
 *
 */
package net.sourceforge.plantuml.cucadiagram;

public class EntityGenderUtils {

	static public EntityGender byEntityType(final EntityType type) {
		return new EntityGender() {
			public boolean contains(IEntity test) {
				return test.getType() == type;
			}
		};
	}

	static public EntityGender byEntityAlone(final IEntity entity) {
		return new EntityGender() {
			public boolean contains(IEntity test) {
				return test == entity;
			}
		};
	}

	static public EntityGender byStereotype(final String stereotype) {
		return new EntityGender() {
			public boolean contains(IEntity test) {
				if (test.getStereotype() == null) {
					return false;
				}
				return stereotype.equals(test.getStereotype().getLabel());
			}
		};
	}

	static public EntityGender byPackage(final Group group) {
		if (group == null) {
			throw new IllegalArgumentException();
		}
		return new EntityGender() {
			public boolean contains(IEntity test) {
				if (test.getParent() == null) {
					return false;
				}
				if (group == test.getParent()) {
					return true;
				}
				return false;
			}
		};
	}

	static public EntityGender and(final EntityGender g1, final EntityGender g2) {
		return new EntityGender() {
			public boolean contains(IEntity test) {
				return g1.contains(test) && g2.contains(test);
			}
		};
	}


	static public EntityGender all() {
		return new EntityGender() {
			public boolean contains(IEntity test) {
				return true;
			}
		};
	}

	static public EntityGender emptyMethods() {
		return new EntityGender() {
			public boolean contains(IEntity test) {
				return test.getMethodsToDisplay().size()==0;
			}
		};
	}

	static public EntityGender emptyFields() {
		return new EntityGender() {
			public boolean contains(IEntity test) {
				return test.getFieldsToDisplay().size()==0;
			}
		};
	}

	static public EntityGender emptyMembers() {
		return new EntityGender() {
			public boolean contains(IEntity test) {
				return test.getMethodsToDisplay().size()==0 && test.getFieldsToDisplay().size()==0;
			}
		};
	}

}
