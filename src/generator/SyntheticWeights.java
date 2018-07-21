package generator;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;

import weights.Weights;

public class SyntheticWeights
{
	public static Weights[] weights;
	private static double ranges[][][];
	
	static 
	{
		try
		{
			Weights grep1 = new Weights(0.920, 0.520, 0.070);
			Weights grep2 = new Weights(0.648, 0.925, 0.008);
			Weights grep3 = new Weights(0.997, 0.581, 0.002);
			Weights kmeans1 = new Weights(0.880, 0.540, 0.020);
			Weights pi1 = new Weights(0.996, 0.998, 0.000);
			Weights sort1 = new Weights(0.090, 0.700, 0.030);
			Weights sort2 = new Weights(0.806, 0.415, 0.027);
			Weights sort3 = new Weights(0.823, 0.801, 0.089);
			Weights validate1 = new Weights(0.999, 0.283, 0.007);
			Weights wc1 = new Weights(0.210, 0.370, 0.020);
			Weights wc2 = new Weights(0.900, 0.470, 0.110);
			Weights wc3 = new Weights(0.870, 0.888, 0.015);
			Weights wc4 = new Weights(0.911, 0.988, 0.001);
			
			double ranges_grep[][] = new double[][]{{0.025, 0.002}, {0.429, 0.417}, {0.002, 0.003}};
			double ranges_kmeans[][] = new double[][]{{0.151, 0.40}, {0.095, 0.099}, {0.013, 0.007}};
			double ranges_pi[][] = new double[][]{{0.009, 0.003}, {0.001, 0.001}, {0.000, 0.000}};
			double ranges_sort[][] = new double[][]{{0.219, 0.146}, {0.021, 0.046}, {0.060, 0.029}};
			double ranges_validate[][] = new double[][]{{0.001, 0.001}, {0.022, 0.033}, {0.002, 0.005}};
			double ranges_wc[][] = new double[][]{{0.502, 0.049}, {0.001, 0.001}, {0.001, 0.000}};
			
			weights = new Weights[]{
					grep1, grep2, grep3, kmeans1, pi1, sort1, 
					sort2, sort3, validate1, wc1, wc2, wc3, wc4
			};
			
			ranges = new double[][][]{
				ranges_grep, ranges_grep, ranges_grep, ranges_kmeans, ranges_pi, ranges_sort, 
				ranges_sort, ranges_sort, ranges_validate, ranges_wc, ranges_wc, ranges_wc, ranges_wc
			};
		}
		catch(Exception e) { e.printStackTrace(); }
	}
	
	public static ArrayList<Weights> getWeights(int job_id, int number_of_tasks) throws Exception
	{
		double weight_array[] = weights[job_id].getArray();
		double range[][] = ranges[job_id];
		ArrayList<Weights> synthetic_weights = new ArrayList<>();
		
		for(int i = 0; i < number_of_tasks; i++)
		{
			double M1 = Helper.between0And1(Helper.getRandom(weight_array[0] - range[0][0], weight_array[0] + range[0][1], Configuration.number_of_decimals));
			double M2 = 1 - M1;
			double R1 = Helper.between0And1(Helper.getRandom(weight_array[2] - range[1][0], weight_array[2] + range[1][1], Configuration.number_of_decimals));
			double R2 = Helper.between0And1(Helper.getRandom(weight_array[3] - range[2][0], weight_array[3] + range[2][1], Configuration.number_of_decimals));
			double R3 = 1 - (R1 + R2);
			synthetic_weights.add(new Weights(M1, M2, R1, R2, R3));
		}
		
		return synthetic_weights;
	}
	
	public static ArrayList<Weights> getRandomWeights(SimpleEntry<double[], Integer> details, int number_of_tasks) throws Exception
	{
		double m1r1r2[] = details.getKey();
		double range[][] = ranges[details.getValue()];
		ArrayList<Weights> synthetic_weights = new ArrayList<>();
		
		for(int i = 0; i < number_of_tasks; i++)
		{
			double M1 = Helper.between0And1(Helper.getRandom(m1r1r2[0] - range[0][0], m1r1r2[0] + range[0][1], Configuration.number_of_decimals));
			double M2 = 1 - M1;
			double R1 = Helper.between0And1(Helper.getRandom(m1r1r2[1] - range[1][0], m1r1r2[1] + range[1][1], Configuration.number_of_decimals));
			double R2 = Helper.between0And1(Helper.getRandom(m1r1r2[2] - range[2][0], m1r1r2[2] + range[2][1], Configuration.number_of_decimals));
			double R3 = 1 - (R1 + R2);
			synthetic_weights.add(new Weights(M1, M2, R1, R2, R3));
		}
		
		return synthetic_weights;
	}
	
	public static SimpleEntry<double[], Integer> getRandomDetails()
	{
		double m1 = Helper.getRandom(0, 1, Configuration.number_of_decimals);
		double r1 = Helper.getRandom(0, 1, Configuration.number_of_decimals);
		double r2 = Helper.getRandom(0, 1 - r1, Configuration.number_of_decimals);
		return new SimpleEntry<double[], Integer>(new double[]{m1, r1, r2}, helper.Helper.getRandom(0, ranges.length - 1));
	}
}