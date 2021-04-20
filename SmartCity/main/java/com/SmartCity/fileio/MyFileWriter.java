package com.SmartCity.fileio;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MyFileWriter {



	public void write(String fileName, String data) {
		
		String folderName = "results/";
		fileName += ".txt";
		fileName = folderName + fileName;
		data += "\n";
		BufferedWriter bw = null;
		FileWriter fw = null;

		try {

			//data = " This is new content";

			File file = new File(fileName);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			// true = append file
			fw = new FileWriter(file.getAbsoluteFile(), true);
			bw = new BufferedWriter(fw);

			bw.write(data);

			//System.out.println("Done");

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			try {

				if (bw != null)
					bw.close();

				if (fw != null)
					fw.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}
		}

	}

}
