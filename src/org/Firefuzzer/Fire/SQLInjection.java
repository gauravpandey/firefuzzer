package org.Firefuzzer.Fire;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.List;
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
class SQLInjection {
	public static String globalURL;
	public static boolean globalDetailFlag = false;
	public static boolean flipFlop = false;

	private static final Logger logger = Logger.getLogger(SQLInjection.class);

	private static int countForms = 0;
	private static String var;
	private static int[] arrayBuffer = new int[5];

	/**
	 * Initializes the Array
	 * */
	public SQLInjection() {
		for (int i = 0; i < arrayBuffer.length; i++) {
			arrayBuffer[i] = 0;
		}
	}

	/**
	 *Performs analysis over SQL Injection and showcases the result to the User
	 */
	public static void analyzeSQLInjection() {
		logger.info("########################################################################################################################");
		logger.info("<---SQL INJECTION ANALYSIS--->");
		logger.info("Total # of Forms: " + countForms);
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
	 * Traverses over the HTML source file and embeds it with huge sounds
	 * */
	private static void sendBack(final String data)
									   throws MalformedURLException,
									   	      IOException {
		final HttpClient client = new DefaultHttpClient();
		final List<NameValuePair> pairs = Lists.newArrayList();
		pairs.add(new BasicNameValuePair("http.useragent",
				"Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.10) Gecko/2009042708 Fedora/3.0.10-1.fc10 Firefox/3.0.10"));

		if (globalDetailFlag) {
			logger.info("URL: " + var);
		}

		final HttpPost method = new HttpPost(var);
		BufferedReader br = null;
		StringTokenizer str = new StringTokenizer(data, "#");
		
		while (str.hasMoreTokens()) {
			StringTokenizer strr = new StringTokenizer(str.nextToken(), ",");
			String attrib = strr.nextToken();
			String value = strr.nextToken();
			pairs.add(new BasicNameValuePair(attrib, value));
		}
		method.setEntity(new UrlEncodedFormEntity(pairs));

		try {
			// logger.info(method.getResponseHeaders());
			final HttpResponse httpResponse = client.execute(method);
			final StatusLine statusLine = httpResponse.getStatusLine();
			int returnCode = statusLine.getStatusCode();
			if (globalDetailFlag)
				logger.info("Status: " + statusLine.getStatusCode());
			arrayBuffer[(statusLine.getStatusCode() / 100) - 1]++;
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
	 * Parses the HTML source input file and populates the List data structure
	 */
	public static void parseInput() throws IOException {
		Source source = null;
		try {
			source = new Source(new FileReader("page.loaded"));
		} catch (FileNotFoundException fnfe) {
			logger.error("File not found. Error: " + fnfe.getMessage());
		} catch (IOException ioe) {
			logger.error("IOException occurred. Error: " + ioe.getMessage());
		}
		int currentLoop = 0;

		List<StartTag> branches = source.getAllStartTags(HTMLElementName.FORM);
		countForms = branches.size();
		System.out
				.println("########################################################################################################################");

		Attributes attr;
		String s = "", data = "";
		String str = "", pattern = "", temp = "";
		File dir1 = new File (".");
		String location = dir1.getCanonicalPath()+"/src/inject.conf";
		String[] tempStr = null;
		dir1 = new File(location);
		if(!dir1.exists()) {
			logger.info("\n\"inject.conf\" file does not exist.");
			logger.info("Make sure it exists in the same folder as the runnable jar.");
			logger.info("Please retry again");
			System.exit(0);
		}

		BufferedReader br = new BufferedReader(new FileReader(location));
		while ((s = br.readLine()) != null) {
			if (!s.isEmpty()) {
				currentLoop++;
				for (StartTag sj : branches) {
					attr = sj.getAttributes();
					data = "";
					List<StartTag> segments = sj.getElement().getAllStartTags(
							HTMLElementName.INPUT);
					str = "";
					pattern = "";
					temp = "";
					tempStr = null;

					for (StartTag startTag : segments) {
						str = "";
						temp = "";

						Attributes attributes = startTag.getAttributes();
						String random = s;
						String rel = attributes.getValue("type");
						if (rel == null)
							continue;
						if (rel.equalsIgnoreCase("text")
								|| /* rel.equalsIgnoreCase("hidden") || */rel
										.equalsIgnoreCase("password")) {
							str = startTag.toString();
							if (str.contains("value")) {
								String atrString = attributes.toString();
								StringTokenizer strAtr = new StringTokenizer(
										atrString);
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
									String replace = " value=\"" + random
											+ "\"/>";
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
									String replace = " value=\"" + random
											+ "\"/>";
									p = Pattern.compile(pattern);
									m = p.matcher(tempStr[last]);
									tempStr[last] = m.replaceFirst(replace);
									temp = "";
									for (int j = 0; j < tempStr.length; j++)
										temp = temp + tempStr[j] + " ";
								}
							}
							try {
								data += URLEncoder.encode(attributes
										.getValue("name"), "UTF-8")
										+ ","
										+ URLEncoder.encode(random, "UTF-8")
										+ "#";
							} catch (UnsupportedEncodingException uee) {
								logger.error("Unsupported error");
							}
						}
					}
					var = attr.getValue("action");
					if (var == null || var.equals("")) {
						logger.error("No URL specified in FORM TAG-ACTION field");
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
					if (globalDetailFlag) {
						logger.info("data: " + data);
						logger.info("SQL Injection #: " + currentLoop);
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
		if (br != null) {
			br.close();
		}
	}
}
