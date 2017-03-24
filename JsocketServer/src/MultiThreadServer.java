import java.net.ServerSocket;
import java.net.Socket;

public class MultiThreadServer{
   Socket csocket;
   int i;
   MultiThreadServer(Socket csocket, int i) {
      this.csocket = csocket;
      this.i = i;
   }
   public static void main(String args[]) throws Exception { 
	  int port = 8080;
	  if (!(args.length == 0)){
		  port = Integer.parseInt(args[0]);
	  }
      ServerSocket ssock = new ServerSocket(port);
      System.out.println("Listening");
      while (true) {
         Socket sock = ssock.accept();	
         System.out.println("New request");
         new Thread(new ClientThread(sock)).start();
      }
   }
}