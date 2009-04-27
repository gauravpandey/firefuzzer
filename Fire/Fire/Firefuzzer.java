/**
 * 
 */
package Fire;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;


/**
 * @author gaurav
 *
 */
public class Firefuzzer {
	private URL fURL;
	private static int count = 0;
	private static Vector<String> vecStr = new Vector<String>();
	private static Boolean flag = false;

	public Firefuzzer(URL aURL) {
		if ( ! "http".equals(aURL.getProtocol())  ) {
			throw new IllegalArgumentException("URL is not for HTTP Protocol: " + aURL);
		}
	    fURL = aURL;
    }

	public Firefuzzer( String aUrlName ) throws MalformedURLException {
		this ( new URL(aUrlName) );
	}

	public String getPageContent() throws IOException{
		String result = null;
	    URLConnection connection = null;
	    try {
	    	connection =  fURL.openConnection();
	    	Scanner scanner = new Scanner(connection.getInputStream());
	    	scanner.useDelimiter("\\Z");
	    	result = scanner.next();
	    }
	    catch ( IOException ex ) {
	    	log("Cannot open connection to " + fURL.toString());
	    }
	    return result;
	}

	private static void log(Object aObject) throws IOException {
		FileWriter fw = new FileWriter("page.loaded",true);
		fw.append(aObject.toString());
		fw.close();
	}
	
	private static void streamline() {
		try {
			Scanner scan = new Scanner(new FileReader("page.loaded"));
			PrintWriter pw = new PrintWriter("temp.loaded");
			String str = "",temp = "";
			while(scan.hasNext()) {
				str = scan.next().toLowerCase();
				if(str.contains("<input")) {
					temp=temp+str+" ";
					while(scan.hasNext()) {
						str=scan.next();
						//System.out.println(str);
						if(!str.contains(">")) {
							temp=temp+str+" ";
						}
						else if(str.contains(">") && str.contains("<input")) {
							temp=temp+str+" ";
							continue;
						}
						else if(str.contains(">")){
							temp=temp+str+" ";
							break;
						}
						else {
							temp=temp+str+" ";
						}
					}
					pw.println(temp);
					temp = "";
				}
				else {
					pw.println(str);
					pw.flush();
				}
			}
			pw.close();
			scan.close();
		}
		catch (FileNotFoundException fnfe) {
			System.err.println("Exception error: "+fnfe.getMessage());
		}
		catch(IOException ioe) {
			System.err.println("IOException error: "+ioe.getMessage());
		}
		catch(Exception e) {
			System.err.println("Exception error: "+e.getMessage());
		}
	}
	
	private static void patternchecker() throws IOException {
		Scanner scan = new Scanner(new FileReader("temp.loaded"));
		PrintWriter pw = new PrintWriter("value.loaded");
		while(scan.hasNextLine()) {
			String strLine = scan.nextLine();
			if((strLine.contains("type=\"text\"") | strLine.contains("type=\"password\"") | strLine.contains("type=\"hidden\"")) & strLine.contains("<input") & strLine.contains(">")) {
				StringTokenizer strToken = new StringTokenizer(strLine);
				String strLineToken = "",temp = "";
				while(strToken.hasMoreTokens()) {
					strLineToken = strToken.nextToken();
					if(strLineToken.contains("<input")) {
						temp=temp+strLineToken+" ";
						while(strToken.hasMoreTokens()) {
							strLineToken = strToken.nextToken();
							if(strLineToken.contains(">")) {
								temp=temp+strLineToken+" ";
								break;
							}
							else {
								temp=temp+strLineToken+" ";
							}
						}
						String pattern = "value";
						String pattern1 = "/>";
						Pattern p = Pattern.compile(pattern);
						Pattern p1 = Pattern.compile(pattern1);
						Matcher m = p.matcher(temp);
						if(m.find()) {
							System.out.println("MATCH: "+temp);
							//pw.println(temp);
							//pw.flush();
						}
						else if(p1.matcher(temp).find()) {
							String[] tempStr = temp.split(" ");
							int last = tempStr.length-1;
						
							pattern = "/>";  /*either > or /> ..not sure*/
							String replace = " value=\"hello\"/>";
							p = Pattern.compile(pattern);
							m = p.matcher(tempStr[last]);
							tempStr[last]=m.replaceFirst(replace);
							/*temp="";*/
							for(int i=0;i<tempStr.length;i++)
								temp = temp+tempStr[i]+" ";
							System.out.println("REPLACED:"+temp);
							//pw.println(temp);
							//pw.flush();
							//System.out.println("REPLACED: "+);
						}
						else {
							String[] tempStr = temp.split(" ");
							int last = tempStr.length-1;
							
							pattern = ">";  /*either > or /> ..not sure*/
							String replace = " value=\"hello\"/>";
							p = Pattern.compile(pattern);
							m = p.matcher(tempStr[last]);
							tempStr[last]=m.replaceFirst(replace);
							/*temp="";*/
							for(int i=0;i<tempStr.length;i++)
								temp = temp+tempStr[i]+" ";
							System.out.println("REPLACED:"+temp);
							//pw.println(temp);
							//pw.flush();
							//System.out.println("REPLACED: "+);
						}
						pw.println(temp);
						pw.flush();
					}
					else {
						pw.println(strLineToken);
						pw.flush();
					}
				}
			}
			else  {
				pw.println(strLine);
				pw.flush();
			}
		}
	}
	
	private static void readTemp() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("temp.loaded"));
		String s = "";
		while((s=br.readLine())!=null) {
			System.out.println(s);
		}
	}
		  
	public static void main(String []args) throws IOException,MalformedURLException {
		String url = args[0];  
		Firefuzzer fetcher = new  Firefuzzer(url);
		log( fetcher.getPageContent() );
		streamline();
		patternchecker();
		//readTemp();
		}
	}
