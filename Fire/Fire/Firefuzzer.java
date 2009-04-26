/**88888
 * 
 */
package Fire;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;


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

	private static void log(Object aObject) throws IOException{
		//System.out.println(aObject);
		FileWriter fw = new FileWriter("page.html",true);
		fw.append(aObject.toString());
		fw.close();
	}
	
	/*private static void readFile() {
		try {
			BufferedReader br = new BufferedReader(new FileReader("page.html"));
			String s = "",t="";
			int lineCount = 0;
			while((s=br.readLine())!=null) {
				lineCount++;
				if(!s.isEmpty()) {
					if(s.contains("<input")) {
						StringTokenizer str = new StringTokenizer(s);
						while(str.hasMoreTokens()) {
							if(str.nextToken().contains("<input ")) {
								while(str.hasMoreTokens()) {
									String curr = "";
									curr = str.nextToken();
									if(curr.equals("type=\"text\"")) {
										flag = true;
									}
									else if(curr.contains(">")) {
										break;
									}
								}
							}
						}
						vecStr.add(lineCount+"#"+s);
					}
				}
			}
		}
		catch(FileNotFoundException fnfe) {
			System.err.println("Exception error: "+fnfe.getMessage());
		}
		catch(IOException ioe) {
			System.err.println("Exception error: "+ioe.getMessage());
		}
	}
	
	public static void readVec() {
		int veclen = vecStr.size();
		for(int i=0;i<veclen;i++) {
			System.out.println(vecStr.get(i));
		}
	}
	
	public static void randomCall() {
		Random randgen = new Random();
		int length = vecStr.size();
		int selection = randgen.nextInt(length);
		String chosenString = vecStr.get(selection);
		System.out.println("CHOSEN ONE: "+chosenString);
		StringTokenizer str1 = new StringTokenizer(chosenString);
		while(str1.hasMoreTokens()) {
			String currString = str1.nextToken();
			if(currString.contains("<input")) {
				while(str1.hasMoreTokens()) {
					currString = str1.nextToken();
					if(!currString.contains(">")) {
						if(currString.contains("value=")) {
							
							flag = true;							
						}
					}
					else {
						break;
					}
				}
			}
			if(currString.contains("value=")) {
				flag = true;
			}
			if(flag==true) {
				break;
			}
		}
	}*/
	
	private static void read() {
		try {
			Scanner scan = new Scanner(new File("page.loaded"));
			while(scan.hasNext()) {
				System.out.println(scan.next());
			}
		}
		catch (FileNotFoundException fnfe) {
			System.err.println("Exception error: "+fnfe.getMessage());
		}
		
	}
		  
	public static void main(String []args) throws IOException,MalformedURLException {
		String url = args[0];  
		Firefuzzer fetcher = new  Firefuzzer(url);
		log( fetcher.getPageContent() );
		/*readFile();
		readVec();
		randomCall();*/
		//System.out.println(flag);
		//if(flag) {
		
		read();
			
		}
	}
