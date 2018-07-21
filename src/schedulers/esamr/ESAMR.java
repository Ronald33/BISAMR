package schedulers.esamr;

import java.util.ArrayList;

import clustering.kmeans.KMeans;
import point.Point;
import schedulers.Scheduler;
import weights.Weights;

public class ESAMR implements Scheduler
{
	private ArrayList<Point> _historicalMap = new ArrayList<>();
	private ArrayList<Point> _historicalReduce = new ArrayList<>();
	private Point[] _map_centroids;
	private Point[] _reduce_centroids;
	
	private long _map_time;
	private long _reduce_time;
	
	public Point[] getMapCentroids() { return this._map_centroids; }
	public Point[] getReduceCentroids() { return this._reduce_centroids; }
	
	@Override
	public double[] getWeightsMap(double weights_finished[]) throws Exception
	{
		double tempM1 = weights_finished[0];
		int centroids_size = this._map_centroids.length;
		
		double M1 = this._map_centroids[helper.Helper.getRandom(0, centroids_size - 1)].getValues()[0];
		double beta = Math.abs(tempM1 - M1);
		
		for(int i = 0; i < centroids_size; i++)
		{
			double centroid_array[] = this._map_centroids[i].getValues();
			if(Math.abs(centroid_array[0] - tempM1) < beta)
			{
				M1 = centroid_array[0];
				beta = Math.abs(tempM1 - M1);
			}
		}
		
		return new double[]{M1, 1 - M1};
	}
	@Override
	public double[] getWeightsReduce(double weights_finished[]) throws Exception
	{
		double tempR1 = weights_finished[0];
		double tempR2 = weights_finished[1];
		
		int centroids_size = this._reduce_centroids.length;
		int random_index = helper.Helper.getRandom(0, centroids_size - 1);
		double R1 = this._reduce_centroids[random_index].getValues()[0];
		double R2 = this._reduce_centroids[random_index].getValues()[1];
		double beta = Math.abs(tempR1 - R1) + Math.abs(tempR2 - R2);
		
		for(int i = 0; i < centroids_size; i++)
		{
			double centroid_array[] = this._reduce_centroids[i].getValues();
			if(Math.abs((tempR1 - centroid_array[0]) + (tempR2 - centroid_array[1])) < beta)
			{
				R1 = centroid_array[0];
				R2 = centroid_array[1];
				beta = Math.abs(tempR1 - R1) + Math.abs(tempR2 - R2);
			}
		}
		
		return new double[]{R1, R2, 1 - (R1 + R2)};
	}
	
	public void addWeightsToHistorical(Weights weight)
	{
		double weights_array[] = weight.getArray();
		this._historicalMap.add(new Point(weights_array[0], weights_array[1]));
		this._historicalReduce.add(new Point(weights_array[2], weights_array[3], weights_array[4]));
	}
	
	public void updateCentroids() throws Exception
	{
		KMeans map_km = new KMeans();
		map_km.execute(this._historicalMap);
		Point[] map_centroids = map_km.getCentroids();
		this._map_centroids = cleanNaN(map_centroids);
		this._map_time = map_km.getTime();
		
		KMeans reduce_km = new KMeans();
		reduce_km.execute(this._historicalReduce);
		Point[] reduce_centroids = reduce_km.getCentroids();
		this._reduce_centroids = cleanNaN(reduce_centroids);
		this._reduce_time = reduce_km.getTime();
	}
	
	private static Point[] cleanNaN(Point[] centroids)
	{
		ArrayList<Point> cleaned = new ArrayList<>();
		int size = centroids.length;
		for(int i = 0; i < size; i++)
		{
			Point centroid = centroids[i];
			if(Helper.hasNan(centroid.getValues())) { continue; }
			cleaned.add(centroid);
		}
		return cleaned.toArray(new Point[cleaned.size()]);
	}
	
	public long getTimeMap() { return this._map_time; }
	public long getTimeReduce() { return this._reduce_time; }
}