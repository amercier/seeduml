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
 * Revision $Revision: 7745 $
 *
 */
package net.sourceforge.plantuml.cucadiagram;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.Log;
import net.sourceforge.plantuml.UmlDiagram;
import net.sourceforge.plantuml.UmlDiagramInfo;
import net.sourceforge.plantuml.UmlDiagramType;
import net.sourceforge.plantuml.cucadiagram.dot.CucaDiagramFileMaker;
import net.sourceforge.plantuml.cucadiagram.dot.CucaDiagramFileMakerBeta;
import net.sourceforge.plantuml.cucadiagram.dot.CucaDiagramFileMakerResult;
import net.sourceforge.plantuml.cucadiagram.dot.CucaDiagramPngMaker3;
import net.sourceforge.plantuml.cucadiagram.dot.CucaDiagramTxtMaker;
import net.sourceforge.plantuml.cucadiagram.dot.DrawFile;
import net.sourceforge.plantuml.cucadiagram.dot.ICucaDiagramFileMaker;
import net.sourceforge.plantuml.html.CucaDiagramHtmlMaker;
import net.sourceforge.plantuml.png.PngSplitter;
import net.sourceforge.plantuml.skin.VisibilityModifier;
import net.sourceforge.plantuml.svek.CucaDiagramFileMakerSvek;
import net.sourceforge.plantuml.ugraphic.ColorMapper;
import net.sourceforge.plantuml.xmi.CucaDiagramXmiMaker;

public abstract class CucaDiagram extends UmlDiagram implements GroupHierarchy, PortionShower {

	private int horizontalPages = 1;
	private int verticalPages = 1;

	private final LinkedHashMap<String, Entity> entities = new LinkedHashMap<String, Entity>();
	// private final Map<String, Entity> entities = new TreeMap<String,
	// Entity>();
	private final Map<IEntity, Integer> nbLinks = new HashMap<IEntity, Integer>();

	private final List<Link> links = new ArrayList<Link>();

	private final Map<String, Group> groups = new LinkedHashMap<String, Group>();

	private Group currentGroup = null;
	private Rankdir rankdir = Rankdir.TOP_TO_BOTTOM;

	private boolean visibilityModifierPresent;

	public boolean hasUrl() {
		for (IEntity entity : entities.values()) {
			if (entity.getUrl() != null) {
				return true;
			}
		}
		return false;
	}

	public IEntity getOrCreateEntity(String code, EntityType defaultType) {
		IEntity result = entities.get(code);
		if (result == null) {
			result = createEntityInternal(code, code, defaultType, getCurrentGroup());
		}
		return result;
	}

	public Entity createEntity(String code, String display, EntityType type) {
		if (entities.containsKey(code)) {
			throw new IllegalArgumentException("Already known: " + code);
		}
		return createEntityInternal(code, display, type, getCurrentGroup());
	}

	final protected Entity createEntityInternal(String code, String display, EntityType type, Group group) {
		if (display == null) {
			display = code;
		}
		final Entity entity = new Entity(code, display, type, group, getHides());
		entities.put(code, entity);
		nbLinks.put(entity, 0);
		return entity;
	}

	public boolean entityExist(String code) {
		return entities.containsKey(code);
	}

	public void overideGroup(Group g, Entity proxy) {
		if (groups.containsValue(g) == false) {
			throw new IllegalArgumentException();
		}
		if (entities.containsKey(proxy.getCode())) {
			throw new IllegalArgumentException();
		}
		if (entities.containsValue(proxy)) {
			throw new IllegalArgumentException();
		}
		for (final ListIterator<Link> it = links.listIterator(); it.hasNext();) {
			final Link link = it.next();
			final Link newLink = link.mute(g, proxy);
			if (newLink == null) {
				it.remove();
			} else if (newLink != link) {
				it.set(newLink);
			}
		}
		groups.remove(g.getCode());
		assert groups.containsValue(g) == false;

		for (final Iterator<Entity> it = entities.values().iterator(); it.hasNext();) {
			final IEntity ent = it.next();
			if (ent.getParent() == g) {
				it.remove();
			}
		}
		entities.put(proxy.getCode(), proxy);
		// if (proxy.getImageFile() != null) {
		// proxy.addSubImage(proxy.getImageFile());
		// }
	}

	final public Collection<Group> getChildrenGroups(Group parent) {
		final Collection<Group> result = new ArrayList<Group>();
		for (Group g : groups.values()) {
			if (g.getParent() == parent) {
				result.add(g);
			}
		}
		// assert parent == null || result.size() ==
		// parent.getChildren().size();
		return Collections.unmodifiableCollection(result);
	}

	public final Group getOrCreateGroup(String code, String display, String namespace, GroupType type, Group parent) {
		final Group g = getOrCreateGroupInternal(code, display, namespace, type, parent);
		currentGroup = g;
		return g;
	}

	protected final Group getOrCreateGroupInternal(String code, String display, String namespace, GroupType type,
			Group parent) {
		// if (entityExist(code)) {
		// throw new IllegalArgumentException("code=" + code);
		// }
		Group g = groups.get(code);
		if (g == null) {
			g = new GroupImpl(code, display, namespace, type, parent);
			groups.put(code, g);

			Entity entityGroup = entities.get(code);
			if (entityGroup == null) {
				entityGroup = new Entity("$$" + code, code, EntityType.GROUP, g, getHides());
			} else {
				entityGroup.muteToCluster(g, groups.values());
			}
			g.setEntityCluster(entityGroup);
			nbLinks.put(entityGroup, 0);
		}
		return g;
	}

	public final Group getCurrentGroup() {
		return currentGroup;
	}

	public final Group getGroup(String code) {
		final Group p = groups.get(code);
		if (p == null) {
			return null;
		}
		return p;
	}

	public void endGroup() {
		if (currentGroup == null) {
			Log.error("No parent group");
			return;
		}
		currentGroup = currentGroup.getParent();
	}

	public final boolean isGroup(String code) {
		return groups.containsKey(code);
	}

	public final Collection<Group> getGroups(boolean withRootGroup) {
		if (withRootGroup == false) {
			return Collections.unmodifiableCollection(groups.values());
		}
		final Collection<Group> result = new ArrayList<Group>();
		result.add(getRootGroup());
		result.addAll(groups.values());
		return Collections.unmodifiableCollection(result);
	}

	private Group getRootGroup() {
		final Group result = new GroupImpl(null, null, null, GroupType.ROOT, null);
		for (IEntity ent : entities.values()) {
			if (ent.getParent() == null) {
				result.addEntity(ent);
			}
		}
		return result;
	}

	final public Map<String, Entity> entities() {
		if (getSkinParam().isSvek()) {
			return Collections.unmodifiableMap(entities);
		}
		return Collections.unmodifiableMap(new TreeMap<String, Entity>(entities));
	}

	final public void addLink(Link link) {
		links.add(link);
		inc(link.getEntity1());
		inc(link.getEntity2());
	}

	final protected void removeLink(Link link) {
		final boolean ok = links.remove(link);
		if (ok == false) {
			throw new IllegalStateException();
		}
	}

	private void inc(IEntity ent) {
		if (ent == null) {
			throw new IllegalArgumentException();
		}
		nbLinks.put(ent, nbLinks.get(ent) + 1);
	}

	final public List<Link> getLinks() {
		return Collections.unmodifiableList(links);
	}

	final public int getHorizontalPages() {
		return horizontalPages;
	}

	final public void setHorizontalPages(int horizontalPages) {
		this.horizontalPages = horizontalPages;
	}

	final public int getVerticalPages() {
		return verticalPages;
	}

	final public void setVerticalPages(int verticalPages) {
		this.verticalPages = verticalPages;
	}

	final public List<File> createPng2(File pngFile) throws IOException, InterruptedException {
		final CucaDiagramPngMaker3 maker = new CucaDiagramPngMaker3(this);
		return maker.createPng(pngFile);
	}

	final public void createPng2(OutputStream os) throws IOException {
		final CucaDiagramPngMaker3 maker = new CucaDiagramPngMaker3(this);
		maker.createPng(os);
	}

	abstract protected List<String> getDotStrings();

	final public String[] getDotStringSkek() {
		final List<String> result = new ArrayList<String>();
		for (String s : getDotStrings()) {
			if (s.startsWith("nodesep") || s.startsWith("ranksep")) {
				result.add(s);
			}
		}
		return result.toArray(new String[result.size()]);
	}

	private void createFilesXmi(OutputStream suggestedFile, FileFormat fileFormat) throws IOException {
		final CucaDiagramXmiMaker maker = new CucaDiagramXmiMaker(this, fileFormat);
		maker.createFiles(suggestedFile);
	}

	public static boolean BETA;

	private List<File> createFilesHtml(File suggestedFile) throws IOException {
		final String name = suggestedFile.getName();
		final int idx = name.lastIndexOf('.');
		final File dir = new File(suggestedFile.getParentFile(), name.substring(0, idx));
		final CucaDiagramHtmlMaker maker = new CucaDiagramHtmlMaker(this, dir);
		return maker.create();
	}

	@Override
	public List<File> exportDiagrams(File suggestedFile, FileFormatOption fileFormat) throws IOException,
			InterruptedException {
		if (suggestedFile.exists() && suggestedFile.isDirectory()) {
			throw new IllegalArgumentException("File is a directory " + suggestedFile);
		}

		if (fileFormat.getFileFormat() == FileFormat.HTML) {
			return createFilesHtml(suggestedFile);
		}

		final StringBuilder cmap = new StringBuilder();
		OutputStream os = null;
		try {
			os = new BufferedOutputStream(new FileOutputStream(suggestedFile));
			this.exportDiagram(os, cmap, 0, fileFormat);
		} finally {
			if (os != null) {
				os.close();
			}
		}
		List<File> result = Arrays.asList(suggestedFile);

		if (this.hasUrl() && cmap.length() > 0) {
			exportCmap(suggestedFile, cmap);
		}

		if (fileFormat.getFileFormat() == FileFormat.PNG) {
			result = new PngSplitter(suggestedFile, this.getHorizontalPages(), this.getVerticalPages(),
					this.getMetadata(), this.getDpi(fileFormat)).getFiles();
		}
		return result;

	}

	@Override
	final protected UmlDiagramInfo exportDiagramInternal(OutputStream os, StringBuilder cmap, int index,
			FileFormatOption fileFormatOption, List<BufferedImage> flashcodes) throws IOException {
		final FileFormat fileFormat = fileFormatOption.getFileFormat();

		if (fileFormat == FileFormat.ATXT || fileFormat == FileFormat.UTXT) {
			try {
				createFilesTxt(os, index, fileFormat);
			} catch (Throwable t) {
				t.printStackTrace(new PrintStream(os));
			}
			return new UmlDiagramInfo();
		}

		if (fileFormat.name().startsWith("XMI")) {
			createFilesXmi(os, fileFormat);
			return new UmlDiagramInfo();
		}
		//
		// if (OptionFlags.getInstance().useJavaInsteadOfDot()) {
		// return createPng2(suggestedFile);
		// }
		if (getUmlDiagramType() == UmlDiagramType.COMPOSITE || (BETA && getUmlDiagramType() == UmlDiagramType.CLASS)) {
			final CucaDiagramFileMakerBeta maker = new CucaDiagramFileMakerBeta(this);
			try {
				maker.createFile(os, getDotStrings(), fileFormat);
			} catch (InterruptedException e) {
				throw new IOException(e.toString());
			}
			return new UmlDiagramInfo();
		}

		if (getUmlDiagramType() == UmlDiagramType.COMPOSITE) {
			final CucaDiagramFileMakerBeta maker = new CucaDiagramFileMakerBeta(this);
			try {
				maker.createFile(os, getDotStrings(), fileFormat);
			} catch (InterruptedException e) {
				e.printStackTrace();
				throw new IOException(e.toString());
			}
			return new UmlDiagramInfo();
		}
		final ICucaDiagramFileMaker maker;
		if (getSkinParam().isSvek()) {
			maker = new CucaDiagramFileMakerSvek(this, flashcodes);
		} else {
			maker = new CucaDiagramFileMaker(this, flashcodes);
		}
		try {
			final CucaDiagramFileMakerResult result = maker.createFile(os, getDotStrings(), fileFormatOption);
			if (result != null && cmap != null && result.getCmapResult() != null) {
				cmap.append(result.getCmapResult());
			}
			if (result != null) {
				this.warningOrError = result.getWarningOrError();
			}
			return result == null ? new UmlDiagramInfo() : new UmlDiagramInfo(result.getWidth());
		} catch (InterruptedException e) {
			Log.error(e.toString());
			throw new IOException(e.toString());
		}
	}

	private String warningOrError;

	@Override
	public String getWarningOrError() {
		final String generalWarningOrError = super.getWarningOrError();
		if (warningOrError == null) {
			return generalWarningOrError;
		}
		if (generalWarningOrError == null) {
			return warningOrError;
		}
		return generalWarningOrError + "\n" + warningOrError;
	}

	private void createFilesTxt(OutputStream os, int index, FileFormat fileFormat) throws IOException {
		final CucaDiagramTxtMaker maker = new CucaDiagramTxtMaker(this, fileFormat);
		maker.createFiles(os, index);
	}

	public final Rankdir getRankdir() {
		return rankdir;
	}

	public final void setRankdir(Rankdir rankdir) {
		this.rankdir = rankdir;
	}

	public boolean isAutarkic(Group g) {
		if (g.getType() == GroupType.PACKAGE) {
			return false;
		}
		if (g.getType() == GroupType.INNER_ACTIVITY) {
			return true;
		}
		if (g.getType() == GroupType.CONCURRENT_ACTIVITY) {
			return true;
		}
		if (g.getType() == GroupType.CONCURRENT_STATE) {
			return true;
		}
		if (getChildrenGroups(g).size() > 0) {
			return false;
		}
		for (Link link : links) {
			final IEntity e1 = link.getEntity1();
			final IEntity e2 = link.getEntity2();
			if (e1.getParent() != g && e2.getParent() == g && e2.getType() != EntityType.GROUP) {
				return false;
			}
			if (e2.getParent() != g && e1.getParent() == g && e1.getType() != EntityType.GROUP) {
				return false;
			}
			if (link.isAutolink(g)) {
				continue;
			}
			if (e1.getType() == EntityType.GROUP && e2.getParent() == e1.getParent() && e1.getParent() == g) {
				return false;
			}
			if (e2.getType() == EntityType.GROUP && e2.getParent() == e1.getParent() && e1.getParent() == g) {
				return false;
			}

		}
		return true;
		// return false;
	}

	private static boolean isNumber(String s) {
		return s.matches("[+-]?(\\.?\\d+|\\d+\\.\\d*)");
	}

	public void resetPragmaLabel() {
		getPragma().undefine("labeldistance");
		getPragma().undefine("labelangle");
	}

	public String getLabeldistance() {
		if (getPragma().isDefine("labeldistance")) {
			final String s = getPragma().getValue("labeldistance");
			if (isNumber(s)) {
				return s;
			}
		}
		if (getPragma().isDefine("defaultlabeldistance")) {
			final String s = getPragma().getValue("defaultlabeldistance");
			if (isNumber(s)) {
				return s;
			}
		}
		// Default in dot 1.0
		return "1.7";
	}

	public String getLabelangle() {
		if (getPragma().isDefine("labelangle")) {
			final String s = getPragma().getValue("labelangle");
			if (isNumber(s)) {
				return s;
			}
		}
		if (getPragma().isDefine("defaultlabelangle")) {
			final String s = getPragma().getValue("defaultlabelangle");
			if (isNumber(s)) {
				return s;
			}
		}
		// Default in dot -25
		return "25";
	}

	final public boolean isEmpty(Group gToTest) {
		for (Group g : groups.values()) {
			if (g == gToTest) {
				continue;
			}
			if (g.getParent() == gToTest) {
				return false;
			}
		}
		return gToTest.entities().size() == 0;
	}

	public final boolean isVisibilityModifierPresent() {
		return visibilityModifierPresent;
	}

	public final void setVisibilityModifierPresent(boolean visibilityModifierPresent) {
		this.visibilityModifierPresent = visibilityModifierPresent;
	}

	private boolean isAutonom(Group g) {
		for (Link link : links) {
			final CrossingType type = g.getCrossingType(link);
			if (type == CrossingType.CUT) {
				return false;
			}
		}
		return true;
	}

	public final void computeAutonomyOfGroups() {
		for (Group g : groups.values()) {
			g.setAutonom(isAutonom(g));
		}
	}

	public final boolean showPortion(EntityPortion portion, IEntity entity) {
		boolean result = true;
		for (HideOrShow cmd : hideOrShows) {
			if (cmd.portion == portion && cmd.gender.contains(entity)) {
				result = cmd.show;
			}
		}
		return result;
	}

	public final void hideOrShow(EntityGender gender, Set<EntityPortion> portions, boolean show) {
		for (EntityPortion portion : portions) {
			this.hideOrShows.add(new HideOrShow(gender, portion, show));
		}
	}

	public void hideOrShow(Set<VisibilityModifier> visibilities, boolean show) {
		if (show) {
			hides.removeAll(visibilities);
		} else {
			hides.addAll(visibilities);
		}
	}

	private final List<HideOrShow> hideOrShows = new ArrayList<HideOrShow>();
	private final Set<VisibilityModifier> hides = new HashSet<VisibilityModifier>();

	static class HideOrShow {
		private final EntityGender gender;
		private final EntityPortion portion;
		private final boolean show;

		public HideOrShow(EntityGender gender, EntityPortion portion, boolean show) {
			this.gender = gender;
			this.portion = portion;
			this.show = show;
		}
	}

	@Override
	public int getNbImages() {
		return this.horizontalPages * this.verticalPages;
	}

	public final Set<VisibilityModifier> getHides() {
		return Collections.unmodifiableSet(hides);
	}

	public void clean() throws IOException {
		for (Imaged entity : entities().values()) {
			cleanTemporaryFiles(entity);
		}
		for (Imaged entity : getLinks()) {
			cleanTemporaryFiles(entity);
		}
		for (Group g : groups.values()) {
			final IEntity entity = g.getEntityCluster();
			if (entity != null) {
				cleanTemporaryFiles(entity);
			}
		}
		for (DrawFile f : ensureDeletes) {
			f.deleteDrawFile();
		}
	}

	private void cleanTemporaryFiles(Imaged entity) {
		if (entity.getImageFile() != null) {
			entity.getImageFile().deleteDrawFile();
		}
		if (entity instanceof Entity) {
			((Entity) entity).cleanSubImage();
		}
	}

	private final Set<DrawFile> ensureDeletes = new HashSet<DrawFile>();

	public void ensureDelete(DrawFile imageFile) {
		if (imageFile == null) {
			throw new IllegalArgumentException();
		}
		ensureDeletes.add(imageFile);
	}

	public ColorMapper getColorMapper() {
		return getSkinParam().getColorMapper();
	}

	final public boolean isStandalone(IEntity ent) {
		for (final Link link : getLinks()) {
			if (link.getEntity1() == ent || link.getEntity2() == ent) {
				return false;
			}
		}
		return true;
	}

	final public Link getLastLink() {
		final List<Link> links = getLinks();
		for (int i = links.size() - 1; i >= 0; i--) {
			final Link link = links.get(i);
			if (link.getEntity1().getType() != EntityType.NOTE && link.getEntity2().getType() != EntityType.NOTE) {
				return link;
			}
		}
		return null;
	}

	final public IEntity getLastEntity() {
		for (final Iterator<Entity> it = entities.values().iterator(); it.hasNext();) {
			final Entity ent = it.next();
			if (it.hasNext() == false) {
				return ent;
			}
		}
		return null;
	}

}
