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
 * Revision $Revision: 6104 $
 *
 */
package net.sourceforge.plantuml.cucadiagram.dot;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.OptionFlags;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.graphic.GraphicStrings;

abstract class Executable {

	private final File dotExe;

	static boolean isWindows() {
		return File.separatorChar == '\\';
	}

	Executable(String dotString, String... type) {
		if (type == null) {
			throw new IllegalArgumentException();
		}
		this.dotExe = searchDotExe();
	}

	private File searchDotExe() {
		if (OptionFlags.getInstance().getDotExecutable() == null) {
			final String getenv = GraphvizUtils.getenvGraphvizDot();
			if (getenv == null) {
				return specificDotExe();
			}
			return new File(getenv);
		}

		return new File(OptionFlags.getInstance().getDotExecutable());

	}

	abstract protected File specificDotExe();


	final public String dotVersion() throws IOException, InterruptedException {
		final String cmd = getCommandLineVersion();
		return executeCmd(cmd);
	}

	private String executeCmd(final String cmd) throws IOException, InterruptedException {
		final ProcessRunner p = new ProcessRunner(cmd);
		p.run(null, null);
		final StringBuilder sb = new StringBuilder();
		if (StringUtils.isNotEmpty(p.getOut())) {
			sb.append(p.getOut());
		}
		if (StringUtils.isNotEmpty(p.getError())) {
			if (sb.length() > 0) {
				sb.append(' ');
			}
			sb.append(p.getError());
		}
		return sb.toString().replace('\n', ' ').trim();
	}

	final private void createPngNoGraphviz(OutputStream os, FileFormatOption format) throws IOException {
		final List<String> msg = new ArrayList<String>();
		msg.add("Dot Executable: " + dotExe);
		if (dotExe != null) {
			if (dotExe.exists() == false) {
				msg.add("File does not exist");
			} else if (dotExe.isDirectory()) {
				msg.add("It should be an executable, not a directory");
			} else if (dotExe.isFile() == false) {
				msg.add("Not a valid file");
			} else if (dotExe.canRead() == false) {
				msg.add("File cannot be read");
			}
		}
		msg.add("Cannot find Graphviz. You should try");
		msg.add(" ");
		msg.add("@startuml");
		msg.add("testdot");
		msg.add("@enduml");
		msg.add(" ");
		msg.add(" or ");
		msg.add(" ");
		msg.add("java -jar plantuml.jar -testdot");
		final GraphicStrings errorResult = new GraphicStrings(msg);
		errorResult.writeImage(os, format);
	}

	abstract String getCommandLine();

	abstract String getCommandLineVersion();

	public final File getDotExe() {
		return dotExe;
	}


}
