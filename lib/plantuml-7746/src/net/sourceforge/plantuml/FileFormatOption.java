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
 * Revision $Revision: 5333 $
 *
 */
package net.sourceforge.plantuml;

import java.awt.geom.AffineTransform;

public class FileFormatOption {

	private final FileFormat fileFormat;
	private final AffineTransform affineTransform;

	public FileFormatOption(FileFormat fileFormat) {
		this(fileFormat, null);
	}

	public FileFormatOption(FileFormat fileFormat, AffineTransform at) {
		this.fileFormat = fileFormat;
		this.affineTransform = at;
	}

	public final FileFormat getFileFormat() {
		return fileFormat;
	}

	public AffineTransform getAffineTransform() {
		return affineTransform;
	}

}
