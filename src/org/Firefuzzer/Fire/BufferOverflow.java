package org.Firefuzzer.Fire;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.htmlparser.jericho.Attributes;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import com.google.common.collect.Lists;

/**
 * 
 * @author pandey
 *
 */
class BufferOverflow {
	public static String globalURL;
	public static boolean globalDetailFlag = false;
	public static boolean flipFlop = false;

	private static final Logger logger = Logger.getLogger(BufferOverflow.class);

	private static int countForms = 0;
	private static int countInputs = 0;
	private static String var;
	private static int[] arrayBuffer = new int[5];

	/**
	 * Initialize the Array
	 * */
	public BufferOverflow() {
		for (int i = 0; i < arrayBuffer.length; i++) {
			arrayBuffer[i] = 0;
		}
	}

	/**
	 * Showcases the analysis of Buffer Overflow
	 * */
	public void analyzeBufferOverflow() {
		logger.info("########################################################################################################################");
		logger.info("<---BUFFER OVERFLOW ANALYSIS--->");
		logger.info("Total # of Forms: " + countForms);
		logger.info("Total # of Input tags: " + countInputs);
		logger.info("<<-Categorizing the available data on basis of HTTP Status Codes->>");
		logger.info("Informational Codes 1xx Series: " + arrayBuffer[0]);
		logger.info("Successful Client Interaction related 2xx Series: "
				+ arrayBuffer[1]);
		logger.info("Redirection related 3xx Series: " + arrayBuffer[2]);
		logger.info("Client Error related 4xx Series: " + arrayBuffer[3]);
		logger.info("Server Error related 5xx Series: " + arrayBuffer[4]);
		logger.info("########################################################################################################################");
		logger.info("########################################################################################################################");
		logger.info("For more Information on HTTP Status Code Series, refer the 'HTTP_STATUS_CODE.pdf' in Document folder.");
		logger.info("########################################################################################################################");
	}

	/**
	 * Traverses over the HTML source file and embeds random String into the
	 * value of the 'input' tag
	 */
	private static void sendBack(String data) throws MalformedURLException,
			IOException {
		final HttpClient client = new DefaultHttpClient();
		final List<NameValuePair> pairs = Lists.newArrayList();
		pairs.add(new BasicNameValuePair("http.useragent",
				"Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.10) Gecko/2009042708 Fedora/3.0.10-1.fc10 Firefox/3.0.10"));
		if (globalDetailFlag) {
			logger.info("URL: " + var);
			logger.info("Data passed: " + data);
		}

		final HttpPost method = new HttpPost(var);
		BufferedReader br = null;
		StringTokenizer str = new StringTokenizer(data, "#");
		if (globalDetailFlag)
			logger.info("Total # of Input Fields: " + str.countTokens());
		countInputs += str.countTokens();
		while (str.hasMoreTokens()) {
			StringTokenizer strr = new StringTokenizer(str.nextToken(), ",");
			String attrib = strr.nextToken();
			String value = strr.nextToken();

			pairs.add(new BasicNameValuePair(attrib, value));
		}
		method.setEntity(new UrlEncodedFormEntity(pairs));

		try {
			final HttpResponse httpResponse = client.execute(method);
			final StatusLine statusLine = httpResponse.getStatusLine();
			final int returnCode = statusLine.getStatusCode();
			if (globalDetailFlag)
				logger.info("Return: " + returnCode + " "
						+ statusLine.getReasonPhrase());
			arrayBuffer[(returnCode / 100) - 1]++;
			if (returnCode == HttpStatus.SC_NOT_IMPLEMENTED) {
				logger.error("The Post method is not implemented by this URI");
				httpResponse.getEntity().getContent();
			} else {
				br = new BufferedReader(new InputStreamReader(
						httpResponse.getEntity().getContent()));
				String readLine;
				PrintWriter pw = null;
				try {
					pw = new PrintWriter("temp.html");
					pw.println("Address: " + var);
					while (((readLine = br.readLine()) != null)) {
						pw.println(readLine);
						pw.flush();
					}
				} finally {
					if (pw != null) {
						pw.close();
					}
				}
			}
		} catch (Exception e) {
			logger.error(e);
		} finally {
			// new ProcessBuilder("firefox", "temp.html").start();
			method.releaseConnection();

			if (br != null)
				try {
					br.close();
				} catch (Exception fe) {
				}
		}
	}

	/**
	 * Randomly generates a long String
	 * */
	private static String randomizer() {
		String str1 = new String(
				"QAa0bcLdUK2eHfJgTP8XhiFj61DOklNm9nBoI5pGqYVrs3CtSuMZvwWx4yE7zR");
		StringBuffer sb = new StringBuffer();
		Random r = new Random();
		int te = 0;
		for (int i = 1; i <= 300; i++) {
			te = r.nextInt(62);
			sb.append(str1.charAt(te));
		}
		String token1 = sb.toString();
		return token1;
	}

	/** Parses over the HTML file and populates the List data structure */
	public void parseInput() throws IOException {
		Source source = null;
		try {
			source = new Source(new FileReader("page.loaded"));
		} catch (FileNotFoundException fnfe) {
			logger.error("File not found. Error: " + fnfe.getMessage());
		} catch (IOException ioe) {
			logger.error("IOException occurred. Error: " + ioe.getMessage());
		}
		int currentForm = 0;

		List<StartTag> branches = source.getAllStartTags(HTMLElementName.FORM);
		countForms = branches.size();
		logger.info("########################################################################################################################");

		Attributes attr;
		String data = "";
		String str = "", pattern = "", temp = "";
		String[] tempStr = null;

		for (StartTag sj : branches) {
			currentForm++;
			attr = sj.getAttributes();
			data = "";
			List<StartTag> segments = sj.getElement().getAllStartTags(
					HTMLElementName.INPUT);
			str = "";
			pattern = "";
			temp = "";
			tempStr = null;
			boolean radioSelect = false;

			for (StartTag startTag : segments) {
				str = "";
				temp = "";

				Attributes attributes = startTag.getAttributes();
				String random = randomizer();
				String rel = attributes.getValue("type");
				if (rel == null)
					continue;
				if (rel.equalsIgnoreCase("text")
						|| rel.equalsIgnoreCase("hidden")
						|| rel.equalsIgnoreCase("password")) {
					str = startTag.toString();
					if (str.contains("value")) {
						String atrString = attributes.toString();
						StringTokenizer strAtr = new StringTokenizer(atrString);
						String strtag = "<input ";
						while (strAtr.hasMoreTokens()) {
							String strA = strAtr.nextToken();
							if (!strA.contains("value")) {
								strtag = strtag + strA + " ";
							}
						}
						strtag = strtag + "value=\"" + random + "\"/>";
						temp = strtag;
					} else {
						pattern = "/>";
						Pattern p = Pattern.compile(pattern);
						Matcher m = null;
						if (p.matcher(str).find()) {
							tempStr = str.split(" ");
							int last = tempStr.length - 1;
							pattern = "/>";
							String replace = " value=\"" + random + "\"/>";
							p = Pattern.compile(pattern);
							m = p.matcher(tempStr[last]);
							tempStr[last] = m.replaceFirst(replace);
							temp = "";
							for (int j = 0; j < tempStr.length; j++)
								temp = temp + tempStr[j] + " ";
						} else {
							tempStr = str.split(" ");
							int last = tempStr.length - 1;
							pattern = ">";
							String replace = " value=\"" + random + "\"/>";
							p = Pattern.compile(pattern);
							m = p.matcher(tempStr[last]);
							tempStr[last] = m.replaceFirst(replace);
							temp = "";
							for (int j = 0; j < tempStr.length; j++)
								temp = temp + tempStr[j] + " ";
						}
					}
					try {
						data += URLEncoder.encode(attributes.getValue("name"),
								"UTF-8")
								+ ","
								+ URLEncoder.encode(random, "UTF-8")
								+ "#";
					} catch (UnsupportedEncodingException uee) {
						logger.error("Unsupported error");
					}
				} else if (rel.equalsIgnoreCase("radio") && radioSelect) {
					radioSelect = true;
					str = startTag.toString();
					if (str.contains("checked")) {
						String atrString = attributes.toString();
						StringTokenizer strAtr = new StringTokenizer(atrString);
						String strtag = "<input ";
						while (strAtr.hasMoreTokens()) {
							String strA = strAtr.nextToken();
							if (!strA.contains("")) {
								strtag = strtag + strA + " ";
							}
						}
						strtag = strtag + "value=\"" + randomizer() + "\"/>";
						temp = strtag;
						logger.info("Line: " + rel + " is checked");
					} else {
						pattern = "/>";
						Pattern p = Pattern.compile(pattern);
						Matcher m = null;
						if (p.matcher(str).find()) {
							tempStr = str.split(" ");
							int last = tempStr.length - 1;
							pattern = "/>";
							String replace = " checked/>";
							p = Pattern.compile(pattern);
							m = p.matcher(tempStr[last]);
							tempStr[last] = m.replaceFirst(replace);
							temp = "";
							for (int j = 0; j < tempStr.length; j++)
								temp = temp + tempStr[j] + " ";
						} else {
							tempStr = str.split(" ");
							int last = tempStr.length - 1;
							pattern = ">";
							String replace = " checked/>";
							p = Pattern.compile(pattern);
							m = p.matcher(tempStr[last]);
							tempStr[last] = m.replaceFirst(replace);
							temp = "";
							for (int j = 0; j < tempStr.length; j++)
								temp = temp + tempStr[j] + " ";
						}
					}

					try {
						data += URLEncoder.encode(attributes.getValue("name"),
								"UTF-8")
								+ ","
								+ URLEncoder.encode("checked", "UTF-8")
								+ "#";
					} catch (UnsupportedEncodingException uee) {
						logger.error("Unsupported error");
					}
				} else if (rel.equalsIgnoreCase("checkbox")) {
					str = startTag.toString();
					if (str.contains("checked")) {
						String atrString = attributes.toString();
						StringTokenizer strAtr = new StringTokenizer(atrString);
						String strtag = "<input ";
						while (strAtr.hasMoreTokens()) {
							String strA = strAtr.nextToken();
							if (!strA.contains("")) {
								strtag = strtag + strA + " ";
							}
						}
						strtag = strtag + "value=\"" + randomizer() + "\"/>";
						temp = strtag;
						logger.info("Line: " + rel + " is checked");
					} else {
						pattern = "/>";
						Pattern p = Pattern.compile(pattern);
						Matcher m = null;
						if (p.matcher(str).find()) {
							tempStr = str.split(" ");
							int last = tempStr.length - 1;
							pattern = "/>";
							String replace = " checked/>";
							p = Pattern.compile(pattern);
							m = p.matcher(tempStr[last]);
							tempStr[last] = m.replaceFirst(replace);
							temp = "";
							for (int j = 0; j < tempStr.length; j++)
								temp = temp + tempStr[j] + " ";
						} else {
							tempStr = str.split(" ");
							int last = tempStr.length - 1;
							pattern = ">";
							String replace = " checked/>";
							p = Pattern.compile(pattern);
							m = p.matcher(tempStr[last]);
							tempStr[last] = m.replaceFirst(replace);
							temp = "";
							for (int j = 0; j < tempStr.length; j++)
								temp = temp + tempStr[j] + " ";
						}
					}

					try {
						data += URLEncoder.encode(attributes.getValue("name"),
								"UTF-8")
								+ ","
								+ URLEncoder.encode("checked", "UTF-8")
								+ "#";
					} catch (UnsupportedEncodingException uee) {
						logger.error("Unsupported error");
					}
				}
			}

			var = attr.getValue("action");
			// new FileWriter("temp.html").append(var);
			if (var == null || var.equals("")) {
				continue;
			}
			if (var.charAt(0) == '/') {
				var = globalURL + var;
			} else if (!var.contains("http") & !var.contains("https")
					& !var.contains("www")) {
				int ind = globalURL.lastIndexOf("/");
				int cind = globalURL.lastIndexOf("//");
				String rt;
				if (ind == cind + 1) {
					rt = globalURL;
				} else {
					rt = globalURL.substring(0, ind);
				}
				var = rt + '/' + var;
			}
			// new ProcessBuilder("C:\\Program Files\\Mozilla Firefox\\firefox",
			// "temp.html").start();
			if (globalDetailFlag) {
				logger.info("data: " + data);
				logger.info("Form #: " + currentForm);
			}
			if (!globalDetailFlag)
				if (!flipFlop) {
					logger.info(">>");
					flipFlop = true;
				} else {
					logger.info("<<");
					flipFlop = false;
				}
			sendBack(data);
			if (globalDetailFlag)
				logger.info("########################################################################################################################");
		}
	}
}
