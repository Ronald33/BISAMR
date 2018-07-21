package schedulers;

import java.util.ArrayList;
import weights.Weights;

public class Helper
{
	public static Weights getAverageWeights(ArrayList<Weights> finished_tasks) throws Exception
	{
		double weights_avg[] = new double[5];
		int size = finished_tasks.size();
		
		if(size > 0)
		{
			for(int i = 0; i < size; i++)
			{
				double finished_task[] = finished_tasks.get(i).getArray();
				for(int j = 0; j < 5; j++) { weights_avg[j] += finished_task[j] / size; }
			}
			return new Weights(weights_avg);
		}
		else { throw new Exception("There isn't tasks finished"); }
	}
}
