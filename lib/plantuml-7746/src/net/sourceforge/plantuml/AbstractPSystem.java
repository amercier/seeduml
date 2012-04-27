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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import net.sourceforge.plantuml.version.Version;

public abstract class AbstractPSystem implements PSystem {

	private UmlSource source;

	private String getVersion() {
		final StringBuilder toAppend = new StringBuilder();
		toAppend.append("PlantUML version ");
		toAppend.append(Version.version());
		toAppend.append("(" + new Date(Version.compileTime()) + ")\n");
		final Properties p = System.getProperties();
		toAppend.append(p.getProperty("java.runtime.name"));
		toAppend.append('\n');
		toAppend.append(p.getProperty("java.vm.name"));
		toAppend.append('\n');
		toAppend.append(p.getProperty("java.runtime.version"));
		toAppend.append('\n');
		toAppend.append(p.getProperty("os.name"));

		return toAppend.toString();
	}

	final public String getMetadata() {
		if (source == null) {
			return getVersion();
		}
		return source.getPlainString() + "\n" + getVersion();
	}

	final public UmlSource getSource() {
		return source;
	}

	final public void setSource(UmlSource source) {
		this.source = source;
	}

	public int getNbImages() {
		return 1;
	}

	public List<File> exportDiagrams(File suggestedFile, FileFormatOption fileFormat) throws IOException,
			InterruptedException {
		if (suggestedFile.exists() && suggestedFile.isDirectory()) {
			throw new IllegalArgumentException("File is a directory " + suggestedFile);
		}
		OutputStream os = null;
		try {
			os = new BufferedOutputStream(new FileOutputStream(suggestedFile));
			this.exportDiagram(os, null, 0, fileFormat);
		} finally {
			if (os != null) {
				os.close();
			}
		}
		return Arrays.asList(suggestedFile);
	}

	public List<? extends CharSequence> getTitle() {
		if (source == null) {
			return Collections.emptyList();
		}
		return source.getTitle();
	}
	
	public String getWarningOrError() {
		return null;
	}

}
