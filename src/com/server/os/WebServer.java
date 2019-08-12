//java imports
import java.net.*;

/*
Creates the web server and main thread
Each new connection gets it's own thread
*/
public final class WebServer {
	public static void main(String argv[]) throws Exception {
		//socket in port 5050
		int port = 8000;

		@SuppressWarnings("resource")
		ServerSocket my_socket = new ServerSocket(port);
		//show server is up and running & wants to add connects
		System.out.println("Waiting for connections...");

		//infinite loop to wait for connections
		while (true) {
			//finds a connection
			Socket my_connection = my_socket.accept();
			//creates a new http request for the connection
			HttpRequest my_request = new HttpRequest(my_connection);
			//makes a slave thread from the master
			Thread my_thread = new Thread(my_request);
			my_thread.start();
		}
	}
}
