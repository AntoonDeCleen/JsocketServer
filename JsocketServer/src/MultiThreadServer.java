import java.io.IOException;
import java.io.PrintStream;
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
      ServerSocket ssock = new ServerSocket(8080);
      System.out.println("Listening");
      while (true) {
         Socket sock = ssock.accept();	
         System.out.println("New connection");
         new Thread(new ClientThread(sock)).start();
      }
   }
   public void run() {
      
   }
}