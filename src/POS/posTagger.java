package POS;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class posTagger {
	
	public static HashMap <String ,ArrayList<tagType> > arabicTag = new HashMap <String ,ArrayList<tagType> >();
	public static HashMap <String ,ArrayList<tagType> > englishTag = new HashMap <String ,ArrayList<tagType> >();

	public static HashMap <String ,ArrayList<String> > arabicTags = new HashMap <String ,ArrayList<String> >();
	public static HashMap <String ,ArrayList<String> > englishTags = new HashMap <String ,ArrayList<String> >();
	public static void loadTag () throws NumberFormatException, IOException
	{
		String file = "finalArabic";
		BufferedReader br =new BufferedReader(new FileReader(file));
		String line = new String();
		while ((line = br.readLine()) != null) {
			String colums[] = line.split(" ");
			tagType t = new tagType();
			t.freq=Integer.parseInt(colums[2]);
			t.tag=colums[1];
			if (arabicTag.containsKey(colums[0]))
			{
				arabicTag.get(colums[0]).add(t);
				arabicTags.get(colums[0]).add(colums[1]);
			}
			else
			{
				ArrayList <tagType> tArray = new ArrayList<>();
				ArrayList <String> a = new ArrayList<>();
				tArray.add(t);
				a.add(colums[1]);
				arabicTag.put(colums[0], tArray);
				arabicTags.put(colums[0], a);
			}
			
		}
		
		String file2 = "finalEnglish";
		BufferedReader br2 =new BufferedReader(new FileReader(file2));
		String line2 = new String();
		while ((line2 = br2.readLine()) != null) {
			String colums[] = line2.split(" ");
			tagType t = new tagType();
			t.freq=Integer.parseInt(colums[2]);
			t.tag=colums[1];
			if (englishTag.containsKey(colums[0]))
			{
				englishTag.get(colums[0]).add(t);
				englishTags.get(colums[0]).add(colums[1]);

			}
			else
			{
				ArrayList <tagType> tArray = new ArrayList<>();
				ArrayList <String> a = new ArrayList<>();
				tArray.add(t);
				a.add(colums[1]);
				englishTag.put(colums[0], tArray);
				englishTags.put(colums[0], a);
			}
			
		}
		
		br.close();
		br2.close();
	}
}
