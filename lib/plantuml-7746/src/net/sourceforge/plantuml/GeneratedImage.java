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
package net.sourceforge.plantuml;

import java.io.File;

public class GeneratedImage implements Comparable<GeneratedImage> {

	private final File pngFile;
	private final String description;
	private final PSystem system;

	public GeneratedImage(File pngFile, String description, PSystem system) {
		this.system = system;
		this.pngFile = pngFile;
		this.description = description;
	}

	public File getPngFile() {
		return pngFile;
	}

	public String getDescription() {
		return description;
	}

	public boolean isError() {
		return system instanceof PSystemError;
	}

	@Override
	public String toString() {
		return pngFile.getAbsolutePath() + " " + description;
	}

	public int compareTo(GeneratedImage this2) {
		final int cmp = this.pngFile.compareTo(this2.pngFile);
		if (cmp != 0) {
			return cmp;
		}
		return this.description.compareTo(this2.description);
	}

	@Override
	public int hashCode() {
		return pngFile.hashCode() + description.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		final GeneratedImage this2 = (GeneratedImage) obj;
		return this2.pngFile.equals(this.pngFile) && this2.description.equals(this.description);
	}

}
