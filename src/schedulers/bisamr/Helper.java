package schedulers.bisamr;

import java.util.Arrays;

public abstract class Helper
{
	public static double getDistance(double a[], double b[]) throws Exception
	{
		double distance = 0;
		int a_length = a.length, b_length = b.length;
		if(a_length == b_length)
		{
			for(int i = 0; i < a_length; i++)
			{
				double difference = a[i] - b[i]; 
				distance += Math.pow(difference, 2);
			}
		}
		else { throw new Exception(Arrays.toString(a) + " and " + Arrays.toString(b) + " have different dimentions"); }
		return Math.sqrt(distance);
	}
}
