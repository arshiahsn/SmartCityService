package com.SmartCity.tsp;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Random;
import java.util.Map.Entry;

import com.SmartCity.fileio.MyFileWriter;
import com.SmartCity.smartcity.SmartCity;

public class AntTsp {
    // Algorithm parameters:
    // original amount of trail
    private double c = 1.0;
    // trail preference
    private double alpha = 1;
    // greedy preference
    private double beta = 5;
    // trail evaporation coefficient
    private double evaporation = 0.5;
    // new trail deposit coefficient;
    private double Q = 500;
    // number of ants used = numAntFactor*numTowns
    private double numAntFactor = 0.8;
    // probability of pure random selection of the next town
    private double pr = 0.01;

    // Reasonable number of iterations
    // - results typically settle down by 500
    private int maxIterations = 100;

    public int n = 0; // # towns
    public int m = 0; // # ants
    private double graph[][] = null;
    private double trails[][] = null;
    private Ant ants[] = null;
    private Random rand = new Random();
    private double probs[] = null;

    private int currentIndex = 0;

    public int[] bestTour;
    public double bestTourLength;
    
	private int route[];
	private HashMap<Integer,SmartCity> map;
	private MyFileWriter myFileWriter = new MyFileWriter();
	
	public AntTsp(AdjacencyMatrix adjacencyMatrix,
			HashMap<Integer,SmartCity> map){

		this.graph = standardizeMatrix(adjacencyMatrix.getMatrix());
		this.map = map;
		//Make route array equal to number of columns in matrix(number of nodes)
		route = new int[this.graph[0].length];
		
        n = graph.length;
        m = (int) (n * numAntFactor);

        // all memory allocations done here
        trails = new double[n][n];
        probs = new double[n];
        ants = new Ant[m];
        for (int j = 0; j < m; j++)
            ants[j] = new Ant();
	}
	public AntTsp(){
		
	}
	public double[][] standardizeMatrix(double[][] matrix){
		for(int i=0; i<matrix[0].length;i++)
			for(int j=0; j<matrix[0].length;j++)
			{
				matrix[i][j] = 1 - matrix[i][j];
				matrix[i][j]*=10000;
				matrix[i][j]+=1;
			}
		return matrix;
	}

    // Ant class. Maintains tour and tabu information.
    private class Ant {
        public int tour[] = new int[graph.length];
        // Maintain visited list for towns, much faster
        // than checking if in tour so far.
        public boolean visited[] = new boolean[graph.length];

        public void visitTown(int town) {
            tour[currentIndex + 1] = town;
            visited[town] = true;
        }

        public boolean visited(int i) {
            return visited[i];
        }

        public double tourLength() {
            double length = graph[tour[n - 1]][tour[0]];
            for (int i = 0; i < n - 1; i++) {
                length += graph[tour[i]][tour[i + 1]];
            }
            return length;
        }

        public void clear() {
            for (int i = 0; i < n; i++)
                visited[i] = false;
        }
    }

    // Read in graph from a file.
    // Allocates all memory.
    // Adds 1 to edge lengths to ensure no zero length edges.
    public void readGraph(String path) throws IOException {
        FileReader fr = new FileReader(path);
        BufferedReader buf = new BufferedReader(fr);
        String line;
        int i = 0;

        while ((line = buf.readLine()) != null) {
            String splitA[] = line.split(" ");
            LinkedList<String> split = new LinkedList<String>();
            for (String s : splitA)
                if (!s.isEmpty())
                    split.add(s);

            if (graph == null)
                graph = new double[split.size()][split.size()];
            int j = 0;

            for (String s : split)
                if (!s.isEmpty())
                    graph[i][j++] = Double.parseDouble(s) + 1;

            i++;
        }

        n = graph.length;
        m = (int) (n * numAntFactor);

        // all memory allocations done here
        trails = new double[n][n];
        probs = new double[n];
        ants = new Ant[m];
        for (int j = 0; j < m; j++)
            ants[j] = new Ant();
    }
    
    public void inputGraph(AdjacencyMatrix adjacencyMatrix) throws IOException {

    	graph = adjacencyMatrix.getMatrix();
        n = graph.length;
        m = (int) (n * numAntFactor);

        // all memory allocations done here
        trails = new double[n][n];
        probs = new double[n];
        ants = new Ant[m];
        for (int j = 0; j < m; j++)
            ants[j] = new Ant();
    }

    // Approximate power function, Math.pow is quite slow and we don't need accuracy.
    // See: 
    // http://martin.ankerl.com/2007/10/04/optimized-pow-approximation-for-java-and-c-c/
    // Important facts:
    // - >25 times faster
    // - Extreme cases can lead to error of 25% - but usually less.
    // - Does not harm results -- not surprising for a stochastic algorithm.
    public static double pow(final double a, final double b) {
        final int x = (int) (Double.doubleToLongBits(a) >> 32);
        final int y = (int) (b * (x - 1072632447) + 1072632447);
        return Double.longBitsToDouble(((long) y) << 32);
    }

    // Store in probs array the probability of moving to each town
    // [1] describes how these are calculated.
    // In short: ants like to follow stronger and shorter trails more.
    private void probTo(Ant ant) {
        int i = ant.tour[currentIndex];

        double denom = 0.0;
        for (int l = 0; l < n; l++)
            if (!ant.visited(l))
                denom += pow(trails[i][l], alpha)
                        * pow(1.0 / graph[i][l], beta);


        for (int j = 0; j < n; j++) {
            if (ant.visited(j)) {
                probs[j] = 0.0;
            } else {
                double numerator = pow(trails[i][j], alpha)
                        * pow(1.0 / graph[i][j], beta);
                probs[j] = numerator / denom;
            }
        }

    }

    // Given an ant select the next town based on the probabilities
    // we assign to each town. With pr probability chooses
    // totally randomly (taking into account tabu list).
    private int selectNextTown(Ant ant) {
        // sometimes just randomly select
        if (rand.nextDouble() < pr) {
            int t = rand.nextInt(n - currentIndex); // random town
            int j = -1;
            for (int i = 0; i < n; i++) {
                if (!ant.visited(i))
                    j++;
                if (j == t)
                    return i;
            }

        }
        // calculate probabilities for each town (stored in probs)
        probTo(ant);
        // randomly select according to probs
        double r = rand.nextDouble();
        double tot = 0;
        for (int i = 0; i < n; i++) {
            tot += probs[i];
            if (tot >= r)
                return i;
        }

        throw new RuntimeException("Not supposed to get here.");
    }

    // Update trails based on ants tours
    private void updateTrails() {
        // evaporation
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                trails[i][j] *= evaporation;

        // each ants contribution
        for (Ant a : ants) {
            double contribution = Q / a.tourLength();
            for (int i = 0; i < n - 1; i++) {
                trails[a.tour[i]][a.tour[i + 1]] += contribution;
            }
            trails[a.tour[n - 1]][a.tour[0]] += contribution;
        }
    }

    // Choose the next town for all ants
    private void moveAnts() {
        // each ant follows trails...
        while (currentIndex < n - 1) {
            for (Ant a : ants)
                a.visitTown(selectNextTown(a));
            currentIndex++;
        }
    }

    // m ants with random start city
    private void setupAnts() {
        currentIndex = -1;
        for (int i = 0; i < m; i++) {
            ants[i].clear(); // faster than fresh allocations.
            ants[i].visitTown(rand.nextInt(n));
        }
        currentIndex++;

    }

    private void updateBest() {
        if (bestTour == null) {
            bestTour = ants[0].tour;
            bestTourLength = ants[0].tourLength();
        }
        for (Ant a : ants) {
            if (a.tourLength() < bestTourLength) {
                bestTourLength = a.tourLength();
                bestTour = a.tour.clone();
            }
        }
    }

    public static String tourToString(int tour[]) {
        String t = new String();
        for (int i : tour)
            t = t + " " + i;
        return t;
    }

    public int[] solve() {
        // clear trails
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                trails[i][j] = c;

        int iteration = 0;
        // run for maxIterations
        // preserve best tour
        while (iteration < maxIterations) {
            setupAnts();
            moveAnts();
            updateTrails();
            updateBest();
            iteration++;
        }
        // Subtract n because we added one to edges on load
        System.out.println("Best tour length: " + (bestTourLength - n));
        System.out.println("Best tour:" + tourToString(bestTour));
        return bestTour.clone();
    }
    
	List<SmartCity> getMinRoute(){
		return getRoute(bestTour);
	}

	public List<SmartCity> getRoute(int[] route){
		List<SmartCity> list = new ArrayList<SmartCity>();
		for(int i = 0; i<route.length; i++){
			list.add(this.getMap().get(route[i]));
		}
		return this.correctMinRouteList(list);
	}
	
	public List<SmartCity> correctMinRouteList(List<SmartCity> minRouteList){
		String tempServiceType = new String();

		ListIterator<SmartCity> liter = minRouteList.listIterator();
		while(liter.hasNext()){
			SmartCity listIdx = liter.next();
			if(listIdx.getServiceType().equals("start")
					|| listIdx.getServiceType().equals("finish"))
				continue;
			else
				if(listIdx.getServiceType().equals("dummy"))
					liter.remove();
			//					minRouteList.remove(listIdx);
				else{
					if(listIdx.getServiceType().equals(tempServiceType))
						//						minRouteList.remove(listIdx);
						liter.remove();
					else
						tempServiceType = listIdx.getServiceType();
				}											

		}

		//Reverse if needed
		List<SmartCity> correctList = new ArrayList<SmartCity>();
		liter = minRouteList.listIterator();
		SmartCity listIdx = liter.next();
		SmartCity listIdxNext = liter.next();
		if(listIdx.getServiceType().equals("start") &&
				listIdxNext.getServiceType().equals("finish"))
		{
			correctList.add(listIdx);
			while(liter.hasNext())
				liter.next();
			while(liter.hasPrevious()){
				listIdx = liter.previous();
				if(!listIdx.getServiceType().equals("start") && 
						!listIdx.getServiceType().equals("finish"))
					correctList.add(listIdx);
			}
			correctList.add(listIdxNext);
			//printRouteAndCost(correctList);
			printRouteAndCostPrime(correctList);
			return correctList;
		}
		else{
			//printRouteAndCost(minRouteList);
			printRouteAndCostPrime(minRouteList);
			return minRouteList;
		}


	}
	
	public void printRouteAndCostPrime(List<SmartCity> route){
		//A better function to calculate cost(Utility of a given route)
		UtilityFunction utilityFunction = new UtilityFunction();
		double nodeCount = 0;
		System.out.print("Final route: ");
		for(SmartCity node : route){
			System.out.print(getKeyByValue(node)+" ");
			nodeCount++;
		}
		System.out.print("\n");
		
		ListIterator<SmartCity> liter = route.listIterator();
		double cumuUtility = 0;
		double cumuDist = 0;
		double cumuDur = 0;
		double cumuRealDist = 0;
		int cumuQual = 0;
		double cumuServTime = 0;
		
		SmartCity listIdx = liter.next();
		while(liter.hasNext()){
			SmartCity listNextIdx = liter.next();
			//cumuUtility += costs[getKeyByValue(listIdx)][getKeyByValue(listNextIdx)];
			cumuUtility += utilityFunction.calculateUtility(listIdx, listNextIdx).getUtility();
			cumuDist += utilityFunction.getAttributes().getDist();
			cumuDur += utilityFunction.getAttributes().getDuration();
			cumuRealDist += utilityFunction.getAttributes().getRealDist();
			cumuQual += utilityFunction.getAttributes().getQual();
			cumuServTime += utilityFunction.getAttributes().getServiceTime();
			
			listIdx = listNextIdx;
		}
		System.out.print("ACO Utility: "+cumuUtility+"\n");

		
		//Write quality into file
		//It's normalized according to number of nodes(genes)
		//n genes indicate n-1 utilities
		int reqCount = (int) nodeCount-2;
		myFileWriter.write("util-aco"+String.valueOf(reqCount)+"req",String.valueOf(cumuUtility));
		myFileWriter.write("dist-aco"+String.valueOf(reqCount)+"req",String.valueOf(cumuDist));
		myFileWriter.write("realDist-aco"+String.valueOf(reqCount)+"req",String.valueOf(cumuRealDist));
		myFileWriter.write("qual-aco"+String.valueOf(reqCount)+"req",String.valueOf(cumuQual));
		myFileWriter.write("dur-aco"+String.valueOf(reqCount)+"req",String.valueOf(cumuDur));
		myFileWriter.write("servTime-aco"+String.valueOf(reqCount)+"req",String.valueOf(cumuServTime));
		myFileWriter.write("totalTime-aco"+String.valueOf(reqCount)+"req",String.valueOf(cumuServTime+cumuDur));
	}
	
	public Integer getKeyByValue(SmartCity value) {
		for (Entry<Integer, SmartCity> entry : getMap().entrySet()) {
			if (Objects.equals(value, entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}
	public HashMap<Integer, SmartCity> getMap() {
		return map;
	}
	public void setMap(HashMap<Integer, SmartCity> map) {
		this.map = map;
	}
}
