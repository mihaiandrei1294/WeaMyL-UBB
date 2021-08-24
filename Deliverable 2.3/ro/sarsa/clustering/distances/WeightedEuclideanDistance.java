package ro.sarsa.clustering.distances;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import ro.sarsa.clustering.IDistance;

public class WeightedEuclideanDistance implements IDistance<double[]>{
	
	private double[] weights;
	
	public WeightedEuclideanDistance(String fileName){
		weights = readFromFile(fileName);
	}
	
	public double[] getW(){
		return this.weights;
	}
	
	private double[] readFromFile(String fileName){
		double[] w = null;
		try{
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String line = null;
			while((line = reader.readLine()) != null){
				String[] parts = line.split("[,;-]");
				if(w == null){
					w = new double[parts.length];
				}
				
				int i;
				for(i=0; i < parts.length; i++){
					w[i] = Double.parseDouble(parts[i]);
				}
			}
			
			reader.close();
		}catch(IOException ex)
		{
			System.out.println(ex.getMessage());
		}
		
		return w;
	}
	

	@Override
	public double distance(double[] a, double b[]) {
		double dist = 0;
		for (int i = 0; i < a.length; i++) {
			double aux = a[i] - b[i];
			dist += weights[i] * aux * aux;
		}
		return Math.sqrt(dist);

	}

	@Override
	public double getIninity() {
		return Double.MAX_VALUE;
	}
	
	
}
