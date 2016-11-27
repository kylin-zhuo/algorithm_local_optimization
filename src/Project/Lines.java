package Project;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Lines {

	/*
	 * verLines: the x coordinates of vertical lines
	 * | 	|   |  | 
	 * | 	|   |  | 
	 * | 	|   |  |  
	 * x0 	x1  x2 x3 
	 * horLines: the y coordinates of horizontal lines 
	 * ------------ y0
	 *
	 * ------------ y1 
	 * ------------ y2
	 */

	// There should not be more than one lines within the adjacent two points
	// along x or y direction
	private int N;

	/**
	 * lines: 2 * N-1 matrix Row 0: x coordinates of vertical lines Row 1: y
	 * coordinates of horizontal lines
	 */
	private double[][] lines;

	/**
	 * valid: 2 * N-1 matrix 
	 * Row 0: indicators for vertical lines' validity 
	 * Row 1: indicators for horizontal lines' validity
	 * 
	 */
	private boolean[][] valid;

	public int numCombinations;
	public int numValid;
	public int numInvalid;
	public boolean finished;

	public int numIterations = 0;
	public int numCallingIsFeasible = 0;

	public List<int[]> validLines = new ArrayList<int[]>();
	public List<int[]> invalidLines = new ArrayList<int[]>();
	
	// List<int[]>, e,g. int[] {0,1,0,2} means valid[0][1] and valid[0][2] are a
	// combination
	public List<int[]> combinations = new ArrayList<int[]>();

	// set value for vertical or horizontal lines
	public void setValue(double value, int row, int col) {
		if (row != 0 && row != 1)
			System.err.println("error.");
		else
			this.lines[row][col] = value;
	}

	// set value for validity information
	public void setValid(boolean bool, int row, int col) {
		if (row != 0 && row != 1)
			System.err.println("error.");
		else
			this.valid[row][col] = bool;
	}

	public double getValue(int row, int col) {
		return this.lines[row][col];
	}

	public boolean getValid(int row, int col) {
		return this.valid[row][col];
	}

	public void updateLines() {
		validLines.clear();
		invalidLines.clear();
		combinations.clear();
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < getN(); j++) {
				if (this.getValid(i, j))
					validLines.add(new int[] { i, j });
				else
					invalidLines.add(new int[] { i, j });
			}
		}

		this.numValid = validLines.size();
		this.numInvalid = invalidLines.size();
		this.numCombinations = numValid * (numValid - 1) / 2;

		/**
		 * Update all the combinations of lines in the class Equivalent to
		 * getting combs from the "true"s
		 */
		for (int i = 0; i < numValid - 1; i++) {
			for (int j = i + 1; j < numValid; j++) {
				combinations.add(new int[] { validLines.get(i)[0], validLines.get(i)[1], validLines.get(j)[0],
						validLines.get(j)[1] });
			}
		}
	}

	// Set all the valid values to false, i.e. remove all lines
	public void clearLines() {
		int n = getN();
		for (int i = 0; i < n; i++) {
			setValid(false, 0, i);
			setValid(false, 1, i);
		}
	}

	// Return the total number of lines
	public int numLines() {
		return numLines("vertical") + numLines("horizontal");
	}

	/**
	 * Count the number of lines
	 * 
	 * @param direction: should be either "vertical" or "horizontal"
	 * @return the number of lines "vertical" or "horizontal"
	 */
	public int numLines(String direction) {
		int n = this.lines[0].length;
		int count = 0;
		int r = direction == "vertical" ? 0 : 1;
		for (int i = 0; i < n; i++) {
			if (getValid(r, i))
				count += 1;
		}
		return count;
	}

	/**
	 * @return double[][]
	 */
	public double[][] getLines() {
		return this.lines;
	}

	/**
	 * Getter
	 * @return a list of either vertical or horizontal direction, which has been sorted
	 */
	public List<Double> getLines(String direction) {
		List<Double> resultList = new ArrayList<Double>();
		int row = direction == "vertical" ? 0 : 1;
		int n = this.lines[0].length;
		for (int i = 0; i < n; i++) {
			if (this.getValid(row, i))
				resultList.add(this.getValue(row, i));
		}
		Collections.sort(resultList);
		return resultList;
	}

	// Constructor
	public Lines() {
		this.combinations = new ArrayList<int[]>();
		this.finished = false;
	}

	/**
	 * Constructor with parameters points: the object of points to be split
	 */
	public Lines(Points points) {
		int N = points.getN();
		this.setN(N - 1);
		lines = new double[2][N - 1];
		valid = new boolean[2][N - 1];
	}

	/**
	 * Initialize the lines in the fashion of vertical dominant strategy Choice:
	 * 0 global optimization 1 local optimization 2 greedy algorithm
	 */
	public void initializeLines(Points points, int choice) {
		double[] xs = points.getX_Ys("x");
		double[] ys = points.getX_Ys("y");
		Arrays.sort(xs);
		Arrays.sort(ys);
		int n = xs.length;
		for (int i = 0; i < n - 1; i++) {
			setValue(xs[i] + (xs[i + 1] - xs[i]) / 2, 0, i);
			setValue(ys[i] + (ys[i + 1] - ys[i]) / 2, 1, i);
			setValid(choice == 1 ? true : false, 0, i);
			setValid(false, 1, i);
		}
		this.updateLines();
	}

	public int getN() {
		return N;
	}

	public void setN(int n) {
		N = n;
	}

	/**
	 * One iteration of local optimization
	 * 
	 * @param points
	 */
	public void iterateLocalOptimization(Points points) {

		// this.updateLines();
		this.numIterations += 1;

		int[] comb, zero1, zero2, one1;

		for (int i = 0; i < this.numCombinations; i++) {
			comb = this.combinations.get(i);
			zero1 = new int[] { comb[0], comb[1] };
			zero2 = new int[] { comb[2], comb[3] };

			for (int j = 0; j < this.numInvalid; j++) {
				one1 = this.invalidLines.get(j);
				Functions.updateLines(this, zero1, zero2, one1);
				this.numCallingIsFeasible += 1;
				if (Functions.isFeasible(points, this)) {
					//String d1 = comb[0]==0? "v":"h";
					//String d2 = comb[2]==0? "v":"h";
					//String a1 = one1[0]==0? "v":"h";
					//System.out.print("subtract "+ d1 + this.getValue(comb[0], comb[1]) +" & "+d2+ this.getValue(comb[2], comb[3]));
					//System.out.println("; add "+ a1 + this.getValue(one1[0], one1[1]));
					this.updateLines();
					return;
				}
				Functions.undoUpdateLines(this, zero1, zero2, one1);
			}
		}
		// After the number of combinations, if no better solution comes out,
		// then finished
		this.finished = true;
	}

	/**
	 * Keep iterating the local optimization method, until finished
	 * 
	 * @param points
	 */
	public void executeLocalOptimization(Points points) {
		while (!this.finished) {
			this.iterateLocalOptimization(points);
		}
	}

	public int globalOptimum;
	public long globalOptimunPermutation;

	/**
	 * Use brute force method to solve the global optimization NP HARD 
	 * == This could take a long time ==
	 * 
	 * @param points
	 */
	public void executeGlobalOptimization(Points points) {
		
		// there are 2^(2N) possibilities of combination of lines
		long numTotal = (long) Math.round(Math.pow(2.0, 2.0 * getN()));
		// Each i represents the i-th permutation of the lines
		/**
		 * 0 0 1 0 0 1 0 1 1 0
		 * 
		 */
		int min = Integer.MAX_VALUE;
		for (long i = 0; i < numTotal; i++) {
			this.setLinesFromPermutation(i);
			if (Functions.isFeasible(points, this) && this.numValid < min) {
				min = numValid;
				this.globalOptimunPermutation = i;
			}
			else
				continue;
		}
		this.globalOptimum = min;
		setLinesFromPermutation(globalOptimunPermutation);
	}

	// The distribution of lines according to the i-th permutation of combinations
	public void setLinesFromPermutation(long i) {
		this.clearLines();
		int count = 0;
		while (i > 0) {
			if (i % 2 == 1) {
				this.setValid(true, count / getN(), count % getN());
			}
			i = i / 2;
			count += 1;
		}
		updateLines();
	}
	
	/**	Write the lines into the files in the format
	 * 
	 * @param filePath
	 */
	public void writeToFile(String filePath) {

		try {
			PrintWriter writer = new PrintWriter(filePath, "UTF-8");
			writer.write("" + this.numValid);

			int numHor = numLines("horizontal");
			int numVer = numLines("vertical");

			List<Double> verLines = getLines("vertical");
			List<Double> horLines = getLines("horizontal");

			for (int i = 0; i < numVer; i++)
				writer.write("\nv " + verLines.get(i));

			for (int i = 0; i < numHor; i++)
				writer.write("\nh " + horLines.get(i));

			writer.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}
	
	// print out all the valid lines
	public void printValid() {
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < N; j++) {
				System.out.print(this.getValid(i, j) + " ");
			}
			System.out.println();
		}
	}

}
