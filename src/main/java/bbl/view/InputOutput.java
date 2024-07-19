package bbl.view;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;

public interface InputOutput
{
	String readString(String prompt);
	void writeString(String str);
	default void writeLine(Object obj)
	{
		writeString(obj.toString()+"\n");
	}
	default <T>T readObject(String prompt, String errorPrompt,Function <String,T> mapper)
	{
		T res=null;
		boolean running=false;
		do
		{
			String str=readString(prompt);
			running=false;
			try
			{
				res=mapper.apply(str);
			}
			catch (RuntimeException e)
			{
				writeLine(errorPrompt+" "+e.getMessage());
				running=true;
			}
		}
		while(running);
		return res;
	}
	
	default Integer readInt(String prompt,String errorPrompt)
	{
		// Entered string must be a number otherwise errorPrompt  
		Integer res= readObject(prompt,errorPrompt,str->Integer.parseInt(str));
		return res;
	}
	

	
	default Double readDouble(String prompt,String errorPrompt)
	{
		// Entered string must be a number otherwise errorPrompt  
		Double res= readObject(prompt,errorPrompt,str->Double.parseDouble(str));	
		return res;
	}
	
	default Double readNumberRange(String prompt,String errorPrompt,double min, double max)
	{
		// Entered string must be a number in range [min, max[ otherwise errorPrompt  
		Double res= readObject(prompt,errorPrompt,str->
								{
									Double d=Double.parseDouble(str);
									if(d<min || d>max) 
									{
										String erStr=String.format("%f < %f or %f > %f ", d,min,d,max);
										throw new RuntimeException(erStr);
									}
									return d;
								}
					);
		return res;			
	}
	
	default Double readNumberRangeWithPredicate(String prompt,String errorPrompt,
												String errorPromptForPredicate,double min, double max,Predicate<String> predicate)
	{
		Double res= readObject(prompt,errorPrompt,str->
		{
			Double d=Double.parseDouble(str);
			if(d<min || d>max) 
			{
				String erStr=String.format("%f < %f or %f > %f ", d,min,d,max);
				throw new RuntimeException(erStr);
			}
			if(!predicate.test(str))
			{
				throw new RuntimeException(errorPromptForPredicate);
			}		
			return d;
		}
								);
return res;		
	}
	
	default String readStringPredicate(String prompt,String errorPrompt,Predicate<String> predicate) 
	{
		// Entered string must match a given predicate
		String res=readObject(prompt,errorPrompt,str->
					{
						if(!predicate.test(str))
						{
							throw new RuntimeException("predicate not test");
						}
						return str;
					}
		
							 );
		return res;
	}
	
	default String readStringOptions(String prompt,String errorPrompt,HashSet<String> options) 
	{
		// Entered string must  be one out of given options
		String res=readObject(prompt,errorPrompt,str->
		{
			if(!options.contains(str))
			{
				String erStr=String.format("%s is not in options", str);
				throw new RuntimeException(erStr);
			}
			return str;
		}

				 );
		return res;
	}
	
	default LocalDate readIsoDate(String prompt,String errorPrompt)
	{
		// Entered string must be LocalDate in format (yyyy-mm-dd)
		LocalDate ld=readObject(prompt,errorPrompt,str->LocalDate.parse(str));
		return ld;
	}
	
	default LocalDate readIsoDateRange(String prompt,String errorPrompt, LocalDate minDate, LocalDate maxDate)
	{
		// Entered string must be LocalDate in format (yyyy-mm-dd) in the]minDate , maxDate[
		LocalDate ld=readObject(prompt,errorPrompt,str->
									{
										LocalDate date=LocalDate.parse(str);
										if(date.compareTo(minDate)<0 || date.compareTo(maxDate)>=0)
										{
											String erStr=String.format("date %s is out of range", date.toString());
											throw new RuntimeException(erStr);
										}
										return date;
									}
							   );
		return ld;
	}
}

