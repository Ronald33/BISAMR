package schedulers;

public interface Scheduler
{
	public double[] getWeightsMap(double weights_finished[]) throws Exception;
	public double[] getWeightsReduce(double weights_finished[]) throws Exception;
}
