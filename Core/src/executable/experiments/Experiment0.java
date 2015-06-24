package executable.experiments;

import iniloader.IniLoader;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;

import utility.Stopwatch;
import datasetEvaluator.DatasetEvaluatorSandbox;
import datatypes.configuration.Config;

public class Experiment0 {

	static int experimentNumber = 0;
	static String dataset = "aida";

	public static void main(String[] args) {
		String filepath = "../config.ini";
		if (args.length == 1)
			filepath = args[0];
		System.out.println("Using config file: " + filepath);
		IniLoader iniLoader = new IniLoader();
		iniLoader.parse(filepath);
		double result = 0;

		Config config = Config.getInstance();
		dataset = config.getParameter("datasetPath");
		DatasetEvaluatorSandbox sandbox = new DatasetEvaluatorSandbox();

		try {
			BufferedWriter fw = new BufferedWriter(new FileWriter("experiment_" + experimentNumber + ".txt"));
			
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			String formattedDate = sdf.format(date);
			fw.write(formattedDate + "\n");
			
			Stopwatch swTotal = new Stopwatch(Stopwatch.UNIT.MINUTES);

			for (int i = 0; i < 100; i++)
				result += sandbox.evaluate();

			result /= 100.0;
			swTotal.stop();
			fw.write(dataset);
			fw.write("Total time: " + round(swTotal.doubleTime, 4) + " minutes.\n");
			fw.write("Result: " + result);
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

}
