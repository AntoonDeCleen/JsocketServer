import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.Buffer;

public class ClientThread implements Runnable{
	int i;
	Socket csocket;
	BufferedReader in;
	PrintWriter out;
	boolean ended = false;
	
	public ClientThread(Socket csocket, int i){
		this.csocket = csocket;
		this.i = i;
		
	}
	public void run() {
	      try {
	    	  in = new BufferedReader(
	                  new InputStreamReader(csocket.getInputStream()));
	               out = new PrintWriter(
	                  new OutputStreamWriter(csocket.getOutputStream()));
	         PrintStream pstream = new PrintStream(csocket.getOutputStream());
	         System.out.println("Client says: "+ in.readLine());
/*	         for (int i = 100; i >= 0; i--) {
	             pstream.println(i + " bottles of beer on the wall");
	          }*/
	         while (!ended){
	        	 if(in.ready()){
	        		 String s = in.readLine();
	        		 System.out.println("Client says: "+s);
	        		 pstream.println(s);
	        	 }else{break;}
	         }
	         pstream.close();
	         csocket.close();
	      } catch (IOException e) {
	         System.out.println(e);
	      }
	   }
}


