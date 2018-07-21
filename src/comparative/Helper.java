package comparative;

import java.util.ArrayList;
import java.util.HashSet;

public abstract class Helper
{
	public static double round(double value, int places)
	{
	    if (places < 0) throw new IllegalArgumentException();
	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
	public static double convertToBase100(double result, int places)
	{
		return round(result * 100, places);
	}
	public static void convertToBase100(double result[], int places)
	{
		int size = result.length;
		for(int i = 0; i < size; i++)
		{
			result[i] = convertToBase100(result[i], places);
		}
	}
	public static boolean in_array(int value, ArrayList<Integer> array)
	{
		int size = array.size();
		for (int i = 0; i < size; i++) { if(array.get(i) == value) { return true; } }
		return false;
	}
	public static int getRandomUnique(int min, int max, ArrayList<Integer> selecteds)
	{
		HashSet<Integer> discarteds = new HashSet<>();
		do
		{
			int random = helper.Helper.getRandom(min, max);
			if (Helper.in_array(random, selecteds)) { discarteds.add(random); }
			else { selecteds.add(random); return random; }
		}
		while ((max - min + 1) != discarteds.size());
		return -1;
	}
	
	public static String arrayToCSV(double result[][], String headers[], int row_groups[], boolean print_headers)
	{
		int headers_size = headers.length, 
			row_groups_size = row_groups.length;
		StringBuilder sb = new StringBuilder();
		
		if(print_headers)
		{
			sb.append(" \t");
			for(int i = 0; i < headers_size; i++) { sb.append(headers[i] + "\t"); }
			sb.append("\n");
		}
		
		for(int i = 0; i < row_groups_size; i++)
		{
			sb.append(row_groups[i] + "\t");
			for(int j = 0; j < headers_size; j++)
			{
				sb.append(result[i][j] + "\t");
			}
			sb.append("\n");
		}
		
		return sb.toString();
	}
}
