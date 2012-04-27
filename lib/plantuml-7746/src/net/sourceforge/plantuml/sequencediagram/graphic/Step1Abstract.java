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
 * Revision $Revision: 4683 $
 *
 */
package net.sourceforge.plantuml.sequencediagram.graphic;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.sequencediagram.AbstractMessage;
import net.sourceforge.plantuml.sequencediagram.LifeEvent;
import net.sourceforge.plantuml.sequencediagram.LifeEventType;
import net.sourceforge.plantuml.sequencediagram.MessageNumber;
import net.sourceforge.plantuml.sequencediagram.NotePosition;
import net.sourceforge.plantuml.sequencediagram.Participant;
import net.sourceforge.plantuml.skin.ArrowConfiguration;
import net.sourceforge.plantuml.skin.Component;
import net.sourceforge.plantuml.skin.ComponentType;

abstract class Step1Abstract {

	private final StringBounder stringBounder;

	private final DrawableSet drawingSet;

	private final AbstractMessage message;

	private Frontier freeY2;

//	private ComponentType type;
	private ArrowConfiguration config;

	private Component note;

	private ParticipantRange range;

	Step1Abstract(ParticipantRange range, StringBounder stringBounder, AbstractMessage message, DrawableSet drawingSet,
			Frontier freeY2) {
		if (freeY2 == null) {
			throw new IllegalArgumentException();
		}
		this.range = range;
		this.stringBounder = stringBounder;
		this.message = message;
		this.freeY2 = freeY2;
		this.drawingSet = drawingSet;
	}

	protected final ParticipantRange getParticipantRange() {
		return range;
	}

	abstract Frontier prepareMessage(ConstraintSet constraintSet, InGroupablesStack groupingStructures);

	protected final List<? extends CharSequence> getLabelOfMessage(AbstractMessage message) {
		if (message.getMessageNumber() == null) {
			return message.getLabel();
		}
		final List<CharSequence> result = new ArrayList<CharSequence>();
		result.add(new MessageNumber(message.getMessageNumber()));
		result.addAll(message.getLabel());
		return result;
	}

	protected final void beforeMessage(LifeEvent n, final double pos) {
		final Participant p = n.getParticipant();
		final LifeLine line = drawingSet.getLivingParticipantBox(p).getLifeLine();

		if (n.getType() != LifeEventType.ACTIVATE) {
			return;
		}
		assert n.getType() == LifeEventType.ACTIVATE;

		int delta = 0;
		if (message.isCreate()) {
			delta += 10;
		}
		line.addSegmentVariation(LifeSegmentVariation.LARGER, pos + delta, n.getSpecificBackColor());
	}

	protected final void afterMessage(StringBounder stringBounder, LifeEvent n, final double pos) {
		final Participant p = n.getParticipant();
		final LifeLine line = drawingSet.getLivingParticipantBox(p).getLifeLine();

		if (n.getType() == LifeEventType.ACTIVATE || n.getType() == LifeEventType.CREATE) {
			return;
		}

		if (n.getType() == LifeEventType.DESTROY) {
			final Component comp = drawingSet.getSkin().createComponent(ComponentType.DESTROY, null,
					drawingSet.getSkinParam(), null);
			final double delta = comp.getPreferredHeight(stringBounder) / 2;
			final LifeDestroy destroy = new LifeDestroy(pos - delta, drawingSet.getLivingParticipantBox(p)
					.getParticipantBox(), comp);
			if (lifelineAfterDestroy()) {
				line.setDestroy(pos);
			}
			drawingSet.addEvent(n, destroy);
		} else if (n.getType() != LifeEventType.DEACTIVATE) {
			throw new IllegalStateException();
		}

		line.addSegmentVariation(LifeSegmentVariation.SMALLER, pos, n.getSpecificBackColor());
	}

	private boolean lifelineAfterDestroy() {
		final String v = drawingSet.getSkinParam().getValue("lifelineafterdestroy");
		return false;
	}

//	protected final ComponentType getType() {
//		return type;
//	}
//
//	protected final void setType(ComponentType type) {
//		this.type = type;
//	}
	
	protected final ArrowConfiguration getConfig() {
		return config;
	}

	protected final void setConfig(ArrowConfiguration config) {
		this.config = config;
	}



	protected final Component getNote() {
		return note;
	}

	protected final void setNote(Component note) {
		this.note = note;
	}

	protected final StringBounder getStringBounder() {
		return stringBounder;
	}

	protected final AbstractMessage getMessage() {
		return message;
	}

	protected final DrawableSet getDrawingSet() {
		return drawingSet;
	}

	protected final Frontier getFreeY() {
		return freeY2;
	}

	protected final void incFreeY(double v) {
		freeY2 = freeY2.add(v, range);
	}

	protected final NoteBox createNoteBox(StringBounder stringBounder, Arrow arrow, Component noteComp,
			NotePosition notePosition, Url url) {
		final LivingParticipantBox p = arrow.getParticipantAt(stringBounder, notePosition);
		final NoteBox noteBox = new NoteBox(arrow.getStartingY(), noteComp, p, null, notePosition, url);

		if (arrow instanceof MessageSelfArrow && notePosition == NotePosition.RIGHT) {
			noteBox.pushToRight(arrow.getPreferredWidth(stringBounder));
		}
		// if (arrow instanceof MessageExoArrow) {
		// final MessageExoType type = ((MessageExoArrow) arrow).getType();
		// if (type.isRightBorder()) {
		// final double width = noteBox.getPreferredWidth(stringBounder);
		// noteBox.pushToRight(-width);
		// }
		// }

		return noteBox;
	}

}
