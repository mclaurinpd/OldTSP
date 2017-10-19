import java.util.*;


public class TSPAnnealing {
	private static Trip route = new Trip();
	private static Trip best;
	private static Trip worst;
	private static double startTemp = 100000;
	private static double finalTemp = .0001;
	private static double temp;
	private static final double k = 1.38064852e-23; // boltzmann constant

	// variables relating to statistical anyalysis
	private static double longestPossible = 1.094372401818199 * 14;
	private static double shortestPossible = .05431844593233095 * 14;
	private static double dx = (longestPossible - shortestPossible) / 100;
	private static double[] bins = new double[100];
	private static double sum = 0;
	private static double stdDev = 0;
	private static double numberOfSolns = 0;

	public static void main (String[] args) {
		// some initiliazion below
		long startTime = System.currentTimeMillis();
		temp = startTemp;
		// best and worst are set to route, which is the first trip created
		best = new Trip(route);
		worst = new Trip(route);
		// just for information it prints our starting route and its length
		best.printTrip();
		System.out.println(best.getTripLength());

		sum += route.getTripLength();
		stdDev += (route.getTripLength() * route.getTripLength());
		int binIndex = (int)((route.getTripLength() - shortestPossible)/dx);
		bins[binIndex]++;
		numberOfSolns++;

		// until the temperature reaches a certain point OR a sufficient solution is found
		while (temp > finalTemp && best.getTripLength() > 3.3f) {
			Trip newRoute = new Trip(route);
			Random random = new Random();

			int swap1 = random.nextInt(newRoute.route.size());
			int swap2 = random.nextInt(newRoute.route.size());

			// if they're the same, make a new swap1 index
			while (swap1 == swap2)
				swap1 = random.nextInt(newRoute.route.size());

			newRoute.swapCities(swap1, swap2);

			double delta = newRoute.getTripLength() - route.getTripLength();
			double val = k*temp;

			sum += newRoute.getTripLength();
			stdDev += (newRoute.getTripLength() * newRoute.getTripLength());
			binIndex = (int)((newRoute.getTripLength() - shortestPossible)/dx);
			bins[binIndex]++;
			numberOfSolns++;

			if (delta < 0) {
				route = newRoute;
			}

			else if ((delta >= 0 && ((1/val)*Math.exp(-delta/(val))) > random.nextDouble()/val)) {
				route = newRoute;
			}

			if (best.getTripLength() > route.getTripLength())
				best = new Trip(route);
			if (worst.getTripLength() < route.getTripLength())
				worst = new Trip(route);

			temp = temp/(1 + ((System.currentTimeMillis() - startTime)/1000));
		}
		best.printTrip();
		System.out.println(best.getTripLength());
		worst.printTrip();
		System.out.println(worst.getTripLength());
		double mean = sum / numberOfSolns;
		stdDev = stdDev - (Math.pow(sum, 2) / numberOfSolns);
		stdDev = Math.sqrt(stdDev / (numberOfSolns - 1));
		System.out.println("Mean = " + mean + "\nStandard Deviation: " + stdDev);
		for (int i=0; i < bins.length; i++) {
			System.out.println(bins[i]);
		}
	}
}
