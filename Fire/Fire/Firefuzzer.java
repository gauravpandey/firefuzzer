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
	
	private static void readFile() {
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
		readFile();
			
		}
	}
