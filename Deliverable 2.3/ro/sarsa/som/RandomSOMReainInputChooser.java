package ro.sarsa.som;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ro.sarsa.som.trainsamplechooser.SOMTrainInputChooser;

/**
 * Alege aleator un sample la fiecare pas de antrenare (epoca)
 * 
 * @author istvan
 * 
 */

public class RandomSOMReainInputChooser implements SOMTrainInputChooser {
	private Random rnd = new Random();
	private int nrInputSamples;
	private List<Integer> remainIndexes;
	private int current_index;

	public RandomSOMReainInputChooser(int nrInputSamples) {
		this.nrInputSamples = nrInputSamples;
		reinitRemainIndexes();
		current_index = 0;
	}

	private void reinitRemainIndexes() {
		remainIndexes = new ArrayList<Integer>(nrInputSamples);
		for (int i = 0; i < nrInputSamples; i++) {
			remainIndexes.add(i);
		}
	}

	@Override
	public int[] getNextInputIndex() {
		if (remainIndexes.isEmpty()) {
			reinitRemainIndexes();
		}
		return new int[] { rnd.nextInt(remainIndexes.size()) };
	}
}


//	@Override
//	public int[] getNextInputIndex() {
//		current_index += 1;
//		if (current_index >= remainIndexes.size()) {
//			current_index = 0;
//		}
//		return new int[] { current_index };
//	}
//}