package ro.sarsa.som.mainapps;

import java.awt.Color;
import javax.swing.JFrame;

import ro.sarsa.som.SOM;
import ro.sarsa.som.topology.TorusSOMTopology;
import ro.sarsa.som.traindata.FileTrainData;
import ro.sarsa.som.traindata.FilterdTrainingData;
import ro.sarsa.som.traindata.NormalizedTrainingData;
import ro.sarsa.som.traindata.SOMTrainData;
import ro.sarsa.som.traindata.SimpleTrainData;
import ro.sarsa.som.umatrix.LabelApparenceProvider;
import ro.sarsa.som.umatrix.UMatrixPanel;

public class Meteo {
	public static void main(String[] args) {

		int row = 80;
		int col = 80;

		int ftrsToRemove[] = new int[] {};

		SOMTrainData unNormalizedtrData = new FileTrainData("107_VIL_AllCells_AtLeastOneNonEmpty_TimeWindow(1)_NeighborsWindow(25).txt");
		
		
		unNormalizedtrData = new FilterdTrainingData(unNormalizedtrData, ftrsToRemove);

		SOMTrainData trData = new NormalizedTrainingData(unNormalizedtrData);
		// make a copy so we do not compute the input each time
		trData = new SimpleTrainData(trData);

		//SOMTrainData trData = new SimpleTrainData(unNormalizedtrData);

		TorusSOMTopology topo = new TorusSOMTopology(row, col, trData.getDataDimension());

		topo.initRandom(0, 1);

		SOM som = new SOM(trData.getDataDimension(), topo);

		/*
		 * Latice2DSOMTopology topo = new Latice2DSOMTopology(row, col,
		 * trData.getDataDimension()); topo.initRandom(-0.5, 0.5); SOM som = new
		 * SOM(trData.getDataDimension(), topo);
		 */

		// umatrix
		UMatrixPanel umP = new UMatrixPanel(som, trData, new LabelApparenceProvider() {

			@Override
			public String getText(Object label) {
				return "x";
			}

			@Override()
			public Color getColor(Object label) {
				if (label == null) {
					System.err.println();
				}
				System.out.println(label.toString());
				
				if (label.toString().charAt(0) == 'a') {
					return Color.red;
				}
				if (label.toString().charAt(0) == 'b')
					return Color.blue;
				if (label.toString().charAt(0) == 'c')
					return Color.green;
				if (label.toString().charAt(0) == 'd')
					return Color.orange;
				if (label.toString().charAt(0) == 'e')
					return Color.cyan;
				if (label.toString().charAt(0) == 'f')
					return Color.yellow;
				if (label.toString().charAt(0) == 'g')
					return Color.white;
				if (label.toString().charAt(0) == 'h')
					return Color.pink;
				if (label.toString().charAt(0) == 'i')
					return Color.gray;
				if (label.toString().charAt(0) == 'j')
					return Color.lightGray;
				if (label.toString().charAt(0) == 'k')
					return Color.darkGray;
				if (label.toString().charAt(0) == 'l')
					return Color.getHSBColor(0.25f, 0.67f, 0.9f);
				return Color.magenta;
				// return Color.red;
			}
		});
		JFrame jf = new JFrame("UMatrix");
		jf.setSize(400, 400);
		jf.getContentPane().add(umP);
		jf.setVisible(true);

		// antrenam
		// som.train2Phase(3000, trData, 0.7, topo.getMaxRadius() / 3, umP);
		som.train(200001, trData, 0.1, umP);
	}
}
