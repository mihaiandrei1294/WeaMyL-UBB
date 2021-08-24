package ro.sarsa.som.traindata;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ByteFileReader implements SOMTrainData {

	private byte[][] data;
	private String[] ids;
	private String fileName;

	public ByteFileReader(String fileName) {
		this.fileName = fileName;
		try {
			data = readFromFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public int size() {
		return data.length;
	}

	@Override
	public double[] get(int inputIndex) {
		byte[] rezByte = data[inputIndex];
		double[] rez = new double[rezByte.length];
		for (int i=0;i<rez.length;i++) {
			rez[i] = rezByte[i];
		}
		return rez;
	}

	@Override
	public int getDataDimension() {
		return data[0].length;
	}

	@Override
	public Object getLabel(int inputIndex) {
		int row = inputIndex % data.length;
		return ids[row];

	}

	boolean apare(int i, int ftrsToRemove[]) {
		for (int j = 0; j < ftrsToRemove.length; j++)
			if (i == ftrsToRemove[j])
				return true;
		return false;
	}

	private byte[][] readFromFile() throws IOException {
		byte data[][] = null;

		BufferedReader reader = new BufferedReader(new FileReader(this.fileName));

		int nrInstances = Integer.parseInt(reader.readLine());
		int nrAttr = Integer.parseInt(reader.readLine());
//		TO DO de nu uitat de schimbat asta
//		int nrAttr = 5000;

		System.out.println(nrInstances);
		System.out.println(nrAttr);

		ids = new String[nrInstances];
		data = new byte[nrInstances][nrAttr];
		// System.out.println("LEN = " + data.length);

		String line = null;
		int contor = 0;
		while ((line = reader.readLine()) != null) {

			// System.out.println("LINE = " + line);

			String[] parts = line.split(",");
			int i;
			ids[contor] = parts[0];// .substring(0, 1);

			// System.out.println("Id[" + contor + "] = " + ids[contor][0]);
			// System.out.println(ids[contor]);

			int k = 0;
			for (i = 1; i <= nrAttr; i++) {

				// System.out.println("contor = " + contor + " ");
				// System.out.println("ID = " + ids[contor]);
				try {
					double val = Double.parseDouble(parts[i]);
					if (val > 127 || val < -127) {
						System.out.println("out of bounds!!!! val=" + val);
					} else {
						data[contor][k] = (byte)val;
					}
						k++;
					
				} catch (Exception e) {
					System.out.println(e);
				}
				// System.out.print(parts[i]+" ");

			}

			contor++;
			if (contor % 10_000 == 0){
				System.out.println("Reading line " + contor + " of " + nrInstances);
			}
		}

		reader.close();

		return data;
	}

}
