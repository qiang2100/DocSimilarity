
package ProprocessDoc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import kex.stopwords.Stopwords;
import kex.stopwords.StopwordsEnglish;

public class AllData {
	

	ArrayList<String> wordsArr = new ArrayList<String>();

	ArrayList<Integer> wordFreArr = new ArrayList<Integer>();
	
	ArrayList<Integer> labelArr = new ArrayList<Integer>();
	
	ArrayList<ArrayList<Integer>> textContentArr = new ArrayList<ArrayList<Integer>>();

	
	int docNum = 0;
	
	private Stopwords m_EnStopwords = new StopwordsEnglish();

	MaxentTagger tagger = new MaxentTagger("C:/Users/jipeng/Desktop/TopicModel/stanford-postagger-2014-01-04/models/english-left3words-distsim.tagger");
	

	
	HashMap<Integer,Integer> id2DocFre = new HashMap<Integer,Integer>();
	
	Pattern p = Pattern.compile("[^a-zA-Z]", Pattern.CASE_INSENSITIVE);
	
	int idNum = 0;
	int clusterId = 0;
	DecimalFormat df = new DecimalFormat("0.000");
	
	String news20Path = "Source/20news-bydate-train";
	String newsName = "20News/news20";
	public void getText(String pathDir) throws Exception
	{
	
		File folder = new File(pathDir);
		File[] listOfFiles = folder.listFiles();
	
		
		
		for (File file : listOfFiles) {
		  
			File[] nameList = file.listFiles();
		
			for(File subFile:nameList)
			{
				BufferedReader br = new BufferedReader(new FileReader(subFile.getAbsolutePath()));
					//System.out.println(subFile.getAbsolutePath());
				String line = br.readLine();
				ArrayList<Integer> oneTextArr = new ArrayList<Integer>();
				ArrayList<String> allWords = new ArrayList<String>();
				
				while(line!=null)
				{
					//System.out.println(line);
					
					String tagged = tagger.tagString(line);
					
					String words[] = tagged.split("[_ ]");
					
					for(int i=0; i<words.length; i=i+2)
					{
						allWords.add(words[i]);
					}
					line = br.readLine();
				}
				for(int i=0;  i<allWords.size(); i++)
				{
						String lowWord = allWords.get(i).toLowerCase();
						
						Matcher m = p.matcher(lowWord); // only save these strings only contains characters
				    	if( !m.find() && lowWord.length()>=3 && lowWord.length() < 25)
				    	{				
							 if (!m_EnStopwords.isStopword(lowWord)) 
			    			  
					    	  {
				    			  if (!wordsArr.contains(lowWord))
				    			  {
				    				  wordsArr.add(lowWord);
				    				  //word2IdArr.add(id);
				    				  wordFreArr.add(1);
				    				//  outputText += id + " ";
				    				 // wordFre.add(1);
				    				  oneTextArr.add(idNum);
				    				  idNum++;
				    				 
				    			  }else
				    			  {
				    				  int index = wordsArr.indexOf(lowWord);
				    				  //int id = word2IdArr.get(index);
				    				  wordFreArr.set(index, wordFreArr.get(index)+1);
				    				  oneTextArr.add(index);
				    				 // outputText += word2id.get(token) + " ";
				    			  }

					    	  }
				    	  }
				}
				docNum++;
				Collections.sort(oneTextArr);
				textContentArr.add(oneTextArr);
				labelArr.add(clusterId);
			}
			clusterId++;
		}
		
		
		//System.out.println(flag);
		//numNews = flag;
	}

	
	

	
	public void printTFIDF() throws Exception
	{
		docNum = textContentArr.size();
		int [][]dataTF = new int[docNum][idNum];
		
		
		
		for(int i=0; i<textContentArr.size(); i++)
		{
			ArrayList<Integer> oneText = textContentArr.get(i);
			
			HashSet<Integer> unitSet = new HashSet<Integer>();
			for(int j=0; j<oneText.size(); j++)
			{
				int id = oneText.get(j);
				if(!unitSet.contains(id))
				{
					if(id2DocFre.containsKey(id))
						id2DocFre.put(id, id2DocFre.get(id)+1);
					else
						id2DocFre.put(id,1);
					
					
					dataTF[i][id] =  1;
					
					
					unitSet.add(id);
				}else
				{
					dataTF[i][id] +=  1;
					
				}
			}
		}
		
		
		
		FileWriter fwTrainText = new FileWriter("C:/Users/jipeng/Desktop/Qiang/KNN/" + newsName + "TTFIDF.txt");
		BufferedWriter bwTrainText = new BufferedWriter(fwTrainText);
		
		
		FileWriter fwTFText = new FileWriter("C:/Users/jipeng/Desktop/Qiang/KNN/" + newsName + "TF.txt");
		BufferedWriter bwTFText = new BufferedWriter(fwTFText);
		
		
		for(int i=0; i<dataTF.length; i++)
		{
			//String oneText = "";
			
			if(i%100==0)
				System.out.println(i);
			
			for(int j=0; j<dataTF[i].length; j++)
			{
				if(dataTF[i][j]<0.5)
				{
					bwTrainText.write("0 ");
					bwTFText.write("0 ");
				}
				else
				{
					double tfidf = dataTF[i][j]*Math.log((double)docNum/id2DocFre.get(j));
				
				//if(tfidf>500)
					//System.out.println("id: " + id + " tf: " + trainTF[i][j] + " idf:"+ Math.log((double)totalDoc/id2DocFre.get(j)));
					bwTFText.write(String.valueOf(dataTF[i][j]) + " ");
					bwTrainText.write(df.format(tfidf) + " ");
				}
				
			}
			
			//bwTrainText.write(oneText);
			bwTrainText.newLine();
			bwTFText.newLine();
		}
		bwTrainText.close();
		fwTrainText.close();
		bwTFText.close();
		fwTFText.close();
		
		/*FileWriter fwText = new FileWriter("C:/Users/jipeng/Desktop/Qiang/KNN/OHSUMED/OHSUMEDTextRemove.txt");
		BufferedWriter bwText = new BufferedWriter(fwText);
		
		int freDoc = docNum/5;
		
		for(int i=0; i<textContentArr.size(); i++)
		{
			ArrayList<Integer> oneText = textContentArr.get(i);
			
				//int preWord = -1;
				for(int j=0; j<oneText.size(); j++)
				{
					//if(oneText.get(j) == preWord)
						//continue;
					if(id2DocFre.get(oneText.get(j))>=freDoc)
						continue;
					bwText.write(String.valueOf(oneText.get(j))+ " ");
					//preWord = oneText.get(j);
				}
				bwText.write("-1");
				bwText.newLine();
		}
		bwText.close();
		fwText.close();*/
		splitCataPrint();
	}
	
	public void splitCataPrint()
	{
		try
		{

			String dir = "C:/Users/jipeng/Desktop/Qiang/KNN/20News/cata/";
			int freDoc = docNum/10;
			
			//boolean lock = true;
			int currClust = 0;
			FileWriter fwText = null;
			BufferedWriter bwText = null;
			fwText = new FileWriter(dir+String.valueOf(0)+".txt");
			 bwText = new BufferedWriter(fwText);
			for(int i=0; i<textContentArr.size(); i++)
			{
				
				if(labelArr.get(i)!=currClust)
				{
					 currClust = labelArr.get(i);
					bwText.close();
					fwText.close();
					fwText = new FileWriter(dir+String.valueOf(currClust)+".txt");
					 bwText = new BufferedWriter(fwText);
					//break;
					 
				}
				ArrayList<Integer> oneText = textContentArr.get(i);
				
					//int preWord = -1;
				for(int j=0; j<oneText.size(); j++)
				{
						//if(oneText.get(j) == preWord)
							//continue;
					if(id2DocFre.get(oneText.get(j))>=freDoc)
						continue;
					bwText.write(String.valueOf(oneText.get(j))+ " ");
						//preWord = oneText.get(j);
				}
				bwText.write("-1");
			    bwText.newLine();
			}
			bwText.close();
			fwText.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void printText() throws Exception
	{
		FileWriter fwText = new FileWriter("C:/Users/jipeng/Desktop/Qiang/KNN/" + newsName + "Text.txt");
		BufferedWriter bwText = new BufferedWriter(fwText);
		
	
		
		FileWriter fwLabel = new FileWriter("C:/Users/jipeng/Desktop/Qiang/KNN/" + newsName + "Label.txt");
		BufferedWriter bwLabel = new BufferedWriter(fwLabel);
		
		
		
		FileWriter fwWI = new FileWriter("C:/Users/jipeng/Desktop/Qiang/KNN/" + newsName + "Word.txt");
		BufferedWriter bwWI = new BufferedWriter(fwWI);
		//ArrayList<Integer> validIdArr = new ArrayList<Integer>();
		
		FileWriter fwST = new FileWriter("C:/Users/jipeng/Desktop/Qiang/KNN/" + newsName + "Stat.txt");
		BufferedWriter bwST = new BufferedWriter(fwST);
		
	
		int allWords= 0;
		for(int i=0; i<textContentArr.size(); i++)
		{
			ArrayList<Integer> oneText = textContentArr.get(i);
			
		
				for(int j=0; j<oneText.size(); j++)
				{
					bwText.write(String.valueOf(oneText.get(j))+ " ");
					
				}
				allWords += oneText.size();
				bwText.write("-1");
				bwText.newLine();
				bwLabel.write(String.valueOf(labelArr.get(i)));
				bwLabel.newLine();
			
		}
		
		
		for(int i=0; i<wordsArr.size(); i++)
		{
			bwWI.write(wordsArr.get(i)+ " ");
			bwWI.newLine();
		}
		bwText.close();
		fwText.close();
		bwLabel.close();
		fwLabel.close();
		bwWI.close();
		
		fwWI.close();
		
		bwST.write("wordNum " + String.valueOf(idNum));
		bwST.newLine();
		bwST.write("docNum " + String.valueOf(docNum));
		bwST.newLine();
		bwST.write("aveWordsNum " + String.valueOf(allWords/docNum));
		bwST.newLine();
		bwST.write("clustNum " + String.valueOf(clusterId));
		
		bwST.close();
		fwST.close();
	}
	
	
	
	
	public static void main(String []args)
	{
		//String path = "C:/Users/qjp/Workspaces/MyEclipse Professional 2014/TopicModel/src/shortLongText/";
		//TextCluster tc = new TextCluster();
		//tc.readFile();
		try
		{
			AllData en = new AllData();
			
			//String pathDir = "C:/Users/jipeng/Desktop/Qiang/KNN/BBCSPORT/bbcsport/Data";
			String pathDir = "C:/Users/jipeng/Desktop/Qiang/KNN/20News/Source/20news-bydate-train";
			//String pathDir = "C:/Users/jipeng/Desktop/Qiang/KNN/OHSUMED/ohsumed-first-20000-docs/training";
			
			en.getText(pathDir);
			
			en.printText();
			//en.validText();
			en.printTFIDF();
			//en.getValidText();
			
			//en.printText();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
