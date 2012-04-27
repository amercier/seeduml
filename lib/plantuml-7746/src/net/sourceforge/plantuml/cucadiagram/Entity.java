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
 * Revision $Revision: 7736 $
 *
 */
package net.sourceforge.plantuml.cucadiagram;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.UniqueSequence;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.cucadiagram.dot.DrawFile;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.TextBlockWidth;
import net.sourceforge.plantuml.graphic.TextBlockWidthVertical;
import net.sourceforge.plantuml.skin.VisibilityModifier;
import net.sourceforge.plantuml.svek.IEntityImage;

public class Entity implements IEntity {

	private final String code;
	private List<? extends CharSequence> display2;

	private final String uid;
	private EntityType type;

	private Stereotype stereotype;
	private String generic;

	private final List<String> rawBody = new ArrayList<String>();

	private final Set<VisibilityModifier> hides;

	private Group container;

	private DrawFile imageFile;
	private Url url2;

	private boolean top;

	public final boolean isTop() {
		return top;
	}

	public final void setTop(boolean top) {
		this.top = top;
	}

	public Entity(String code, String display, EntityType type, Group entityPackage, Set<VisibilityModifier> hides) {
		this("cl", UniqueSequence.getValue(), code, display, type, entityPackage, hides);
	}

	public Entity(String uid1, int uid2, String code, String display, EntityType type, Group entityPackage,
			Set<VisibilityModifier> hides) {
		this(uid1, uid2, code, StringUtils.getWithNewlines(display), type, entityPackage, hides);
	}

	public Entity(String uid1, int uid2, String code, List<? extends CharSequence> display, EntityType type,
			Group entityPackage, Set<VisibilityModifier> hides) {
		if (code == null || code.length() == 0) {
			throw new IllegalArgumentException();
		}
		if (display == null /* || display.length() == 0 */) {
			throw new IllegalArgumentException();
		}
		this.hides = hides;
		this.uid = StringUtils.getUid(uid1, uid2);
		this.type = type;
		this.code = code;
		this.display2 = display;
		this.container = entityPackage;
		if (entityPackage != null && type != EntityType.GROUP) {
			entityPackage.addEntity(this);
		}
	}

	public void setEntityPackage(Group entityPackage) {
		if (entityPackage == null) {
			throw new IllegalArgumentException();
		}
		if (this.container != null) {
			throw new IllegalStateException();
		}
		this.container = entityPackage;
		entityPackage.addEntity(this);
	}

	public void addFieldOrMethod(String s) {
		rawBody.add(s);
	}

	private boolean isBodyEnhanced() {
		for (String s : rawBody) {
			if (BodyEnhanced.isBlockSeparator(s)) {
				return true;
			}
		}
		return false;
	}

	private boolean isMethod(String s) {
		if (getType() == EntityType.ABSTRACT_CLASS || getType() == EntityType.CLASS
				|| getType() == EntityType.INTERFACE || getType() == EntityType.ENUM) {
			return StringUtils.isMethod(s);
		}
		return false;
	}

	private IEntity blocDisplayProxy;

	public void overidesFieldsToDisplay(IEntity blocDisplayProxy) {
		this.blocDisplayProxy = blocDisplayProxy;
	}

	public List<Member> getMethodsToDisplay() {
		if (blocDisplayProxy != null) {
			return blocDisplayProxy.getMethodsToDisplay();
		}
		final List<Member> result = new ArrayList<Member>();
		for (int i = 0; i < rawBody.size(); i++) {
			final String s = rawBody.get(i);
			if (isMethod(i, rawBody) == false) {
				continue;
			}
			if (s.length() == 0 && result.size() == 0) {
				continue;
			}
			final Member m = new MemberImpl(s, true);
			if (hides == null || hides.contains(m.getVisibilityModifier()) == false) {
				result.add(m);
			}
		}
		removeFinalEmptyMembers(result);
		return Collections.unmodifiableList(result);
	}

	private boolean isMethod(int i, List<String> rawBody) {
		if (i > 0 && i < rawBody.size() - 1 && rawBody.get(i).length() == 0 && isMethod(rawBody.get(i - 1))
				&& isMethod(rawBody.get(i + 1))) {
			return true;
		}
		return isMethod(rawBody.get(i));
	}

	public List<Member> getFieldsToDisplay() {
		if (blocDisplayProxy != null) {
			return blocDisplayProxy.getFieldsToDisplay();
		}
		final List<Member> result = new ArrayList<Member>();
		for (String s : rawBody) {
			if (isMethod(s) == true) {
				continue;
			}
			if (s.length() == 0 && result.size() == 0) {
				continue;
			}
			final Member m = new MemberImpl(s, false);
			if (hides == null || hides.contains(m.getVisibilityModifier()) == false) {
				result.add(m);
			}
		}
		removeFinalEmptyMembers(result);
		return Collections.unmodifiableList(result);
	}

	private void removeFinalEmptyMembers(List<Member> result) {
		while (result.size() > 0 && result.get(result.size() - 1).getDisplay(false).trim().length() == 0) {
			result.remove(result.size() - 1);
		}
	}

	public EntityType getType() {
		return type;
	}

	public void muteToType(EntityType newType) {
		if (type != EntityType.ABSTRACT_CLASS && type != EntityType.CLASS && type != EntityType.ENUM
				&& type != EntityType.INTERFACE) {
			throw new IllegalArgumentException("type=" + type);
		}
		if (newType != EntityType.ABSTRACT_CLASS && newType != EntityType.CLASS && newType != EntityType.ENUM
				&& newType != EntityType.INTERFACE) {
			throw new IllegalArgumentException("newtype=" + newType);
		}
		this.type = newType;
	}

	public String getCode() {
		return code;
	}

	public List<? extends CharSequence> getDisplay2() {
		return display2;
	}

	public void setDisplay2(String display) {
		this.display2 = StringUtils.getWithNewlines(display);
	}

	public void setDisplay2(List<? extends CharSequence> display) {
		this.display2 = display;
	}

	public String getUid() {
		return uid;
	}

	public Stereotype getStereotype() {
		return stereotype;
	}

	public final void setStereotype(Stereotype stereotype) {
		this.stereotype = stereotype;
	}

	public final Group getParent() {
		return container;
	}

	@Override
	public String toString() {
		if (type == EntityType.GROUP) {
			return display2 + "(" + getType() + ")" + this.container;
		}
		return display2 + "(" + getType() + ") " + xposition + " " + getUid();
	}

	public void muteToCluster(Group newGroup, Collection<Group> others) {
		if (type == EntityType.GROUP) {
			throw new IllegalStateException();
		}
		for (Group other : others) {
			other.removeInternal(this);
		}
		this.type = EntityType.GROUP;
		this.container = newGroup;
	}

	public void moveTo(Group dest) {
		this.container = dest;
		dest.addEntity(this);
	}

	public final DrawFile getImageFile() {
		return imageFile;
	}

	public final void setImageFile(DrawFile imageFile) {
		this.imageFile = imageFile;
	}

	private HtmlColor specificBackcolor;

	public HtmlColor getSpecificBackColor() {
		return specificBackcolor;
	}

	public void setSpecificBackcolor(HtmlColor color) {
		this.specificBackcolor = color;
	}

	public final Url getUrl() {
		return url2;
	}

	public final void setUrl(Url url) {
		this.url2 = url;
	}

	@Override
	public int hashCode() {
		return uid.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		final IEntity other = (IEntity) obj;
		return uid.equals(other.getUid());
	}

	private final Set<DrawFile> subImages = new HashSet<DrawFile>();

	public void addSubImage(DrawFile subImage) {
		if (subImage == null) {
			throw new IllegalArgumentException();
		}
		subImages.add(subImage);
	}

	public void addSubImage(Entity other) {
		subImages.addAll(other.subImages);
	}

	public DrawFile getImageFile(File searched) throws IOException {
		if (imageFile != null && imageFile.getPng().getCanonicalFile().equals(searched)) {
			return imageFile;
		}
		for (DrawFile f : subImages) {
			if (f.getPng().getCanonicalFile().equals(searched)) {
				return f;
			}
		}
		return null;
	}

	public void cleanSubImage() {
		for (DrawFile f : subImages) {
			f.deleteDrawFile();
		}
	}

	private boolean nearDecoration = false;

	public final boolean hasNearDecoration() {
		return nearDecoration;
	}

	public final void setNearDecoration(boolean nearDecoration) {
		this.nearDecoration = nearDecoration;
	}

	public int compareTo(IEntity other) {
		return getUid().compareTo(other.getUid());
	}

	private int xposition;

	public int getXposition() {
		return xposition;
	}

	public void setXposition(int pos) {
		xposition = pos;
	}

	private IEntityImage svekImage;

	public final IEntityImage getSvekImage() {
		return svekImage;
	}

	public final void setSvekImage(IEntityImage svekImage) {
		this.svekImage = svekImage;
	}

	public final void setGeneric(String generic) {
		this.generic = generic;
	}

	public final String getGeneric() {
		return generic;
	}

	public BlockMember getBody(final PortionShower portionShower) {
		if (getType() == EntityType.CLASS && isBodyEnhanced()) {
			return getBodyEnhanced();
		}
		return new BlockMember() {
			public TextBlockWidth asTextBlock(FontParam fontParam, ISkinParam skinParam) {
				if (getType() == EntityType.ABSTRACT_CLASS || getType() == EntityType.CLASS
						|| getType() == EntityType.INTERFACE || getType() == EntityType.ENUM) {

					final boolean showMethods = portionShower.showPortion(EntityPortion.METHOD, Entity.this);
					final boolean showFields = portionShower.showPortion(EntityPortion.FIELD, Entity.this);

					if (showFields && showMethods) {
						return new TextBlockWidthVertical(new BlockMemberImpl(getFieldsToDisplay()).asTextBlock(
								fontParam, skinParam), new BlockMemberImpl(getMethodsToDisplay()).asTextBlock(
								fontParam, skinParam));
					} else if (showFields) {
						return new BlockMemberImpl(getFieldsToDisplay()).asTextBlock(fontParam, skinParam);
					} else if (showMethods) {
						return new BlockMemberImpl(getMethodsToDisplay()).asTextBlock(fontParam, skinParam);
					}
					return null;
				}
				if (getType() == EntityType.OBJECT) {
					return new BlockMemberImpl(getFieldsToDisplay()).asTextBlock(fontParam, skinParam);
				}
				throw new UnsupportedOperationException();
			}
		};
	}

	private BlockMember getBodyEnhanced() {
		return new BlockMember() {
			public TextBlockWidth asTextBlock(FontParam fontParam, ISkinParam skinParam) {
				return new BodyEnhanced(rawBody, fontParam, skinParam);
			}
		};
	}

	public BlockMember getMouseOver() {
		if (mouseOver.size() == 0) {
			return null;
		}
		return new BlockMember() {
			public TextBlockWidth asTextBlock(FontParam fontParam, ISkinParam skinParam) {
				return new BodyEnhanced(mouseOver, fontParam, skinParam);
			}
		};
	}

	private final List<String> mouseOver = new ArrayList<String>();

	public void mouseOver(String s) {
		mouseOver.add(s);
	}
}
