package schedulers.samr;

import schedulers.Scheduler;
import weights.Weights;

public class SAMR implements Scheduler
{
	private double _HP;
	private Weights _W_Old;
	
	public SAMR() throws Exception
	{
		this._W_Old = new Weights(1, 0, 0.3, 0.3, 0.4);
		this._HP = 0.2;
	}
	
	public void adjustWeights(Weights weights) throws Exception
	{
		double weights_array[] = weights.getArray();
		double M1M2[] = this.getWeightsMap(new double[]{weights_array[0], weights_array[1]});
		double R1R2R3[] = this.getWeightsReduce(new double[]{weights_array[2], weights_array[3], weights_array[4]});
		this._W_Old = new Weights(M1M2[0], M1M2[1], R1R2R3[0], R1R2R3[1], R1R2R3[2]);
	}
	
	
	@Override
	public double[] getWeightsMap(double weights_finished[]) throws Exception
	{
		double [] w_old_array = this._W_Old.getArray();
		double M1 = w_old_array[0] * this._HP + weights_finished[0] * (1 - this._HP), M2 = 1 - M1;
		return new double[]{M1, M2};
	}

	@Override
	public double[] getWeightsReduce(double weights_finished[]) throws Exception
	{
		double [] w_old_array = this._W_Old.getArray();
		double R1 = w_old_array[2] * this._HP + weights_finished[0] * (1 - this._HP);
		double R2 = w_old_array[3] * this._HP + weights_finished[1] * (1 - this._HP);
		double R3 = 1 - (R1 + R2);
		return new double[]{R1, R2, R3};
	}
}