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
package net.sourceforge.plantuml.acearth;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import net.sourceforge.plantuml.AbstractPSystem;
import net.sourceforge.plantuml.FileFormatOption;

import com.ctreber.acearth.ACearth;
import com.ctreber.acearth.ConfigurationACearth;
import com.ctreber.acearth.plugins.markers.Marker;

public class PSystemXearth extends AbstractPSystem {

	final private int width;
	final private int height;
	final private Map<String, String> config;
	final private List<Marker> markers;

	final private Collection<String> enums = Arrays.asList("viewPositionType");
	final private Collection<String> doubles = Arrays.asList("sunPosRelLat", "sunPosRelLong", "orbitPeriod",
			"orbitInclination", "viewPosLat", "viewPosLong", "starFrequency", "viewMagnification");
	final private Collection<String> integers = Arrays.asList("daySideBrightness", "nightSideBrightness",
			"terminatorDiscontinuity", "gridDivision", "gridPixelDivision", "bigStars");
	final private Collection<String> booleans = Arrays.asList("shadeP", "gridP", "starsP");

	public PSystemXearth(int width, int height, Map<String, String> config, List<Marker> markers) {
		this.width = width;
		this.height = height;
		this.config = config;
		this.markers = markers;
	}

	public void exportDiagram(OutputStream os, StringBuilder cmap, int index, FileFormatOption fileFormat)
			throws IOException {
		final ACearth earth = new ACearth(markers);
		final ConfigurationACearth conf = earth.getConf();
		conf.setInt("imageWidth", width);
		conf.setInt("imageHeight", height);

		for (Map.Entry<String, String> ent : config.entrySet()) {
			final String key = ent.getKey();
			final String value = ent.getValue();
			if (key.equalsIgnoreCase("GMT")) {
				final Date date = extractGmt(value);
				conf.setInt("fixedTime", (int) (date.getTime() / 1000L));
			} else if (enums.contains(key)) {
				conf.getMOEnum(key).set(value);
			} else if (doubles.contains(key)) {
				conf.setDouble(key, Double.parseDouble(value));
			} else if (integers.contains(key)) {
				conf.setInt(key, Integer.parseInt(value));
			} else if (booleans.contains(key)) {
				conf.setBoolean(key, value.equalsIgnoreCase("true"));
			} else {
				throw new UnsupportedOperationException(key);
			}
		}
		earth.exportPng(os);
	}

	private Date extractGmt(String s) {
		final SimpleDateFormat timeFormat;
		if (s.matches("\\d{4}/\\d{2}/\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
			timeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		} else if (s.matches("\\d{4}/\\d{2}/\\d{2} \\d{2}:\\d{2}")) {
			timeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		} else {
			throw new UnsupportedOperationException(s);
		}
		timeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		try {
			return timeFormat.parse(s);
		} catch (ParseException e) {
			throw new UnsupportedOperationException(s);
		}

	}

	public String getDescription() {
		return "(XEarth)";
	}

}
