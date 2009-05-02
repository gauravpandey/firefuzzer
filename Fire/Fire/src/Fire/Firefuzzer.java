/**
 *
 */
package Fire;

import net.htmlparser.jericho.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * @author gaurav & Sumeet
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
        {       HttpClient client = new HttpClient();
        client.getParams().setParameter("http.useragent", "Test Client");
        
        System.out.println("VAR: "+var);
               
        PostMethod method = new PostMethod(var);
        BufferedReader br = null;
        StringTokenizer str = new StringTokenizer(data,"#");
        while(str.hasMoreTokens()) {
                StringTokenizer strr = new StringTokenizer(str.nextToken(),",");
                String attrib = strr.nextToken();
                String value = strr.nextToken();
                method.addParameter(attrib,value);
        }
        client.executeMethod(method);
       
/*      if (!method.getURI().toString().equals(var) ) {
                URI redirectLocation = null;
                try {
                        redirectLocation = new URI( method.getURI().toString(), method.getResponseHeader("location").getValue(),method.getName().toString());
                }
                catch(Exception e) {
                        System.out.println("Exception"+e.getMessage());
                }
                method = new PostMethod(redirectLocation.toString());
                StringTokenizer str1 = new StringTokenizer(data,"#");
                while(str.hasMoreTokens()) {
                        StringTokenizer strr = new StringTokenizer(str1.nextToken(),",");
                        String attrib = strr.nextToken();
                        String value = strr.nextToken();
                        method.addParameter(attrib,value);
                }
                client.executeMethod(method);
        }
        method.setFollowRedirects(true);*/
       
        try{
                int returnCode = client.executeMethod(method);

            if(returnCode == HttpStatus.SC_NOT_IMPLEMENTED) {
                System.err.println("The Post method is not implemented by this URI");
                method.getResponseBodyAsString();
            }
            else {
                br = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
                String readLine;
                PrintWriter pw = new PrintWriter("temp.html");
                while(((readLine = br.readLine()) != null)) {
                        pw.println(readLine);
                        pw.flush();
                }
                pw.println("<html><body>HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH</body></html>");
            }
        }
        catch (Exception e) {
            System.err.println(e);
        }
        finally {
                new ProcessBuilder("C:\\Program Files\\Mozilla Firefox\\firefox", "temp.html").start();
            method.releaseConnection();
   
            if(br != null)
                try {
                        br.close();
                }
                catch (Exception fe) {}
        }
        }
       
        private static String randomizer(int rand,String max_len) {
        	if(rand==0)
        	{
        		
                 String token1 = "sumeet jindal";
                 return token1;
        	}
        	else
        	{
                String str1=new  String("QAa0bcLdUK2eHfJgTP8XhiFj61DOklNm9nBoI5pGqYVrs3CtSuMZvwWx4yE7zR");
                StringBuffer sb=new StringBuffer();
                Random r = new Random();
                int te=0;
                for(int i=1;i<=5000;i++){
                        te=r.nextInt(62);
                        sb.append(str1.charAt(te));
                }
                String token1 = sb.toString();
                return token1;
        	}
        }
       
        private static void parseInput() throws IOException{
        	for(int i=0;i<2;i++)
        	{
        		
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
               
                for(StartTag sj:branches) {
                        Attributes attr=sj.getAttributes();
                        String data="";
                        List<StartTag> segments = sj.getElement().getAllStartTags(HTMLElementName.INPUT);
                                       
                        String str = "",pattern = "",temp = "";
                        String[] tempStr = null;
                        for (StartTag startTag : segments) {
                                str = "";
                                temp="";
                               
                            Attributes attributes=startTag.getAttributes();
                           
                            String rel=attributes.getValue("type");
                            if(rel==null)
                                continue;
                            if(rel.equalsIgnoreCase("text") |/* rel.equalsIgnoreCase("hidden") |*/ rel.equalsIgnoreCase("password")) {
                               String mx_len=attributes.getValue("maxLength");
                               if(mx_len==null)
                               {mx_len="10";}
                               //System.out.println(mx_len);
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
                                        strtag=strtag+"value=\""+randomizer(i,mx_len)+"\"/>";//randomizer()
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
                                                        String replace = " value=\""+randomizer(i,mx_len)+"\"/>";//randomizer()
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
                                                        String replace = " value=\""+randomizer(i,mx_len)+"\"/>";//randomizer()
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
                                        data+=URLEncoder.encode(attributes.getValue("name"),"UTF-8")+ "," + URLEncoder.encode(randomizer(i,mx_len), "UTF-8")+"#";
                                    }
                                    catch(UnsupportedEncodingException uee)
                                    {
                                        System.err.println("Unsupported error");
                                    }  
                            }
                            
                        }      
                       
                        var=attr.getValue("action");
                        
                        if(var==null || var.equals(""))
                        { continue;}

                        if(var.charAt(0)=='/') {
                                var=gurl+var;                        
            }
            else if(!var.contains("http") & !var.contains("https")) {
                var=gurl+'/'+var;
            }

                    
                        sendBack(data);
                    System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                }
        	}
        }
                 
        public static void main(String []args) throws IOException,InterruptedException {
                new ProcessBuilder("C:\\Program Files\\Mozilla Firefox\\firefox", "about:blank").start();
                Thread.sleep(1000);
                String url = args[0];
                /*if(!url.contains("https://"))
                        url="http://"+url;*/
                gurl=url;
                System.out.println(gurl);
                Firefuzzer fetcher = new  Firefuzzer(url);
                log( fetcher.getPageContent() );
               
                parseInput();
        }
}