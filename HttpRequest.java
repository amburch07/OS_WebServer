//imports
import java.io.*;
import java.net.*;
import java.util.*;

/*
Class for web socket to call to start HTTP request
*/
final class HttpRequest implements Runnable {
	//end of line characters
	final static String CRLF = "\r\n";

	//need a socket
	Socket my_socket;

	public HttpRequest(Socket my_socket) throws Exception {
		this.my_socket = my_socket;
	}

	//try to run process or throw error
	public void run() {
		try {
			processRequest();
		} 
		//catch any exceptions and print
		catch (Exception e) {
			System.out.println(e);
		}
	}

	//run process
	private void processRequest() throws Exception {
		//start up input/output streams and buffered reader to pass info
		InputStream inStream = my_socket.getInputStream();
		DataOutputStream outStream = new DataOutputStream(my_socket.getOutputStream());
		BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));

		String getLine = bReader.readLine();

		StringTokenizer tokens = new StringTokenizer(getLine);
		//GET method is next token, so go past
		tokens.nextToken();
		String fileName = tokens.nextToken();

		//figure out file name and if file exists
		fileName = "." + fileName;
		FileInputStream finStream = null;
		boolean fileExists = true;
		try {
			finStream = new FileInputStream(fileName);
		} 
		catch (FileNotFoundException e) {
			fileExists = false;
		}

		//successful connection
		System.out.println("Got connection from " + my_socket.getInetAddress().getHostAddress() + ", " + my_socket.getPort());
		System.out.println(getLine);
		//need to read in http header lines
		String headerLine = null;
		while ((headerLine = bReader.readLine()).length() != 0) {
			System.out.println(headerLine);
		}
		//need to check if file exists, if not 404 not found
		String statusLine = null;
		String entityBody = null;
		if (fileExists) {
			statusLine = "HTTP/1.1 200 OK" + CRLF;
		}
		else {
			statusLine = "HTTP/1.0 404 Not Found" + CRLF;
			//if file doesn't exist then 404 page loads
			entityBody = "<HTML>" + "<HEAD><TITLE>Not Found</TITLE></HEAD>" + "<BODY><H1>Not Found</H1></BODY></HTML>";
		}
		//write out page
		outStream.writeBytes(statusLine);
		outStream.writeBytes(CRLF);

		//send data if file exists, otherwise just 404 page
		if (fileExists) {
			sendBytes(finStream, outStream);
			finStream.close();
		}
		else {
			outStream.writeBytes(entityBody);
		}

		//close everything
		outStream.close();
		bReader.close();
		my_socket.close();
	}

	//helper method to loop through everything needing to be sent
	private static void sendBytes(FileInputStream finStream, OutputStream outStream) throws Exception {
		byte[] buffer = new byte[1024];
		int bytes = 0;

		while ((bytes = finStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, bytes);
		}
	}
}
