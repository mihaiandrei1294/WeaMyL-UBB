package ro.sarsa.som.topology;

import java.awt.Shape;
import java.util.List;

import ro.sarsa.clustering.IDistance;
import ro.sarsa.som.NeighborSOMNeuron;
import ro.sarsa.som.SOMNeuron;

public interface SOMTopology {

	int getNrNeurons();

	SOMNeuron getNeuron(int i);

	/**
	 * Obtine toti vecinii pe o raza data
	 *
	 * @param bmu
	 * @param radius
	 * @return
	 */
	List<NeighborSOMNeuron> getNeighbors(SOMNeuron bmu, double radius);

	/**
	 * Pentru un neuron returneaza toti vecinii imediati (cel mai apropiati)
	 *
	 * @param n
	 * @return
	 */
	List<NeighborSOMNeuron> getImediateNeighbors(SOMNeuron n);

	double getMaxRadius();

	/**
	 * Returneaza distanta intre 2 neuroni
	 *
	 * @param n
	 * @param neuron
	 * @return
	 */
	double weightDistance(SOMNeuron n, SOMNeuron neuron);

	/**
	 * Pentru a realiza o reprezentare grafica Metoda returneaza spatiul ocupat
	 * de acest neuron scalat la dimensiunile date
	 *
	 * @param w
	 * @param h
	 * @param n
	 * @return
	 */
	public Shape getNeuronShape(int w, int h, SOMNeuron n);

	/**
	 * Return the general graphic representation of a neuron
	 * We can translate this to the desired position to draw all the neurons
	 * Putem folosi
	 * @param w
	 * @param h
	 * @return
	 */
	public Shape getNeuronShape(int w, int h);

	/**
	 * Centrul neuronului scalat la w,h
	 *
	 * @param w
	 * @param h
	 * @param n
	 * @return
	 */
	public double[] getNeuronCenter(int w, int h, SOMNeuron n);

	/**
	 * Dimensiune neuronului (w,h) scalat la w,h dat Util daca vrem sa scriem
	 * ceva in interiorul neuronului
	 *
	 * @param w
	 * @param h
	 * @param n
	 * @return
	 */
	public double[] getNeuronDim(int w, int h, SOMNeuron n);

	List<SOMNeuron> getAllNeurons();

	boolean isNeighbors(SOMNeuron n1, SOMNeuron n2);
	
	public IDistance<double[]> getTopoDistance();

	public void initFromExisting(List<SOMNeuron> existing_neurons);

}