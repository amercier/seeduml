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
 * Revision $Revision: 5041 $
 *
 */
package net.sourceforge.plantuml.command;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.plantuml.PSystem;

public abstract class CommandMultilinesBracket<S extends PSystem> implements Command {

	private final S system;

	private final Pattern starting;

	public CommandMultilinesBracket(final S system, String patternStart) {
		if (patternStart.startsWith("(?i)^") == false || patternStart.endsWith("$") == false) {
			throw new IllegalArgumentException("Bad pattern " + patternStart);
		}
		this.system = system;
		this.starting = Pattern.compile(patternStart);
	}

	protected boolean isCommandForbidden() {
		return false;
	}
	
	public String[] getDescription() {
		return new String[] { "BRACKET: " + starting.pattern() };
	}

	protected void actionIfCommandValid() {
	}

	protected S getSystem() {
		return system;
	}

	protected final Pattern getStartingPattern() {
		return starting;
	}

	public boolean isDeprecated(List<String> line) {
		return false;
	}

	public String getHelpMessageForDeprecated(List<String> lines) {
		return null;
	}

	final public CommandControl isValid(List<String> lines) {
		if (isCommandForbidden()) {
			return CommandControl.NOT_OK;
		}
		final Matcher m1 = starting.matcher(lines.get(0).trim());
		if (m1.matches() == false) {
			return CommandControl.NOT_OK;
		}
		if (lines.size() == 1) {
			return CommandControl.OK_PARTIAL;
		}

		int level = 1;
		for (int i = 1; i < lines.size(); i++) {
			final String s = lines.get(i).trim();
			if (isLineConsistent(s, level) == false) {
				return CommandControl.NOT_OK;
			}
			if (s.endsWith("{")) {
				level++;
			}
			if (s.endsWith("}")) {
				level--;
			}
			if (level < 0) {
				return CommandControl.NOT_OK;
			}
		}

		if (level != 0) {
			return CommandControl.OK_PARTIAL;
		}

		actionIfCommandValid();
		return CommandControl.OK;
	}

	protected abstract boolean isLineConsistent(String line, int level);
}
