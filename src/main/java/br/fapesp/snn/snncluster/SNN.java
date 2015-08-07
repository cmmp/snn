package br.fapesp.snn.snncluster;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import org.la4j.iterator.VectorIterator;
import org.la4j.matrix.sparse.CRSMatrix;

import net.sf.javaml.core.kdtree.KDTree;

/**
 * Shared Nearest Neighbors clustering algorithm
 * 
 * @author Cássio M. M. Pereira <cassiomartini@gmail.com> Created: 05-08-2015
 * 
 *         Paper reference: Ertoz, L., Steinbach, M., & Kumar, V. (2003).
 *         Finding clusters of different sizes, shapes, and densities in noisy,
 *         high dimensional data. SDM. Retrieved from
 *         http://epubs.siam.org/doi/abs/10.1137/1.9781611972733.5
 * 
 *
 */
public class SNN {

	/**
	 * Run the SNN-clustering algorithm
	 * 
	 * @param X
	 *            the data matrix, one example per row
	 * @param K
	 *            number of neighbors to form the sparse similarity matrix
	 * @param Eps
	 *            used for determining the SNN density of a point
	 * @param MinPts
	 *            used for determining core points
	 * @return cluster labels for the points
	 */
	public static int[] snn(double[][] X, int K, double Eps, int MinPts) {
		int N = X.length; // number of points
		int d = X[0].length; // dimensionality

		if (MinPts >= K) {
			throw new RuntimeException(
					"MinPts has to be smaller than K. No sense in a point having more than K neighbors.");
		}

		int[] labels = new int[N];

		// STEP 1 - get a similarity matrix

		// construct the kd-tree for knn queries
		KDTree kdtree = new KDTree(d);

		for (int i = 0; i < N; i++)
			kdtree.insert(X[i], i);

		// STEP 2 - sparsify the matrix by keeping only the k most similar
		// neighbors

		// find the K-neighbors of each point
		HashMap<Integer, HashSet<Integer>> kns = new HashMap<Integer, HashSet<Integer>>();
		HashSet<Integer> hs;

		for (int i = 0; i < N; i++) {
			// we will query for K + 1 nns because the
			// first nn is always the point itself
			Object[] nns = kdtree.nearest(X[i], K + 1);

			hs = new HashSet<Integer>();

			for (int j = 1; j < nns.length; j++) // start from the 2nd nn
				hs.add((Integer) nns[j]);

			kns.put(i, hs);
		}

		// STEP 3 - construct the shared nearest neighbor graph from the
		// sparsified matrix

		// The sparse matrix S holds in element (i,j) the SNN-similarity between
		// points i and j.
		CRSMatrix S = new CRSMatrix(N, N);
		int count;

		for (int i = 0; i < (N - 1); i++) {
			for (int j = i + 1; j < N; j++) {
				// create a link between i-j only if i is in j's kNN
				// neighborhood
				// and j is in i's kNN neighborhood
				if (kns.get(i).contains(j) && kns.get(j).contains(i)) {
					count = countIntersect(kns.get(i), kns.get(j));
					S.set(i, j, count);
					S.set(j, i, count);
				}
			}
		}

		// System.out.println(S.toCSV());

		// STEP 4 - find the SNN density of each point
		double[] snnDens = new double[N]; // should only contain ints though
		VectorIterator vi;
		double snnSim;

		for (int i = 0; i < N; i++) {
			vi = S.nonZeroIteratorOfRow(i);
			while (vi.hasNext()) {
				snnSim = vi.next();
				if (snnSim >= Eps)
					snnDens[i]++;
			}
		}

		// STEP 5 - find the core points
		// using MinPts, find all points that have SNN density greater than
		// MinPts
		ArrayList<Integer> corePts = new ArrayList<Integer>(N);
		boolean[] cores = new boolean[N]; // initialized to false by default

		for (int i = 0; i < N; i++) {
			if (snnDens[i] >= MinPts) {
				corePts.add(i);
				cores[i] = true;
			}
		}

		System.out.println("Core pts list:");
		System.out.println(corePts.toString());

		// System.out.println("similarities for point 0:");
		// vi = S.nonZeroIteratorOfRow(0);
		// while(vi.hasNext()) {
		// System.out.println("sim to 0: " + vi.next());
		// }

		// STEP 6 - form clusters from the core points. If two core pts are
		// within
		// Eps of each other, then place them in the same cluster
		int C = 0;
		HashSet<Integer> visited = new HashSet<Integer>(corePts.size());

		for (int i = 0; i < corePts.size(); i++) {
			int p = corePts.get(i);
			if (visited.contains(p))
				continue;
			visited.add(p);
			C++;
			labels[p] = C;
			ArrayDeque<Integer> neighCore = findCoreNeighbors(p, corePts, S, Eps);
			expandCluster(labels, neighCore, corePts, C, S, Eps, visited);
		}

		System.out.println("labels after corepts merges:");
		System.out.println(Arrays.toString(labels));

		// STEP 7 & STEP 8
		//
		// All points that are not within a radius of Eps of a core point are
		// discarded (noise);
		//
		// Assign all non-noise, non-core points to their nearest
		// core point

		for (int i = 0; i < N; i++) {
			boolean notNoise = false;
			double maxSim = Double.MIN_VALUE;
			int bestCore = -1;
			double sim;

			if (cores[i]) // this is a core point
				continue;

			for (int j = 0; j < corePts.size(); j++) {
				int p = corePts.get(j);
				sim = S.get(i, p);
				if (sim >= Eps)
					notNoise = true;
				if (sim > maxSim) {
					maxSim = sim;
					bestCore = p;
				}
			}

			if (notNoise)
				labels[i] = labels[bestCore];
		}

		return labels;
	}

	private static void expandCluster(int[] labels, ArrayDeque<Integer> neighbors, ArrayList<Integer> corePts, int C,
			CRSMatrix S, double Eps, HashSet<Integer> visited) {
		
		while (neighbors.size() > 0) {
			int p = neighbors.poll();
			
			if (visited.contains(p))
				continue;

			labels[p] = C;
			visited.add(p);

			ArrayDeque<Integer> neigh = findCoreNeighbors(p, corePts, S, Eps);
			neighbors.addAll(neigh);
		}
		
	}

	private static ArrayDeque<Integer> findCoreNeighbors(final int p, ArrayList<Integer> corePts, CRSMatrix S,
			final double Eps) {
		ArrayDeque<Integer> neighbors = new ArrayDeque<Integer>(corePts.size() / 2);
		int p2;
		for (int i = 0; i < corePts.size(); i++) {
			p2 = corePts.get(i);
			if (p != p2 && S.get(p, p2) >= Eps)
				neighbors.add(p2);
		}
		return neighbors;
	}

	public static int countIntersect(HashSet<Integer> h1, HashSet<Integer> h2) {
		int count = 0;
		for (Integer i : h1)
			if (h2.contains(i))
				count++;
		return count;
	}

	// public static void main(String[] args) throws IOException {
	//
	// // test data set -> two normals: (0,0.5) and (10,0.5); 30 points each
	// CSVLoader csvLoader = new CSVLoader();
	// csvLoader.setSource(new File("C:/Users/Cássio/Dropbox/snn/simple.csv"));
	// csvLoader.setNoHeaderRowPresent(true);
	// Instances inst = csvLoader.getDataSet();
	//
	// double[][] X = new double[inst.numInstances()][inst.numAttributes()];
	// for (int i = 0; i < inst.numInstances(); i++)
	// X[i] = inst.instance(i).toDoubleArray();
	//
	// int[] labels = snn(X, 20, 10, 5);
	// plotScatter(X, labels);
	// }
	//
	// private static void plotScatter(double[][] X, int[] labels) {
	// List<Paint> colors = Arrays.asList(ChartColor.createDefaultPaintArray());
	// Collections.reverse(colors);
	//
	// XYSeriesCollection dataset = new XYSeriesCollection();
	//
	// HashSet<Integer> clusters = MyUtils.getUniqueElements(labels);
	//
	// Object[] uclusters = clusters.toArray();
	//
	// for (int i = 0; i < uclusters.length; i++) {
	// int mi = (Integer) uclusters[i];
	//
	// XYSeries p = new XYSeries("Cluster " + mi);
	// for (int j = 0; j < labels.length; j++)
	// if (labels[j] == mi)
	// p.add(X[j][0], X[j][1]);
	// dataset.addSeries(p);
	// }
	//
	// // create chart:
	// JFreeChart chart = ChartFactory.createScatterPlot("Scatter", "X0", "X1",
	// dataset);
	// XYPlot xypl = chart.getXYPlot();
	//
	// for (int i = 0; i < dataset.getSeriesCount(); i++) {
	// int mi = (Integer) uclusters[i];
	// xypl.getRenderer().setSeriesPaint(i, colors.get(i));
	// }
	//
	// for(int i = 0; i < dataset.getSeriesCount(); i++)
	// xypl.getRenderer().setSeriesShape(i, ShapeUtilities.createDiamond(3));
	//
	// ChartFrame frame = new ChartFrame("PROStream",chart);
	// frame.pack();
	// frame.setVisible(true);
	//
	// }
	//
}
