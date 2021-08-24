package ro.sarsa.clustering.distances;

import ro.sarsa.clustering.IDistance;

public class EuclideanDistance implements IDistance<double[]> {

	@Override
	public double distance(double[] a, double[] b) {
		double dist = 0;
		double Numarator=0;
		double Numitor1=0;
		double Numitor2=0;
		for (int i = 0; i < a.length; i++) {
			// pentru evitarea datelor -100 in cazul datelor radar METEO
			// TODO de schimbat inapoi după ce am terminat cu datele meteo
			if ( (a[i] + 100.0 < 0.001) || (b[i] + 100.0 < 0.001)) {
				System.out.println("Am gasit o valoare -100!!!");
//				continue;
			}
			double aux = a[i] - b[i];
//			Numarator += a[i] * b[i];
//			Numitor1 += a[i] * a[i];
//			Numitor2 += b[i] * b[i];
			dist += ((i == 0) ? 1 : 1) * aux * aux;

		}
		//ptr cosine distace
		//return 1-Numarator/(Math.sqrt(Numitor1)*Math.sqrt(Numitor2));
		//pentru Euclidian distance
		return Math.sqrt(dist);
	}

	@Override
	public double getIninity() {
		return Double.MAX_VALUE;
	}

}
