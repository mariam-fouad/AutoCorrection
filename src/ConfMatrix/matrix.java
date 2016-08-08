package ConfMatrix;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class matrix {
	
	public static HashMap<String, Integer> subMatrix = new HashMap<String,Integer>();
	public static HashMap<String, Integer> delMatrix = new HashMap<String,Integer>();
	public static HashMap<String, Integer> insMatrix = new HashMap<String,Integer>();
	
	public static void loadSubMatrix () throws IOException
	{
		String allData = new String(Files.readAllBytes(Paths.get("subMatrix.txt")));
		allData = allData.replaceAll("\\r|\\n", "");
		String [] partionData = allData.split(",");
		for (int i=0;i<partionData.length;i++)
		{
			String key = partionData[i].substring(2, 4); //get the two char 
			int value = Integer.parseInt(partionData[i].substring(7));//get the weight
			subMatrix.put(key, value);
		}
		
	}
	
	public static void loadInsMatrix () throws IOException
	{
		String allData = new String(Files.readAllBytes(Paths.get("insMatrix.txt")));
		allData = allData.replaceAll("\\r|\\n", "");
		String [] partionData = allData.split(",");
		for (int i=0;i<partionData.length;i++)
		{
			String key = partionData[i].substring(2, 4); //get the two char 
			int value = Integer.parseInt(partionData[i].substring(7));//get the weight
			insMatrix.put(key, value);
		}
		
	}
	
	public static void loadDelMatrix () throws IOException
	{
		String allData = new String(Files.readAllBytes(Paths.get("delMatrix.txt")));
		allData = allData.replaceAll("\\r|\\n", "");
		
		String [] partionData = allData.split(",");
		for (int i=0;i<partionData.length;i++)
		{
			String key = partionData[i].substring(2, 4); //get the two char 
			int value = Integer.parseInt(partionData[i].substring(7));//get the weight
			delMatrix.put(key, value);
		}
		
	}

	
	

}
