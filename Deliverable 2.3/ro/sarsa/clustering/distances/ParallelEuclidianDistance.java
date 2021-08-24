package ro.sarsa.clustering.distances;

import ro.sarsa.clustering.IDistance;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ParallelEuclidianDistance implements IDistance<double[]> {
    private ExecutorService executor;

    public ParallelEuclidianDistance(){
        executor = Executors.newCachedThreadPool();
    }

    @Override
    public double distance(double[] a, double[] b) {
        double dist = 0;
        for (int i = 0; i < a.length; i++) {
            double aux = a[i] - b[i];
            dist += ((i == 0) ? 1 : 1) * aux * aux;
        }
        return Math.sqrt(dist);
    }

    @Override
    public double getIninity() {return Double.MAX_VALUE;}

    @Override
    public List<Double> allDistances(double[] a, List<double[]> b) {
        List<Future<Double>> futures = new ArrayList<>(b.size());
        for (double[] doubles : b) {
            CallableEuclidianDistance calldist = new CallableEuclidianDistance(a, doubles);
            futures.add(executor.submit(calldist));
        }
        List<Double> rez = new ArrayList<>(futures.size());
        for (Future<Double> f:futures){
            try {
                rez.add(f.get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return rez;
    }

    @Override
    public List<Double> allDistances(List<double[]> a, double[] b) {
        List<Future<Double>> futures = new ArrayList<>(a.size());
        for (double[] doubles : a) {
            CallableEuclidianDistance calldist = new CallableEuclidianDistance(b, doubles);
            futures.add(executor.submit(calldist));
        }
        List<Double> rez = new ArrayList<>(futures.size());
        for (Future<Double> f:futures){
            try {
                rez.add(f.get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return rez;
    }

    @Override
    public List<Double> allDistances(List<double[]> a, List<double[]> b) {
        List<Future<Double>> futures = new ArrayList<>(b.size());
        for (int i =0; i<b.size(); i++){
            CallableEuclidianDistance calldist = new CallableEuclidianDistance(a.get(i), b.get(i));
            futures.add(executor.submit(calldist));
        }
        List<Double> rez = new ArrayList<>(futures.size());
        for (Future<Double> f:futures){
            try {
                rez.add(f.get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return rez;
    }
}

class CallableEuclidianDistance implements Callable<Double> {

    private double[] a;
    private double[] b;

    CallableEuclidianDistance(double[] a, double[] b){
        this.a = a;
        this.b = b;
    }

    @Override
    public Double call() throws Exception {
        double dist = 0;
        for (int i = 0; i < a.length; i++) {
            double aux = a[i] - b[i];
            dist += ((i == 0) ? 1 : 1) * aux * aux;
        }
        return Math.sqrt(dist);
    }
}