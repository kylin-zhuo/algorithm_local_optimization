package Project;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Points {

	private double[][] coordinates;

	// in order to group the points
	private int[] union;

	public void setUnion(int row, int u) {
		this.union[row] = u;
	}

	public void setUnion(int[] u) {
		this.union = u;
	}

	public int[] getUnion() {
		return this.union;
	}

	public int getUnion(int row) {
		return this.union[row];
	}

	private int N;

	public void setN(int n) {
		N = n;
	}

	public int getN() {
		return this.N;
	}

	public double[][] getCoordinates() {
		return coordinates;
	}

	public double getCoordinate(int row, int col) {
		return this.coordinates[row][col];
	}

	public Points(String filePath) throws Exception {
		this.orderOfPoints = "x";
		readFile(this.coordinates, filePath);
	}

	public void readFile(double[][] coordinates, String filePath) throws Exception {

		BufferedReader br = null;
		FileReader fr = null;

		try {
			String currentLine;
			fr = new FileReader(filePath);
			br = new BufferedReader(fr);

			// read the number of dots
			currentLine = br.readLine();
			int N = Integer.parseInt(currentLine);
			this.setN(N);

			// initialize the 2d array
			this.coordinates = new double[N][2];
			this.union = new int[N];
			for (int i = 0; i < N; i++) {
				union[i] = 0;
			}

			int row = 0;
			currentLine = br.readLine();

			while (currentLine != null) {

				double x = Double.parseDouble(currentLine.split(" ")[0]);
				double y = Double.parseDouble(currentLine.split(" ")[1]);
				this.setXY(x, y, row);
				row += 1;
				currentLine = br.readLine();
			}

			fr.close();
			br.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void printXY() {
		for (int i = 0; i < this.N; i++) {
			System.out.println("" + this.coordinates[i][0] + " " + this.coordinates[i][1]);
		}
	}

	public void setXY(double x, double y, int row) {
		this.coordinates[row][0] = x;
		this.coordinates[row][1] = y;
	}

	// get the x or y coordinates
	public double[] getX_Ys(String x_y) {
		double[] res = new double[getN()];
		int j = x_y == "x" ? 0 : 1;
		for (int i = 0; i < getN(); i++) {
			res[i] = this.coordinates[i][j];
		}
		return res;
	}

	/**
	 * indicate which direction according to which are the points sorted the
	 * values can be {x,y}
	 * 
	 */
	public String orderOfPoints;

	/**
	 * Sort the points according to x or y coordinates Default: the points have
	 * been sorted according to x coordinates Insertion sort. O(n^2)
	 */
	public void shuffleSortPoints(String direction) {
		double key, key1, key2;
		double[] unit;
		int u;
		int col = 0;
		if (direction == "x")
			col = 0;
		else if (direction == "y")
			col = 1;
		else
			System.err.println("the direction should be either x or y");

		for (int j = 1; j < N; j++) {
			unit = this.coordinates[j];
			key1 = unit[0];
			key2 = unit[1];
			key = col == 1 ? key2 : key1;
			u = this.union[j];
			// insert the i of points[i][1] into sorted sequence temp[0...i-1]
			int i = j - 1;
			while (i >= 0 && this.getCoordinate(i, col) > key) {
				this.coordinates[i + 1][0] = this.coordinates[i][0];
				this.coordinates[i + 1][1] = this.coordinates[i][1];
				this.union[i + 1] = this.union[i];
				i = i - 1;
			}
			this.coordinates[i + 1][0] = key1;
			this.coordinates[i + 1][1] = key2;
			this.union[i + 1] = u;
		}
	}

	public void printPoints() {
		for (int i = 0; i < this.N; i++) {
			System.out.println(this.coordinates[i][0] + " " + this.coordinates[i][1] + ", union:" + this.union[i]);
		}
	}

}
