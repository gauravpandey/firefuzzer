package org.Firefuzzer.Fire;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * @author Gaurav Pandey (gip2103) and Sumit Jindal
 *
 */

/**
 * Main Class of the Project
 * */
public class Firefuzzer {
	private URL fURL;

	/**
	 * Constructor checks the URL and ascertains whether it is HTTP or not
	 * */
	public Firefuzzer(URL aURL) {
		if (!"http".equals(aURL.getProtocol())) {
			throw new IllegalArgumentException("URL is not for HTTP Protocol"
					+ ": " + aURL);
		}
		fURL = aURL;
	}

	/**
	 * Constructor checks whether the URL is malformed or not
	 * */
	public Firefuzzer(String aUrlName) throws MalformedURLException {
		this(new URL(aUrlName));
	}

	/**
	 * Fetches the HTML source file from a given URL
	 * */
	public String getPageContent() throws IOException {
		String result = null;
		URLConnection connection = null;
		try {
			connection = fURL.openConnection();
			Scanner scanner = new Scanner(connection.getInputStream());
			scanner.useDelimiter("\\Z");
			result = scanner.next();
		} catch (IOException ex) {
			log("Cannot open connection to " + fURL.toString());
		}
		return result;
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
	public static void main(String[] args) throws IOException,
			InterruptedException {

		System.out.println("");
		System.out
				.println("\t8888888888 8888888 8888888b.  8888888888 8888888888 888     888 8888888888P 8888888888P 8888888888 8888888b.");
		System.out
				.println("\t888          888   888   Y88b 888        888        888     888       d88P        d88P  888        888   Y88b ");
		System.out
				.println("\t888          888   888    888 888        888        888     888      d88P        d88P   888        888    888 ");
		System.out
				.println("\t8888888      888   888   d88P 8888888    8888888    888     888     d88P        d88P    8888888    888   d88P ");
		System.out
				.println("\t888          888   8888888P\"  888        888        888     888    d88P        d88P     888        8888888P\" ");
		System.out
				.println("\t888          888   888 T88b   888        888        888     888   d88P        d88P      888        888 T88b ");
		System.out
				.println("\t888          888   888  T88b  888        888        Y88b. .d88P  d88P        d88P       888        888  T88b");
		System.out
				.println("\t888        8888888 888   T88b 8888888888 888         \"Y88888P\"  d8888888888 d8888888888 8888888888 888   T88b ");
		System.out.println("");
		Thread.sleep(1000);
		String url = null;

		if (args.length == 2 || args.length == 3) {
			url = args[0];
		} else {
			System.err.println("Incorrect number of parameters");
			System.err
					.println("Syntax is java Firefuzzer <url> <buffer/sql> <detail(OPTIONAL)>");
			System.exit(0);
		}
		if (args[1].equalsIgnoreCase("buffer")
				|| args[1].equalsIgnoreCase("sql")) {
		} else {
			System.err.println("Incorrect parameters entered");
			System.err
					.println("Syntax is java Firefuzzer <url> <buffer/sql> <detail(OPTIONAL)>");
			System.exit(0);
		}
		if (args.length == 3) {
			if (args[2].equalsIgnoreCase("detail")) {
			} else {
				System.err.println("Incorrect parameters entered");
				System.err
						.println("Syntax is java Firefuzzer <url> <buffer/sql> <detail(OPTIONAL)>");
				System.exit(0);
			}
		}
		if (url.contains("http://")) {
		} else if (url.startsWith("www."))
			url = "http://" + url;

		else {
			System.out
					.println("Only http protocol based sites are handled. Wait for future release.");
			System.exit(0);
		}
		new File("page.loaded").deleteOnExit();
		new File("temp.html").deleteOnExit();
		System.out
				.println("########################################################################################################################");
		System.out.println("Targeted URL: " + url);
		System.out
				.println("########################################################################################################################");
		Firefuzzer fetcher = new Firefuzzer(url);
		log(fetcher.getPageContent());
		if (args[1].equalsIgnoreCase("buffer")) {
			if (args.length == 3)
				if (args[2].equalsIgnoreCase("detail"))
					BufferOverflow.globalDetailFlag = true;
			BufferOverflow.globalURL = url;
			System.out.println("<---BUFFER OVERFLOW ATTACK--->");
			System.out
					.println("########################################################################################################################");
			BufferOverflow.parseInput();
			BufferOverflow.analyzeBufferOverflow();
		} else if (args[1].equalsIgnoreCase("sql")) {
			if (args.length == 3)
				if (args[2].equalsIgnoreCase("detail"))
					SQLInjection.globalDetailFlag = true;
			SQLInjection.globalURL = url;
			System.out.println("<---SQL INJECTION ATTACK--->");
			System.out
					.println("########################################################################################################################");
			SQLInjection.parseInput();
			SQLInjection.analyzeSQLInjection();
		}
	}
}
