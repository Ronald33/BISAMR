package clustering.birch;

import java.util.ArrayList;

import clustering.birch.cftree.CF;
import clustering.birch.cftree.CFTree;
import point.Point;

public class BIRCH extends clustering.Clustering
{
	private CFTree _cftree;
	private double _T = Config.T;
	private int _B = Config.B;
	private int _L = Config.L;
	private long _timeLastInsert = 0;
	
	public BIRCH(double T, int B, int L) throws Exception
	{
		this._T = T;
		this._B = B;
		this._L = L;
		this._cftree = new CFTree(this._T, this._B, this._L);
	}
	
	public void insertToCFTree(ArrayList<Point> points) throws Exception
	{
		long startTime = System.currentTimeMillis();
		this.setPoints(points);
		this.fillFirstCFTree();
		this.fillCFTreeCompacted();
		this._timeLastInsert = System.currentTimeMillis() - startTime;
	}
	
	public void setCentroids() throws Exception
	{
		this.start();
		ArrayList<CF> entries = phase3(this._cftree.getEntries(), this._T);
		this._centroids = clustering.birch.cftree.Helper.getMedias(entries);
		this.end();
	}
	
	@Override
	public void execute(ArrayList<Point> points) throws Exception
	{
		this.insertToCFTree(points);
		this.setCentroids();
	}
	
	private void fillFirstCFTree() throws Exception
	{
		int points_size = this._points.size();
		for(int i = 0; i < points_size; i++) { this._cftree.insert(this._points.get(i)); }
	}
	
	private void fillCFTreeCompacted() throws Exception
	{
		CFTree cftreeCompacted = new CFTree(this._cftree.getT(), this._cftree.getB(), this._cftree.getL());
		ArrayList<CF> entries = this._cftree.getEntries();
		
		int entries_size = entries.size();
		for(int i = 0; i < entries_size; i++) { cftreeCompacted.insert(entries.get(i)); }
		this._cftree = cftreeCompacted;
	}
	
	private static ArrayList<CF> phase3(ArrayList<CF> entries, double T) throws Exception
	{
		int centroids_size = entries.size();
		if(centroids_size > 1)
		{
			double distance = 0, max_distance = Double.MAX_VALUE;
			int c1 = 0, c2 = 0;
			
			for(int i = 0; i < centroids_size - 1; i++)
			{
				for(int j = i + 1; j < centroids_size; j++)
				{
					distance = entries.get(i).distance(entries.get(j));
					if(distance < max_distance)
					{
						max_distance = distance;
						c1 = i;
						c2 = j;
					}
				}
			}
			if(distance < T)
			{
				entries.get(c1).add(entries.get(c2));
				entries.remove(c2);
				return phase3(entries, T);
			}
			else { return entries; }
		}
		else { return entries; }
	}
	public long getTime(boolean add_time_last_insert) { return this.getTime() + this._timeLastInsert; }
	@Override
	public long getTime() { return this._end - this._start; }
	/* GyS */
	public CFTree getCFTree() { return this._cftree; }
	public double getT() { return this._T; }
	public int getB() { return this._B; }
	public int getL() { return this._L; }
	/* End GyS */
}