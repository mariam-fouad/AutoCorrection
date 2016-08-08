package Corpus;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class EnglishCorpus {

	public static HashMap<String, Integer> Gram4Corpus = new HashMap<String, Integer>();
	public static HashMap<String, Integer> Gram3Corpus = new HashMap<String, Integer>();
	public static HashMap<String, Integer> Gram2Corpus = new HashMap<String, Integer>();
	public static HashMap<String, Integer> Gram1Corpus = new HashMap<String, Integer>();

	public static void loadAllCorpus() throws NumberFormatException, IOException
	{
		 String file = "1GramEnglish.txt";
		BufferedReader br =new BufferedReader(new FileReader(file));
		String line = new String();
		while ((line = br.readLine()) != null) {
			
			String colums[] = line.split("\t");
			int frq = Integer.parseInt(colums[1]);
			Gram1Corpus.put(colums[0].toLowerCase(), frq);
			
		}
		
		file = "2GramEnglish.txt";
		 br =new BufferedReader(new FileReader(file));
		line = new String();
		while ((line = br.readLine()) != null) {
			
			String colums[] = line.split("\t");
			int frq = Integer.parseInt(colums[2]);
			Gram2Corpus.put(colums[0].toLowerCase()+" "+colums[1].toLowerCase(), frq);
			
		}
		
		file = "3GramEnglish.txt";
		 br =new BufferedReader(new FileReader(file));
		line = new String();
		while ((line = br.readLine()) != null) {
			
			String colums[] = line.split("\t");
			int frq = Integer.parseInt(colums[3]);
			Gram3Corpus.put(colums[0].toLowerCase()+" "+colums[1].toLowerCase()+" "+colums[2].toLowerCase(), frq);
			
		}
		
		file = "4GramEnglish.txt";
		 br =new BufferedReader(new FileReader(file));
		line = new String();
		while ((line = br.readLine()) != null) {
			
			String colums[] = line.split("\t");
			int frq = Integer.parseInt(colums[4]);
			Gram4Corpus.put((colums[0].toLowerCase()+" "+colums[1].toLowerCase()+" "+colums[2].toLowerCase()+" "+colums[3].toLowerCase()), frq);
			
		}
		
		
		
		

	}

}
