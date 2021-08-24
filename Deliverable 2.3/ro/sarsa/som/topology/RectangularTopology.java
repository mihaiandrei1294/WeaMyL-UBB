package ro.sarsa.som.topology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ro.sarsa.clustering.IDistance;
import ro.sarsa.clustering.distances.EuclideanDistance;
import ro.sarsa.clustering.distances.WeightedEuclideanDistance;
import ro.sarsa.som.NeighborSOMNeuron;
import ro.sarsa.som.SOMNeuron;

public abstract class RectangularTopology implements SOMTopology {

	protected IDistance<double[]> dist;
	// private IDistance dist = new ManhattanDistance();
	protected SOMNeuron[][] neurons;
	protected int row;
	protected int column;
	protected int size;
	/** distanta minima la care pot avea vecini */
	protected double minDist;

	public RectangularTopology(int row, int column, int size) {
		this(row, column, size, new EuclideanDistance());
		// this(row, column, size, new
		// WeightedEuclideanDistance("weightsGender.txt"));
	}

	public RectangularTopology(int row, int column, int size,
			IDistance<double[]> dist) {
		this.row = row;
		this.column = column;
		this.size = size;
		this.dist = dist;
	}

	public IDistance<double[]> getTopoDistance() {
		return this.dist;
	}

	protected void initminDistance() {
		if (getNrNeurons() < 2) {
			minDist = Double.MAX_VALUE;
		} else {
			minDist = dist.distance(getNeuron(0).getNeuronPosition(),
					getNeuron(1).getNeuronPosition());
		}
	}

	public abstract double gridDistance(SOMNeuron a, SOMNeuron b);

	private Double cache[][];

	private synchronized double cachedGridDistance(SOMNeuron a, SOMNeuron b) {
		if (cache == null) {
			cache = new Double[getNrNeurons()][];
			for (int i = 0; i < getNrNeurons(); i++) {
				cache[i] = new Double[i + 1];
			}
		}

		int x = Math.max(a.getIndex(), b.getIndex());
		int y = Math.min(a.getIndex(), b.getIndex());
		// int x = a.getIndex();
		// int y = b.getIndex();
		Double val = cache[x][y];
		if (val != null) {
			return val;
		}
		val = gridDistance(a, b);
		cache[x][y] = val;
		return val;
	}

	@Override
	public List<NeighborSOMNeuron> getNeighbors(SOMNeuron bmu, double radius) {
		if (radius < minDist) {
			return null; // nu am vecini
		}
		List<NeighborSOMNeuron> rez = new ArrayList<NeighborSOMNeuron>();
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < column; j++) {
				if (bmu == neurons[i][j]) {
					continue;
				}
				double distance = cachedGridDistance(bmu, neurons[i][j]);
				if (distance <= radius) {
					rez.add(new NeighborSOMNeuron(bmu, neurons[i][j], distance));
				}
			}
		}
		return rez;
	}

	@Override
	public int getNrNeurons() {
		return row * column;
	}

	@Override
	public SOMNeuron getNeuron(int i) {
		int r = i / column;
		int c = i % column;
		return neurons[r][c];
	}

	@Override
	public double getMaxRadius() {
		return dist.distance(neurons[0][0].getNeuronPosition(),
				neurons[row - 1][column - 1].getNeuronPosition());
	}

	public int getWidth() {
		return column;
	}

	public int getHeight() {
		return row;
	}

	public SOMNeuron getNeuron(int i, int j) {
		return neurons[i][j];
	}

	@Override
	public List<NeighborSOMNeuron> getImediateNeighbors(SOMNeuron n) {
		return getNeighbors(n, minDist);
	}

	@Override
	public double weightDistance(SOMNeuron n, SOMNeuron neuron) {
		return this.dist.distance(n.getWeights(), neuron.getWeights());
	}

	@Override
	public List<SOMNeuron> getAllNeurons() {
		List<SOMNeuron> rez = new ArrayList<SOMNeuron>(row * column);
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < column; j++) {
				rez.add(neurons[i][j]);
			}
		}
		return rez;
	}

	@Override
	public double[] getNeuronCenter(int w, int h, SOMNeuron n) { // nu ar trebui
																	// w /
																	// column si
																	// h / row?
		double[] poz = n.getNeuronPosition();
		double nW = (double) w / row;
		double nH = (double) h / column;
		double x = poz[0] * nW + nW / 2; // left + width / 2
		double y = poz[1] * nH + nH / 2;
		return new double[] { x, y };
	}

	@Override
	public double[] getNeuronDim(int w, int h, SOMNeuron n) {
		// double nW = (double) w / getHeight();
		// double nH = (double) h / getWidth();
		double nW = (double) w / getWidth();
		double nH = (double) h / getHeight();

		return new double[] { nW, nH };
	}

	// private Double[][] isNeighbors;

	@Override
	public boolean isNeighbors(SOMNeuron n1, SOMNeuron n2) {
		List<NeighborSOMNeuron> neighbors = getImediateNeighbors(n1);
		for (int i = 0; i < neighbors.size(); i++) {
			if (neighbors.get(i).getNeuron().equals(n2)) {
				return true;
			}
		}
		return false;
	}
}
