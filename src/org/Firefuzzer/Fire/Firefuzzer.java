package org.Firefuzzer.Fire;

import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * @author Gaurav Pandey
 * 
 * Main FireFuzzer class
 * */
public class Firefuzzer {
	private static final Logger logger = Logger.getLogger(Firefuzzer.class);

	private final URL url;

	public Firefuzzer(final URL url) {
		if (!"http".equals(url.getProtocol())) {
			throw new IllegalArgumentException(
				String.format(
					"Only http protocol is supported at the moment: %s", url));
		}
		this.url = url;
	}

	public Firefuzzer(final String url) throws MalformedURLException {
		this(new URL(url));
	}

	/**
	 * Fetches HTML source file from a given URL
	 * */
	public String getPageContent() throws IOException {
		Scanner scanner = null;

		try {
			final URLConnection connection = url.openConnection();
			scanner = new Scanner(connection.getInputStream());
			scanner.useDelimiter("\\Z");
			return scanner.next();
		} catch (final IOException ex) {
			logger.info("Cannot open connection to: " + url.toString());
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
		return null;
	}

	/**
	 * Logs the incoming HTML source infomation into a file
	 * */
	private static void log(Object aObject) {
		try {
			FileWriter fww = new FileWriter("page.loaded");
			fww.close();
			FileWriter fw = new FileWriter("page.loaded", true);
			fw.append(aObject.toString());
			fw.close();
		} catch (Exception e) {
			System.err.println("Exception occured: " + e.getMessage());
		}
	}

	/**
	 * Main function of the Project to start either Buffer Overflow or SQL
	 * Injection Attack on the target URL
	 * */
	public static void main(final String[] args) throws IOException,
														InterruptedException {
		logger.info("");
		logger.info("\t8888888888 8888888 8888888b.  8888888888 8888888888 888     888 8888888888P 8888888888P 8888888888 8888888b.");
		logger.info("\t888          888   888   Y88b 888        888        888     888       d88P        d88P  888        888   Y88b ");
		logger.info("\t888          888   888    888 888        888        888     888      d88P        d88P   888        888    888 ");
		logger.info("\t8888888      888   888   d88P 8888888    8888888    888     888     d88P        d88P    8888888    888   d88P ");
		logger.info("\t888          888   8888888P\"  888        888        888     888    d88P        d88P     888        8888888P\" ");
		logger.info("\t888          888   888 T88b   888        888        888     888   d88P        d88P      888        888 T88b ");
		logger.info("\t888          888   888  T88b  888        888        Y88b. .d88P  d88P        d88P       888        888  T88b");
		logger.info("\t888        8888888 888   T88b 8888888888 888         \"Y88888P\"  d8888888888 d8888888888 8888888888 888   T88b ");
		logger.info("");
		Thread.sleep(1000);
		String url = null;

		if (args.length == 2 || args.length == 3) {
			url = args[0].trim();
		} else {
			logger.error("Incorrect number of parameters");
			logger.error("Syntax is java Firefuzzer <url> <buffer/sql> <detail(OPTIONAL)>");
			System.exit(0);
		}

		if (!args[1].equalsIgnoreCase("buffer")
				&& !args[1].equalsIgnoreCase("sql")) {
			logger.error("Incorrect parameters entered");
			logger.error("Syntax is java Firefuzzer <url> <buffer/sql> <detail(OPTIONAL)>");
			System.exit(0);
		}

		if (args.length == 3) {
			if (!args[2].equalsIgnoreCase("detail")) {
				logger.error("Incorrect parameters entered");
				logger.error("Syntax is java Firefuzzer <url> <buffer/sql> <detail(OPTIONAL)>");
				System.exit(0);
			}
		}

		if (url.startsWith("www.")) {
			url = "http://" + url;
		}

		if (!url.startsWith("http://")) {
			logger.error("Only http protocol based sites are handled. Wait for future release.");
			System.exit(0);
		}

		new File("page.loaded").deleteOnExit();
		new File("temp.html").deleteOnExit();
		logger.info("########################################################################################################################");
		logger.info("Targeted URL: " + url);
		logger.info("########################################################################################################################");
		Firefuzzer fetcher = new Firefuzzer(url);
		log(fetcher.getPageContent());
		if (args[1].equalsIgnoreCase("buffer")) {
			if (args.length == 3)
				if (args[2].equalsIgnoreCase("detail"))
					BufferOverflow.globalDetailFlag = true;
			BufferOverflow.globalURL = url;
			logger.info("<---BUFFER OVERFLOW ATTACK--->");
			logger.info("########################################################################################################################");
			BufferOverflow.parseInput();
			BufferOverflow.analyzeBufferOverflow();
		} else if (args[1].equalsIgnoreCase("sql")) {
			if (args.length == 3)
				if (args[2].equalsIgnoreCase("detail"))
					SQLInjection.globalDetailFlag = true;
			SQLInjection.globalURL = url;
			logger.info("<---SQL INJECTION ATTACK--->");
			logger.info("########################################################################################################################");
			SQLInjection.parseInput();
			SQLInjection.analyzeSQLInjection();
		}
	}
}
