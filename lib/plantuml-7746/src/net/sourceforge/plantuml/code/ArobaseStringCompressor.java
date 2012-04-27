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
package net.sourceforge.plantuml.code;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArobaseStringCompressor implements StringCompressor {

	private final static Pattern p = Pattern.compile("(?s)(?i)^\\s*(@startuml[^\\n\\r]*)?\\s*(.*?)\\s*(@enduml)?\\s*$");

	public String compress(String s) throws IOException {
		final Matcher m = p.matcher(s);
		if (m.find()) {
			return clean(m.group(2));
		}
		return "";
	}

	public String decompress(String s) throws IOException {
		String result = clean(s);
		if (result.startsWith("@start")) {
			return result;
		}
		result = "@startuml\n" + result;
		if (result.endsWith("\n") == false) {
			result += "\n";
		}
		result += "@enduml";
		return result;
	}

	private String clean(String s) {
		s = s.trim();
		s = clean1(s);
		s = s.replaceAll("@enduml[^\\n\\r]*", "");
		s = s.replaceAll("@startuml[^\\n\\r]*", "");
		s = s.trim();
		return s;
	}

	private String clean1(String s) {
		final Matcher m = p.matcher(s);
		if (m.matches()) {
			return m.group(2);
		}
		return s;
	}

}