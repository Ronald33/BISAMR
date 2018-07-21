package schedulers.bisamr;

import java.util.ArrayList;

import clustering.birch.BIRCH;
import point.Point;
import schedulers.Scheduler;
import weights.Weights;

public class BISAMR implements Scheduler
{
	private BIRCH _map_birch;
	private BIRCH _reduce_birch;
	
	private long _map_time;
	private long _reduce_time;
	
	public BISAMR() throws Exception
	{
		this._map_birch = new BIRCH(Configuration.T_map, Configuration.B_map, Configuration.L_map);
		this._reduce_birch = new BIRCH(Configuration.T_reduce, Configuration.B_reduce, Configuration.L_reduce);
	}
	
	public void addWeightsToHistorical(Weights weight) throws Exception
	{
		double weights_reference_array[] = weight.getArray();
		
		ArrayList<Point> map_points = new ArrayList<>();
		map_points.add(new Point(weights_reference_array[0]));
		this._map_birch.insertToCFTree(map_points);
		
		ArrayList<Point> reduce_points = new ArrayList<>();
		reduce_points.add(new Point(weights_reference_array[2], weights_reference_array[3], weights_reference_array[4]));
		this._reduce_birch.insertToCFTree(reduce_points);
	}
	
	public void updateCentroids() throws Exception
	{
		this._map_birch.setCentroids();
		this._map_time = this._map_birch.getTime(true); // Adding the time to the last insert to the cftree
		this._reduce_birch.setCentroids();
		this._reduce_time = this._reduce_birch.getTime(true); // Adding the time to the last insert to the cftree
	}

	@Override
	public double[] getWeightsMap(double weights_finished[]) throws Exception
	{
		Point centroids[] = this._map_birch.getCentroids();
		double tempM1 = weights_finished[0];
		int centroids_size = centroids.length;
		
		double M1 = centroids[helper.Helper.getRandom(0, centroids_size - 1)].getValues()[0];
		double beta = Math.abs(tempM1 - M1);
		
		for(int i = 0; i < centroids_size; i++)
		{
			double centroid_array[] = centroids[i].getValues();
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
		Point centroids[] = this._reduce_birch.getCentroids();
		int centroids_size = centroids.length;
		double distance_min = Double.MAX_VALUE;
		int index_min = -1;
		
		for(int i = 0; i < centroids_size; i++)
		{
			Point centroid = centroids[i];
			double distance = Helper.getDistance(weights_finished, centroid.getValues());
			
			if(distance < distance_min)
			{
				distance_min = distance;
				index_min = i;
			}
		}
		return centroids[index_min].getValues();
	}
	
	public long getTimeMap() { return this._map_time; }
	public long getTimeReduce() { return this._reduce_time; }
}