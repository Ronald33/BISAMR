package schedulers.late;

import schedulers.Scheduler;

public class LATE implements Scheduler
{
	@Override
	public double[] getWeightsMap(double weights_finished[]) throws Exception
	{
		return new double[]{1, 0};
	}

	@Override
	public double[] getWeightsReduce(double weights_finished[]) throws Exception
	{
		return new double[]{0.33, 0.33, 0.34};
	}
}