
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
* CGI PROGRAM, create HTML page from the from data
* Prints the HTML tags,
* Pipe the output stream of the Program to the input stream of the server upon program exit. 
*/
public class FormSubmition{
    private String firstName = null;
    private String lastName = null;
    private String email = null;
    private String sex = null;
    public FormSubmition(){
        firstName = null;
        lastName = null;
        email = null;
        sex = null;
    }

    public boolean setParameters(String s){
        Pattern pattern = Pattern.compile("/[^&=]+=[^&=]+");
        Matcher matcher = pattern.matcher(s);
        System.out.println("Does Match: " + matcher.matches());
        if(matcher.matches()){
            System.out.println(matcher.group(0)); // whole matched expression
            System.out.println(matcher.group(1)); // first expression from round brackets (Testing)
            System.out.println(matcher.group(2)); // second one (123)
            System.out.println(matcher.group(3)); // third one (Testing)
            return true;
        }else {
            return false;
        }
    }

    public static void main(String argv[]) throws Exception {
        System.out.println( "<HTML>\n");
        System.out.println( "<HEAD>\n");
        System.out.println( "<TITLE>A simple cgi script</TITLE>\n");
        System.out.println( "</HEAD>\n");
        System.out.println( "<BODY bgcolor=\"#4444aa\"text=\"#bbffbb\">\n");

        System.out.println( "<H1>This is a simple cgi script written in JAVA</H1>\n");
        System.out.println( "<HR>\n");
        System.out.println( "<H1>Form Submitted, Thank you!!</H1>\n");
        System.out.println( "<table><tr><th>Firstname</th>");
        System.out.println( "<td>" + System.getProperty("cgi.fname") + "</td></tr>");
        System.out.println( "<tr><th>Lastname</th>");
        System.out.println( "<td>" + System.getProperty("cgi.lname") + "</td></tr>");
        System.out.println( "<tr><th>Email</th>");
        System.out.println( "<td>" + System.getProperty("cgi.email") + "</td></tr>");
        System.out.println( "<tr><th>Gender</th>");
        System.out.println( "<td>" + System.getProperty("cgi.gender") + "</td></tr></table>");
        System.out.println( "<HR>\n");
        System.out.println( "<img src=\"https://www.qegsblackburn.com/wp-content/uploads/Schools-out-for-Summer.jpg\" style=\"width:350px;height:150px;\">");
        System.out.println("</BODY>\n");
        System.out.println("</HTML>\n");
        System.exit(0);
    }

}
