package wrdca.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class SimpleTextInputFileNormalizer {
	
	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			System.out.println("Should give the input file to normalize and the number of objects in the file as argument");
			System.exit(-1);
		}
		File inputFile = new File(args[0]);
		int n = Integer.parseInt(args[1]);
		if (!inputFile.isFile()) {
			System.out.println("Argument: " + inputFile.toString() + " is not a file");
			System.exit(-2);
		}
		if (!inputFile.canRead()) {
			System.out.println("Input file " + inputFile.toString() + " is not readable");
			System.exit(-3);
		}
		BufferedReader buf = new BufferedReader(new FileReader(inputFile));
		for (int i = 0; i < n; i++) {
			buf.readLine();
		}
		DissimMatrix dissimMatrix = new DissimMatrix(n);
		for (int i = 0; i < n; i++) {
			final String line = buf.readLine();
			final StringTokenizer strtok = new StringTokenizer(line, ",");
			for(int j = 0; j <= i; j++) {
				final double dissim = Double.parseDouble(strtok.nextToken());
				dissimMatrix.putDissim(i, j, dissim);
			}
		}
		buf.close();
		Set<Integer> incomparables = flagIncomparable(dissimMatrix);
		dispersionNormalize(dissimMatrix);
		printObjects(dissimMatrix, inputFile, n, incomparables);
		for (int i = 0; i < n; i++) {
			if (!incomparables.contains(i)) System.out.print(dissimMatrix.getDissim(i, 0));
			else continue;
			for(int j = 1; j <= i; j++) {
				if (!incomparables.contains(j)) System.out.print("," + dissimMatrix.getDissim(i, j));
			}
			System.out.println("");
		}
		
	}

	public static void printObjects(DissimMatrix dissimMatrix, File inputFile, int nObjects, Set<Integer> incomparables) throws IOException {
		BufferedReader buf = new BufferedReader(new FileReader(inputFile));
		for (int i = 0; i < nObjects; i++) {
			final String line = buf.readLine();
			if (!incomparables.contains(i)) {
				System.out.println(line);
			}
		}
		buf.close();
	}

	private static Set<Integer> flagIncomparable(DissimMatrix dissimMatrix) {
		Set<Integer> result = new HashSet<Integer>();
		final int n = dissimMatrix.length();
		for (int i = 0; i < n; i++) {
			int negative = 0;
			for(int j = 0; j < n; j++) {
				if (i != j && dissimMatrix.getDissim(i, j) < 0) {
					negative++;
				}
			}
			if (negative == (n - 1)) {
				result.add(i);
			}
		}
		return result;
	}

	private static void dispersionNormalize(DissimMatrix dissimMatrix) {
		final int n = dissimMatrix.length();
		double minDissim = Double.MAX_VALUE;
		for (int i = 0; i < n; i++) {
			double myDissim = 0.0;
			for (int j = 0; j < n; j++) {
				final double dissim = dissimMatrix.getDissim(i, j);
				if (dissim > 0.0) myDissim += dissim;
			}
			if (myDissim < minDissim && myDissim > 0.0) {
				minDissim = myDissim;
			}
		}
		for (int i = 0; i < n; i++) {
			for(int j = 0; j <= i; j++) {
				final double dissim = dissimMatrix.getDissim(i, j);
				if (dissim > 0.0)
					dissimMatrix.putDissim(i, j, dissim/minDissim);
			}
		}
	}

}
