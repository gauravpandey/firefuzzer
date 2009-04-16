/**
 * 
 */
package Fire;

import java.io.*;
import java.net.*;
import java.util.Scanner;



/**
 * @author gaurav
 *
 */
/*public class Firefuzzer {
	public static void main(String[] args) {
		try {
			String page = "";
			URL address = new URL("http://register.rediff.com/register/register.php?FormName=user_details");
			address.
			page = page+(new DataInputStream(address.openStream())).readUTF(); 
			System.out.println(page);
		}
		
		catch (MalformedURLException murle) {
			System.err.println("MalformedURLException Error: "+murle.getMessage());
		}
		
		catch (IOException ioe) {
			System.err.println("IOException Error: "+ioe.getMessage());
		}
	}

}*/

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

	public String getPageContent() {
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

	private static void log(Object aObject) {
		System.out.println(aObject);
	}
	  
	public static void main(String []args) throws MalformedURLException {
		String url = args[0];
		Firefuzzer fetcher = new  Firefuzzer(url);
		log( fetcher.getPageContent() );
	}
}
