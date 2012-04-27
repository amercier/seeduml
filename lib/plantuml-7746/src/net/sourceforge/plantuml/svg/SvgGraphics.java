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
package net.sourceforge.plantuml.svg;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.sourceforge.plantuml.Log;
import net.sourceforge.plantuml.code.Base64Coder;
import net.sourceforge.plantuml.eps.EpsGraphics;
import net.sourceforge.plantuml.ugraphic.UPath;
import net.sourceforge.plantuml.ugraphic.USegment;
import net.sourceforge.plantuml.ugraphic.USegmentType;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SvgGraphics {

	// http://tutorials.jenkov.com/svg/index.html
	// http://www.svgbasics.com/
	// http://apike.ca/prog_svg_text.html
	// http://www.w3.org/TR/SVG11/shapes.html
	// http://en.wikipedia.org/wiki/Scalable_Vector_Graphics

	// Animation:
	// http://srufaculty.sru.edu/david.dailey/svg/
	// Shadow:
	// http://www.svgbasics.com/filters3.html
	// http://www.w3schools.com/svg/svg_feoffset.asp
	// http://www.adobe.com/svg/demos/samples.html

	final private Document document;
	final private Element root;
	final private Element defs;
	final private Element gRoot;

	private String fill = "black";
	private String stroke = "black";
	private String strokeWidth = "1";
	private String strokeDasharray = null;
	private final String backcolor;

	private int maxX = 10;
	private int maxY = 10;

	final protected void ensureVisible(double x, double y) {
		if (x > maxX) {
			maxX = (int) (x + 1);
		}
		if (y > maxY) {
			maxY = (int) (y + 1);
		}
	}

	public SvgGraphics() {
		this(null);
	}

	public SvgGraphics(String backcolor) {
		try {
			this.document = getDocument();
			this.backcolor = backcolor;

			this.root = getRootNode();

			// Create a node named defs, which will be the parent
			// for a pair of linear gradient definitions.
			defs = simpleElement("defs");
			gRoot = simpleElement("g");
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			throw new IllegalStateException(e);
		}

	}

	// This method returns a reference to a simple XML
	// element node that has no attributes.
	private Element simpleElement(String type) {
		final Element theElement = (Element) document.createElement(type);
		root.appendChild(theElement);
		return theElement;
	}

	private Document getDocument() throws ParserConfigurationException {
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		final DocumentBuilder builder = factory.newDocumentBuilder();
		final Document document = builder.newDocument();
		document.setXmlStandalone(true);
		return document;
	}

	// This method returns a reference to a root node that
	// has already been appended to the document.
	private Element getRootNode() {
		// Create the root node named svg and append it to
		// the document.
		final Element svg = (Element) document.createElement("svg");
		document.appendChild(svg);

		// Set some attributes on the root node that are
		// required for proper rendering. Note that the
		// approach used here is somewhat different from the
		// approach used in the earlier program named Svg01,
		// particularly with regard to the style.
		svg.setAttribute("xmlns", "http://www.w3.org/2000/svg");
		svg.setAttribute("xmlns:xlink", "http://www.w3.org/1999/xlink");
		svg.setAttribute("version", "1.1");

		return svg;
	}

	public void svgEllipse(double x, double y, double xRadius, double yRadius, double deltaShadow) {
		manageShadow(deltaShadow);
		final Element elt = (Element) document.createElement("ellipse");
		elt.setAttribute("cx", "" + x);
		elt.setAttribute("cy", "" + y);
		elt.setAttribute("rx", "" + xRadius);
		elt.setAttribute("ry", "" + yRadius);
		elt.setAttribute("fill", fill);
		elt.setAttribute("style", getStyle());
		if (deltaShadow > 0) {
			elt.setAttribute("filter", "url(#f1)");
		}
		getG().appendChild(elt);
		ensureVisible(x + xRadius + deltaShadow * 2, y + yRadius + deltaShadow * 2);
	}

	private Map<List<String>, String> gradients = new HashMap<List<String>, String>();

	public String createSvgGradient(String color1, String color2) {
		final List<String> key = Arrays.asList(color1, color2);
		String id = gradients.get(key);
		if (id == null) {
			final Element elt = (Element) document.createElement("linearGradient");
			elt.setAttribute("x1", "0%");
			elt.setAttribute("y1", "0%");
			elt.setAttribute("x2", "100%");
			elt.setAttribute("y2", "100%");
			id = "gr" + gradients.size();
			gradients.put(key, id);
			elt.setAttribute("id", id);

			final Element stop1 = (Element) document.createElement("stop");
			stop1.setAttribute("stop-color", color1);
			stop1.setAttribute("offset", "0%");
			final Element stop2 = (Element) document.createElement("stop");
			stop2.setAttribute("stop-color", color2);
			stop2.setAttribute("offset", "100%");

			elt.appendChild(stop1);
			elt.appendChild(stop2);
			defs.appendChild(elt);
		}
		return id;
	}

	public final void setFillColor(String fill) {
		this.fill = fill == null ? "none" : fill;
	}

	public final void setStrokeColor(String stroke) {
		this.stroke = stroke;
	}

	public final void setStrokeWidth(String strokeWidth, String strokeDasharray) {
		this.strokeWidth = strokeWidth;
		this.strokeDasharray = strokeDasharray;
	}

	// private boolean xlinkXmlns = false;

	public void closeLink() {
		gRoot.appendChild(pendingLink);
		pendingLink = null;
	}

	private Element pendingLink;

	public void openLink(String url, String title) {
		if (url == null) {
			throw new IllegalArgumentException();
		}
		// if (xlinkXmlns == false) {
		// root.setAttribute("xmlns:xlink", "http://www.w3.org/1999/xlink");
		// xlinkXmlns = true;
		// }
		if (pendingLink != null) {
			throw new IllegalStateException();
		}
		pendingLink = (Element) document.createElement("a");
		pendingLink.setAttribute("xlink:href", url);
		if (title == null) {
			pendingLink.setAttribute("xlink:title", url);
		} else {
			pendingLink.setAttribute("xlink:title", title);
		}
	}
	
//	public void startHiddenGroup() {
//		if (pendingLink != null) {
//			throw new IllegalStateException();
//		}
//		pendingLink = (Element) document.createElement("g");
//		pendingLink.setAttribute("visibility", "hidden");
//		pendingLink.setAttribute("id", "toto3");
//	}
//
//	public void closeHiddenGroup() {
//		closeLink();
//	}


	public final Element getG() {
		if (pendingLink == null) {
			return gRoot;
		}
		return pendingLink;
	}

	public void svgRectangle(double x, double y, double width, double height, double rx, double ry, double deltaShadow) {
		manageShadow(deltaShadow);
		final Element elt = (Element) document.createElement("rect");
		elt.setAttribute("x", "" + x);
		elt.setAttribute("y", "" + y);
		elt.setAttribute("width", "" + width);
		elt.setAttribute("height", "" + height);
		elt.setAttribute("fill", fill);
		elt.setAttribute("style", getStyle());
		if (deltaShadow > 0) {
			elt.setAttribute("filter", "url(#f1)");
		}
		if (rx > 0 && ry > 0) {
			elt.setAttribute("rx", "" + rx);
			elt.setAttribute("ry", "" + ry);
		}

		getG().appendChild(elt);

		ensureVisible(x + width + 2 * deltaShadow, y + height + 2 * deltaShadow);
	}

	public void svgLine(double x1, double y1, double x2, double y2, double deltaShadow) {
		manageShadow(deltaShadow);
		final Element elt = (Element) document.createElement("line");
		elt.setAttribute("x1", "" + x1);
		elt.setAttribute("y1", "" + y1);
		elt.setAttribute("x2", "" + x2);
		elt.setAttribute("y2", "" + y2);
		elt.setAttribute("style", getStyle());
		if (deltaShadow > 0) {
			elt.setAttribute("filter", "url(#f1)");
		}
		getG().appendChild(elt);
		ensureVisible(x1 + 2 * deltaShadow, y1 + 2 * deltaShadow);
		ensureVisible(x2 + 2 * deltaShadow, y2 + 2 * deltaShadow);
	}

	private String getStyle() {
		return getStyle(strokeWidth);
	}

	private String getStyle(String strokeWidth) {
		final StringBuilder style = new StringBuilder("stroke: " + stroke + "; stroke-width: " + strokeWidth + ";");
		if (strokeDasharray != null) {
			style.append(" stroke-dasharray: " + strokeDasharray + ";");
		}
		return style.toString();
	}

	public void svgPolygon(double deltaShadow, double... points) {
		manageShadow(deltaShadow);
		final Element elt = (Element) document.createElement("polygon");
		final StringBuilder sb = new StringBuilder();
		for (double coord : points) {
			if (sb.length() > 0) {
				sb.append(",");
			}
			sb.append(coord);
		}
		elt.setAttribute("points", sb.toString());
		elt.setAttribute("fill", fill);
		elt.setAttribute("style", getStyle());
		if (deltaShadow > 0) {
			elt.setAttribute("filter", "url(#f1)");
		}
		getG().appendChild(elt);

		for (int i = 0; i < points.length; i += 2) {
			ensureVisible(points[i] + 2 * deltaShadow, points[i + 1] + 2 * deltaShadow);
		}

	}

	public void text(String text, double x, double y, String fontFamily, int fontSize, String fontWeight,
			String fontStyle, String textDecoration, double textLength) {
		final Element elt = (Element) document.createElement("text");
		elt.setAttribute("x", "" + x);
		elt.setAttribute("y", "" + y);
		elt.setAttribute("fill", fill);
		elt.setAttribute("font-size", "" + fontSize);
		// elt.setAttribute("text-anchor", "middle");
		elt.setAttribute("lengthAdjust", "spacingAndGlyphs");
		elt.setAttribute("textLength", "" + textLength);
		if (fontWeight != null) {
			elt.setAttribute("font-weight", fontWeight);
		}
		if (fontStyle != null) {
			elt.setAttribute("font-style", fontStyle);
		}
		if (textDecoration != null) {
			elt.setAttribute("text-decoration", textDecoration);
		}
		if (fontFamily != null) {
			elt.setAttribute("font-family", fontFamily);
		}
		elt.setTextContent(text);
		getG().appendChild(elt);
		ensureVisible(x, y);
		ensureVisible(x + textLength, y);
	}

	public final Element getDefs() {
		return defs;
	}

	private Transformer getTransformer() throws TransformerException {
		// Get a TransformerFactory object.
		final TransformerFactory xformFactory = TransformerFactory.newInstance();
		// try {
		// final Class<?> factoryClass = Class
		// .forName("com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
		// xformFactory = (TransformerFactory) factoryClass.newInstance();
		// } catch (Exception e) {
		// xformFactory = TransformerFactory.newInstance();
		// }
		Log.info("TransformerFactory=" + xformFactory.getClass());

		// Get an XSL Transformer object.
		final Transformer transformer = xformFactory.newTransformer();
		Log.info("Transformer=" + transformer.getClass());

		// // Sets the standalone property in the first line of
		// // the output file.
		transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
		//
		// Properties proprietes = new Properties();
		// proprietes.put("standalone", "yes");
		// transformer.setOutputProperties(proprietes);
		//
		// transformer.setParameter(OutputKeys.STANDALONE, "yes");

		return transformer;
	}

	public void createXml(OutputStream os) throws TransformerException {

		// // Add lines
		// for (Line l : lines) {
		// l.drawNow();
		// }

		// Get a DOMSource object that represents the
		// Document object
		final DOMSource source = new DOMSource(document);

		String style = "width:" + maxX + "px;height:" + maxY + "px;";
		if (backcolor != null) {
			style += "background:" + backcolor + ";";
		}
		root.setAttribute("style", style);
		root.setAttribute("width", "" + maxX + "pt");
		root.setAttribute("height", "" + maxY + "pt");
		root.setAttribute("viewBox", "0 0 " + maxX + " " + maxY);

		// Get a StreamResult object that points to the
		// screen. Then transform the DOM sending XML to
		// the screen.
		final StreamResult scrResult = new StreamResult(os);
		getTransformer().transform(source, scrResult);
	}

	// String getGElement() throws TransformerException, IOException {
	// final ByteArrayOutputStream os = new ByteArrayOutputStream();
	// final StreamResult sr = new StreamResult(os);
	// getTransformer().transform(new DOMSource(gRoot), sr);
	// os.close();
	// final String s = new String(os.toByteArray(), "UTF-8");
	// return s.replaceFirst("^\\<\\?xml.*?\\?\\>", "");
	// }

	public void svgPath(double x, double y, UPath path, double deltaShadow) {
		manageShadow(deltaShadow);
		final StringBuilder sb = new StringBuilder();
		ensureVisible(x, y);
		for (USegment seg : path) {
			final USegmentType type = seg.getSegmentType();
			final double coord[] = seg.getCoord();
			if (type == USegmentType.SEG_MOVETO) {
				sb.append("M" + format(coord[0] + x) + "," + format(coord[1] + y) + " ");
				ensureVisible(coord[0] + x + 2 * deltaShadow, coord[1] + y + 2 * deltaShadow);
			} else if (type == USegmentType.SEG_LINETO) {
				sb.append("L" + format(coord[0] + x) + "," + format(coord[1] + y) + " ");
				ensureVisible(coord[0] + x + 2 * deltaShadow, coord[1] + y + 2 * deltaShadow);
			} else if (type == USegmentType.SEG_QUADTO) {
				sb.append("Q" + format(coord[0] + x) + "," + format(coord[1] + y) + " " + format(coord[2] + x) + ","
						+ format(coord[3] + y) + " ");
				ensureVisible(coord[0] + x + 2 * deltaShadow, coord[1] + y + 2 * deltaShadow);
				ensureVisible(coord[2] + x + 2 * deltaShadow, coord[3] + y + 2 * deltaShadow);
			} else if (type == USegmentType.SEG_CUBICTO) {
				sb.append("C" + format(coord[0] + x) + "," + format(coord[1] + y) + " " + format(coord[2] + x) + ","
						+ format(coord[3] + y) + " " + format(coord[4] + x) + "," + format(coord[5] + y) + " ");
				ensureVisible(coord[0] + x + 2 * deltaShadow, coord[1] + y + 2 * deltaShadow);
				ensureVisible(coord[2] + x + 2 * deltaShadow, coord[3] + y + 2 * deltaShadow);
				ensureVisible(coord[4] + x + 2 * deltaShadow, coord[5] + y + 2 * deltaShadow);
			} else if (type == USegmentType.SEG_CLOSE) {
				// Nothing
			} else {
				System.err.println("unknown " + seg);
			}

		}
		final Element elt = (Element) document.createElement("path");
		elt.setAttribute("d", sb.toString());
		elt.setAttribute("style", getStyle());
		elt.setAttribute("fill", fill);
		if (deltaShadow > 0) {
			elt.setAttribute("filter", "url(#f1)");
		}
		getG().appendChild(elt);
	}

	private StringBuilder currentPath = null;

	public void newpath() {
		currentPath = new StringBuilder();

	}

	public void moveto(double x, double y) {
		currentPath.append("M" + format(x) + "," + format(y) + " ");
		ensureVisible(x, y);
	}

	public void lineto(double x, double y) {
		currentPath.append("L" + format(x) + "," + format(y) + " ");
		ensureVisible(x, y);
	}

	public void closepath() {
		currentPath.append("Z ");

	}

	public void curveto(double x1, double y1, double x2, double y2, double x3, double y3) {
		currentPath.append("C" + format(x1) + "," + format(y1) + " " + format(x2) + "," + format(y2) + " " + format(x3)
				+ "," + format(y3) + " ");
		ensureVisible(x1, y1);
		ensureVisible(x2, y2);
		ensureVisible(x3, y3);

	}

	public void quadto(double x1, double y1, double x2, double y2) {
		currentPath.append("Q" + format(x1) + "," + format(y1) + " " + format(x2) + "," + format(y2) + " ");
		ensureVisible(x1, y1);
		ensureVisible(x2, y2);
	}

	private static String format(double x) {
		return EpsGraphics.format(x);
	}

	public void fill(int windingRule) {
		final Element elt = (Element) document.createElement("path");
		elt.setAttribute("d", currentPath.toString());
		// elt elt.setAttribute("style", getStyle());
		getG().appendChild(elt);
		currentPath = null;

	}

	public void svgImage(BufferedImage image, double x, double y) throws IOException {
		final Element elt = (Element) document.createElement("image");
		elt.setAttribute("width", "" + image.getWidth());
		elt.setAttribute("height", "" + image.getHeight());
		elt.setAttribute("x", "" + x);
		elt.setAttribute("y", "" + y);
		final String s = toBase64(image);
		elt.setAttribute("xlink:href", "data:image/png;base64," + s);
		getG().appendChild(elt);
		ensureVisible(x, y);
		ensureVisible(x + image.getWidth(), y + image.getHeight());

	}

	private String toBase64(BufferedImage image) throws IOException {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, "png", baos);
		final byte data[] = baos.toByteArray();
		return new String(Base64Coder.encode(data));
	}

	// Shadow

	private boolean withShadow = false;

	private void manageShadow(double deltaShadow) {
		if (deltaShadow != 0) {
			if (withShadow == false) {
				// <filter id="f1" x="0" y="0" width="120%" height="120%">
				final Element filter = (Element) document.createElement("filter");
				filter.setAttribute("id", "f1");
				filter.setAttribute("x", "-1");
				filter.setAttribute("y", "-1");
				filter.setAttribute("width", "300%");
				filter.setAttribute("height", "300%");
				addFilter(filter, "feGaussianBlur", "result", "blurOut", "stdDeviation", "2");
				addFilter(filter, "feColorMatrix", "type", "matrix", "in", "blurOut", "result", "blurOut2", "values",
						"0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 .4 0");
				addFilter(filter, "feOffset", "result", "blurOut3", "in", "blurOut2", "dx", "4", "dy", "4");
				addFilter(filter, "feBlend", "in", "SourceGraphic", "in2", "blurOut3", "mode", "normal");
				defs.appendChild(filter);

			}
			withShadow = true;
		}
	}

	private void addFilter(Element filter, String name, String... data) {
		final Element elt = (Element) document.createElement(name);
		for (int i = 0; i < data.length; i += 2) {
			elt.setAttribute(data[i], data[i + 1]);
		}
		filter.appendChild(elt);
	}

}
