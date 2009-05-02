package Fire;

import net.htmlparser.jericho.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.*;

/**
 * @author gaurav
 *
 */
public class Firefuzzer {
	private URL fURL;
	private static String var;
    private static String gurl;

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
       
    private static void sendBack(String data) throws MalformedURLException,IOException
    {   HttpClient client = new HttpClient();
    	client.getParams().setParameter("http.useragent", "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.10) Gecko/2009042708 Fedora/3.0.10-1.fc10 Firefox/3.0.10");
        System.out.println("VAR: "+var);
        System.out.println("Data: "+data);
               
        PostMethod method = new PostMethod(var);
        BufferedReader br = null;
        StringTokenizer str = new StringTokenizer(data,"#");
        while(str.hasMoreTokens()) {
        	StringTokenizer strr = new StringTokenizer(str.nextToken(),",");
            String attrib = strr.nextToken();
            String value = strr.nextToken();
            method.addParameter(attrib,value);
        }
        
        /*String redirectLocation;
        	Header locationHeader = method.getResponseHeader("location");
        	if (locationHeader != null) {
        		var = locationHeader.getValue();
        		sendBack("hrllo");
        	}
        	else {
        		System.out.println("404 ERROR");
        	}*/
        

        
        try{
        	int returnCode = client.executeMethod(method);

            if(returnCode == HttpStatus.SC_NOT_IMPLEMENTED) {
                System.err.println("The Post method is not implemented by this URI");
                method.getResponseBodyAsString();
            }
            /*else if ((returnCode == HttpStatus.SC_MOVED_TEMPORARILY) ||
                (returnCode == HttpStatus.SC_MOVED_PERMANENTLY) ||
                (returnCode == HttpStatus.SC_SEE_OTHER) ||
                (returnCode == HttpStatus.SC_TEMPORARY_REDIRECT)) {
            	//method.setFollowRedirects(true);
            	
                Header header = method.getResponseHeader("location");
                if (header != null) {
                    String newuri = header.getValue();
                    if ((newuri == null) || (newuri.equals(""))) {
                        newuri = "/";
                    }
                    System.out.println("Redirect target: " + newuri); 
                    PostMethod redirect = new PostMethod(newuri);

                    client.executeMethod(redirect);
                    System.out.println("Redirect: " + redirect.getStatusLine().toString()); 
                    // release any connection resources used by the method
                    redirect.releaseConnection();
                } 
                else {
                    System.out.println("Invalid redirect");
                    System.exit(1);
                }
            }*/
            else {
                br = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
                String readLine;
                PrintWriter pw = new PrintWriter("temp.html");
                pw.println("Address: "+var);
                while(((readLine = br.readLine()) != null)) {
                	pw.println(readLine);
                    pw.flush();
                }
            }
        }
        catch (Exception e) {
            System.err.println(e);
        }
        finally {
        	new ProcessBuilder("firefox", "temp.html").start();
            method.releaseConnection();
   
            if(br != null)
            	try {
            		br.close();
                }
                catch (Exception fe) {}
        }
    }
       
    private static String randomizer() {
        String str1=new  String("QAa0bcLdUK2eHfJgTP8XhiFj61DOklNm9nBoI5pGqYVrs3CtSuMZvwWx4yE7zR");
        StringBuffer sb=new StringBuffer();
        Random r = new Random();
        int te=0;
        for(int i=1;i<=300;i++){
        	te=r.nextInt(62);
            sb.append(str1.charAt(te));
        }
        String token1 = sb.toString();
        return token1;
    }
       
    private static void parseInput() throws IOException{
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
           
        List<StartTag> branches=source.getAllStartTags(HTMLElementName.FORM);
        System.out.println("FORM Tags: ");
        System.out.println(branches.toString());
        System.out.println("Input Tags: ");
        OutputDocument outputDocument=new OutputDocument(source);
           
        Attributes attr;
        String data = "";
        String str = "",pattern = "",temp = "";
        String[] tempStr = null;
        
        for(StartTag sj:branches) {
        	attr=sj.getAttributes();
            data="";
            List<StartTag> segments = sj.getElement().getAllStartTags(HTMLElementName.INPUT);        
            str = "";
            pattern = "";
            temp = "";
            tempStr = null;
            boolean radioSelect = false;
            boolean checkSelect = false;
            
            for (StartTag startTag : segments) {
            	str = "";
            	temp="";
                           
            	Attributes attributes=startTag.getAttributes();
                String random = randomizer();
            	String rel=attributes.getValue("type");
            	if(rel==null)
            		continue;
            	if(rel.equalsIgnoreCase("text") || rel.equalsIgnoreCase("hidden") || rel.equalsIgnoreCase("password")) {
            		str = startTag.toString();
                    if(str.contains("value")) {
                    	String atrString = attributes.toString();
                        StringTokenizer strAtr = new StringTokenizer(atrString);
                        String strtag = "<input ";
                        while(strAtr.hasMoreTokens()) {
                        	String strA = strAtr.nextToken();
                            if(!strA.contains("value")) {
                            	strtag = strtag+strA+" ";
                            }
                        }
                        strtag=strtag+"value=\""+random+"\"/>";
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
                            String replace = " value=\""+random+"\"/>";
                            p = Pattern.compile(pattern);
                            m = p.matcher(tempStr[last]);
                            tempStr[last]=m.replaceFirst(replace);
                            temp="";
                            for(int j=0;j<tempStr.length;j++)
                            	temp = temp+tempStr[j]+" ";
                        }
                        else {
                        	tempStr = str.split(" ");
                            int last = tempStr.length-1;
                            pattern = ">";  //either > or /> ..not sure
                            String replace = " value=\""+random+"\"/>";
                            p = Pattern.compile(pattern);
                            m = p.matcher(tempStr[last]);
                            tempStr[last]=m.replaceFirst(replace);
                            temp="";
                            for(int j=0;j<tempStr.length;j++)
                            	temp = temp+tempStr[j]+" ";
                        }
                    }
                    System.out.println("1: "+startTag);
                    System.out.println("2: "+temp);
                    outputDocument.replace(startTag,temp);
                               
                    try {
                    	//outputDocument.writeTo(new FileWriter("temp.html"));    
                        data+=URLEncoder.encode(attributes.getValue("name"),"UTF-8")+ "," + URLEncoder.encode(random, "UTF-8")+"#";
                    }
                    catch(UnsupportedEncodingException uee) {
                    	System.err.println("Unsupported error");
                    }  
            	}
            	else if(rel.equalsIgnoreCase("radio") && radioSelect==false) {
            		radioSelect=true;
            		str = startTag.toString();
                    if(str.contains("checked")) {
                    	String atrString = attributes.toString();
                        StringTokenizer strAtr = new StringTokenizer(atrString);
                        String strtag = "<input ";
                        while(strAtr.hasMoreTokens()) {
                        	String strA = strAtr.nextToken();
                            if(!strA.contains("")) {
                            	strtag = strtag+strA+" ";
                            }
                        }
                        strtag=strtag+"value=\""+randomizer()+"\"/>";
                        temp=strtag;
                    	System.out.println("Line: "+rel+" is checked");
                    }
                    else {
                    	pattern = "/>";
                        Pattern p = Pattern.compile(pattern);
                        Matcher m = null;
                        if(p.matcher(str).find()) {
                        	tempStr = str.split(" ");
                            int last = tempStr.length-1;
                            pattern = "/>";  //either > or /> ..not sure
                            String replace = " checked/>";
                            p = Pattern.compile(pattern);
                            m = p.matcher(tempStr[last]);
                            tempStr[last]=m.replaceFirst(replace);
                            temp="";
                            for(int j=0;j<tempStr.length;j++)
                            	temp = temp+tempStr[j]+" ";
                        }
                        else {
                        	tempStr = str.split(" ");
                            int last = tempStr.length-1;
                            pattern = ">";  //either > or /> ..not sure
                            String replace = " checked/>";
                            p = Pattern.compile(pattern);
                            m = p.matcher(tempStr[last]);
                            tempStr[last]=m.replaceFirst(replace);
                            temp="";
                            for(int j=0;j<tempStr.length;j++)
                            	temp = temp+tempStr[j]+" ";
                        }
                    }
                    System.out.println("1: "+startTag);
                    System.out.println("2: "+temp);
                    outputDocument.replace(startTag,temp);
                               
                    try {
                    	outputDocument.writeTo(new FileWriter("temp.html"));    
                        data+=URLEncoder.encode(attributes.getValue("name"),"UTF-8")+ "," + URLEncoder.encode("checked", "UTF-8")+"#";
                    }
                    catch(UnsupportedEncodingException uee) {
                    	System.err.println("Unsupported error");
                    }  
            	}
            	else if(rel.equalsIgnoreCase("checkbox")) {
            		str = startTag.toString();
                    if(str.contains("checked")) {
                    	String atrString = attributes.toString();
                        StringTokenizer strAtr = new StringTokenizer(atrString);
                        String strtag = "<input ";
                        while(strAtr.hasMoreTokens()) {
                        	String strA = strAtr.nextToken();
                            if(!strA.contains("")) {
                            	strtag = strtag+strA+" ";
                            }
                        }
                        strtag=strtag+"value=\""+randomizer()+"\"/>";
                        temp=strtag;
                    	System.out.println("Line: "+rel+" is checked");
                    }
                    else {
                    	pattern = "/>";
                        Pattern p = Pattern.compile(pattern);
                        Matcher m = null;
                        if(p.matcher(str).find()) {
                        	tempStr = str.split(" ");
                            int last = tempStr.length-1;
                            pattern = "/>";  //either > or /> ..not sure
                            String replace = " checked/>";
                            p = Pattern.compile(pattern);
                            m = p.matcher(tempStr[last]);
                            tempStr[last]=m.replaceFirst(replace);
                            temp="";
                            for(int j=0;j<tempStr.length;j++)
                            	temp = temp+tempStr[j]+" ";
                        }
                        else {
                        	tempStr = str.split(" ");
                            int last = tempStr.length-1;
                            pattern = ">";  //either > or /> ..not sure
                            String replace = " checked/>";
                            p = Pattern.compile(pattern);
                            m = p.matcher(tempStr[last]);
                            tempStr[last]=m.replaceFirst(replace);
                            temp="";
                            for(int j=0;j<tempStr.length;j++)
                            	temp = temp+tempStr[j]+" ";
                        }
                    }
                    System.out.println("1: "+startTag);
                    System.out.println("2: "+temp);
                    outputDocument.replace(startTag,temp);
                               
                    try {
                    	outputDocument.writeTo(new FileWriter("temp.html"));    
                        data+=URLEncoder.encode(attributes.getValue("name"),"UTF-8")+ "," + URLEncoder.encode("checked", "UTF-8")+"#";
                    }
                    catch(UnsupportedEncodingException uee) {
                    	System.err.println("Unsupported error");
                    }  
            	}
            	
            }      
            
            var=attr.getValue("action");
            //new FileWriter("temp.html").append(var);
            if(var==null || var.equals("")) {
            	continue;
            }
            if(var.charAt(0)=='/') {
            	var=gurl+var;                        
            }
            else if(!var.contains("http") & !var.contains("https")) {
            	var=gurl+'/'+var;
            }
            new ProcessBuilder("firefox", "temp.html").start();
            sendBack(data);
            System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
            }
    }
                 
    public static void main(String []args) throws IOException,InterruptedException {
    	new ProcessBuilder("firefox", "about:blank").start();
        Thread.sleep(1000);
        String url = args[0];
        if(url.contains("http://")) {}
        else if(url.startsWith("www."))
        	url="http://"+url;
        else {
        	System.out.println("Only http protocol based sites are handled. Wait for future release.");
        	System.exit(0);
        }
        gurl=url;
        System.out.println(gurl);
        Firefuzzer fetcher = new  Firefuzzer(url);
        log( fetcher.getPageContent() );
        parseInput();
    }
}
