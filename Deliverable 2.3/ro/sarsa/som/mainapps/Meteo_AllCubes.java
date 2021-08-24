package ro.sarsa.som.mainapps;

import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFrame;

import ro.sarsa.clustering.distances.ParallelEuclidianDistance;
import ro.sarsa.som.SOM;
import ro.sarsa.som.SOMNeuron;
import ro.sarsa.som.topology.TorusSOMTopology;
import ro.sarsa.som.traindata.ByteFileReader;
import ro.sarsa.som.traindata.FilterdTrainingData;
import ro.sarsa.som.traindata.SOMTrainData;
import ro.sarsa.som.traindata.SimpleTrainData;
import ro.sarsa.som.umatrix.LabelApparenceProvider;
import ro.sarsa.som.umatrix.UMatrixPanel;

public class Meteo_AllCubes {

	private static double compute_som_measure(SOM som, SOMTrainData trData, boolean with_0){
		double rezsum = 0.0;
		int rezcount = 0;
		HashMap<SOMNeuron, List<Double>> labelsPerNeuron = som.getLabelsPerNeuron(trData);
		for (List<Double> labels:labelsPerNeuron.values()){
			boolean containsZero = false;
			boolean containsNonZero = false;
			double sum = 0.0;
			int count = 0;
			for (int i=0; i<labels.size()-1; i++){
				for (int j=i+1; j<labels.size(); j++){
					sum += Math.abs(labels.get(j) - (double)labels.get(i));
					count += 1;
					if (sum == 0.0){
						containsZero = true;
					} else {
						containsNonZero = true;
					}
				}
			}
			if (with_0){
				if (containsNonZero && count > 0) {
					rezsum += (sum / count);
					rezcount += 1;
				}
			} else {
				if ((!containsZero) && count > 0) {
					rezsum += (sum / count);
					rezcount += 1;
				}
			}
		}

		System.out.println("Number of neurons with nonzero labels: " + rezcount);

		return rezsum/rezcount;
	}

	public static void main(String[] args) throws IOException {

		int row = 40;
		int col = 40;

		int ftrsToRemove[] = new int[] {};
		String featureToPredict = "VIL";
		String catiTimpi = "5";

		String path = "...";

		SOMTrainData unNormalizedtrData = new ByteFileReader(path +
				featureToPredict + "_date_per_punct_141_cu_" + catiTimpi +"_timpi_inainte.txt");

		unNormalizedtrData = new FilterdTrainingData(unNormalizedtrData, ftrsToRemove);


		SOMTrainData trData = new SimpleTrainData(unNormalizedtrData);

		TorusSOMTopology topo = new TorusSOMTopology(row, col, trData.getDataDimension(), new ParallelEuclidianDistance());

		topo.initRandom(0, 1);

		SOM som = new SOM(trData.getDataDimension(), topo);

		//For training
//		long time = System.currentTimeMillis();
//		som.train(1_000_000, trData, 0.1, null);
//		System.out.println("Training time:" + ((double)(System.currentTimeMillis() - time))/1000.0 + "seconds");

		String model_path = "C:\\Users\\Andrei\\Desktop\\andrei workplace\\Java\\JavaSOM\\src\\ro\\sarsa\\som\\trainedmodels";
//		som.save_to_disk(model_path + "\\" + featureToPredict +"_141_" + catiTimpi + "_timpi");

		som.init_from_file(model_path + "\\" + featureToPredict +"_141_" + catiTimpi + "_timpi");

		double som_measure = compute_som_measure(som, trData, true);
		System.out.println("Final overall error 1: " + som_measure);
		System.out.println("Final overall error 1 normalized: " + som_measure/65.0);

		double som_measure_2 = compute_som_measure(som, trData, false);
		System.out.println("Final overall error 2: " + som_measure_2);
		System.out.println("Final overall error 2 normalized: " + som_measure_2/65.0);


		/*
		 * Latice2DSOMTopology topo = new Latice2DSOMTopology(row, col,
		 * trData.getDataDimension()); topo.initRandom(-0.5, 0.5); SOM som = new
		 * SOM(trData.getDataDimension(), topo);
		 */

		// umatrix
		UMatrixPanel umP = new UMatrixPanel(som, trData, new LabelApparenceProvider() {

			@Override
			public String getText(Object label) {
				if (label == null) {
					return "null";
				}
				String str_label = label.toString();
				double d_label = Double.parseDouble(str_label);
				int int_label = (int)d_label;
				return Integer.toString(int_label);
			}

			public Color getColor(Object label) {
				if (label == null) {
					System.err.println();
					return Color.black;
				}
				double max_value;
				if (featureToPredict == "V01" || featureToPredict == "V02"){
					max_value = 33;
				} else {
					max_value = 65;
				}
				double v = Double.parseDouble(label.toString());
				if (v == 0.0) {
					double hue = 0;
					double saturation = 0;
					double brightness = 1.0;
					return Color.getHSBColor((float) hue, (float) saturation, (float) brightness);
				}
				if (v > 0.0) {
					double hue = 0;
					double saturation = 0.1 + (v * 0.7) / max_value;
					double brightness = 0.9 - (v * 0.7) / max_value;
//					double brightness = 1.0 - (v/ 33)*0.6;
					return Color.getHSBColor((float) hue, (float) saturation, (float) brightness);
				} else {
					v = Math.abs(v);
					double hue = 0.3;
					double saturation = 0.1 + (v * 0.7) / max_value;
					double brightness = 0.9 - (v * 0.7) / max_value;
					return Color.getHSBColor((float) hue, (float) saturation, (float) brightness);
				}
			}

		});
		JFrame jf = new JFrame("UMatrix");
		jf.setSize(500, 500);
		jf.getContentPane().add(umP);
		jf.setVisible(true);

	}
}
