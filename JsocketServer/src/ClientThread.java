import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ClientThread implements Runnable{
	private Socket csocket;
	private BufferedReader in;
	private PrintWriter out;
	public boolean ended = false;
	//private static final String basePath = "C:\\Users\\Beheerder\\Desktop\\ServerResources";
	private static final String basePath = "/home/r0464173/Desktop/CNServerresources/";
	private boolean firstLine = true;
	private boolean badCommand = false;
	private FileInputStream fin;
	private BufferedReader reader;
	private boolean headerRead = false;
	private boolean headerSent = false;
	private String firstLineS;
	private boolean commandExecuted = false;
	private boolean hostFound = false;
	
	public ClientThread(Socket csocket){
		this.csocket = csocket;
		
	}
	public void run() {
	      try {
	    	  in = new BufferedReader(
	                  new InputStreamReader(csocket.getInputStream()));

		    PrintWriter pout =
	        	        new PrintWriter(csocket.getOutputStream(), true);
	         String line;
	         while(!headerRead){
	        	 line = in.readLine();
	        	 System.out.println("Client says: "+line);
	        	 if (firstLine){
	        		 firstLineS = line;
	        		 firstLine = false;
	        	 }
	        	 if (line.length() == 0){
	        		 System.out.println("Header successfully read");
	        		 headerRead = true;
	        	 }
	        	 else if (line.contains("Host: ")){
	        		 //System.out.println("host found");
	        		 hostFound = true;
	        	 }
	         } 
	       //  System.out.println("firstline: "+firstLineS);
	         if (hostFound == false &&headerRead == true){
	        	 pout.println("400 Bad request");
	        	 System.out.println("Received a bad request");
	        	 csocket.close();
	        	 return;
	         }
	         
	         //System.out.println("Firstline: "+firstLineS);
	       
	         while(!commandExecuted){
	        	 System.out.println("reading request");

    			 String resource;
    			 if (firstLineS.startsWith("GET")){
    				 System.out.println("Get command received");
    				 
    				 resource = firstLineS.replace("GET ", "");
    				 resource = resource.replace(" HTTP/1.1", "");
    				 if (resource.equals("/")){
    					 resource = "/index.html";
    				 }
    				 System.out.println("resource: "+ resource);
    				 if (!resource.contains(".html")){
    					 pout.println("404 NOT FOUND");
    					 csocket.close();
    					 return;
    				 }
    				 
    				 //System.out.println("resource "+resource);
    				 File file = new File(basePath+resource);
    				 if (!file.exists()){
    					 pout.println("404 NOT FOUND");
    					 csocket.close();
    					 return;
    				 }
    				 
    				 fin = new FileInputStream(file);
    				 reader = new BufferedReader(new InputStreamReader(fin, "utf-8"));
    				 
    				 //Send header 
    				 TimeUnit.SECONDS.sleep(1);
    				 
    				 pout.println("HTTP/1.1 200 OK	");
    				 pout.println("Date: "+new Date().toString());
    				 pout.println("Content-Type: text/html");
    				 pout.println("Content-Length: "+file.length());
    				 pout.println("\r\n");
    				 headerSent = true;
    				 
    				 //without the second delay the connection sometimes does not last
    				 
    				 
    				 
    				 //send body
    				 long sentLength= 1;
    				 long totalLength = file.length();
    				 //System.out.println("totalLength: "+totalLength);
    				 String fileLine;
    				 if (headerSent){
        				 while(sentLength < totalLength){
        					 
        					 fileLine = reader.readLine();
        					 
        					 if (fileLine == null){
        						 System.out.println("ending filetransfer");
        						 pout.println("\r\n");
        						 commandExecuted = true;
        						 pout.close();
        						 in.close();
        						 csocket.close();
        						 Thread.currentThread().interrupt();
        						 return;
        					 }
        					 //System.out.println("filelinelength: "+fileLine.length());
        					 sentLength += fileLine.length();
        					 //System.out.println(sentLength);
        					 //System.out.println(fileLine);
        					 pout.println(fileLine);
        					 //pout.flush();
    					 
        				 }
        				 System.out.println("ending filetransfer");
						 pout.println("\r\n");
						 commandExecuted = true;
						 pout.close();
						 in.close();
						 csocket.close();
						 Thread.currentThread().interrupt();
						 return;
    				 }
        				 
        			 }else if(firstLineS.startsWith("HEAD")){
        				 System.out.println("Head command received");
        
        				 resource = firstLineS.replace("HEAD ", "");
        				 resource = resource.replace(" HTTP/1.1", "");
        				 if (resource.equals("/")){
        					 resource = "/index.html";
        				 }
        				 System.out.println("resource: "+ resource);
        				 if (!resource.contains(".html")){
        					 pout.println("404 NOT FOUND");
        					 csocket.close();
        					 return;
        				 }
        				 
        				 System.out.println(basePath+resource);
        				 File file = new File(basePath+resource);
        				 if (!file.exists()){
        					 pout.println("404 NOT FOUND");
        					 System.out.println("file was not found");
        					 csocket.close();
        					 return;
        				 }
        				 fin = new FileInputStream(file);
        				 reader = new BufferedReader(new InputStreamReader(fin, "utf-8"));
        				 
        				 //Send header 
        			
        				 
        				 pout.println("HTTP/1.1 200 OK	");
        				 pout.println("Date: "+new Date().toString());
        				 pout.println("Content-Type: text/html");
        				 pout.println("Content-Length: "+file.length());
        				 pout.println("\r\n");
        				 headerSent = true;
        				 
						 pout.println("\r\n");
						 commandExecuted = true;
						 pout.close();
						 in.close();
						 csocket.close();
						 Thread.currentThread().interrupt();
						 return;
        				 
        			 }else if(firstLineS.startsWith("PUT")){
        				 System.out.println("Put command received");
        				 
        				 resource = firstLineS.replace("PUT ", "");
        				 resource = resource.replace(" HTTP/1.1", "");
        				 if (resource.equals("/")){
        					 resource = "/dump.html";
        				 }
        				 System.out.println("resource: "+ resource);
        				 if (!resource.contains(".html")){
        					 pout.println("404 NOT FOUND ");
        					 csocket.close();
        					 return;
        				 }
        				 
        				 File file = new File(basePath+resource);
        				 if (!file.exists()){
        					 pout.println("404 NOT FOUND");
        					 csocket.close();
        					 return;
        				 }
        				 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
        				          new FileOutputStream(basePath+resource), "utf-8"));
        				 //reads the empty line between header and body
        				 in.readLine();
        				 
        				 //reads the actual data
        				 String postline;
        				 while(!commandExecuted){
        					 postline = in.readLine();
        					 System.out.println("this: "+ postline);
        					 writer.write(postline.toString());
        					 writer.flush();
        					 writer.newLine();
        					 if(!in.ready()){
        						 break;
        					 }
        					 
        				 }
        				 
        				 
        				 pout.println("HTTP/1.1 200 OK	");
        				 pout.println("PUT succesfully received and written at "+resource);
        				 commandExecuted =true;
        				 in.close();
        				 pout.close();
        				 csocket.close();
        				 
        				 
        			 }else if(firstLineS.startsWith("POST")){
        				 System.out.println("Post command received");

           				 resource = firstLineS.replace("POST ", "");
        				 resource = resource.replace(" HTTP/1.1", "");
        				 if (resource.equals("/")){
        					 resource = "/dump.html";
        				 }
        				 System.out.println("resource: "+ resource);
        				 if (!resource.contains(".html")){
        					 pout.println("404 NOT FOUND ");
        					 csocket.close();
        					 return;
        				 }
        				 
        				 File file = new File(basePath+resource);
        				 if (!file.exists()){
        					 pout.println("404 NOT FOUND");
        					 csocket.close();
        					 return;
        				 }
        				 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
        				          new FileOutputStream(basePath+resource), "utf-8"));
        				 //reads the empty line between header and body
        				 in.readLine();
        				 
        				 //reads the actual data
        				 String postline;
        				 while(!commandExecuted){
        					 postline = in.readLine();
        					 System.out.println("this: "+ postline);
        					 writer.write(postline.toString());
        					 writer.flush();
        					 writer.newLine();
        					 if(!in.ready()){
        						 break;
        					 }
        					 
        				 }
        				 
        				 
        				 pout.println("HTTP/1.1 200 OK	");
        				 pout.println("Post succesfully received and written at "+resource);
        				 commandExecuted =true;
        				 csocket.close();
        				 
        				 
        			 }else{
        				 pout.println("504 Method Not Implemented");
        				 System.out.println("Method not implemented");
        			 }
	        		 
	        	
	        	 }}
	        
	       catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{  try {
			csocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}}
	
	}
	
	public void end(){
		ended = true;
	}
}


