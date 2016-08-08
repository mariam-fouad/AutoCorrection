package Prosess;

import java.util.Comparator;

public class SimpleType {
	
	public int number =0;
	public String Tag = new String ();
	public String word = new String ();
	
	public static Comparator<SimpleType> comparMin()
	{   
	 Comparator comp = new Comparator<SimpleType>(){
	     @Override
	     public int compare(SimpleType s1, SimpleType s2)
	     {
	         return Integer.compare(s1.number, s2.number);
	     }        
	 };
	 return comp;
	}  
	
	

}
