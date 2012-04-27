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
package net.sourceforge.plantuml.graphic;

import java.awt.Color;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.sourceforge.plantuml.ugraphic.ColorChangerMonochrome;
import net.sourceforge.plantuml.ugraphic.ColorMapperIdentity;
import net.sourceforge.plantuml.ugraphic.UGradient;

public class HtmlColor {

	private static final Map<String, String> htmlNames;
	private static final Set<String> names;

	static {
		// Taken from http://perl.wikipedia.com/wiki/Named_colors ?
		// http://www.w3schools.com/HTML/html_colornames.asp
		htmlNames = new HashMap<String, String>();
		names = new TreeSet<String>();
		register("AliceBlue", "#F0F8FF");
		register("AntiqueWhite", "#FAEBD7");
		register("Aqua", "#00FFFF");
		register("Aquamarine", "#7FFFD4");
		register("Azure", "#F0FFFF");
		register("Beige", "#F5F5DC");
		register("Bisque", "#FFE4C4");
		register("Black", "#000000");
		register("BlanchedAlmond", "#FFEBCD");
		register("Blue", "#0000FF");
		register("BlueViolet", "#8A2BE2");
		register("Brown", "#A52A2A");
		register("BurlyWood", "#DEB887");
		register("CadetBlue", "#5F9EA0");
		register("Chartreuse", "#7FFF00");
		register("Chocolate", "#D2691E");
		register("Coral", "#FF7F50");
		register("CornflowerBlue", "#6495ED");
		register("Cornsilk", "#FFF8DC");
		register("Crimson", "#DC143C");
		register("Cyan", "#00FFFF");
		register("DarkBlue", "#00008B");
		register("DarkCyan", "#008B8B");
		register("DarkGoldenRod", "#B8860B");
		register("DarkGray", "#A9A9A9");
		register("DarkGrey", "#A9A9A9");
		register("DarkGreen", "#006400");
		register("DarkKhaki", "#BDB76B");
		register("DarkMagenta", "#8B008B");
		register("DarkOliveGreen", "#556B2F");
		register("Darkorange", "#FF8C00");
		register("DarkOrchid", "#9932CC");
		register("DarkRed", "#8B0000");
		register("DarkSalmon", "#E9967A");
		register("DarkSeaGreen", "#8FBC8F");
		register("DarkSlateBlue", "#483D8B");
		register("DarkSlateGray", "#2F4F4F");
		register("DarkSlateGrey", "#2F4F4F");
		register("DarkTurquoise", "#00CED1");
		register("DarkViolet", "#9400D3");
		register("DeepPink", "#FF1493");
		register("DeepSkyBlue", "#00BFFF");
		register("DimGray", "#696969");
		register("DimGrey", "#696969");
		register("DodgerBlue", "#1E90FF");
		register("FireBrick", "#B22222");
		register("FloralWhite", "#FFFAF0");
		register("ForestGreen", "#228B22");
		register("Fuchsia", "#FF00FF");
		register("Gainsboro", "#DCDCDC");
		register("GhostWhite", "#F8F8FF");
		register("Gold", "#FFD700");
		register("GoldenRod", "#DAA520");
		register("Gray", "#808080");
		register("Grey", "#808080");
		register("Green", "#008000");
		register("GreenYellow", "#ADFF2F");
		register("HoneyDew", "#F0FFF0");
		register("HotPink", "#FF69B4");
		register("IndianRed", "#CD5C5C");
		register("Indigo", "#4B0082");
		register("Ivory", "#FFFFF0");
		register("Khaki", "#F0E68C");
		register("Lavender", "#E6E6FA");
		register("LavenderBlush", "#FFF0F5");
		register("LawnGreen", "#7CFC00");
		register("LemonChiffon", "#FFFACD");
		register("LightBlue", "#ADD8E6");
		register("LightCoral", "#F08080");
		register("LightCyan", "#E0FFFF");
		register("LightGoldenRodYellow", "#FAFAD2");
		register("LightGray", "#D3D3D3");
		register("LightGrey", "#D3D3D3");
		register("LightGreen", "#90EE90");
		register("LightPink", "#FFB6C1");
		register("LightSalmon", "#FFA07A");
		register("LightSeaGreen", "#20B2AA");
		register("LightSkyBlue", "#87CEFA");
		register("LightSlateGray", "#778899");
		register("LightSlateGrey", "#778899");
		register("LightSteelBlue", "#B0C4DE");
		register("LightYellow", "#FFFFE0");
		register("Lime", "#00FF00");
		register("LimeGreen", "#32CD32");
		register("Linen", "#FAF0E6");
		register("Magenta", "#FF00FF");
		register("Maroon", "#800000");
		register("MediumAquaMarine", "#66CDAA");
		register("MediumBlue", "#0000CD");
		register("MediumOrchid", "#BA55D3");
		register("MediumPurple", "#9370D8");
		register("MediumSeaGreen", "#3CB371");
		register("MediumSlateBlue", "#7B68EE");
		register("MediumSpringGreen", "#00FA9A");
		register("MediumTurquoise", "#48D1CC");
		register("MediumVioletRed", "#C71585");
		register("MidnightBlue", "#191970");
		register("MintCream", "#F5FFFA");
		register("MistyRose", "#FFE4E1");
		register("Moccasin", "#FFE4B5");
		register("NavajoWhite", "#FFDEAD");
		register("Navy", "#000080");
		register("OldLace", "#FDF5E6");
		register("Olive", "#808000");
		register("OliveDrab", "#6B8E23");
		register("Orange", "#FFA500");
		register("OrangeRed", "#FF4500");
		register("Orchid", "#DA70D6");
		register("PaleGoldenRod", "#EEE8AA");
		register("PaleGreen", "#98FB98");
		register("PaleTurquoise", "#AFEEEE");
		register("PaleVioletRed", "#D87093");
		register("PapayaWhip", "#FFEFD5");
		register("PeachPuff", "#FFDAB9");
		register("Peru", "#CD853F");
		register("Pink", "#FFC0CB");
		register("Plum", "#DDA0DD");
		register("PowderBlue", "#B0E0E6");
		register("Purple", "#800080");
		register("Red", "#FF0000");
		register("RosyBrown", "#BC8F8F");
		register("RoyalBlue", "#4169E1");
		register("SaddleBrown", "#8B4513");
		register("Salmon", "#FA8072");
		register("SandyBrown", "#F4A460");
		register("SeaGreen", "#2E8B57");
		register("SeaShell", "#FFF5EE");
		register("Sienna", "#A0522D");
		register("Silver", "#C0C0C0");
		register("SkyBlue", "#87CEEB");
		register("SlateBlue", "#6A5ACD");
		register("SlateGray", "#708090");
		register("SlateGrey", "#708090");
		register("Snow", "#FFFAFA");
		register("SpringGreen", "#00FF7F");
		register("SteelBlue", "#4682B4");
		register("Tan", "#D2B48C");
		register("Teal", "#008080");
		register("Thistle", "#D8BFD8");
		register("Tomato", "#FF6347");
		register("Turquoise", "#40E0D0");
		register("Violet", "#EE82EE");
		register("Wheat", "#F5DEB3");
		register("White", "#FFFFFF");
		register("WhiteSmoke", "#F5F5F5");
		register("Yellow", "#FFFF00");
		register("YellowGreen", "#9ACD32");
	}

	private static void register(String s, String color) {
		htmlNames.put(s.toLowerCase(), color);
		names.add(s);
	}

	private final Color color;
	private final boolean monochrome;

	@Override
	public int hashCode() {
		return color.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof HtmlColor == false) {
			return false;
		}
		return this.color.equals(((HtmlColor) other).color);
	}

	public static final HtmlColor BLACK = HtmlColor.getColorIfValid("black");
	public static final HtmlColor WHITE = HtmlColor.getColorIfValid("white");
	public static final HtmlColor RED = HtmlColor.getColorIfValid("#FF0000");
	public static final HtmlColor GREEN = HtmlColor.getColorIfValid("#00FF00");
	public static final HtmlColor BLUE = HtmlColor.getColorIfValid("#0000FF");
	public static final HtmlColor GRAY = HtmlColor.getColorIfValid("#808080");
	public static final HtmlColor LIGHT_GRAY = HtmlColor.getColorIfValid("#C0C0C0");

	private HtmlColor(String s) {
		if (s.matches("#[0-9A-Fa-f]{6}")) {
			color = new Color(Integer.parseInt(s.substring(1), 16));
		} else {
			s = removeFirstDieseAndToLowercase(s);
			final String value = htmlNames.get(s);
			if (value == null) {
				throw new IllegalArgumentException(s);
			}
			color = new Color(Integer.parseInt(value.substring(1), 16));
		}
		monochrome = false;
		assert isValid(s);
	}

	// public static HtmlColor fromColor(Color c) {
	// return new HtmlColor(c, false);
	// }

	private HtmlColor(Color c, boolean monochrome) {
		this.color = c;
		this.monochrome = monochrome;
	}

	static public boolean isValid(String s) {
		if (s.matches("#[0-9A-Fa-f]{6}")) {
			return true;
		}
		s = removeFirstDieseAndToLowercase(s);
		if (htmlNames.containsKey(s)) {
			return true;
		}
		return false;

	}

	static public HtmlColor getColorIfValid(String s) {
		if (s == null || isValid(s) == false) {
			return null;
		}
		return new HtmlColor(s);
	}

	static private String removeFirstDieseAndToLowercase(String s) {
		s = s.toLowerCase();
		if (s.startsWith("#")) {
			s = s.substring(1);
		}
		return s;
	}

	public Color getColor999() {
		return color;
	}

	public HtmlColor asMonochrome() {
		if (monochrome) {
			throw new IllegalStateException();
		}
		return new HtmlColor(new ColorChangerMonochrome().getChangedColor(color), true);
	}

	public static Collection<String> names() {
		return Collections.unmodifiableSet(names);
	}

}
