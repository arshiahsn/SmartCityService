package iotnode;

import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;

public class ReadFile {
	private String path;
	public ReadFile(String filePath){
		path = filePath;
	}
	public String[] OpenFile() throws IOException{
		FileReader fileReader = new FileReader(path);
		BufferedReader bufferedReader = new BufferedReader(fileReader);

		int numberOfLines = ReadLines();

		String[] textData = new String[numberOfLines];
		for(int i=0;i<numberOfLines;i++){
			textData[i] = bufferedReader.readLine();
		}
		bufferedReader.close();
		return textData;
	}
	public int ReadLines() throws IOException{
		FileReader fileReader = new FileReader(path);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String counterLine;
		int numberOfLines = 0;
		while((counterLine = bufferedReader.readLine()) != null){
			numberOfLines++;
		}
		bufferedReader.close();
		return numberOfLines;
	}

}
