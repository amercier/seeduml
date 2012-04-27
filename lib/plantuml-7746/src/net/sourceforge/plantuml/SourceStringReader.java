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
 * Revision $Revision: 4041 $
 *
 */
package net.sourceforge.plantuml;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.graphic.GraphicStrings;
import net.sourceforge.plantuml.preproc.Defines;

public class SourceStringReader {

	final private List<BlockUml> blocks;

	public SourceStringReader(String source) {
		this(new Defines(), source, Collections.<String> emptyList());
	}

	public SourceStringReader(Defines defines, String source, List<String> config) {
		try {
			final BlockUmlBuilder builder = new BlockUmlBuilder(config, defines, new StringReader(source), null);
			this.blocks = builder.getBlockUmls();
		} catch (IOException e) {
			throw new IllegalStateException();
		}
	}

	public String generateImage(OutputStream os) throws IOException {
		return generateImage(os, 0);
	}

	public String generateImage(File f) throws IOException {
		final OutputStream os = new BufferedOutputStream(new FileOutputStream(f));
		final String result = generateImage(os, 0);
		os.close();
		return result;
	}

	public String generateImage(OutputStream os, FileFormatOption fileFormatOption) throws IOException {
		return generateImage(os, 0, fileFormatOption);
	}

	public String generateImage(OutputStream os, int numImage) throws IOException {
		return generateImage(os, numImage, new FileFormatOption(FileFormat.PNG));
	}

	public String generateImage(OutputStream os, int numImage, FileFormatOption fileFormatOption) throws IOException {
		if (blocks.size() == 0) {
			final GraphicStrings error = new GraphicStrings(Arrays.asList("No @startuml found"));
			error.writeImage(os, fileFormatOption);
			return null;
		}
		try {
			for (BlockUml b : blocks) {
				final PSystem system = b.getSystem();
				final int nbInSystem = system.getNbImages();
				if (numImage < nbInSystem) {
					final StringBuilder cmap = new StringBuilder();
					system.exportDiagram(os, cmap, numImage, fileFormatOption);
					if (cmap.length() > 0) {
						return system.getDescription() + "\n" + cmap;
					}
					return system.getDescription();
				}
				numImage -= nbInSystem;
			}
		} catch (InterruptedException e) {
			return null;
		}
		Log.error("numImage is too big = " + numImage);
		return null;

	}

	public final List<BlockUml> getBlocks() {
		return Collections.unmodifiableList(blocks);
	}

}
