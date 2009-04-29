/**
 * 
 */
package Fire;

import net.htmlparser.jericho.*;
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
		File f = new File("page.loaded");
		if (f.exists()) {
			f.delete();
			f.createNewFile();
		}
		FileWriter fw = new FileWriter("page.loaded",true);
		fw.append(aObject.toString());
		fw.close();
	}
	
	/*private static void streamline() {
		try {
			Scanner scan = new Scanner(new FileReader("page.loaded"));
			File f = new File("temp.loaded");
			if (f.exists()) {
				f.delete();
				f.createNewFile();
			}
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
	
	 private static int processWordCount(String data,String pat) {
		Scanner s = new Scanner(data);
		s.useDelimiter(pat);
		System.out.println("data: "+data);
		Pattern p = Pattern.compile(pat);
		String words = null;
		int count = 0;
		while (s.hasNext()) {
			s.next();
			count += 1;
		}
		return count;
	 }
	
	private static void patternchecker() throws IOException {
		Scanner scan = new Scanner(new FileReader("temp.loaded"));
		File f = new File("value.loaded");
		if (f.exists()) {
			f.delete();
			f.createNewFile();
		}
		PrintWriter pw = new PrintWriter("value.loaded");
		Boolean flag = false;
		while(scan.hasNextLine()) {
			String strLine = scan.nextLine().toLowerCase();
			if((strLine.contains("type=\"text\"") | strLine.contains("type=text") | strLine.contains("type=\"password\"") | strLine.contains("type=password") | strLine.contains("type=\"hidden\"") | strLine.contains("type=hidden")) & strLine.contains("<input") & strLine.contains(">")) {
				StringTokenizer strToken = new StringTokenizer(strLine);
				String strLineToken = "",temp = "";
				int counter=0;
				while(strToken.hasMoreTokens()) {
					strLineToken = strToken.nextToken();
					if(strLineToken.contains("<input") | flag==true) {
						temp=temp+strLineToken+" ";
						while(strToken.hasMoreTokens()) {
							strLineToken = strToken.nextToken();
							if(strLineToken.contains(">")) {
								temp=temp+strLineToken+" ";
								if(strLineToken.contains("<input")) {
									flag=true;
									counter++;
								}
								else {
									flag = false;
									counter=0;
								}
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
						System.out.println("Counter: "+processWordCount(temp, "value"));
						if(m.find()) {
							System.out.println("MATCH: "+temp);
							//pw.println(temp);
							//pw.flush();
						}
						else if(p1.matcher(temp).find()) {
							String[] tempStr = temp.split(" ");
							int last = tempStr.length-1;
						
							pattern = "/>";  //either > or /> ..not sure
							String replace = " value=\"hello\"/>";
							p = Pattern.compile(pattern);
							m = p.matcher(tempStr[last]);
							tempStr[last]=m.replaceFirst(replace);
							temp="";
							for(int i=0;i<tempStr.length;i++)
								temp = temp+tempStr[i]+" ";
							System.out.println("REPLACED1:"+temp);
							//pw.println(temp);
							//pw.flush();
							//System.out.println("REPLACED: "+);
						}
						else {
							String[] tempStr = temp.split(" ");
							int last = tempStr.length-1;
							
							pattern = ">";  either > or /> ..not sure
							String replace = " value=\"hello\"/>";
							p = Pattern.compile(pattern);
							m = p.matcher(tempStr[last]);
							tempStr[last]=m.replaceFirst(replace);
							temp="";
							for(int i=0;i<tempStr.length;i++)
								temp = temp+tempStr[i]+" ";
							System.out.println("REPLACED2:"+temp);
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
	}*/
	
	private static void parseInput() {
		Source source = null;
		try {
		source = new Source(new FileReader("page.loaded"));
		}
		catch (FileNotFoundException fnfe) {
			System.err.println("File not found. Error: "+fnfe.getMessage());
		}
		catch (IOException ioe) {
			System.err.println("IOException occurred. Error: "+ioe.getMessage());
		}
		System.out.println("Input Tags: ");
		List<StartTag> segments = source.getAllStartTags(HTMLElementName.INPUT);
		OutputDocument outputDocument=new OutputDocument(source);
		String str = "",pattern = "",temp = "";
		String[] tempStr = null;
		for (StartTag startTag : segments) {
			str = "";
			temp="";
		    //StartTag startTag = (StartTag)i.next();
		    Attributes attributes=startTag.getAttributes();
		    String rel=attributes.getValue("type");
		    if(rel.equals("text") | rel.equals("hidden") | rel.equals("password")) {
		    	//System.out.println(startTag);
		    	str = startTag.toString();
		    	if(str.contains("value")) {
		    		//System.out.println("valueeeeeeeeeee: "+attributes.getValue("value"));
		    		String atrString = attributes.toString();
		    		StringTokenizer strAtr = new StringTokenizer(atrString);
		    		String strtag = "<input ";
		    		while(strAtr.hasMoreTokens()) {
		    			String strA = strAtr.nextToken();
		    			if(!strA.contains("value")) {
		    				strtag = strtag+strA+" ";
		    			}
		    		}
		    		Random randgen = new Random();
		    		//String token1 = Double.toString(Math.abs(randgen.nextDouble()));
		    		String str1=new  String("QAa0bcLdUK2eHfJgTP8XhiFj61DOklNm9nBoI5pGqYVrs3CtSuMZvwWx4yE7zR");
		    	 	StringBuffer sb=new StringBuffer();
		    	 	Random r = new Random();
		    	 	int te=0;
		    	 	for(int i=1;i<=300;i++){
		    	 		te=r.nextInt(62);
		    	 		sb.append(str1.charAt(te));
		    	 	}
		    	 	String token1 = sb.toString();
		    	 	//System.out.println("STRING: "+token1);
		    		strtag=strtag+"value=\""+token1+"\"/>";
		    		//System.out.println(attributes.getValue("value"));
		    		//System.out.println("O: "+str);
		    		temp=strtag;
		    	}
		    	else {
		    		pattern = "/>";
					Pattern p = Pattern.compile(pattern);
					Matcher m = null;
					if(p.matcher(str).find()) {
						tempStr = str.split(" ");
						int last = tempStr.length-1;
					
						pattern = "/>";  //either > or /> ..not sure
						String replace = " value=\"hello\"/>";
						p = Pattern.compile(pattern);
						m = p.matcher(tempStr[last]);
						tempStr[last]=m.replaceFirst(replace);
						temp="";
						for(int j=0;j<tempStr.length;j++)
							temp = temp+tempStr[j]+" ";
						//System.out.println("REPLACED1:"+temp);
					}
					else {
						tempStr = str.split(" ");
						int last = tempStr.length-1;
						
						pattern = ">";  //either > or /> ..not sure
						String replace = " value=\"hello\"/>";
						p = Pattern.compile(pattern);
						m = p.matcher(tempStr[last]);
						tempStr[last]=m.replaceFirst(replace);
						temp="";
						for(int j=0;j<tempStr.length;j++)
							temp = temp+tempStr[j]+" ";
						//System.out.println("REPLACED2:"+temp);
					}
		    	}
		    	//System.out.println("out: "+temp);
			    System.out.println("1: "+startTag);
			    System.out.println("2: "+temp);
			    outputDocument.replace(startTag,temp);
		    }
		  }
		try {
			outputDocument.writeTo(new FileWriter("temp.html"));
			//outputDocument.writeTo(new OutputStreamWriter(System.out));
		}
		catch (IOException ioe) {
			System.err.println("IOException error: "+ioe.getMessage());
		}

		/*for(Segment segment : segments) {
			System.out.println(segment);
		}*/
	}
		  
	public static void main(String []args) throws IOException,MalformedURLException {
		//String url = args[0];  
		//Firefuzzer fetcher = new  Firefuzzer(url);
		//log( fetcher.getPageContent() );
		/*streamline();
		patternchecker();*/
		//readTemp();
		parseInput();
		//Process p = new ProcessBuilder("firefox", "temp.html").start();
		
		ClientHttpRequest chr = new ClientHttpRequest();
		
		
		}
	}
