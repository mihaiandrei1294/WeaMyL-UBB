package ro.sarsa.som;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import ro.sarsa.clustering.IDistance;
import ro.sarsa.som.topology.SOMTopology;
import ro.sarsa.som.traindata.SOMTrainData;
import ro.sarsa.som.trainsamplechooser.SOMTrainInputChooser;

public class SOM {

	/**
	 * Discriminant function Folosit pentru a determina cel mai apropiat neuron
	 * pentru 0 intrare
	 */
	private IDistance<double[]> dist;
	private SOMTopology comptationL;

	public SOM(int inputSpaceDimension, SOMTopology topo) {
		this(inputSpaceDimension, topo, topo.getTopoDistance());
	}

	public SOM(int inputSpaceDimension, SOMTopology topo,
			   IDistance<double[]> dist) {
		comptationL = topo;
		this.dist = dist;
	}

	public SOMTopology getTopo() {
		return comptationL;
	}

	public BMU computeBestMatchingUnit(double[] input, Object label) {
		BMU rez = computeBestMatchingUnit(input);
		rez.setLabel(label);
		return rez;
	}

	public BMU computeBestMatchingUnit(double[] input) {
		//Acest algoritm de "Compute best matching unit" presupune ca neuronul de la comptationL.getAllNeurons.get(i) este
		// acelasi ca si neuronul de la comptationL.getNeuron(i)
		//Nu sunt sigur daca asta e adevarat pentru orice topologie si daca ar trebui sa fie impus in interfata SOMTopology
		//(daca asa ceva este macar posibil) - dar pentru RectangularTopology este adevarat
		int nrNeurons = comptationL.getNrNeurons();
		List<SOMNeuron> allNeurons= comptationL.getAllNeurons();
		List<double[]> allWeights = new ArrayList<>(allNeurons.size());
		for (SOMNeuron n:allNeurons){
			allWeights.add(n.getWeights());
		}
		List<Double> distances = dist.allDistances(input, allWeights);
		SOMNeuron bmuN = comptationL.getNeuron(0); // neuronul castigator
		double minDist = distances.get(0);


		for (int i = 1; i < nrNeurons; i++) {
			if (distances.get(i) < minDist) { // cu cat distanta e mai mica, neuronul e mai
				// aproape de vectorul de input
				minDist = distances.get(i);
				bmuN = comptationL.getNeuron(i);
			}
		}

		return new BMU(bmuN, minDist, input);
	}

	public void train2Phase(int nrIteration, SOMTrainData trData,
							double startLearnigRate, double startNeighborRadius,
							SOMTrainingListener l) {
		train2Phase(nrIteration, trData, startLearnigRate, startNeighborRadius,
				l, new RandomSOMReainInputChooser(trData.size()));
	}

	public void train2Phase(int nrIteration, SOMTrainData trData,
							double startLearnigRate, double startNeighborRadius,
							SOMTrainingListener l, SOMTrainInputChooser inputChosser) { // ????
		train(nrIteration, trData, startLearnigRate, startNeighborRadius, l,
				inputChosser);
		train(nrIteration / 2, trData, startLearnigRate / 10, 0, l,
				inputChosser);
	}

	public void train(int nrIteration, SOMTrainData trData,
					  double startLearnigRate, SOMTrainingListener l) {
		double maxNeighborRadius = comptationL.getMaxRadius() / 2d;
		train(nrIteration, trData, startLearnigRate, maxNeighborRadius, l);
	}

	public void train(int nrIteration, SOMTrainData trData,
					  double startLearnigRate, double startNeighborRadius,
					  SOMTrainingListener l) {
		train(nrIteration, trData, startLearnigRate, startNeighborRadius, l,
				new RandomSOMReainInputChooser(trData.size()));
	}

	public void train(int nrIteration, SOMTrainData trData,
					  double startLearnigRate, double startNeighborRadius,
					  SOMTrainingListener l, SOMTrainInputChooser inputChooser) {
		double lambdaTimeConstant = nrIteration / Math.log(startNeighborRadius);

		long time = System.currentTimeMillis();
		for (int iteration = 0; iteration < nrIteration; iteration++) {
			// calculez learningRate
			double learningRate = startLearnigRate
					* Math.exp(-(double) iteration / lambdaTimeConstant); // Math.exp(-(double)
			// iteration
			// /
			// nrIteration)
			double neighborRadius = startNeighborRadius
					* Math.exp(-(double) iteration / lambdaTimeConstant);

			// aleg input
			int[] inputIndexes = inputChooser.getNextInputIndex(); // alege un
			// numar
			// (corespunzator
			// unui
			// vector de
			// intrare)
			for (int i = 0; i < inputIndexes.length; i++) {
				int inputIndex = inputIndexes[i];
				trainingStep2(iteration, trData.get(inputIndex), learningRate,
						neighborRadius, l, trData.getLabel(inputIndex));
			}

			if (iteration % 500 == 0) {
				System.out.println("Training time for 500 iter.:" + ((double)(System.currentTimeMillis() - time))/1000.0 + "seconds");
				System.out.println("Iteration:" + iteration);
				time = System.currentTimeMillis();
			}
		}

	}

	/**
	 * O iteratie din training
	 */

	public void trainingStep(int curIter, double input[], double learningRate,
							 double neighborRadius, SOMTrainingListener l) {
		// caut Best maching unit
		BMU bmu = computeBestMatchingUnit(input);

//		bestMatchingUnits.put(Arrays.hashCode(input), bmu);
		// actualizez weights de la bmu
		bmu.getNeuron().adjustWeights(input, learningRate, 1);
		// obtin lista vecinilor ce pica in radiusul cerut
		List<NeighborSOMNeuron> neighbors = comptationL.getNeighbors(
				bmu.getNeuron(), neighborRadius);
		// System.out.println("lr:" + learningRate + " nR:" + neighborRadius);

		// actualizez valorile in functie de cat de apropiat e de BMU
		if (neighbors != null && neighbors.size() > 0) {
			updateNeighbors(input, learningRate, neighborRadius, neighbors);
		}

		if (l != null) {
			l.trainStepPerformed(curIter, input, bmu, neighbors); // face un
			// repaint
		}
	}

	public void trainingStep2(int curIter, double input[], double learningRate,
							  double neighborRadius, SOMTrainingListener l, Object lbl) {
		// caut Best maching unit
		BMU bmu = computeBestMatchingUnit(input, lbl);

		// actualizez weights de la bmu
		bmu.getNeuron().adjustWeights(input, learningRate, 1);
		// obtin lista vecinilor ce pica in radiusul cerut
		List<NeighborSOMNeuron> neighbors = comptationL.getNeighbors(
				bmu.getNeuron(), neighborRadius);
		// System.out.println("lr:" + learningRate + " nR:" + neighborRadius);

		// actualizez valorile in functie de cat de apropiat e de BMU
		if (neighbors != null && neighbors.size() > 0) {
			updateNeighbors(input, learningRate, neighborRadius, neighbors);
		}

		if (l != null) {
			l.trainStepPerformed(curIter, input, bmu, neighbors); // face un
			// repaint
		}
	}

	private void updateNeighbors(double[] input, double learningRate,
								 double neighborRadius, List<NeighborSOMNeuron> neighbors) {
		final double aux = 2 * neighborRadius * neighborRadius;
		for (int i = 0; i < neighbors.size(); i++) {
			NeighborSOMNeuron neighborSOMNeuron = neighbors.get(i);
			double influence = Math
					.exp(-(neighborSOMNeuron.getDistance() * neighborSOMNeuron
							.getDistance()) / aux);
			neighborSOMNeuron.getNeuron().adjustWeights(input, learningRate,
					influence);
		}
	}

	/**
	 * Calculeaza eroarea... e vorba de media dintre |x-mx| pentru fiecare
	 * input... unde x e un imput iar mx e bmu pentru acel input
	 *
	 * @param td
	 * @return
	 */
	public double computeAverageQuantizationError(SOMTrainData td) {
		double rez = 0;
		for (int i = 0; i < td.size(); i++) {
			double[] input = td.get(i);
			BMU bmu = computeBestMatchingUnit(input);
			rez += dist.distance(input, bmu.getNeuron().getWeights());
		}
		return rez / td.size();
	}

	public HashMap<SOMNeuron, List<Double>> getLabelsPerNeuron(SOMTrainData trData){
		HashMap<SOMNeuron, List<Double>> rez = new HashMap<>();
		for (int i = 0; i < trData.size(); i++) {
			BMU bmu = computeBestMatchingUnit(trData.get(i), trData.getLabel(i));
			SOMNeuron neuron = bmu.getNeuron();
			if (rez.containsKey(neuron)){
				rez.get(neuron).add(Double.parseDouble((String)bmu.getLabel()));
			} else {
				List<Double> l = new ArrayList<>();
				l.add(Double.parseDouble((String)bmu.getLabel()));
				rez.put(neuron, l);
			}
			if (i % 2000 == 0){
				System.out.println("Computed BMU for hashmap " + i + "/" + trData.size());
			}
		}


		return rez;
	}

	public List<BMU> getBMUS(SOMTrainData trD) {
		System.out.println("Starting BMU computing for all instances for drawing.");
		List<BMU> bmus = new ArrayList<BMU>(trD.size());
		for (int i = 0; i < trD.size(); i++) {
			bmus.add(computeBestMatchingUnit(trD.get(i), trD.getLabel(i)));
			if (i % 2000 == 0){
				System.out.println("Computed BMU " + i + "/" + trD.size());
			}
		}
		return bmus;
	}

	/**
	 * Eroare ce ia in calcul si veciinii suma(Hci*|x-mi]^2);
	 *
	 * @param td
	 * @return
	 */
	public double computeAverageDistorsionError(SOMTrainData td) {
		return -1;
	}

	public void save_to_disk(String path){

		BufferedWriter writer;
		try{
			writer = new BufferedWriter(new FileWriter(path));
		} catch (IOException e){
			e.printStackTrace();
			return;
		}

		List<SOMNeuron> neurons = this.comptationL.getAllNeurons();
		int contor = 0;
		for (SOMNeuron neuron: neurons){
			contor += 1;
			if (contor == 390){
				System.out.println("LALALANDY?");
			}
			double[] weights = neuron.getWeights();
			String weightsAsString = java.util.Arrays.toString(weights);
			try {
				writer.write(weightsAsString);
				writer.newLine();
			} catch(IOException e){
				e.printStackTrace();
				return;
			}
		}
		try {
			writer.close();
		} catch(IOException e){
			e.printStackTrace();
			return;
		}
	}

	public void init_from_file(String path) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(path));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        String st;
        List<SOMNeuron> existing_neurons = new ArrayList<>();
        try {
            int contor = 0;
            while ((st = reader.readLine()) != null) {
				st = st.replaceAll("\\[","");
				st = st.replaceAll("]","");
                String[] weightsString = st.split(", ");
                double[] weights = new double[weightsString.length];
                int contor2 = 0;
                for (String weightString:weightsString){
                    weights[contor2] = Double.parseDouble(weightString);
                    contor2 += 1;
                }

                if (contor % 100 == 0){
                    System.out.println("reading neuron " + contor);
                }
                SOMNeuron neuron = new SOMNeuron(contor,new double[] { contor }, weights.length);
                neuron.init(weights);
                existing_neurons.add(neuron);
                contor += 1;
//                if (contor == 398){
//                    continue;
//                }
            }
        } catch (IOException e){
            e.printStackTrace();
            return;
        }

        comptationL.initFromExisting(existing_neurons);
    }
}
