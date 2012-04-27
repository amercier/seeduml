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
package net.sourceforge.plantuml;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import net.sourceforge.plantuml.code.Compression;
import net.sourceforge.plantuml.code.CompressionZlib;
import net.sourceforge.plantuml.graphic.HorizontalAlignement;
import net.sourceforge.plantuml.mjpeg.MJPEGGenerator;
import net.sourceforge.plantuml.pdf.PdfConverter;
import net.sourceforge.plantuml.ugraphic.Sprite;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public abstract class UmlDiagram extends AbstractPSystem implements PSystem {

	private boolean rotation;
	private boolean hideUnlinkedData;

	private int minwidth = Integer.MAX_VALUE;

	private List<? extends CharSequence> title;
	private List<String> header;
	private List<String> footer;
	private HorizontalAlignement headerAlignement = HorizontalAlignement.RIGHT;
	private HorizontalAlignement footerAlignement = HorizontalAlignement.CENTER;
	private final Pragma pragma = new Pragma();
	private Scale scale;

	private final SkinParam skinParam = new SkinParam(getUmlDiagramType());

	final public void setTitle(List<? extends CharSequence> strings) {
		this.title = strings;
	}

	final public List<? extends CharSequence> getTitle() {
		return title;
	}

	final public int getMinwidth() {
		return minwidth;
	}

	final public void setMinwidth(int minwidth) {
		this.minwidth = minwidth;
	}

	final public boolean isRotation() {
		return rotation;
	}

	final public void setRotation(boolean rotation) {
		this.rotation = rotation;
	}

	public final ISkinParam getSkinParam() {
		return skinParam;
	}

	public void setParam(String key, String value) {
		skinParam.setParam(key.toLowerCase(), value);
	}

	public final List<String> getHeader() {
		return header;
	}

	public final void setHeader(List<String> header) {
		this.header = header;
	}

	public final List<String> getFooter() {
		return footer;
	}

	public final void setFooter(List<String> footer) {
		this.footer = footer;
	}

	public final HorizontalAlignement getHeaderAlignement() {
		return headerAlignement;
	}

	public final void setHeaderAlignement(HorizontalAlignement headerAlignement) {
		this.headerAlignement = headerAlignement;
	}

	public final HorizontalAlignement getFooterAlignement() {
		return footerAlignement;
	}

	public final void setFooterAlignement(HorizontalAlignement footerAlignement) {
		this.footerAlignement = footerAlignement;
	}

	abstract public UmlDiagramType getUmlDiagramType();

	public Pragma getPragma() {
		return pragma;
	}

	final public void setScale(Scale scale) {
		this.scale = scale;
	}

	final public Scale getScale() {
		return scale;
	}

	public final double getDpiFactor(FileFormatOption fileFormatOption) {
		if (getSkinParam().getDpi() == 96) {
			return 1.0;
		}
		return getSkinParam().getDpi() / 96.0;
	}

	public final int getDpi(FileFormatOption fileFormatOption) {
		return getSkinParam().getDpi();
	}

	public final boolean isHideUnlinkedData() {
		return hideUnlinkedData;
	}

	public final void setHideUnlinkedData(boolean hideUnlinkedData) {
		this.hideUnlinkedData = hideUnlinkedData;
	}

	final public void exportDiagram(OutputStream os, StringBuilder cmap, int index, FileFormatOption fileFormatOption)
			throws IOException {
		List<BufferedImage> flashcodes = null;
		try {
			if ("split".equalsIgnoreCase(getSkinParam().getValue("flashcode"))
					&& fileFormatOption.getFileFormat() == FileFormat.PNG) {
				final String s = getSource().getPlainString();
				flashcodes = exportSplitCompress(s);
			} else if ("compress".equalsIgnoreCase(getSkinParam().getValue("flashcode"))
					&& fileFormatOption.getFileFormat() == FileFormat.PNG) {
				final String s = getSource().getPlainString();
				flashcodes = exportFlashcodeCompress(s);
			} else if (getSkinParam().getValue("flashcode") != null
					&& fileFormatOption.getFileFormat() == FileFormat.PNG) {
				final String s = getSource().getPlainString();
				flashcodes = exportFlashcodeSimple(s);
			}
		} catch (WriterException e) {
			Log.error("Cannot generate flashcode");
			e.printStackTrace();
			flashcodes = null;
		}
		if (fileFormatOption.getFileFormat() == FileFormat.PDF) {
			exportDiagramInternalPdf(os, cmap, index, flashcodes);
			return;
		}
		if (fileFormatOption.getFileFormat() == FileFormat.MJPEG) {
			// exportDiagramInternalMjpeg(os);
			// return;*
			throw new UnsupportedOperationException();
		}
		lastInfo = exportDiagramInternal(os, cmap, index, fileFormatOption, flashcodes);
	}

	private void exportDiagramInternalMjpeg(OutputStream os) throws IOException {
		final File f = new File("c:/test.avi");
		int nb = 150;
		double framerate = 30;
		final MJPEGGenerator m = new MJPEGGenerator(f, 640, 480, framerate, nb);

		for (int i = 0; i < nb; i++) {
			final AffineTransform at = new AffineTransform();
			final double coef = (nb - 1 - i) * 1.0 / nb;
			at.setToShear(coef, coef);
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			exportDiagram(baos, null, 0, new FileFormatOption(FileFormat.PNG, at));
			baos.close();
			final BufferedImage im = ImageIO.read(new ByteArrayInputStream(baos.toByteArray()));
			m.addImage(im);
		}
		m.finishAVI();

	}

	private UmlDiagramInfo lastInfo;

	private void exportDiagramInternalPdf(OutputStream os, StringBuilder cmap, int index, List<BufferedImage> flashcodes)
			throws IOException {
		final File svg = FileUtils.createTempFile("pdf", ".svf");
		final File pdfFile = FileUtils.createTempFile("pdf", ".pdf");
		final OutputStream fos = new BufferedOutputStream(new FileOutputStream(svg));
		exportDiagram(fos, cmap, index, new FileFormatOption(FileFormat.SVG));
		fos.close();
		PdfConverter.convert(svg, pdfFile);
		FileUtils.copyToStream(pdfFile, os);
	}

	protected abstract UmlDiagramInfo exportDiagramInternal(OutputStream os, StringBuilder cmap, int index,
			FileFormatOption fileFormatOption, List<BufferedImage> flashcodes) throws IOException;

	final protected void exportCmap(File suggestedFile, final StringBuilder cmap) throws FileNotFoundException {
		final File cmapFile = new File(changeName(suggestedFile.getAbsolutePath()));
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(cmapFile);
			pw.print(cmap.toString());
			pw.close();
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}

	static String changeName(String name) {
		return name.replaceAll("(?i)\\.\\w{3}$", ".cmapx");
	}

	private List<BufferedImage> exportFlashcodeSimple(String s) throws IOException, WriterException {
		final QRCodeWriter writer = new QRCodeWriter();
		final int multiple = 1;
		final Hashtable hints = new Hashtable();
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		final BitMatrix bit = writer.encode(s, BarcodeFormat.QR_CODE, multiple);
		final BufferedImage im = MatrixToImageWriter.toBufferedImage(bit);
		return Arrays.asList(im);
	}

	private List<BufferedImage> exportFlashcodeCompress(String s) throws IOException, WriterException {
		final QRCodeWriter writer = new QRCodeWriter();
		final int multiple = 1;
		final Hashtable hints = new Hashtable();
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

		final Compression comp = new CompressionZlib();
		final byte data[] = comp.compress(s.getBytes("UTF-8"));

		// Encoder.DEFAULT_BYTE_MODE_ENCODING
		final BitMatrix bit = writer.encode(new String(data, "ISO-8859-1"), BarcodeFormat.QR_CODE, multiple);
		final BufferedImage im = MatrixToImageWriter.toBufferedImage(bit);
		return Arrays.asList(im);
	}

	private List<BufferedImage> exportSplitCompress(String s) throws IOException, WriterException {
		final QRCodeWriter writer = new QRCodeWriter();
		final int multiple = 1;
		final Hashtable hints = new Hashtable();
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

		final Compression comp = new CompressionZlib();
		final byte data[] = comp.compress(s.getBytes("UTF-8"));

		final List<BufferedImage> result = new ArrayList<BufferedImage>();

		final List<byte[]> blocs = new ArrayList<byte[]>();
		for (int i = 0; i < 4; i++) {
			blocs.add(getSplited(data, i, 4));
		}

		blocs.add(xor(blocs));

		for (byte d[] : blocs) {
			// Encoder.DEFAULT_BYTE_MODE_ENCODING
			final BitMatrix bit = writer.encode(new String(d, "ISO-8859-1"), BarcodeFormat.QR_CODE, multiple);
			result.add(MatrixToImageWriter.toBufferedImage(bit));
		}

		return Collections.unmodifiableList(result);
	}

	static byte[] xor(List<byte[]> blocs) {
		final byte result[] = new byte[blocs.get(0).length];
		for (int i = 0; i < result.length; i++) {
			result[i] = xor(blocs, i);
		}
		return result;
	}

	static byte xor(List<byte[]> blocs, int nb) {
		byte result = 0;
		for (byte[] bloc : blocs) {
			result = (byte) (result ^ bloc[nb]);
		}
		return result;
	}

	static byte[] getSplited(byte[] data, int n, int total) {
		final int size = (data.length + total - 1) / total;
		assert size * total >= data.length;
		final byte result[] = new byte[size + 1];
		result[0] = (byte) (1 << n);
		for (int i = 0; (i < size) && (n * total + i < data.length); i++) {
			result[i + 1] = data[n * total + i];
		}
		return result;
	}

	@Override
	public String getWarningOrError() {
		if (lastInfo == null) {
			return null;
		}
		final double actualWidth = lastInfo.getWidth();
		if (actualWidth == 0) {
			return null;
		}
		final String value = getSkinParam().getValue("widthwarning");
		if (value == null) {
			return null;
		}
		if (value.matches("\\d+") == false) {
			return null;
		}
		final int widthwarning = Integer.parseInt(value);
		if (actualWidth > widthwarning) {
			return "The image is " + ((int) actualWidth) + " pixel width. (Warning limit is " + widthwarning + ")";
		}
		return null;
	}

//	public final Map<String, Sprite> getSprites() {
//		return Collections.unmodifiableMap(sprites);
//	}
//	private final Map<String, Sprite> sprites = new HashMap<String, Sprite>();


	public void addSprite(String name, Sprite sprite) {
		skinParam.addSprite(name, sprite);
	}
}
