package point;

import java.util.ArrayList;

public class Point
{
	private double _values[];
	
	public Point(double ... values) { this._values = values; }

	public double getDistance(Point point) throws Exception
	{
		int dimention = this.getDimention();
		if(dimention == point.getDimention())
		{
			double result = 0;
			for(int i = 0; i < dimention; i++)
			{
				result += Math.pow(this._values[i] - point.getValues()[i], 2);
			}
			return Math.sqrt(result);
		}
		else { throw new Exception(); }
	}
	public Point getClone() { return new Point(this._values); }
	public int getDimention() { return this._values.length; }
	public String toString(String separator)
	{
		int dimention = this._values.length;
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < dimention; i++)
		{
			sb.append(this._values[i]);
			if(i != dimention - 1) { sb.append(separator); }
		}
		return sb.toString();
	}
//	/* GyS */
	public void setValues(double ... values) { this._values = values; }
	public double[] getValues() { return this._values; }
	/* End GyS */
//	@Override
//	public String toString() { return Arrays.toString(this._values); }
	
	public static ArrayList<Point> getPoints2d(int size)
	{
		ArrayList<Point> points = new ArrayList<>();
		for(int i = 0; i < size; i++)
		{
			points.add(new Point(helper.Helper.getRandom(0, 100), helper.Helper.getRandom(0, 100)));
		}
		return points;
	}
}