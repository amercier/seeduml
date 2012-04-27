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
 * Revision $Revision: 4236 $
 * 
 */
package net.sourceforge.plantuml.svek;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.Log;
import net.sourceforge.plantuml.OptionFlags;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.UmlDiagramType;
import net.sourceforge.plantuml.cucadiagram.Group;
import net.sourceforge.plantuml.cucadiagram.Rankdir;
import net.sourceforge.plantuml.cucadiagram.dot.DotData;
import net.sourceforge.plantuml.cucadiagram.dot.Graphviz;
import net.sourceforge.plantuml.cucadiagram.dot.GraphvizUtils;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.posimo.Moveable;

public class DotStringFactory implements Moveable {

	private final List<Shape> allShapes = new ArrayList<Shape>();
	private final List<Cluster> allCluster = new ArrayList<Cluster>();

	final private Set<String> rankMin = new HashSet<String>();

	private final ColorSequence colorSequence;
	private final Cluster root;
	private final List<Line> lines0 = new ArrayList<Line>();
	private final List<Line> lines1 = new ArrayList<Line>();
	private final List<Line> allLines = new ArrayList<Line>();
	private Cluster current;
	private final DotData dotData;

	private final StringBounder stringBounder;

	public DotStringFactory(ColorSequence colorSequence, StringBounder stringBounder, DotData dotData) {
		this.colorSequence = colorSequence;
		this.dotData = dotData;
		this.stringBounder = stringBounder;
		this.root = new Cluster(colorSequence, dotData.getSkinParam());
		this.current = root;
	}

	public void addShape(Shape shape) {
		allShapes.add(shape);
		current.addShape(shape);
	}

	private void printMinRanking(StringBuilder sb) {
		if (rankMin.size() == 0) {
			return;
		}
		sb.append("{ rank = min;");
		for (String id : rankMin) {
			sb.append(id);
			sb.append(";");
		}
		sb.append("}");

	}

	private double getHorizontalDzeta() {
		double max = 0;
		for (Line l : allLines) {
			final double c = l.getHorizontalDzeta(stringBounder);
			if (c > max) {
				max = c;
			}
		}
		return max / 10;
	}

	private double getVerticalDzeta() {
		double max = 0;
		for (Line l : allLines) {
			final double c = l.getVerticalDzeta(stringBounder);
			if (c > max) {
				max = c;
			}
		}
		return max / 10;
	}

	String createDotString(String... dotStrings) {
		final StringBuilder sb = new StringBuilder();

		double nodesep = getHorizontalDzeta();
		if (nodesep < getMinNodeSep()) {
			nodesep = getMinNodeSep();
		}
		final String nodesepInches = SvekUtils.pixelToInches(nodesep);
		// System.err.println("nodesep=" + nodesepInches);
		double ranksep = getVerticalDzeta();
		if (ranksep < getMinRankSep()) {
			ranksep = getMinRankSep();
		}
		final String ranksepInches = SvekUtils.pixelToInches(ranksep);
		// System.err.println("ranksep=" + ranksepInches);
		sb.append("digraph unix {");
		SvekUtils.println(sb);

		for (String s : dotStrings) {
			if (s.startsWith("ranksep")) {
				sb.append("ranksep=" + ranksepInches + ";");
			} else if (s.startsWith("nodesep")) {
				sb.append("nodesep=" + nodesepInches + ";");
			} else {
				sb.append(s);
			}
			SvekUtils.println(sb);
		}
		sb.append("remincross=true;");
		SvekUtils.println(sb);
		sb.append("searchsize=500;");
		SvekUtils.println(sb);
		sb.append("compound=true;");
		SvekUtils.println(sb);
		
		if (dotData.getRankdir() == Rankdir.LEFT_TO_RIGHT) {
			sb.append("rankdir=LR;");
			SvekUtils.println(sb);
		}


		root.printCluster1(sb, allLines);
		for (Line line : lines0) {
			line.appendLine(sb);
		}
		root.fillRankMin(rankMin);
		root.printCluster2(sb, allLines);
		printMinRanking(sb);

		for (Line line : lines1) {
			line.appendLine(sb);
		}
		SvekUtils.println(sb);
		sb.append("}");

		return sb.toString();
	}

	private int getMinRankSep() {
		if (dotData.getUmlDiagramType() == UmlDiagramType.ACTIVITY) {
			// return 29;
			return 40;
		}
		return 60;
	}

	private int getMinNodeSep() {
		if (dotData.getUmlDiagramType() == UmlDiagramType.ACTIVITY) {
			// return 15;
			return 20;
		}
		return 35;
	}

	String getSVG(String... dotStrings) throws IOException, InterruptedException {
		final String dotString = createDotString(dotStrings);
		if (OptionFlags.TRACE_DOT) {
			System.err.println("dotString=" + dotString);
		}

		final Graphviz graphviz = GraphvizUtils.create(dotString, "svg");
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		graphviz.createFile(baos);
		baos.close();
		final byte[] result = baos.toByteArray();
		final String s = new String(result, "UTF-8");

		if (OptionFlags.SVEK || OptionFlags.getInstance().isKeepTmpFiles()) {
			Log.info("Creating temporary file svek.svg");
			SvekUtils.traceSvgString(s);
		}

		return s;
	}

	public boolean illegalDotExe() {
		final Graphviz graphviz = GraphvizUtils.create(null, "svg");
		final File dotExe = graphviz.getDotExe();
		return dotExe == null || dotExe.isFile() == false || dotExe.canRead() == false;
	}

	public File getDotExe() {
		final Graphviz graphviz = GraphvizUtils.create(null, "svg");
		return graphviz.getDotExe();
	}

	public Dimension2D solve(String... dotStrings) throws IOException, InterruptedException {
		final String svg = getSVG(dotStrings);

		final Pattern pGraph = Pattern.compile("(?m)\\<svg\\s+width=\"(\\d+)pt\"\\s+height=\"(\\d+)pt\"");
		final Matcher mGraph = pGraph.matcher(svg);
		if (mGraph.find() == false) {
			throw new IllegalStateException();
		}
		final int fullWidth = Integer.parseInt(mGraph.group(1));
		final int fullHeight = Integer.parseInt(mGraph.group(2));

		for (Shape sh : allShapes) {
			int idx = svg.indexOf("<title>" + sh.getUid() + "</title>");
			if (sh.getType() == ShapeType.RECTANGLE || sh.getType() == ShapeType.DIAMOND) {
				final List<Point2D.Double> points = SvekUtils.extractPointsList(svg, idx, fullHeight);
				final double minX = SvekUtils.getMinX(points);
				final double minY = SvekUtils.getMinY(points);
				sh.setMinX(minX);
				sh.setMinY(minY);
			} else if (sh.getType() == ShapeType.ROUND_RECTANGLE) {
				idx = svg.indexOf("points=\"", idx + 1);
				final List<Point2D.Double> points = SvekUtils.extractPointsList(svg, idx, fullHeight);
				for (int i = 0; i < 3; i++) {
					idx = svg.indexOf("points=\"", idx + 1);
					points.addAll(SvekUtils.extractPointsList(svg, idx, fullHeight));
				}
				final double minX = SvekUtils.getMinX(points);
				final double minY = SvekUtils.getMinY(points);
				sh.setMinX(minX);
				sh.setMinY(minY);
			} else if (sh.getType() == ShapeType.CIRCLE || sh.getType() == ShapeType.CIRCLE_IN_RECT
					|| sh.getType() == ShapeType.OVAL) {
				final double cx = SvekUtils.getValue(svg, idx, "cx");
				final double cy = SvekUtils.getValue(svg, idx, "cy") + fullHeight;
				final double rx = SvekUtils.getValue(svg, idx, "rx");
				final double ry = SvekUtils.getValue(svg, idx, "ry");
				sh.setMinX(cx - rx);
				sh.setMinY(cy - ry);
			} else {
				throw new IllegalStateException(sh.getType().toString() + " " + sh.getUid());
			}
		}

		for (Cluster cluster : allCluster) {
			// final String key = "=\"" + StringUtils.getAsHtml(cluster.getColor()).toLowerCase() + "\"";
			int idx = getClusterIndex(svg, cluster.getColor());
//			int idx = svg.indexOf(key);
//			if (idx == -1) {
//				throw new IllegalStateException(key);
//			}
			final List<Point2D.Double> points = SvekUtils.extractPointsList(svg, idx, fullHeight);
			final double minX = SvekUtils.getMinX(points);
			final double minY = SvekUtils.getMinY(points);
			final double maxX = SvekUtils.getMaxX(points);
			final double maxY = SvekUtils.getMaxY(points);
			cluster.setPosition(minX, minY, maxX, maxY);

			if (cluster.getTitleWidth() == 0 || cluster.getTitleHeight() == 0) {
				continue;
			}
			idx = getClusterIndex(svg, cluster.getTitleColor());
			final List<Point2D.Double> pointsTitle = SvekUtils.extractPointsList(svg, idx, fullHeight);
			final double minXtitle = SvekUtils.getMinX(pointsTitle);
			final double minYtitle = SvekUtils.getMinY(pointsTitle);
			cluster.setTitlePosition(minXtitle, minYtitle);

		}

		for (Line line : allLines) {
			line.solveLine(svg, fullHeight);
		}

		for (Line line : allLines) {
			line.manageCollision(allShapes);
		}

		return new Dimension2DDouble(fullWidth, fullHeight);
	}

	private int getClusterIndex(final String svg, int colorInt) {
		final String colorString = StringUtils.getAsHtml(colorInt).toLowerCase();
		final String keyTitle1 = "=\"" + colorString + "\"";
		int idx = svg.indexOf(keyTitle1);
		if (idx == -1) {
			final String keyTitle2 = "stroke:" + colorString + ";";
			idx = svg.indexOf(keyTitle2);
		}
		if (idx == -1) {
			throw new IllegalStateException("Cannot find color " + colorString);
		}
		return idx;
	}

	public void addLine(Line line) {
		allLines.add(line);
		if (first(line)) {
			lines0.add(line);
		} else {
			lines1.add(line);
		}
	}

	private static boolean first(Line line) {
		final int length = line.getLength();
		if (length == 1) {
			return true;
		}
		return false;
	}

	public final List<Shape> getShapes() {
		return Collections.unmodifiableList(allShapes);
	}

	public List<Line> getLines() {
		return Collections.unmodifiableList(allLines);
	}

	public void openCluster(Group g, int titleWidth, int titleHeight, TextBlock title, boolean isSpecialGroup) {
		this.current = current.createChild(g, titleWidth, titleHeight, title, isSpecialGroup, colorSequence, dotData
				.getSkinParam());
		this.allCluster.add(this.current);
	}

	public void closeCluster() {
		if (current.getParent() == null) {
			throw new IllegalStateException();
		}
		this.current = current.getParent();
	}

	public final List<Cluster> getAllSubCluster() {
		return Collections.unmodifiableList(allCluster);
	}

	public void moveSvek(double deltaX, double deltaY) {
		for (Shape sh : allShapes) {
			sh.moveSvek(deltaX, deltaY);
		}
		for (Line line : allLines) {
			line.moveSvek(deltaX, deltaY);
		}
		for (Cluster cl : allCluster) {
			cl.moveSvek(deltaX, deltaY);
		}

	}

}
