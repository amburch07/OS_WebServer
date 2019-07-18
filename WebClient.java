import java.net.*;
import java.io.*;

public class WebClient {
	final static String CRLF = "\r\n";

	public static void main(String args[]) throws IOException {
		// Open your connection to a server, at port 1234
		Socket socket = new Socket("localhost",1234);
		
		// Get an input file handle from the socket and read the input
		InputStream is = socket.getInputStream();
		DataOutputStream os = new DataOutputStream(socket.getOutputStream());
		
		// Construct the request message.		
		String requestLine = "GET /HelloWorld.html HTTP/1.1" + CRLF;
		String headerLine = "User-Agent: Bot" + CRLF;

		// Send the request line.
		os.writeBytes(requestLine);

		// Send the header line.
		os.writeBytes(headerLine);

		// Send a blank line to indicate the end of the header lines.
		os.writeBytes(CRLF);
		
		// Set up input stream filters.
		String response = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		while ((response = br.readLine()) != null) {
			System.out.println(response);
		}
		
		is.close();
		os.close();
		socket.close();
	}
}