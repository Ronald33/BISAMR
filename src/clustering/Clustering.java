package clustering;

import java.io.FileWriter;
import java.util.ArrayList;

import point.Point;

public abstract class Clustering
{
	protected long _start;
	protected long _end;
	protected int _dimention;
	protected ArrayList<Point> _points;
	protected Point _centroids[];
	
	public abstract void execute(ArrayList<Point> points) throws Exception;
	
	protected void start() { this._start = System.currentTimeMillis(); }
	protected void end() { this._end = System.currentTimeMillis(); }
	public abstract long getTime();
	public Point[] getCentroids() { return this._centroids; }
	
	public void setPoints(ArrayList<Point> points)
	{
		this._points = points;
		this._dimention = this._points.get(0).getDimention();
	}
	
	public void savePointsClustered(String path, String filename, String separator) throws Exception
	{
		helper.Helper.createFolder(path);
		FileWriter fw = new FileWriter(path + "/" + filename, true);
		int centroidIndex = -1, 
			points_size = this._points.size(), 
			centroids_size = this._centroids.length;
		double distance_min;
		
		for(int i = 0; i < points_size; i++)
		{
			Point point = this._points.get(i);
			distance_min = Double.MAX_VALUE;
			for(int j = 0; j < centroids_size; j++)
			{
				double distance = point.getDistance(this._centroids[j]);
				if(distance < distance_min)
				{
					distance_min = distance;
					centroidIndex = j;
				}
			}
			fw.write(point.toString(separator) + separator + centroidIndex + "\n");
		}
		fw.close();
	}
}