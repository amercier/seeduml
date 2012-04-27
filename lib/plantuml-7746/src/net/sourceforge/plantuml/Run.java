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

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.imageio.ImageIO;
import javax.swing.UIManager;

import net.sourceforge.plantuml.activitydiagram.ActivityDiagramFactory;
import net.sourceforge.plantuml.classdiagram.ClassDiagramFactory;
import net.sourceforge.plantuml.code.Transcoder;
import net.sourceforge.plantuml.code.TranscoderUtil;
import net.sourceforge.plantuml.command.AbstractUmlSystemCommandFactory;
import net.sourceforge.plantuml.componentdiagram.ComponentDiagramFactory;
import net.sourceforge.plantuml.ftp.FtpServer;
import net.sourceforge.plantuml.objectdiagram.ObjectDiagramFactory;
import net.sourceforge.plantuml.png.MetadataTag;
import net.sourceforge.plantuml.preproc.Defines;
import net.sourceforge.plantuml.sequencediagram.SequenceDiagramFactory;
import net.sourceforge.plantuml.statediagram.StateDiagramFactory;
import net.sourceforge.plantuml.swing.MainWindow2;
import net.sourceforge.plantuml.ugraphic.SpriteGrayLevel;
import net.sourceforge.plantuml.ugraphic.SpriteUtils;
import net.sourceforge.plantuml.usecasediagram.UsecaseDiagramFactory;

public class Run {

	public static void main(String[] argsArray) throws IOException, InterruptedException {
		final long start = System.currentTimeMillis();
		final Option option = new Option(argsArray);
		if (OptionFlags.getInstance().isEncodesprite()) {
			encodeSprite(option.getResult());
			return;
		}
		if (OptionFlags.getInstance().isVerbose()) {
			Log.info("GraphicsEnvironment.isHeadless() " + GraphicsEnvironment.isHeadless());
		}
		if (OptionFlags.getInstance().isPrintFonts()) {
			printFonts();
			return;
		}

		if (option.getFtpPort() != -1) {
			goFtp(option);
			return;
		}

		boolean error = false;
		boolean forceQuit = false;
		if (option.isPattern()) {
			managePattern();
		} else if (OptionFlags.getInstance().isGui()) {
			try {
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			} catch (Exception e) {
			}
			new MainWindow2(option);
		} else if (option.isPipe() || option.isSyntax()) {
			managePipe(option);
			forceQuit = true;
		} else {
			error = manageAllFiles(option);
			forceQuit = true;
		}

		if (option.isDuration()) {
			final long duration = System.currentTimeMillis() - start;
			Log.error("Duration = " + (duration / 1000L) + " seconds");
		}

		if (error) {
			Log.error("Some diagram description contains errors");
			System.exit(1);
		}

		if (forceQuit && OptionFlags.getInstance().isSystemExit()) {
			System.exit(0);
		}
	}

	private static void encodeSprite(List<String> result) throws IOException {
		SpriteGrayLevel level = SpriteGrayLevel.GRAY_16;
		boolean compressed = false;
		final File f;
		if (result.size() > 1 && result.get(0).matches("(4|8|16)z?")) {
			if (result.get(0).startsWith("8")) {
				level = SpriteGrayLevel.GRAY_8;
			}
			if (result.get(0).startsWith("4")) {
				level = SpriteGrayLevel.GRAY_4;
			}
			compressed = result.get(0).toLowerCase().endsWith("z");
			f = new File(result.get(1));
		} else {
			f = new File(result.get(0));
		}
		final BufferedImage im = ImageIO.read(f);
		String name = getSpriteName(f);
		final String s = compressed ? SpriteUtils.encodeCompressed(im, name, level) : SpriteUtils.encode(im, name,
				level);
		System.out.println(s);
	}

	private static String getSpriteName(File f) {
		final String s = getSpriteNameInternal(f);
		if (s.length() == 0) {
			return "test";
		}
		return s;
	}

	private static String getSpriteNameInternal(File f) {
		final StringBuilder sb = new StringBuilder();
		for (char c : f.getName().toCharArray()) {
			if (("" + c).matches("[\\p{L}0-9_]")) {
				sb.append(c);
			} else {
				return sb.toString();
			}
		}
		return sb.toString();
	}

	private static void goFtp(Option option) throws IOException {
		final int ftpPort = option.getFtpPort();
		System.err.println("ftpPort=" + ftpPort);
		final FtpServer ftpServer = new FtpServer(ftpPort);
		ftpServer.go();
	}

	static void printFonts() {
		final Font fonts[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
		for (Font f : fonts) {
			System.out.println("f=" + f + "/" + f.getPSName() + "/" + f.getName() + "/" + f.getFontName() + "/"
					+ f.getFamily());
		}
		final String name[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		for (String n : name) {
			System.out.println("n=" + n);
		}

	}

	private static void managePattern() {
		printPattern(new SequenceDiagramFactory());
		printPattern(new ClassDiagramFactory());
		printPattern(new ActivityDiagramFactory());
		printPattern(new UsecaseDiagramFactory());
		printPattern(new ComponentDiagramFactory());
		printPattern(new StateDiagramFactory());
		printPattern(new ObjectDiagramFactory());
	}

	private static void printPattern(AbstractUmlSystemCommandFactory factory) {
		factory.init(null);
		System.out.println();
		System.out.println(factory.getClass().getSimpleName().replaceAll("Factory", ""));
		for (String s : factory.getDescription()) {
			System.out.println(s);
		}
	}

	private static void managePipe(Option option) throws IOException {
		final String charset = option.getCharset();
		final BufferedReader br;
		if (charset == null) {
			br = new BufferedReader(new InputStreamReader(System.in));
		} else {
			br = new BufferedReader(new InputStreamReader(System.in, charset));
		}
		managePipe(option, br, System.out);
	}

	static void managePipe(Option option, final BufferedReader br, final PrintStream ps) throws IOException {
		final StringBuilder sb = new StringBuilder();
		String s = null;
		while ((s = br.readLine()) != null) {
			sb.append(s);
			sb.append("\n");
		}
		String source = sb.toString();
		if (source.contains("@startuml") == false) {
			source = "@startuml\n" + source + "\n@enduml";
		}
		final SourceStringReader sourceStringReader = new SourceStringReader(new Defines(), source, option.getConfig());

		if (option.isSyntax()) {
			try {
				final PSystem system = sourceStringReader.getBlocks().get(0).getSystem();
				if (system instanceof UmlDiagram) {
					ps.println(((UmlDiagram) system).getUmlDiagramType().name());
					ps.println(system.getDescription());
				} else if (system instanceof PSystemError) {
					ps.println("ERROR");
					final PSystemError sys = (PSystemError) system;
					ps.println(sys.getHigherErrorPosition());
					for (ErrorUml er : sys.getErrorsUml()) {
						ps.println(er.getError());
					}
				} else {
					ps.println("OTHER");
					ps.println(system.getDescription());
				}
			} catch (InterruptedException e) {
				Log.error("InterruptedException " + e);
			}
		} else if (option.isPipe()) {
			final String result = sourceStringReader.generateImage(ps, 0, option.getFileFormatOption());
		}
	}

	private static boolean manageAllFiles(Option option) throws IOException, InterruptedException {

		File lockFile = null;
		try {
			if (OptionFlags.getInstance().isWord()) {
				final File dir = new File(option.getResult().get(0));
				final File javaIsRunningFile = new File(dir, "javaisrunning.tmp");
				javaIsRunningFile.delete();
				lockFile = new File(dir, "javaumllock.tmp");
			}
			return processArgs(option);
		} finally {
			if (lockFile != null) {
				lockFile.delete();
			}
		}

	}

	private static boolean processArgs(Option option) throws IOException, InterruptedException {
		if (option.isDecodeurl() == false && option.getNbThreads() > 0 && option.isCheckOnly() == false
				&& OptionFlags.getInstance().isMetadata() == false) {
			return multithread(option);
		}
		for (String s : option.getResult()) {
			if (option.isDecodeurl()) {
				final Transcoder transcoder = TranscoderUtil.getDefaultTranscoder();
				System.out.println("@startuml");
				System.out.println(transcoder.decode(s));
				System.out.println("@enduml");
			} else {
				final FileGroup group = new FileGroup(s, option.getExcludes(), option);
				for (File f : group.getFiles()) {
					try {
						final boolean error = manageFileInternal(f, option);
						if (error) {
							return true;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return false;
	}

	private static boolean multithread(final Option option) throws InterruptedException {
		Log.info("Using several threads: " + option.getNbThreads());
		final ExecutorService executor = Executors.newFixedThreadPool(option.getNbThreads());
		final AtomicBoolean errors = new AtomicBoolean(false);
		for (String s : option.getResult()) {
			final FileGroup group = new FileGroup(s, option.getExcludes(), option);
			for (final File f : group.getFiles()) {
				final Future<?> future = executor.submit(new Runnable() {
					public void run() {
						if (errors.get()) {
							return;
						}
						try {
							final boolean error = manageFileInternal(f, option);
							if (error) {
								errors.set(true);
							}
						} catch (IOException e) {
							e.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				});
			}
		}
		executor.shutdown();
		executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
		return errors.get();
	}

	private static boolean manageFileInternal(File f, Option option) throws IOException, InterruptedException {
		if (OptionFlags.getInstance().isMetadata()) {
			System.out.println("------------------------");
			System.out.println(f);
			// new Metadata().readAndDisplayMetadata(f);
			System.out.println();
			System.out.println(new MetadataTag(f, "plantuml").getData());
			System.out.println("------------------------");
			return false;
		}
		final ISourceFileReader sourceFileReader;
		if (option.getOutputFile() == null) {
			sourceFileReader = new SourceFileReader(option.getDefaultDefines(), f, option.getOutputDir(),
					option.getConfig(), option.getCharset(), option.getFileFormatOption());
		} else {
			sourceFileReader = new SourceFileReader2(option.getDefaultDefines(), f, option.getOutputFile(),
					option.getConfig(), option.getCharset(), option.getFileFormatOption());
		}
		if (option.isComputeurl()) {
			final List<String> urls = sourceFileReader.getEncodedUrl();
			for (String s : urls) {
				System.out.println(s);
			}
			return false;
		} else if (option.isCheckOnly()) {
			return sourceFileReader.hasError();
		}
		final List<GeneratedImage> result = sourceFileReader.getGeneratedImages();
		if (OptionFlags.getInstance().isFailOnError()) {
			for (GeneratedImage i : result) {
				if (i.isError()) {
					Log.error("Error in file: " + f.getCanonicalPath());
				}
				if (i.isError() && OptionFlags.getInstance().isFailOnError()) {
					return true;
				}
			}
		}
		return false;
	}

}
