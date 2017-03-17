
public class Main {

	public static void main(String[] args){
		MultiThreadedServer server = new MultiThreadedServer(9000);
		new Thread(server).start();
	
		while (!server.isStopped){
			
		}
		System.out.println("Stopping Server");
		server.stop();
	}
}
