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
import net.htmlparser.jericho.OutputDocument;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;

public class SQLInjection {
	private static int countForms = 0;
    private static int countInputs = 0;
    private static String var;
    private static int []arrayBuffer = new int[5]; 
    public static String globalURL;
    public static boolean globalDetailFlag = false;
    
    public SQLInjection() {
    	for(int i=0;i<arrayBuffer.length;i++) {
    		arrayBuffer[i]=0;
    	}
    }
    
	public static void analyzeSQLInjection() {
    	System.out.println("########################################################################################################################");
        System.out.println("<---SQL INJECTION ANALYSIS--->");
    	System.out.println("Total # of Forms: "+countForms);
    	System.out.println("<<-Categorizing the available data on basis of HTTP Status Codes->>");
    	System.out.println("Informational Codes 1xx Series: "+arrayBuffer[0]);
    	System.out.println("Successful Client Interaction related 2xx Series: "+arrayBuffer[1]);
    	System.out.println("Redirection related 3xx Series: "+arrayBuffer[2]);
    	System.out.println("Client Error related 4xx Series: "+arrayBuffer[3]);
    	System.out.println("Server Error related 5xx Series: "+arrayBuffer[4]);
    	System.out.println("########################################################################################################################");
    	System.out.println("########################################################################################################################");
    	System.out.println("For more Information on HTTP Status Code Series, refer the 'HTTP_STATUS_CODE.pdf' in Document folder.");
    	System.out.println("########################################################################################################################");
    }
      
    private static void sendBack(String data) throws MalformedURLException,IOException
    {   HttpClient client = new HttpClient();
        client.getParams().setParameter("http.useragent", "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.10) Gecko/2009042708 Fedora/3.0.10-1.fc10 Firefox/3.0.10");
              
        System.out.println("URL: "+var);
        PostMethod method = new PostMethod(var);
        BufferedReader br = null;
        StringTokenizer str = new StringTokenizer(data,"#");
        countInputs+=str.countTokens();
        while(str.hasMoreTokens()) {
        	StringTokenizer strr = new StringTokenizer(str.nextToken(),",");
            String attrib = strr.nextToken();
            String value = strr.nextToken();
            method.addParameter(attrib,value);
        }
      
        try{
        	int returnCode = client.executeMethod(method);
        	if(globalDetailFlag==true)
        		System.out.println("Status: "+method.getStatusCode());
        	arrayBuffer[(method.getStatusCode()/100)-1]++;
            if(returnCode == HttpStatus.SC_NOT_IMPLEMENTED) {
                System.err.println("The Post method is not implemented by this URI");
                method.getResponseBodyAsString();
            }
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
        	//new ProcessBuilder("firefox", "temp.html").start();
            method.releaseConnection();
  
            if(br != null)
                try {
                	br.close();
                }
                catch (Exception fe) {}
        }
    }
      
    public static void parseInput() throws IOException{
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
        int currentLoop = 0;
        int currentForm = 0;
          
        List<StartTag> branches=source.getAllStartTags(HTMLElementName.FORM);
        countForms = branches.size();
        System.out.println("########################################################################################################################");
        OutputDocument outputDocument=new OutputDocument(source);
        Attributes attr;
        String s="",data = "";
        String str = "",pattern = "",temp = "";
        String[] tempStr = null;
        
        BufferedReader br = new BufferedReader(new FileReader("inject.conf"));
        while((s=br.readLine())!=null) {
        	if(!s.isEmpty()) {
        		currentLoop++;
        for(StartTag sj:branches) {
        	currentForm++;
        	attr=sj.getAttributes();
            data="";
            List<StartTag> segments = sj.getElement().getAllStartTags(HTMLElementName.INPUT);       
            str = "";
            pattern = "";
            temp = "";
            tempStr = null;
          
            for (StartTag startTag : segments) {
                str = "";
                temp="";
                          
                Attributes attributes=startTag.getAttributes();
                String random = s;
                String rel=attributes.getValue("type");
                if(rel==null)
                	continue;
                if(rel.equalsIgnoreCase("text") ||/* rel.equalsIgnoreCase("hidden") || */rel.equalsIgnoreCase("password")) {
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
                            pattern = "/>";
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
                            pattern = ">";
                            String replace = " value=\""+random+"\"/>";
                            p = Pattern.compile(pattern);
                            m = p.matcher(tempStr[last]);
                            tempStr[last]=m.replaceFirst(replace);
                            temp="";
                            for(int j=0;j<tempStr.length;j++)
                                temp = temp+tempStr[j]+" ";
                        }
                    }                              
                    try { 
                        data+=URLEncoder.encode(attributes.getValue("name"),"UTF-8")+ "," + URLEncoder.encode(random, "UTF-8")+"#";
                    }
                    catch(UnsupportedEncodingException uee) {
                        System.err.println("Unsupported error");
                    } 
                }
            }     
            var=attr.getValue("action");
            if(var==null || var.equals("")) {
            	System.err.println("No URL specified in FORM TAG-ACTION field");
                continue;
            }
            if(var.charAt(0)=='/') {
                var=globalURL+var;                       
            }
            else if(!var.contains("http") & !var.contains("https")& !var.contains("www")) {
                int ind=globalURL.lastIndexOf("/");
                int cind=globalURL.lastIndexOf("//");
                String rt;
                if(ind==cind+1) {
                    rt=globalURL;
                }
                else {
                    rt=globalURL.substring(0,ind);
                }
                var=rt+'/'+var;
            }
            if(globalDetailFlag==true) {
            	System.out.println("data: "+data);
            	System.out.println("Current Loop #: "+currentLoop);
            }
            if(globalDetailFlag==false)
            	System.out.println(".");
            sendBack(data);
            if(globalDetailFlag==true)
            	System.out.println("########################################################################################################################");
        }	
        	}
        }
    }
}
