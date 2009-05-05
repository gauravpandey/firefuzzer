package org.Firefuzzer.Fire;

import net.htmlparser.jericho.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * @author Gaurav Pandey (gip2103) and Sumit Jindal
 *
 */
public class Firefuzzer {
	private URL fURL;

    public Firefuzzer(URL aURL) {
        if ( ! "http".equals(aURL.getProtocol())) {
        	throw new IllegalArgumentException("URL is not for HTTP Protocol: " + aURL);
        }
        fURL = aURL;
    }

    public Firefuzzer( String aUrlName ) throws MalformedURLException {
        this ( new URL(aUrlName) );
    }

    public String getPageContent() throws IOException {
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

    private static void log(Object aObject)  {
        try {
        	/*File f = new File("page.loaded");
        	if (f.exists()) {
				f.delete();
            	f.createNewFile();
        	}*/
        	FileWriter fww = new FileWriter("page.loaded");
        	fww.close();
        	FileWriter fw = new FileWriter("page.loaded",true);
        	fw.append(aObject.toString());
        	fw.close();
        }
        catch(Exception e) {
        	System.err.println("Exception occured: "+e.getMessage());
        }
    }
                
    public static void main(String []args) throws IOException,InterruptedException {

    	System.out.println("");
    	System.out.println("\t8888888888 8888888 8888888b.  8888888888 8888888888 888     888 8888888888P 8888888888P 8888888888 8888888b.");
    	System.out.println("\t888          888   888   Y88b 888        888        888     888       d88P        d88P  888        888   Y88b ");
    	System.out.println("\t888          888   888    888 888        888        888     888      d88P        d88P   888        888    888 ");
    	System.out.println("\t8888888      888   888   d88P 8888888    8888888    888     888     d88P        d88P    8888888    888   d88P ");
    	System.out.println("\t888          888   8888888P\"  888        888        888     888    d88P        d88P     888        8888888P\" ");
    	System.out.println("\t888          888   888 T88b   888        888        888     888   d88P        d88P      888        888 T88b ");
    	System.out.println("\t888          888   888  T88b  888        888        Y88b. .d88P  d88P        d88P       888        888  T88b");
    	System.out.println("\t888        8888888 888   T88b 8888888888 888         \"Y88888P\"  d8888888888 d8888888888 8888888888 888   T88b ");
    	System.out.println("");
        Thread.sleep(1000);
        String url = args[0];
    	if(args.length==2 || args.length==3) {}
    	else {
    		System.err.println("Incorrect number of parameters");
    		System.err.println("Syntax is java Firefuzzer <url> <buffer/sql> <detail(OPTIONAL)>");
    		System.exit(0);
    	}
    	if(args[1].equalsIgnoreCase("buffer") || args[1].equalsIgnoreCase("sql")) {}
    	else {
    		System.err.println("Incorrect parameters entered");
    		System.err.println("Syntax is java Firefuzzer <url> <buffer/sql> <detail(OPTIONAL)>");
    		System.exit(0);
    	}
    	if(args.length==3) {
    		if(args[2].equalsIgnoreCase("detail")) {}
    		else {
    			System.err.println("Incorrect parameters entered");
    			System.err.println("Syntax is java Firefuzzer <url> <buffer/sql> <detail(OPTIONAL)>");
    			System.exit(0);
    		}
    	}
        if(url.contains("http://")) {}
        else if(url.startsWith("www."))
                url="http://"+url;

        else {
                System.out.println("Only http protocol based sites are handled. Wait for future release.");
                System.exit(0);
        }
        
        System.out.println("########################################################################################################################");
        System.out.println("Targeted URL: "+url);
        System.out.println("########################################################################################################################");
        Firefuzzer fetcher = new  Firefuzzer(url);
        log(fetcher.getPageContent());
        if(args[1].equalsIgnoreCase("buffer")) {
        	BufferOverflow bo = new BufferOverflow();
        	if(args.length==3)
        		if(args[2].equalsIgnoreCase("detail"))
        			bo.globalDetailFlag = true;
        	bo.globalURL=url;
        	System.out.println("<---BUFFER OVERFLOW ATTACK--->");
        	System.out.println("########################################################################################################################");
        	bo.parseInput();
        	bo.analyzeBufferOverflow();
        }
        else if(args[1].equalsIgnoreCase("sql")) {
        	SQLInjection sql = new SQLInjection();
        	if(args.length==3)
        		if(args[2].equalsIgnoreCase("detail"))
        			sql.globalDetailFlag = true;
        	sql.globalURL=url;
        	System.out.println("<---SQL INJECTION ATTACK--->");
        	System.out.println("########################################################################################################################");
        	sql.parseInput();
        	sql.analyzeSQLInjection();
        }
    }
}