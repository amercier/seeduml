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
 * Revision $Revision: 7720 $
 *
 */
package net.sourceforge.plantuml;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Properties;

import net.sourceforge.plantuml.cucadiagram.dot.GraphvizUtils;
import net.sourceforge.plantuml.version.License;
import net.sourceforge.plantuml.version.PSystemVersion;
import net.sourceforge.plantuml.version.Version;

public class OptionPrint {

	static public void printTestDot() throws InterruptedException {
		for (String s : GraphvizUtils.getTestDotStrings(false)) {
			System.err.println(s);
		}
		exit();
	}

	static public void printHelp() throws InterruptedException {

		final String charset = Charset.defaultCharset().displayName();

		System.err.println("Usage: java -jar plantuml.jar [options] -gui");
		System.err.println("\t(to execute the GUI)");
		System.err.println("    or java -jar plantuml.jar [options] [file/dir] [file/dir] [file/dir]");
		System.err.println("\t(to process files or directories)");
		System.err.println();
		System.err.println("You can use the following wildcards in files/dirs:");
		System.err.println("\t*\tmeans any characters but '" + File.separator + "'");
		System.err.println("\t?\tone and only one character but '" + File.separator + "'");
		System.err.println("\t**\tmeans any characters (used to recurse through directories)");
		System.err.println();
		System.err.println("where options include:");
		System.err.println("    -gui\t\tTo run the graphical user interface");
		System.err.println("    -tsvg\t\tTo generate images using SVG format");
		System.err.println("    -teps\t\tTo generate images using EPS format");
		System.err.println("    -txmi\t\tTo generate XMI file for class diagram");
		System.err.println("    -thtml\t\tTo generate HTML files for class diagram");
		System.err.println("    -ttxt\t\tTo generate images with ASCII art");
		System.err.println("    -tutxt\t\tTo generate images with ASCII art using Unicode characters");
		System.err.println("    -o[utput] \"dir\"\tTo generate images in the specified directory");
		System.err.println("    -DVAR1=value\tTo set a preprocessing variable as if '!define VAR1 value' were used");
		System.err.println("    -Sparam1=value\tTo set a skin parameter as if 'skinparam param1 value' were used");
		System.err.println("    -config \"file\"\tTo read the provided config file before each diagram");
		System.err.println("    -charset xxx\tTo use a specific charset (default is " + charset + ")");
		System.err.println("    -e[x]clude pattern\tTo exclude files that match the provided pattern");
		System.err.println("    -metadata\t\tTo retrieve PlantUML sources from PNG images");
		System.err.println("    -version\t\tTo display information about PlantUML and Java versions");
		System.err.println("    -checkversion\tTo check if a newer version is available for download");
		System.err.println("    -v[erbose]\t\tTo have log information");
		System.err.println("    -quiet\t\tTo NOT print error message into the console");
		System.err.println("    -forcegd\t\tTo force dot to use GD PNG library");
		System.err.println("    -forcecairo\t\tTo force dot to use Cairo PNG library");
		System.err.println("    -keepfiles\t\tTo NOT delete temporary files after process");
		System.err.println("    -h[elp]\t\tTo display this help message");
		System.err.println("    -testdot\t\tTo test the installation of graphviz");
		System.err.println("    -graphvizdot \"exe\"\tTo specify dot executable");
		System.err.println("    -p[ipe]\t\tTo use stdin for PlantUML source and stdout for PNG/SVG/EPS generation");
		System.err.println("    -computeurl\t\tTo compute the encoded URL of a PlantUML source file");
		System.err.println("    -decodeurl\t\tTo retrieve the PlantUML source from an encoded URL");
		System.err.println("    -syntax\t\tTo report any syntax error from standard input without generating images");
		System.err.println("    -language\t\tTo print the list of PlantUML keywords");
		System.err.println("    -nosuggestengine\tTo disable the suggest engine when errors in diagrams");
		System.err.println("    -checkonly\t\tTo check the syntax of files without generating images");
		System.err.println("    -failonerror\tTo stop processing if syntax error in diagram occurs");
		System.err.println("    -pattern\t\tTo print the list of Regular Expression used by PlantUML");
		System.err.println("    -duration\t\tTo print the duration of complete diagrams processing");
		System.err.println("    -nbthread N\t\tTo use (N) threads for processing");
		System.err.println("    -nbthread auto\tTo use " + Option.defaultNbThreads() + " threads for processing");
		System.err.println("    -author[s]\t\tTo print information about PlantUML authors");
		System.err.println();
		System.err.println("If needed, you can setup the environment variable GRAPHVIZ_DOT.");
		exit();
	}

	static private void exit() throws InterruptedException {
		if (OptionFlags.getInstance().isSystemExit()) {
			System.exit(0);
		}
		throw new InterruptedException("exit");
	}

	public static void printLicense() throws InterruptedException {
		for (String s : License.getText()) {
			System.err.println(s);
		}
		exit();
	}

	public static void printVersion() throws InterruptedException {
		System.err.println("PlantUML version " + Version.version() + " (" + new Date(Version.compileTime()) + ")");
		final Properties p = System.getProperties();
		System.err.println(p.getProperty("java.runtime.name"));
		System.err.println(p.getProperty("java.vm.name"));
		System.err.println(p.getProperty("java.runtime.version"));
		System.err.println(p.getProperty("os.name"));
		exit();
	}

	public static void checkVersion() throws InterruptedException {
		System.err.println("PlantUML version " + Version.version() + " (" + new Date(Version.compileTime()) + ")");
		System.err.println();
		final int lastversion = PSystemVersion.extractDownloadableVersion(null, null);
		if (lastversion == -1) {
			System.err.println("Error");
			System.err.println("Cannot connect to http://plantuml.sourceforge.net/");
			System.err.println("Maybe you should set your proxy ?");
		} else if (lastversion == 0) {
			System.err.println("Error");
			System.err.println("Cannot retrieve last version from http://plantuml.sourceforge.net/");
		} else {
			System.err.println("Last available version for download : " + lastversion);
			System.err.println();
			if (Version.version() >= lastversion) {
				System.err.println("Your version is up to date.");
			} else {
				System.err.println("A newer version is available for download.");
			}
		}

		exit();
	}

	public static void printAbout() throws InterruptedException {
		// Duplicate in PSystemVersion
		System.err.println("PlantUML version " + Version.version() + " (" + new Date(Version.compileTime()) + ")");
		System.err.println();
		System.err.println("Original idea: Arnaud Roques");
		System.err.println("Word Macro: Alain Bertucat & Matthieu Sabatier");
		System.err.println("Eclipse Plugin: Claude Durif & Anne Pecoil");
		System.err.println("Servlet & XWiki: Maxime Sinclair");
		System.err.println("Site design: Raphael Cotisson");
		System.err.println();
		System.err.println("http://plantuml.sourceforge.net");
		exit();
	}

	public static void printLanguage() throws InterruptedException {
		new LanguageDescriptor().print(System.out);
		exit();
	}

}
