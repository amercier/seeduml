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
 * Revision $Revision: 6710 $
 *
 */
package net.sourceforge.plantuml.cucadiagram.dot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sourceforge.plantuml.cucadiagram.CucaDiagram;
import net.sourceforge.plantuml.cucadiagram.Entity;
import net.sourceforge.plantuml.cucadiagram.EntityType;
import net.sourceforge.plantuml.cucadiagram.Group;
import net.sourceforge.plantuml.cucadiagram.GroupType;
import net.sourceforge.plantuml.cucadiagram.IEntity;
import net.sourceforge.plantuml.svek.GroupPngMakerActivity;

public final class CucaDiagramSimplifierActivity {

	private final CucaDiagram diagram;

	public CucaDiagramSimplifierActivity(CucaDiagram diagram, List<String> dotStrings) throws IOException, InterruptedException {
		this.diagram = diagram;
		boolean changed;
		do {
			changed = false;
			final Collection<Group> groups = new ArrayList<Group>(diagram.getGroups(false));
			for (Group g : groups) {
				if (diagram.isAutarkic(g)) {
					final EntityType type;
					if (g.getType() == GroupType.INNER_ACTIVITY) {
						type = EntityType.ACTIVITY;
					} else if (g.getType() == GroupType.CONCURRENT_ACTIVITY) {
						type = EntityType.ACTIVITY_CONCURRENT;
					} else {
						throw new IllegalStateException();
					}
					final Entity proxy = new Entity("#" + g.getCode(), g.getDisplay(), type, g.getParent(), diagram
							.getHides());
					
					proxy.overidesFieldsToDisplay(g.getEntityCluster());

					computeImageGroup(g, proxy, dotStrings);
					diagram.overideGroup(g, proxy);
					if (proxy.getImageFile() != null) {
						diagram.ensureDelete(proxy.getImageFile());
					}

					for (IEntity sub : g.entities().values()) {
						final DrawFile subImage = sub.getImageFile();
						if (subImage != null) {
							proxy.addSubImage(subImage);
						}
					}

					changed = true;
				}
			}
		} while (changed);
	}

	private void computeImageGroup(Group g, Entity proxy, List<String> dotStrings) throws IOException, InterruptedException {
		final GroupPngMakerActivity maker = new GroupPngMakerActivity(diagram, g);
		proxy.setSvekImage(maker.getImage());
	}

}
