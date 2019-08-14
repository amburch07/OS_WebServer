//imports
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;
import java.lang.*;

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
		StringBuilder returnhtml = null;

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

		if(Objects.equals(incomingQuery, "/")){
			fileName = "./welcome.html";
		}else{
			System.out.println("Didnot match with /");
			fileName = "." + fileName;
		}

		System.out.println("FILENAME: " + fileName);
    System.out.println("QUERYSTRING: " + querystring);

		//Create File Streamer
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

					tokens = new StringTokenizer(querystring, "&");
					//parsing query string
					StringTokenizer pname = new StringTokenizer(tokens.nextToken(), "=");
					StringTokenizer lname = new StringTokenizer(tokens.nextToken(), "=");
					StringTokenizer email = new StringTokenizer(tokens.nextToken(), "=");
					StringTokenizer gender = new StringTokenizer(tokens.nextToken(), "=");

					pname.nextToken();
					lname.nextToken();
					email.nextToken();
					gender.nextToken();
					if(pname.hasMoreTokens() && lname.hasMoreTokens() && email.hasMoreTokens() && gender.hasMoreTokens()){
						System.out.println("Executing ProcessBuilder...");
					String firstname = pname.nextToken();
					String lastname = lname.nextToken();
					String email_acc = email.nextToken();
					String pgender = gender.nextToken();

						//create process and run 
						String[] command = {"./formsubmition.cgi", querystring, firstname, lastname, email_acc,pgender};
						ProcessBuilder processBuilder = new ProcessBuilder(command);
						//Process p = processBuilder.inheritIO().start();
						p = processBuilder.start();

						p.waitFor();

						if(p.getErrorStream().read() != -1){
							System.out.println("compilation error: "  + p.getErrorStream());
						}
						System.out.println("Done getting result");
						cgiRequest = true;
					}
					else{
						cgiRequest = false;
					}

					//send inpit stream
		}

		//need to check if file exists, if not 404 not found
		String statusLine = null;
		String entityBody = null;
		if (fileExists || isCgiCall(fileName)) {
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
		if (fileExists && isHTMLCall(fileName)) {
			System.out.println("html file");
			System.out.println("Sending FIle");
			sendBytes(fis, outStream);
			fis.close();
		}else if(cgiRequest && isCgiCall(fileName)){

			//is a cgi request
			//get input stream from the process and send it to the Socket
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
		}else if(!cgiRequest && isCgiCall(fileName)){
			System.out.println("Invalid form");
			fis = new FileInputStream("./invalidinput.html");
			sendBytes(fis, outStream);
			fis.close();
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
