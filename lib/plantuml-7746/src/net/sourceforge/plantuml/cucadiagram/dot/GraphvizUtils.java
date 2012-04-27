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
package net.sourceforge.plantuml.cucadiagram.dot;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import net.sourceforge.plantuml.OptionFlags;
import net.sourceforge.plantuml.StringUtils;

public class GraphvizUtils {

	private static final String TMP_TEST_FILENAME = "testdottmp42";
	private static int DOT_VERSION_LIMIT = 226;

	private static boolean isWindows() {
		return File.separatorChar == '\\';
	}

	@Deprecated
	public static Graphviz create(String dotString, String... type) {
		final AbstractGraphviz result;
		if (isWindows()) {
			result = new GraphvizWindows(dotString, type);
		} else {
			result = new GraphvizLinux(dotString, type);
		}
		if (OptionFlags.GRAPHVIZCACHE && DotMaker.isJunit()) {
			return new GraphvizCached(result);
		}
		return result;
	}

	// public static Graphviz create2(GraphvizLayoutStrategy strategy, String
	// dotString, String... type) {
	// return new AbstractGraphviz2(getOS(), strategy, dotString, type);
	// }

	static public File getDotExe() {
		return create(null, "png").getDotExe();
	}

	public static String getenvGraphvizDot() {
		final String env = System.getProperty("GRAPHVIZ_DOT");
		if (StringUtils.isNotEmpty(env)) {
			return env;
		}
		return System.getenv("GRAPHVIZ_DOT");
	}

	public static String getenvLogData() {
		final String env = System.getProperty("PLANTUML_LOGDATA");
		if (StringUtils.isNotEmpty(env)) {
			return env;
		}
		return System.getenv("PLANTUML_LOGDATA");
	}

	private static String dotVersion = null;

	public static String dotVersion() throws IOException, InterruptedException {
		if (dotVersion == null) {
			if (GraphvizUtils.getDotExe() == null) {
				dotVersion = "Error: Dot not installed";
			} else if (GraphvizUtils.getDotExe().exists() == false) {
				dotVersion = "Error: " + GraphvizUtils.getDotExe().getAbsolutePath() + " does not exist";
			} else if (GraphvizUtils.getDotExe().isFile() == false) {
				dotVersion = "Error: " + GraphvizUtils.getDotExe().getAbsolutePath() + " is not a file";
			} else if (GraphvizUtils.getDotExe().canRead() == false) {
				dotVersion = "Error: " + GraphvizUtils.getDotExe().getAbsolutePath() + " cannot be read";
			} else {
				dotVersion = create(null, "png").dotVersion();
			}
		}
		return dotVersion;
	}

	static int retrieveVersion(String s) {
		if (s == null) {
			return -1;
		}
		final Pattern p = Pattern.compile("\\s([12].\\d\\d)\\D");
		final Matcher m = p.matcher(s);
		if (m.find() == false) {
			return -1;
		}
		return Integer.parseInt(m.group(1).replaceAll("\\.", ""));
	}

	public static int getDotVersion() throws IOException, InterruptedException {
		return retrieveVersion(dotVersion());
	}

	static public List<String> getTestDotStrings(boolean withRichText) {
		String red = "";
		String bold = "";
		if (withRichText) {
			red = "<b><color:red>";
			bold = "<b>";
		}
		final List<String> result = new ArrayList<String>();
		final String ent = GraphvizUtils.getenvGraphvizDot();
		if (ent == null) {
			result.add("The environment variable GRAPHVIZ_DOT has not been set");
		} else {
			result.add("The environment variable GRAPHVIZ_DOT has been set to " + ent);
		}
		final File dotExe = GraphvizUtils.getDotExe();
		result.add("Dot executable is " + dotExe);

		boolean ok = true;
		if (dotExe == null) {
			result.add(red + "Error: No dot executable found");
			ok = false;
		} else if (dotExe.exists() == false) {
			result.add(red + "Error: file does not exist");
			ok = false;
		} else if (dotExe.isFile() == false) {
			result.add(red + "Error: not a valid file");
			ok = false;
		} else if (dotExe.canRead() == false) {
			result.add(red + "Error: cannot be read");
			ok = false;
		}

		if (ok) {
			try {
				final String version = GraphvizUtils.dotVersion();
				result.add("Dot version: " + version);
				final int v = GraphvizUtils.getDotVersion();
				if (v == -1) {
					result.add("Warning : cannot determine dot version");
				} else {
					if (v < DOT_VERSION_LIMIT) {
						result.add(bold + "Warning : Your dot installation seems old");
						result.add(bold + "Some diagrams may have issues");
					} else {
						String err = getTestCreateSimpleFile();
						if (err == null) {
							result.add(bold + "Installation seems OK. PNG generation OK");
						} else {
							result.add(red + err);
						}
					}
				}
			} catch (Exception e) {
				result.add(red + e.toString());
				e.printStackTrace();
			}
		} else {
			result.add("Error: only sequence diagrams will be generated");
		}

		return Collections.unmodifiableList(result);
	}

	static String getTestCreateSimpleFile() throws IOException, InterruptedException {
		final Graphviz graphviz = GraphvizUtils.create("", "png");
		final File f = new File(TMP_TEST_FILENAME + ".dot");
		final File fout = new File(TMP_TEST_FILENAME + ".png");
		f.delete();
		fout.delete();
		try {
			final PrintWriter pw = new PrintWriter(f);
			pw.println("digraph foo { test; }");
			pw.close();
			graphviz.testFile(f.getName(), fout.getName());
			f.delete();
			if (fout.exists() == false) {
				return "Error: dot cannot generated PNG file. Check you dot installation.";
			}
			if (fout.length() == 0) {
				return "Error: dot generates empty PNG file. Check you dot installation.";
			}
			try {
				ImageIO.read(fout);
			} catch (IOException e) {
				return "Error: dot generates unreadable PNG file. Check you dot installation.";
			}
			return null;
		} finally {
			fout.delete();
		}
	}

	public static OS getOS() {
		if (isWindows()) {
			return new OSWindows();
		}
		return new OSLinux();
	}

}
