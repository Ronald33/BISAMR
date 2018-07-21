package weights;

import java.util.Arrays;

public class Weights
{
	private double _weights[] = new double[5];
	
	public Weights(double ... weights) throws Exception
	{
		int size = weights.length;
		switch(size)
		{
			case 5:
				this._weights = weights;
			break;
			case 3:
				this._weights = new double[]{weights[0], 1 - weights[0], weights[1], weights[2], 1 - (weights[1] + weights[2])};
			break;
			default:
				throw new Exception("Size of weights unknown");
		}
	}
	
	public double[] getArray() { return this._weights; }
	
	@Override
	public String toString()
	{
		return Arrays.toString(this._weights);
	}
}
