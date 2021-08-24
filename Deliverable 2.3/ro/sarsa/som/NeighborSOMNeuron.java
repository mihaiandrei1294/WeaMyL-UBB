package ro.sarsa.som;

public class NeighborSOMNeuron {
	private SOMNeuron from; //de unde plecam
	private SOMNeuron neighbor; //neuronul vecin
	private double distance; //distanta dintre cei doi neuroni

	public NeighborSOMNeuron(SOMNeuron from, SOMNeuron neighbor, double distance) {
		this.from = from;
		this.neighbor = neighbor;
		this.distance = distance;
	}

	public SOMNeuron getNeuron() {
		return neighbor;
	}

	public SOMNeuron getFrom() {
		return from;
	}

	public double getDistance() {
		return distance;
	}
}
