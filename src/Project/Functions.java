package Project;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class Functions {

	/*
	 * Judge if the assignments are feasible 1. for each vertical v_line[i],
	 * find the points set PS that locate between v_line[i-1] and v_line[i] 2.
	 * for each horizontal h_line[i], count the number of points NP from PS that
	 * locate between h_line[i-1] and h_line[i] 3. when finding any NP that
	 * greater than 1, return false 4. after all the vertical lines have been
	 * examined, return true
	 *
	 * Time complexity: O(n^3)
	 */

	public static boolean isFeasible(Points points, Lines lines) {

		List<Double> v_lines = lines.getLines("vertical");
		List<Double> h_lines = lines.getLines("horizontal");
		int v = v_lines.size();
		int h = h_lines.size();

		double[][] pts = points.getCoordinates();
		double h_bottom, h_upper, v_left, v_right;
		int count;

		for (int i = 0; i <= v; i++) {
			for (int j = 0; j <= h; j++) {
				v_left = (i == 0 ? Double.MIN_VALUE : v_lines.get(i - 1));
				v_right = (i == v ? Double.MAX_VALUE : v_lines.get(i));
				h_bottom = (j == 0 ? Double.MIN_VALUE : h_lines.get(j - 1));
				h_upper = (j == h ? Double.MAX_VALUE : h_lines.get(j));
				count = countPoints(pts, h_bottom, h_upper, v_left, v_right);
				if (count > 1)
					return false;
			}
		}

		return true;
	}

	/**
	 * Count the number of points in a specific area, which are characterized by
	 * four borders:
	 * 
	 * @param pts
	 * @param h_bottom
	 *            y coordination of bottom horizontal line
	 * @param h_upper
	 *            y coordination of upper horizontal line
	 * @param v_left
	 *            x coordination of left vertical line
	 * @param v_right
	 *            x coordination of right vertical line
	 * @return res
	 * 
	 *         Time complexity: O(n)
	 */
	public static int countPoints(double[][] pts, double h_bottom, double h_upper, double v_left, double v_right) {
		if (pts.length == 0)
			return 0;
		if (pts[0].length != 2) {
			System.out.println("error");
			return 0;
		}
		int res = 0;
		int N = pts.length;
		for (int i = 0; i < N; i++) {
			if (pts[i][0] > v_left && pts[i][0] < v_right && pts[i][1] > h_bottom && pts[i][1] < h_upper)
				res += 1;
		}
		return res;
	}

	
	// ------------------------------------------------------------------------------------
	
	/**
	 * Traverse the points and lines first in x-direction Select those points
	 * that are within the same area separated by two contiguous vertical lines
	 * Order them by y coordinates For each this group, traverse the points with
	 * horizontal lines, once there are 2 or more points within the same area
	 * separated by two contiguous horizontal lines, return false. At last
	 * return true.
	 * 
	 * @return whether the current points and lines are feasible
	 */
	public static boolean isFeasible2(Points pts, Lines lines) {

		// Assert that the points have been sorted in x coordinates
		
		Points points = null;
		points = pts;
		
		//points.shuffleSortPoints("x");
		int N = points.getN();
		List<Double> verLines = lines.getLines("vertical");
		List<Double> horLines = lines.getLines("horizontal");

		int indexLine = 0;
		int indexPoint = 0;
		int unionNum = 0;
		while ((indexLine < verLines.size()) && (indexPoint < N)) {

			while ((indexPoint < N) && (points.getCoordinate(indexPoint, 0) < verLines.get(indexLine))) {
				points.setUnion(indexPoint, unionNum);
				indexPoint += 1;
			}
			unionNum += 1;
			indexLine += 1;

		}
		// the points in the right of the rightmost vertical line
		while (indexPoint < N) {
			points.setUnion(indexPoint, unionNum);
			indexPoint += 1;
		}
		// End set unions to the points split by vertical lines

		// Sort the points according to y coordinate
		points.shuffleSortPoints("y");

		// start from beginning again
		indexLine = 0;
		indexPoint = 0;
		HashSet<Integer> unions = new HashSet<Integer>();
		//List<Integer> unions = new LinkedList<Integer>();
		
		while ((indexLine < horLines.size()) && (indexPoint < N)) {
			while ((indexPoint < N) && (points.getCoordinate(indexPoint, 1) < horLines.get(indexLine))) {
				if (unions.contains(points.getUnion(indexPoint)))
					return false;
				unions.add(points.getUnion(indexPoint));
				indexPoint += 1;
			}
			unions.clear();
			unionNum += 1;
			indexLine += 1;
		}

		// the points at the top of the upmost horizontal line
		while (indexPoint < N) {
			if (unions.contains(points.getUnion(indexPoint)))
				return false;
			unions.add(points.getUnion(indexPoint));
			indexPoint += 1;
		}

		return true;
	}

	/**
	 * Print the solution to the console platform
	 * 
	 * @param solution
	 *            the solution, expressed in Class Lines
	 */
	public static void printSolution(Lines lines) {

		int numHor = lines.numLines("horizontal");
		int numVer = lines.numLines("vertical");
		int numLines = numHor + numVer;

		System.out.println(numLines);

		List<Double> verLines = lines.getLines("vertical");
		List<Double> horLines = lines.getLines("horizontal");

		for (int i = 0; i < numVer; i++) {
			System.out.println("v " + verLines.get(i));
		}

		for (int i = 0; i < numHor; i++) {
			System.out.println("h " + horLines.get(i));
		}
	}
	
	/* -------------------------------
	 * zero1: the direction and position of 1st line to withdraw zero2: the
	 * direction and position of 2nd line to withdraw one1: the direction and
	 * position of the line to add
	 */

	public static void updateLines(Lines lines, int[] zero1, int[] zero2, int[] one1) {
		lines.setValid(false, zero1[0], zero1[1]);
		lines.setValid(false, zero2[0], zero2[1]);
		lines.setValid(true, one1[0], one1[1]);
	}

	public static void undoUpdateLines(Lines lines, int[] zero1, int[] zero2, int[] one1) {
		lines.setValid(true, zero1[0], zero1[1]);
		lines.setValid(true, zero2[0], zero2[1]);
		lines.setValid(false, one1[0], one1[1]);
	}

}
