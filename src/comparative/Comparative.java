package comparative;

import java.util.ArrayList;
import java.util.AbstractMap.SimpleEntry;

import generator.SyntheticWeights;
import schedulers.bisamr.BISAMR;
import schedulers.esamr.ESAMR;
import schedulers.late.LATE;
import schedulers.samr.SAMR;
import weights.Weights;

public class Comparative
{
	private int _numberOfJobsForTheSecondPhase;
	private static int _types_of_jobs = generator.SyntheticWeights.weights.length;
	private static String[] _schedulers = new String[]{"LATE", "SAMR", "ESAMR", "BISAMR"};
	private static int _types_of_schedulers = _schedulers.length;
	
	private LATE _late = new LATE();
	private SAMR _samr = new SAMR();
	private ESAMR _esamr = new ESAMR();
	private BISAMR _bisamr = new BISAMR();
	
	private double _late_map_diff = 0;
	private double _late_reduce_diff = 0;
	private double _samr_map_diff = 0;
	private double _samr_reduce_diff = 0;
	private double _esamr_map_diff = 0;
	private double _esamr_reduce_diff = 0;
	private double _bisamr_map_diff = 0;
	private double _bisamr_reduce_diff = 0;
	
	public Comparative(int number_of_jobs_for_the_second_phase) throws Exception
	{
		this._numberOfJobsForTheSecondPhase = number_of_jobs_for_the_second_phase;
	}
	
	public void execute(boolean activate_random) throws Exception
	{
		if(activate_random)
		{
			ArrayList<SimpleEntry<double[], Integer>> details = new ArrayList<>();
			for(int i = 0; i < this._numberOfJobsForTheSecondPhase; i++) { details.add(SyntheticWeights.getRandomDetails()); }
			this.executeFirstPhaseRandom(details);
			this.executeSecondPhaseRandom(details);
		}
		else
		{
			this.executeFirstPhase();
			this.executeSecondPhase();
		}
	}
	private void executeFirstPhase() throws Exception
	{
		ArrayList<Integer> selecteds = new ArrayList<>();
		
		for(int i = 0; i < _types_of_jobs; i++)
		{
			int type_of_job = Helper.getRandomUnique(0, _types_of_jobs - 1, selecteds);
			ArrayList<Weights> weights = generator.SyntheticWeights.getWeights(type_of_job, Configuration.number_of_tasks);
			this.storeHistoricalData(weights);
		}
	}
	private void executeSecondPhase() throws Exception
	{
		for(int i = 0; i < this._numberOfJobsForTheSecondPhase; i++)
		{
			int type_of_job = helper.Helper.getRandom(0, _types_of_jobs - 1);
			ArrayList<Weights> weights = generator.SyntheticWeights.getWeights(type_of_job, Configuration.number_of_tasks);
			this.speculateAndMeasure(weights);
			this.storeHistoricalData(weights);
		}
	}
	private void executeFirstPhaseRandom(ArrayList<SimpleEntry<double[], Integer>> details) throws Exception
	{
		for(int i = 0; i < this._numberOfJobsForTheSecondPhase; i++)
		{
			ArrayList<Weights> weights = SyntheticWeights.getRandomWeights(details.get(i), Configuration.number_of_tasks);
			this.storeHistoricalData(weights);
		}
	}
	private void executeSecondPhaseRandom(ArrayList<SimpleEntry<double[], Integer>> details) throws Exception
	{
		for(int i = 0; i < this._numberOfJobsForTheSecondPhase; i++)
		{
			ArrayList<Weights> weights = SyntheticWeights.getRandomWeights(details.get(i), Configuration.number_of_tasks);
			this.speculateAndMeasure(weights);
		}
	}
	public double[] testTime() throws Exception
	{
		for(int i = 0; i < this._numberOfJobsForTheSecondPhase; i++)
		{
			int type_of_job = helper.Helper.getRandom(0, _types_of_jobs - 1);
			ArrayList<Weights> weights = generator.SyntheticWeights.getWeights(type_of_job, Configuration.number_of_tasks);
			if(i == this._numberOfJobsForTheSecondPhase - 1)
			{
				this._esamr.updateCentroids();
				this._bisamr.updateCentroids();
			}
			else { this.storeHistoricalData(weights); }  // Only store data
		}
		return new double[]{this._esamr.getTimeMap(), this._esamr.getTimeReduce(), this._bisamr.getTimeMap(), this._bisamr.getTimeReduce()};
	}
	private void storeHistoricalData(ArrayList<Weights> weights) throws Exception
	{
		Weights average_weights = schedulers.Helper.getAverageWeights(weights);
		this._samr.adjustWeights(average_weights);
		this._esamr.addWeightsToHistorical(average_weights);
		this._bisamr.addWeightsToHistorical(average_weights);
	}
	private void speculateAndMeasure(ArrayList<Weights> weights) throws Exception
	{
		this._esamr.updateCentroids();
		this._bisamr.updateCentroids();
		int task_id = helper.Helper.getRandom(1, Configuration.number_of_tasks - 1); // Select a random task for speculate		
		this.fillDifferences(weights.get(task_id)); // Measure
	}
	
	private void fillDifferences(Weights weights) throws Exception
	{
		double weights_array[] = weights.getArray();
		double map_weights[] = new double[]{weights_array[0], weights_array[1]};
		double reduce_weights[] = new double[]{weights_array[2], weights_array[3], weights_array[4]};
		// For Map
		int map_phase = helper.Helper.getRandom(0, 1);
		double map_sub_ps = (double) helper.Helper.getRandom(map_phase == 0 ? 1 : 0, map_phase == 1 ? 99 : 100) / 100;
		double map_ps_real = getPSMap(new double[]{weights_array[0], weights_array[1]}, map_phase, map_sub_ps);
		double map_ps_late = getPSMap(this._late.getWeightsMap(map_weights), map_phase, map_sub_ps);
		double map_ps_samr = getPSMap(this._samr.getWeightsMap(map_weights), map_phase, map_sub_ps);
		double map_ps_esamr = getPSMap(this._esamr.getWeightsMap(map_weights), map_phase, map_sub_ps);
		double map_ps_bisamr = getPSMap(this._bisamr.getWeightsMap(map_weights), map_phase, map_sub_ps);
		this._late_map_diff += Math.abs(map_ps_real - map_ps_late) / this._numberOfJobsForTheSecondPhase;
		this._samr_map_diff += Math.abs(map_ps_real - map_ps_samr) / this._numberOfJobsForTheSecondPhase;
		this._esamr_map_diff += Math.abs(map_ps_real - map_ps_esamr) / this._numberOfJobsForTheSecondPhase;
		this._bisamr_map_diff += Math.abs(map_ps_real - map_ps_bisamr) / this._numberOfJobsForTheSecondPhase;
		
		// For Reduce
		int reduce_phase = helper.Helper.getRandom(0, 2);
		double reduce_sub_ps = (double) helper.Helper.getRandom(reduce_phase == 0 ? 1 : 0, reduce_phase == 2 ? 99 : 100) / 100;
		double reduce_ps_real = getPSReduce(new double[]{weights_array[2], weights_array[3], weights_array[4]}, reduce_phase, reduce_sub_ps);
		double reduce_ps_late = getPSReduce(this._late.getWeightsReduce(reduce_weights), reduce_phase, reduce_sub_ps);
		double reduce_ps_samr = getPSReduce(this._samr.getWeightsReduce(reduce_weights), reduce_phase, reduce_sub_ps);
		double reduce_ps_esamr = getPSReduce(this._esamr.getWeightsReduce(reduce_weights), reduce_phase, reduce_sub_ps);
		double reduce_ps_bisamr = getPSReduce(this._bisamr.getWeightsReduce(reduce_weights), reduce_phase, reduce_sub_ps);
		this._late_reduce_diff += Math.abs(reduce_ps_real - reduce_ps_late) / this._numberOfJobsForTheSecondPhase;
		this._samr_reduce_diff += Math.abs(reduce_ps_real - reduce_ps_samr) / this._numberOfJobsForTheSecondPhase;
		this._esamr_reduce_diff += Math.abs(reduce_ps_real - reduce_ps_esamr) / this._numberOfJobsForTheSecondPhase;
		this._bisamr_reduce_diff += Math.abs(reduce_ps_real - reduce_ps_bisamr) / this._numberOfJobsForTheSecondPhase;
	}
	
	public double getLATEMapDiff() { return this._late_map_diff; }
	public double getSAMRMapDiff() { return this._samr_map_diff; }
	public double getESAMRMapDiff() { return this._esamr_map_diff; }
	public double getBISAMRMapDiff() { return this._bisamr_map_diff; }
	
	public double getLATEReduceDiff() { return this._late_reduce_diff; }
	public double getSAMRReduceDiff() { return this._samr_reduce_diff; }
	public double getESAMRReduceDiff() { return this._esamr_reduce_diff; }
	public double getBISAMRReduceDiff() { return this._bisamr_reduce_diff; }
	
	public static double getPSMap(double weights[], int phase, double sub_ps)
	{
		double ps;
		if(phase == 0) { ps = weights[0] * sub_ps; }
		else { ps = weights[0] + weights[1] * sub_ps; }
		return ps;
	}
	public static double getPSReduce(double weights[], int phase, double sub_ps)
	{
		double ps;
		if(phase == 0) { ps = weights[0] * sub_ps; }
		else if(phase == 1) { ps = weights[0] + weights[1] * sub_ps; }
		else { ps = weights[0] + weights[1] + weights[2] * sub_ps; }
		return ps;
	}
	
	public static double[][] test(int number_of_jobs_for_the_second_phase, boolean activate_random) throws Exception
	{
		double result_map[] = new double[_types_of_schedulers], result_reduce[] = new double[_types_of_schedulers];
		
		for(int i = 0; i < Configuration.repetitions; i++)
		{
			if(Configuration.print_number_of_iteration) { System.out.print((i + 1) + " "); }
			Comparative c = new Comparative(number_of_jobs_for_the_second_phase);
			c.execute(activate_random);
			result_map[0] += c.getLATEMapDiff() / Configuration.repetitions;
			result_map[1] += c.getSAMRMapDiff() / Configuration.repetitions;
			result_map[2] += c.getESAMRMapDiff() / Configuration.repetitions;
			result_map[3] += c.getBISAMRMapDiff() / Configuration.repetitions;

			result_reduce[0] += c.getLATEReduceDiff() / Configuration.repetitions;
			result_reduce[1] += c.getSAMRReduceDiff() / Configuration.repetitions;
			result_reduce[2] += c.getESAMRReduceDiff() / Configuration.repetitions;
			result_reduce[3] += c.getBISAMRReduceDiff() / Configuration.repetitions;
		}
		Helper.convertToBase100(result_map, Configuration.number_of_decimals);
		Helper.convertToBase100(result_reduce, Configuration.number_of_decimals);
		return new double[][]{result_map, result_reduce};
	}
	
	public static void multiTest(int number_of_jobs_for_the_second_phase[], boolean activate_random) throws Exception
	{
		int size = number_of_jobs_for_the_second_phase.length;
		double result_map[][] = new double[size][_types_of_schedulers], result_reduce[][] = new double[size][_types_of_schedulers];
		
		for(int i = 0; i < size; i++)
		{
			if(Configuration.print_number_of_iteration) { System.out.print(number_of_jobs_for_the_second_phase[i] + ": "); }
			
			double result[][] = test(number_of_jobs_for_the_second_phase[i], activate_random);
			result_map[i] = result[0];
			result_reduce[i] = result[1];
			if(Configuration.print_number_of_iteration) { System.out.println(); }
		}
		System.out.println(Helper.arrayToCSV(result_map, _schedulers, number_of_jobs_for_the_second_phase, Configuration.print_headers));
		System.out.println();
		System.out.println(Helper.arrayToCSV(result_reduce, _schedulers, number_of_jobs_for_the_second_phase, Configuration.print_headers));
	}
	public static double[] multiTestTime(int number_of_jobs_for_the_second_phase) throws Exception
	{
		double time_map_esamr = 0, time_reduce_esamr = 0, time_map_bisamr = 0, time_reduce_bisamr = 0;
		for(int i = 0; i < Configuration.repetitions; i++)
		{
			System.out.print((i + 1) + " ");
			Comparative comparative = new Comparative(number_of_jobs_for_the_second_phase);
			double results[] = comparative.testTime();
			time_map_esamr += results[0] / Configuration.repetitions;
			time_reduce_esamr += results[1] / Configuration.repetitions;
			time_map_bisamr += results[2] / Configuration.repetitions;
			time_reduce_bisamr += results[3] / Configuration.repetitions;
		}
		time_map_esamr = Helper.round(time_map_esamr, Configuration.number_of_decimals);
		time_reduce_esamr = Helper.round(time_reduce_esamr, Configuration.number_of_decimals);
		time_map_bisamr = Helper.round(time_map_bisamr, Configuration.number_of_decimals);
		time_reduce_bisamr = Helper.round(time_reduce_bisamr, Configuration.number_of_decimals);
		return new double[]{time_map_esamr, time_reduce_esamr, time_map_bisamr, time_reduce_bisamr};
	}
	
	public static void multiTestTime(int number_of_jobs_for_the_second_phase[]) throws Exception
	{
		int size = number_of_jobs_for_the_second_phase.length;
		double results[][] = new double[size][4];
		
		for(int i = 0; i < size; i++)
		{
			System.out.print(number_of_jobs_for_the_second_phase[i] + ": ");
			results[i] = multiTestTime(number_of_jobs_for_the_second_phase[i]);
			System.out.println();
		}
		String headers[] = new String[]{"ESAMR - map", "ESAMR - reduce", "BISAMR - map", "BISAMR - reduce"};
		System.out.println(Helper.arrayToCSV(results, headers, number_of_jobs_for_the_second_phase, Configuration.print_headers));
	}
}