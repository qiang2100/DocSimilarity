package ProprocessDoc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

public class PrintWord2Vec {

	String vectorPath = "C:/Users/jipeng/Desktop/Qiang/Word2Vec/glove.6B.300d.txt";
	
	public void readVector()
	{
		try
		{
			BufferedReader br1 = new BufferedReader(new FileReader(vectorPath));
			String line = "";
			//int num = 0;
			
			FileWriter wordfw = new FileWriter("C:/Users/jipeng/Desktop/Qiang/KNN/Word2Vec/glove.6B.300d.word.txt");
			
			BufferedWriter wordbw = new BufferedWriter(wordfw);
			
			FileWriter vectorfw = new FileWriter("C:/Users/jipeng/Desktop/Qiang/KNN/Word2Vec/glove.6B.300d.vector.txt");
			
			BufferedWriter vectorbw = new BufferedWriter(vectorfw);
			
			//float vector = 0;
			while ((line = br1.readLine()) != null) {
			
				String word[] = line.split(" ");
				
				//allWord.add(word[0]);
				wordbw.write(word[0]);
				wordbw.newLine();
				//float []vec = new float[word.length-1];
				//double len = 0;
				String vector = "";
				for(int i=1; i<word.length; i++)
				{
					vector += word[i] + " ";///(word.length-1);
					
				}
				vectorbw.write(vector);
				vectorbw.newLine();
				
			}
			
			br1.close();
			wordbw.close();
			wordfw.close();
			vectorbw.close();
			vectorfw.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PrintWord2Vec pw = new PrintWord2Vec();
		
		pw.readVector();
	}

}
