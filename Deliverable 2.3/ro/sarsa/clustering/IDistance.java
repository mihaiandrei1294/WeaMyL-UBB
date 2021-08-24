package ro.sarsa.clustering;


import java.util.ArrayList;
import java.util.List;

public interface IDistance<T> {
	public double distance(T a, T b);

	public double getIninity(); // Infinity?

	default public List<Double> allDistances(T a, List<T> b){
		List<Double> rez = new ArrayList<>(b.size());
		for (T t : b) {
			rez.add((Double) distance(a, t));
		}
		return rez;
	}

	default public List<Double> allDistances(List<T> a, T b){
		List<Double> rez = new ArrayList<>(a.size());
		for (T t : a) {
			rez.add((Double) distance(b, t));
		}
		return rez;
	}

	default public List<Double> allDistances(List<T> a, List<T> b){
		List<Double> rez = new ArrayList<>(b.size());
		for (int i = 0; i<b.size();i++){
			rez.add((Double)distance(a.get(i), b.get(i)));
		}
		return rez;
	}
}
