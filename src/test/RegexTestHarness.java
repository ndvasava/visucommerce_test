package test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTestHarness {

    public static void main(String[] args){
        /*Console console = System.console();
        if (console == null) {
            System.err.println("No console.");
            System.exit(1);
        }*/
        //while (true) {

        	//"[A-Za-z0-9._%+-]+@[A-Za-z0-9]+.[A-Za-z]{2,4}"
            
        //}
    	
    	checkEmailAddress();
    }
    
    private static void checkEmailAddress() {
    	Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9]+[.][A-Za-z.]{2,6}");
        Matcher matcher = pattern.matcher("ndvasava@test.co.intt");

        boolean found = false;
        if (matcher.matches()) {
            found = true;
            System.out.println("match found.");
        }
        if(!found){
         	System.out.println("No match found.");
        }
    }
    
    private void checkTelephoneNumber() {
    	
    	Pattern pattern = Pattern.compile("[0-9-()+ ]*{2,20}");
        Matcher matcher = pattern.matcher("0091 (2642) 247-306");

        boolean found = false;
        if (matcher.matches()) {
            found = true;
            System.out.println("match found.");
        }
        if(!found){
         	System.out.println("No match found.");
        }
    }
}
