package schedulers.esamr;

public abstract class Helper
{
	public static boolean hasNan(double values[])
	{
		int size = values.length;
		for(int i = 0; i < size; i++)
		{
			if(Double.isNaN(values[i])) { return true; }
		}
		return false;
	}
}
