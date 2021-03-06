/**
 * @author Sergio Queiroz <srmq@cin.ufpe.br>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or (at
 * your option) any later version.

 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 */
package wrdca.test;

import ilog.concert.IloException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import wrdca.algo.WTDHMClustering;
import wrdca.util.Cluster;
import wrdca.util.ConfusionMatrix;
import wrdca.util.DissimMatrix;


public class YeastDataset {
	
	public static final int NELEM = 1484;	
	public static final int APRIORICLASSES = 10;

	public static void main(String[] args) throws IOException, IloException {
		DissimMatrix tab1 = parseFile(DataFileNames.getString("YeastDataset.YEAST1"));
		DissimMatrix tab2 = parseFile(DataFileNames.getString("YeastDataset.YEAST2"));
		DissimMatrix tab3 = parseFile(DataFileNames.getString("YeastDataset.YEAST3"));
		DissimMatrix tab4 = parseFile(DataFileNames.getString("YeastDataset.YEAST4"));
		DissimMatrix tab5 = parseFile(DataFileNames.getString("YeastDataset.YEAST5"));
		DissimMatrix tab6 = parseFile(DataFileNames.getString("YeastDataset.YEAST6"));
		List<DissimMatrix> dissimMatrices = new ArrayList<DissimMatrix>(6);
		dissimMatrices.add(tab1);
		dissimMatrices.add(tab2);
		dissimMatrices.add(tab3);
		dissimMatrices.add(tab4);
		dissimMatrices.add(tab5);
		dissimMatrices.add(tab6);
		
		final int NUMBER_OF_RUNS = 100;
		int k = APRIORICLASSES;
		double bestJ = Double.MAX_VALUE;
		List<Cluster> bestClusters = null;
		
		for (int i = 0; i < NUMBER_OF_RUNS; i++) {
			System.out.println("Run number: " + (i+1));
			WTDHMClustering clust = new WTDHMClustering(dissimMatrices);
			//clust.setSeedType(WTDHMClustering.SeedingType.PLUSPLUS_SEED);
			clust.cluster(k);
			final List<Cluster> myClusters = clust.getClusters();
			final double myJ = clust.calcJ(myClusters);
			if (myJ < bestJ) {
				bestJ = myJ;
				bestClusters = myClusters;
			}
		}			

		ConfusionMatrix confusionMatrix = new ConfusionMatrix(k, APRIORICLASSES);
		
		for (int i = 0; i < bestClusters.size(); i++) {
			Cluster cluster = bestClusters.get(i);
			for (Integer element : cluster.getElements()) {
				assert(element >= 0 && element < NELEM);
				final int classlabel;
				if (element <= 462) classlabel = 0;
				else if (element <= 891) classlabel = 1;
				else if (element <= 1135) classlabel = 2;
				else if (element <= 1298) classlabel = 3;
				else if (element <= 1349) classlabel = 4;
				else if (element <= 1393) classlabel = 5;
				else if (element <= 1428) classlabel = 6;
				else if (element <= 1458) classlabel = 7;
				else if (element <= 1478) classlabel = 8;
				else classlabel = 9;
				confusionMatrix.putObject(element, i, classlabel);
			}
		}
		
		System.out.println(">>>>>>>>>>>> The F-Measure is: "+ confusionMatrix.fMeasureGlobal());
		System.out.println(">>>>>>>>>>>> The CR-Index  is: "+ confusionMatrix.CRIndex());
		System.out.println(">>>>>>>>>>>> OERC Index    is: " + confusionMatrix.OERCIndex());
		
		
	}
	
	private static DissimMatrix parseFile(String string) throws IOException {
		File file = new File(string);
		BufferedReader bufw = new BufferedReader(new FileReader(file));
		DissimMatrix result = new DissimMatrix(NELEM);
		String line;
		while((line = bufw.readLine()).indexOf("DIST_MATRIX") == -1)
			;
		for (int i = 0; i < NELEM; i++) {
			line = bufw.readLine();
			StringTokenizer sttok = new StringTokenizer(line, ", ()", false); //$NON-NLS-1$
			for (int j = 0; j < i; j++) {
				String dissimji = sttok.nextToken();
				result.putDissim(i, j, Float.parseFloat(dissimji));
			}
			assert(Float.parseFloat(sttok.nextToken()) == 0f);
		}
		bufw.close();
		return result;
	}
	
}
