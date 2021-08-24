package ro.sarsa.som;

public class BMU {
	@SuppressWarnings("unused")
	private double dist = Double.MAX_VALUE;// distanta minima
	@SuppressWarnings("unused")
	private double[] input;
	private SOMNeuron bmuN = null;// neuronul castigator
	private Object label = null;

	public BMU(SOMNeuron n, double dist, double[] input) {
		this.bmuN = n;
		this.dist = dist;
		this.input = input;
	}

	public BMU(SOMNeuron n, double dist, double[] input, String label) {
		this.bmuN = n;
		this.dist = dist;
		this.input = input;
		this.label = label;
	}

	public Object getLabel() {
		return this.label;
	}

	public void setLabel(Object lbl) {
		this.label = lbl;
	}

	public int getNeuronIndex() {
		return bmuN.getIndex();
	}

	public SOMNeuron getNeuron() {
		return bmuN;
	}

	public boolean equals(Object o) {
		return bmuN == ((BMU) o).bmuN;
	}
}
