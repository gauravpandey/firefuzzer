/**
 * 
 */
package Fire;

import java.io.*;
import java.net.*;
import java.util.*;



/**
 * @author gaurav
 *
 */
public class Firefuzzer {
	private URL fURL;

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
		FileWriter fw = new FileWriter("Hero/test.html",true);
		fw.append(aObject.toString());
	}
	  
	public static void main(String []args) throws IOException,MalformedURLException {
		String url = args[0];
		Firefuzzer fetcher = new  Firefuzzer(url);
		log( fetcher.getPageContent() );
	}
}
