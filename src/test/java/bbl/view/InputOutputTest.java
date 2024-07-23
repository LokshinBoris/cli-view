package bbl.view;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import telran.view.*;
record User(String userName, String password, LocalDate dateLastLogin, String phoneNumber, int numberOfLogins) {} 
class InputOutputTest
{
    InputOutput io=new SystemInputOutput();
    @Test 
    void readObjectTest()
    {
    	User user=io.readObject("Enter user in format <username>#<password>#<dateLastLogin>#<phone number>#<number of logins",
    							"Wrong user format", str->
    							{
    								String[] tokens = str.split("#");
    								return new User(tokens[0],tokens[1],LocalDate.parse(tokens[2]), tokens[3], Integer.parseInt(tokens[4]));
    							}
    	);
    	io.writeLine(user);
    }
    @Test
    void readIntTest()
    {
    	Integer a=io.readInt("Input integer", "Wrong integer");
    	io.writeLine("Good - " + a.toString());
    }
    
    @Test
    void readNumberRangeTest()
    {
    	double min=-5.4;
    	double max =9.0;
    	String prompt=String.format("Input double [%f %f[:", min,max);
    	Double a=io.readNumberRange(prompt, "Double is not into range",min, max);
    	io.writeLine(" " + a.toString());
    }
    
    @Test
    void readStringPredicateTest()
    {
    	String myStr=io.readStringPredicate("Input string less than 4 symbols", 
    			                            "string have more or equal than 4 symbols",
    			                            str->str.length()<4);
    	io.writeLine("Input string " + myStr);
    }
    
    @Test
    void readStringOptionsTest()
    {
    	String one="One";
    	String two="Two";
    	String three="Three";
    	String prompt=String.format("Input:\n %s\n %s\n %s", one,two,three);
    	HashSet<String> options= new HashSet<String>();
    	options.add(one);
    	options.add(two);
    	options.add(three);
    	String myStr=io.readStringOptions(prompt, "String", options);
    	io.writeLine(myStr);
    }
    
    @Test
    void readLocalDateTest()
    {
    	LocalDate ld=io.readIsoDate("Input date", "string is not date");
    	io.writeLine(ld.toString());
    }
    @Test
    void readIsoDateRangeTest()
    {
    	LocalDate minDate=LocalDate.parse("1962-04-12");
    	LocalDate maxDate=LocalDate.now();
    	String prompt = String.format("Input date between %s and %s", minDate.toString(),maxDate.toString());
    	LocalDate date=io.readIsoDateRange(prompt, "error", minDate, maxDate);
    	io.writeLine(date.toString());    	
    }
    
	@Test
	void readUserByFields()
	{
		
		// create User object from separate fields and display out
		// username at least 6 ASCII letters - first Capital, others Lower case
		// password at least 8 symbols, at least one capital letter, at least one lower case letter,
		// at least one digit, at least one symbol from "#$*&%"
		// phone number  - Israel mobile phone
		// dateLastLogin not after current date
		// number of logins any positive number
		User user=io.readObject("Enter user in format <username>#<password>#<dateLastLogin>#<phone number>#<number of logins",
				"Wrong user format", str->
						{
						String[] tokens = str.split("#");
						if(!isUserName(tokens[0])) tokens[0]=io.readObject("Wrong login. Input login", " ", s->
							{
								if (!isUserName(s))
								{
									throw new RuntimeException(" ");
								}
								return s;	
							}
																		  );
						if(!isPassword(tokens[1])) tokens[1]=io.readObject("Wrong password. Input pasword", " ", s->
							{
								if (!isPassword(s))
								{
									throw new RuntimeException(" ");
								}
								return s;	
							} 
																	);
						LocalDate date=LocalDate.parse(tokens[2]);
						if(date.compareTo(LocalDate.now())<=0)
							date=io.readIsoDateRange("Wrong date Input date", " ", LocalDate.of(1, 1, 1), LocalDate.now());
						
						if(!isPhoneNumber(tokens[3])) tokens[3]=io.readObject("Wrong phone. Input phone", " ", s->
						{
								if (!isPhoneNumber(s))
								{
									throw new RuntimeException(" ");
								}
								return s;	
						} 
						
																);	
						int numberOfLogins=Integer.parseInt(tokens[4]);
						if( numberOfLogins<=0)
							{
								
								String stmp=io.readStringPredicate("Wrong number of logins. Input numbers of logins", " ",
																	   s->(Integer.valueOf(s)>0)
																 	 );
								numberOfLogins=Integer.valueOf(stmp);
							}
						return new User(tokens[0],tokens[1],date, tokens[3], numberOfLogins);
						}
							   );
		io.writeLine(user);
	}

   private boolean isUserName(String str)
   {
	   boolean res=false;
	   if(str!=null && str.matches("([A-Z]{1}[a-z0-9]{1,4})")) res=true;
	   return res;
   }
   
   private boolean isPassword(String str)
   {
	   boolean res=false;
	   if(str!=null && 
		  str.length()<=8 &&        	   
		  isInString(str,"([A-Z]+)") &&
		  isInString(str,"([a-z]+)") &&	  
		  isInString(str,"([0-9]+)") &&
		  isInString(str,"([\\#\\$\\*\\&\\%]+)")) res=true;
	   return res;
   }
   
   private boolean isPhoneNumber(String str)
   {
	   boolean res=false;
	   if(str!=null && str.matches("\\+972-?5\\d-?\\d{3}-?\\d{2}-?\\d{2}|05\\d-?\\d{3}-?\\d{2}-?\\d{2}")) res=true;
	   return res;
   }
   
   
   private boolean isInString(String str, String regex)
   {
	   Pattern pattern = Pattern.compile(regex);
	   Matcher matcher = pattern.matcher (str);
	  
	   return matcher.find();
   }
@Test
   void isUserNameTest()
   {
	   assertTrue(isUserName("Bbl64"));
	   assertFalse(isUserName("bbl64"));
	   assertFalse(isUserName("4bl64"));
	   assertFalse(isUserName("Bbl1964"));
	   assertFalse(isUserName(""));
   }
   
   @Test
   void isPasswordTest()
   {
	   assertTrue(isPassword("abc5*ZZ"));
	   assertFalse(isPassword("abc5_ZZ"));
	   assertFalse(isPassword("abc%*ZZ"));
	   assertFalse(isPassword("ABC5*ZZ"));
	   assertFalse(isPassword("abc5*#$"));
	   assertFalse(isPassword("abc5*ZZ$&"));
	   assertFalse(isPassword(""));
   }
   
 }
