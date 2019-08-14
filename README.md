# OS_WebServer
Basic web server written in Java, with multi-threading
Supports HTTP/1.1

The server is able to serve git, html, and jpeg files.

The CGI Program serves the requested html page,the web server reads the generated outpur of CGI program and send it to the browser

Project Presentation Available at:

https://docs.google.com/presentation/d/1raardJeNcfpN5quwxElvTzGf-LwfR8WF3lI2PjnFBbM/edit?usp=sharing

## To Run
Start Server
- Compile javac HttpRequest.java
- Run java HttpRequest

On Browser
- Find your IP address (ex. 10.220.81.21)/localhost
- In a browser of your choice, go to: http://<myipaddress>:8000/
- Follow the link to open form
- Refresh browser page
- Input information and submit form
- Result Page Appears

## Project Components
- WebServer.java creates the web server and creates client threads
- HttpRequest.java is called by WebServer, and gives a client thread the web page
- FormSubmition.java process the form
- formsubmition.cgi is a shell script that runs FormSubmition in new PROCESS
