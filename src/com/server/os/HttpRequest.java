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
		BufferedReader procReader = null;

		String getLine = bReader.readLine();

		//successful connection
		System.out.println("Got connection from " + my_socket.getInetAddress().getHostAddress() + ", " + my_socket.getPort());
		System.out.println(getLine);
		//need to read in http header lines
		String headerLine = null;
		while ((headerLine = bReader.readLine()).length() != 0) {
			System.out.println(headerLine);
		}

		StringTokenizer tokens = new StringTokenizer(getLine);
		//GET method is next token, so go past
		tokens.nextToken();

		String incomingQuery = tokens.nextToken();

		System.out.println("INCOMING QUERY: " + incomingQuery);
        // Prepend a "." so that file request is within the current directory.

    tokens = new StringTokenizer(incomingQuery, "?");
    System.out.println("Token Counts: " + tokens.countTokens());

  	String fileName = null;
    String querystring = null;

    if(tokens.countTokens() > 1){
        fileName = tokens.nextToken();
        querystring = tokens.nextToken();
    }else{
        fileName = tokens.nextToken();
    }

		fileName = "." + fileName;
		System.out.println("FILENAME: " + fileName);
    System.out.println("QUERYSTRING: " + querystring);

		FileInputStream fis = null;
		boolean fileExists = false;
		boolean cgiRequest = true;

		if(isHTMLCall(fileName)){
			System.out.println("IS HTML CALL");
			cgiRequest = false;
	    try {
	    	fis = new FileInputStream(fileName);
				fileExists = true;
	    }catch(FileNotFoundException e){
	    	System.out.println(e);
	    	fileExists = false;
	    }
		}

		InputStream inprocstream = null;
		Process p = null;

		if(isCgiCall(fileName)){
					//create a process StringBuilder
          System.out.println("Executing ProcessBuilder...");
					ProcessBuilder processBuilder = new ProcessBuilder("./formsubmition.cgi", querystring);
					//Process p = processBuilder.inheritIO().start();
					p = processBuilder.start();

					p.waitFor();

					if(p.getErrorStream().read() != -1){
						System.out.println("ompilation error: "  + p.getErrorStream());
					}
					System.out.println("Done getting result");
					cgiRequest = true;
					//send inpit stream
		}

		//need to check if file exists, if not 404 not found
		String statusLine = null;
		String entityBody = null;
		if (fileExists || isCgiCall(fileName)) {
			System.out.println("IS HTML File: " + fileName);
			statusLine = "HTTP/1.1 200 OK" + CRLF;
		}
		else {
			statusLine = "HTTP/1.0 404 Not Found" + CRLF;
			//if file doesn't exist then 404 page loads
			entityBody = "<HTML>" + "<HEAD><TITLE>Not Found</TITLE></HEAD>" + "<BODY><H1>Not Found</H1></BODY></HTML>";
		}

		//if file doesn't exist then 404 page loads
		//entityBody = "<HTML>" + "<HEAD><TITLE>Not Found</TITLE></HEAD>" + "<BODY><H1>Not Found</H1></BODY></HTML>";

		//write out page
		outStream.writeBytes(statusLine);
		outStream.writeBytes(CRLF);

		//send data if file exists, otherwise just 404 page
		if (fileExists && isHTMLCall(fileName)) {
			System.out.println("html file");
			System.out.println("Sending FIle");
			sendBytes(fis, outStream);
			fis.close();
		}else if(cgiRequest && isCgiCall(fileName)){
			System.out.println("IS CGI CALL");

			if(p.exitValue() == 0){
				procReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String a = procReader.readLine();
				System.out.println("printing result: " + a);
				//get inputstrip
				while((a = procReader.readLine()) != null){
					outStream.writeBytes(a);
				}
			}

			procReader.close();

		}
		else {
				outStream.writeBytes(entityBody);
				System.out.println("code failure");

		}
		System.out.println("FINISHING PROCESS");
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
	private static boolean isCgiCall(String filename){
        if(filename.endsWith(".cgi")){
            return true;
        }
        else{
            return false;
        }
  }

	private static boolean isHTMLCall(String filename){
      if(filename.endsWith(".html")){
            return true;
        }
        else{
            return false;
        }
    }

}
