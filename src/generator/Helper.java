package generator;

import java.util.Random;

public class Helper
{
	public static double between0And1(double value)
	{
		if(value > 1) { return 1; }
		if(value < 0) { return 0; }
		return value;
	}
	
	public static double getRandom(double start, double end, int number_of_decimals)
	{
		Random r = new Random();
		double a = Math.pow(10.0, number_of_decimals);
		double result = r.nextDouble() * (end - start) + start;
		return Math.round(result *  a) / a;
	}
}
