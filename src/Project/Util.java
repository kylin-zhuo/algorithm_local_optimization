package Project;

import java.io.File;

public class Util {

	/**
	 * The main function for executing 
	 * 1) reads all the input files in folder input 
	 * 2) for each input source do calculation and store the result into
	 * output_local
	 * 3) running time will be printed
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		long start = System.currentTimeMillis();

		File folder = new File("input");
		File[] inputList = folder.listFiles();
		// System.out.println(inputList.length);

		Lines lines = null;
		Points points = null;
		String outputFile = "";

		for (int i = 0; i < inputList.length; i++) {

			if (inputList[i].isFile() && inputList[i].getName().contains("instance")) {

				String input = "input/" + inputList[i].getName();
				points = new Points(input);
				lines = new Lines(points);
				lines.initializeLines(points, 1);
				lines.executeLocalOptimization(points);
				outputFile = "output_local/solution_local" + input.substring(input.length() - 2);
				lines.writeToFile(outputFile);
				System.out.println("Processed " + inputList[i].getName() + ", result saved in " + outputFile);
				// System.out.println(input+" "+outputFile);

			}
		}

		long end = System.currentTimeMillis();
		System.out.println("Running time: " + (end - start) + "ms");
	}

}
