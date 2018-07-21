import comparative.Comparative;

public class App
{
	public static void main(String[] args)
	{
		try
		{
//			comparative.Configuration.repetitions = 2;
//			int amounts[] = new int[]{500, 1000, 1500, 2000, 2500, 3000, 3500, 4000, 4500, 5000};
//			Comparative.multiTest(amounts, true);
			int amounts[] = new int[]{5000, 10000, 15000, 20000, 25000, 30000, 35000, 40000, 45000, 50000};
			Comparative.multiTestTime(amounts);
			System.out.println("Finish - " + new java.util.Date());
		}
		catch(Exception e) { e.printStackTrace(); }
	}
}