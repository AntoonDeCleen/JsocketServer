import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ClientThread implements Runnable{
	private Socket csocket;
	private BufferedReader in;
	private PrintWriter out;
	public boolean ended = false;
	private static final String basePath = "C:\\Users\\Beheerder\\Desktop\\ServerResources";
	private Command command;
	private boolean firstLine = true;
	private boolean badCommand = false;
	private FileInputStream fin;
	private BufferedReader reader;
	private boolean headerRead = false;
	private boolean headerSent = false;
	private String firstLineS;
	private boolean commandExecuted = false;
	
	public ClientThread(Socket csocket){
		this.csocket = csocket;
		
	}
	public void run() {
	      try {
	    	  in = new BufferedReader(
	                  new InputStreamReader(csocket.getInputStream()));

		    // BufferedWriter out = new BufferedWriter(new OutputStreamWriter(csocket.getOutputStream()));
	         PrintWriter pout =
	        	        new PrintWriter(csocket.getOutputStream(), true);
	         String line;
	         while(!headerRead){
	        	 line = in.readLine();
	        	 //System.out.println("Client says: "+line);
	        	 if (firstLine){
	        		 firstLineS = line;
	        		 firstLine = false;
	        	 }
	        	 if (line.length() == 0){
	        		 //System.out.println("Header successfully read");
	        		 headerRead = true;
	        	 }
	        	 
	         }

	         while (!commandExecuted){
	        	 //System.out.println("reading request");

    			 String resource;
    			 if (firstLineS.startsWith("GET")){
    				 //System.out.println("Get command received");
    				 
    				 command = command.GET;
    				 resource = firstLineS.replace("GET ", "");
    				 resource = resource.replace(" HTTP/1.1", "");
    				 if (resource.equals("/")){
    					 resource = "/index.html";
    				 }
    				 System.out.println("resource: "+ resource);
    				 if (!resource.contains(".html")){
    					 pout.println("HTTP/1.1 404 NOT FOUND ");
    					 csocket.close();
    					 break;
    				 }
    				 
    				 //System.out.println("resource "+resource);
    				 File file = new File(basePath+resource);
    				 fin = new FileInputStream(file);
    				 reader = new BufferedReader(new InputStreamReader(fin, "utf-8"));
    				 
    				 //Send header 
    				 TimeUnit.SECONDS.sleep(1);
    				 headerSent = true;
    				 pout.println("HTTP/1.1 200 OK	");
    				 pout.println("Date: "+new Date().toString());
    				 pout.println("Content-Type: text/html");
    				 pout.println("Content-Length: "+file.length());
    				 pout.println("\r\n");
    				 
    				 
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
    				 
        				 
        			 }else if(firstLineS.startsWith("HEAD")){
        				 command = command.HEAD;
        				 
        				 //Send header 
        				 //pout.println("HTTP/1.1 200 OK	");
        				 pout.println("Date: "+new Date().toString());
        				 pout.println("Content-Type: text/html");
        				 pout.println("Content-Length: "+file.length());
        				 pout.println("\r\n");
        				 
        			 }else if(firstLineS.startsWith("PUT")){
        				 command = command.PUT;
        				 
        			 }else if(firstLineS.startsWith("POST")){
        				 command = command.POST;
        				 
        			 }else{
        				 
        			 }
	        		 
	        	
	        	 }}}
	         
//	         
	   
	         
	         //String httpResponse = "HTTP/1.1 200 OK\r\n\r\n"; 
	         //csocket.getOutputStream().write(httpResponse.getBytes("UTF-8"));
	         
	         
	         // pstream.close();
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


