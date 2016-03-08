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

public class TrainTest {
	

	
	//String dir = "C:/Users/qjp/Desktop/UMAB/UMAB/text/Clean2";
	//String dir = "C:/Users/qjp/Desktop/TopicModel/data set/20_newsgroups/";
	//int numNews = 0;
	
	int id = 0;
	//ArrayList<String> title =new ArrayList<String>();
	//ArrayList<String> snippet = new ArrayList<String>();
	//ArrayList<String> text = new ArrayList<String>();
	
	//HashMap<String, Integer> word2id = new HashMap<String,Integer>();
	ArrayList<String> wordsArr = new ArrayList<String>();
	//ArrayList<Integer> word2IdArr = new ArrayList<Integer>();
	
	ArrayList<Integer> wordFreArr = new ArrayList<Integer>();
	
	ArrayList<Integer> labelArr = new ArrayList<Integer>();
	
	ArrayList<ArrayList<Integer>> textContentArr = new ArrayList<ArrayList<Integer>>();
	//ArrayList<Integer> validTextId = new ArrayList<Integer>();
	
	int numTrain = 0;
	
	private Stopwords m_EnStopwords = new StopwordsEnglish();

	MaxentTagger tagger = new MaxentTagger("C:/Users/jipeng/Desktop/TopicModel/stanford-postagger-2014-01-04/models/english-left3words-distsim.tagger");
	
	//HashMap<Integer, Integer> wordFreMap = new HashMap<String, Integer>();

	ArrayList<String> wordsValidArr = new ArrayList<String>();
	
	ArrayList<ArrayList<Integer>> textValidContentArr = new ArrayList<ArrayList<Integer>>();
	
	HashMap<Integer,Integer> id2DocFre = new HashMap<Integer,Integer>();
	
	Pattern p = Pattern.compile("[^a-zA-Z]", Pattern.CASE_INSENSITIVE);
	
	int validIdNum = 0;
	DecimalFormat df = new DecimalFormat("0.000");
	
	public void getValidText(String pathDir) throws Exception
	{
	
		File folder = new File(pathDir);
		File[] listOfFiles = folder.listFiles();
	
		int clusterId = 0;
		
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
				    				  oneTextArr.add(id);
				    				  id++;
				    				 
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
				textContentArr.add(oneTextArr);
				labelArr.add(clusterId);
			}
			clusterId++;
		}
		
		
		//System.out.println(flag);
		//numNews = flag;
	}

	
	
	public static <K, V extends Comparable<V>> Map<K, V> sortByValues(final Map<K, V> map) {
	    Comparator<K> valueComparator =  new Comparator<K>() {
	        public int compare(K k1, K k2) {
	            int compare = map.get(k2).compareTo(map.get(k1));
	            if (compare == 0) return 1;
	            else return compare;
	        }
	    };
	    Map<K, V> sortedByValues = new TreeMap<K, V>(valueComparator);
	    sortedByValues.putAll(map);
	    return sortedByValues;
	}
	
	public void loadTrainTest() throws Exception
	{
		
		
		String trainDir = "C:/Users/jipeng/Desktop/Qiang/KNN/20News/source/20news-bydate-train";
		
		getValidText(trainDir);
		
		numTrain = textContentArr.size();
		
		String testDir = "C:/Users/jipeng/Desktop/Qiang/KNN/20News/source/20news-bydate-test";
		
		getValidText(testDir);
	}
	
	public void validText() throws Exception
	{
		//ArrayList<Integer> validIdArr = new ArrayList<Integer>();
		
		HashMap<Integer,Integer> idInx = new HashMap<Integer,Integer>();
		//int validIdNum = 0;
		
		
		HashMap<String, Integer> wordFreMap = new HashMap<String, Integer>();
		
		for(int i=0; i<wordFreArr.size(); i++)
		{
			int fre = wordFreArr.get(i);
			if(fre>=5)
			{
				//valid
				//validIdArr.add(i);
				wordFreMap.put(wordsArr.get(i), fre);
				
				  idInx.put(i, validIdNum);
				  validIdNum++;
				 
				 wordsValidArr.add(wordsArr.get(i));
			}
			//System.out.println(wordsArr.get(i) + );
		}
		for(int i=0; i<textContentArr.size(); i++)
		{
			ArrayList<Integer> oneText = new ArrayList<Integer>();
			for(int j=0; j<textContentArr.get(i).size(); j++)
			{
				int dd = textContentArr.get(i).get(j);
				if(idInx.containsKey(dd))
				{
					oneText.add(idInx.get(dd));
				}
			}
			textValidContentArr.add(oneText);
		}
		
		
		
	}
	
	public void printTFIDF() throws Exception
	{
		double [][]trainTF = new double[numTrain][validIdNum];
		
		double [][]testTF = new double[textValidContentArr.size()-numTrain][validIdNum];
		
		for(int i=0; i<textValidContentArr.size(); i++)
		{
			ArrayList<Integer> oneText = textValidContentArr.get(i);
			
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
					
					if(i<numTrain)
						trainTF[i][id] =  1;
					else
						testTF[i-numTrain][id] = 1;
					
					unitSet.add(id);
				}else
				{
					if(i<numTrain)
						trainTF[i][id] +=  1;
					else
						testTF[i-numTrain][id] += 1;
				}
			}
		}
		
		int totalDoc = textValidContentArr.size();
		System.out.println("totalDoc: " + totalDoc + " trainDoc: " + numTrain);
		/*for(int i=0; i<totalDoc; i++)
		{
			ArrayList<Integer> oneText = textValidContentArr.get(i);
			for(int j=0; j<oneText.size(); j++)
			{
				int id = oneText.get(j);
				
				double idf = Math.log((double)totalDoc/id2DocFre.get(id)+1);
				
				if(i<numTrain)
				{
					if(trainTFIDF[i][id]*idf>3000)
						System.out.println("id: " + id + " tf: " + trainTFIDF[i][id] + " idf:"+ idf);
					trainTFIDF[i][id] *=  idf;
				}
				else
					testTFIDF[i-numTrain][id] *= idf;
			}
		}*/
		
		FileWriter fwTrainText = new FileWriter("C:/Users/jipeng/Desktop/Qiang/KNN/20News/20NewsTrainTFIDF.txt");
		BufferedWriter bwTrainText = new BufferedWriter(fwTrainText);
		
		FileWriter fwTestText = new FileWriter("C:/Users/jipeng/Desktop/Qiang/KNN/20News/20NewsTestTFIDF.txt");
		BufferedWriter bwTestText = new BufferedWriter(fwTestText);
		
		for(int i=0; i<trainTF.length; i++)
		{
			//String oneText = "";
			
			if(i%100==0)
				System.out.println(i);
			
			for(int j=0; j<trainTF[i].length; j++)
			{
				if(trainTF[i][j]<0.5)
					bwTrainText.write("0 ");
				else
				{
					double tfidf = trainTF[i][j]*Math.log((double)totalDoc/id2DocFre.get(j));
				
				//if(tfidf>500)
					//System.out.println("id: " + id + " tf: " + trainTF[i][j] + " idf:"+ Math.log((double)totalDoc/id2DocFre.get(j)));
					bwTrainText.write(df.format(tfidf) + " ");
				}
				
			}
			
			//bwTrainText.write(oneText);
			bwTrainText.newLine();
		}
		bwTrainText.close();
		fwTrainText.close();
		
		for(int i=0; i<testTF.length; i++)
		{
			if(i%100==0)
				System.out.println(i);
			
			//String oneText = "";
			for(int j=0; j<testTF[i].length; j++)
			{
				if(testTF[i][j]<0.5)
					bwTestText.write("0 ");
				else
				{
					double tfidf = testTF[i][j]*Math.log((double)totalDoc/id2DocFre.get(j));
					bwTestText.write(df.format(tfidf) + " ");
				}
			}
				
			
			//bwTestText.write(oneText);
			bwTestText.newLine();
		}
		bwTestText.close();
		fwTestText.close();
	}
	
	public void printText() throws Exception
	{
		FileWriter fwTrainText = new FileWriter("C:/Users/jipeng/Desktop/Qiang/KNN/20News/t-20NewsTrainText.txt");
		BufferedWriter bwTrainText = new BufferedWriter(fwTrainText);
		
		FileWriter fwTestText = new FileWriter("C:/Users/jipeng/Desktop/Qiang/KNN/20News/t-20NewsTestText.txt");
		BufferedWriter bwTestText = new BufferedWriter(fwTestText);
		
		FileWriter fwTrainLabel = new FileWriter("C:/Users/jipeng/Desktop/Qiang/KNN/20News/t-20NewsTrainLabel.txt");
		BufferedWriter bwTrainLabel = new BufferedWriter(fwTrainLabel);
		
		FileWriter fwTestLabel = new FileWriter("C:/Users/jipeng/Desktop/Qiang/KNN/20News/t-20NewsTestLabel.txt");
		BufferedWriter bwTestLabel = new BufferedWriter(fwTestLabel);
		
		FileWriter fwWI = new FileWriter("C:/Users/jipeng/Desktop/Qiang/KNN/20News/t-20NewsWord.txt");
		BufferedWriter bwWI = new BufferedWriter(fwWI);
		//ArrayList<Integer> validIdArr = new ArrayList<Integer>();
		
		HashMap<Integer,Integer> idInx = new HashMap<Integer,Integer>();
		int inx = 0;
		
		
		HashMap<String, Integer> wordFreMap = new HashMap<String, Integer>();
		
		for(int i=0; i<wordFreArr.size(); i++)
		{
			int fre = wordFreArr.get(i);
			
			//bw.write(wordsArr.get(i) + " " + String.valueOf(wordFreArr.get(i)));
			//bw.newLine();
			
			
			if(fre>=5)
			{
				//valid
				//validIdArr.add(i);
				wordFreMap.put(wordsArr.get(i), fre);
				idInx.put(i, inx);
				inx++;
				 
				wordsValidArr.add(wordsArr.get(i));
				bwWI.write(wordsArr.get(i));
				bwWI.newLine();
			}
			//System.out.println(wordsArr.get(i) + );
		}
		
		//Map<String,Integer> freMap = sortByValues(wordFreMap);
		
		//List<Map.Entry<String, Integer>> list =
	          //  new LinkedList<Map.Entry<String, Integer>>( freMap.entrySet() );
		
	
		for(int i=0; i<textContentArr.size(); i++)
		{
			ArrayList<Integer> oneText = new ArrayList<Integer>();
			for(int j=0; j<textContentArr.get(i).size(); j++)
			{
				int dd = textContentArr.get(i).get(j);
				if(idInx.containsKey(dd))
				{
					oneText.add(idInx.get(dd));
				}
			}
			textValidContentArr.add(oneText);
		}
		
		
		for(int i=0; i<textValidContentArr.size(); i++)
		{
			ArrayList<Integer> oneText = textValidContentArr.get(i);
			
			if(i<numTrain)
			{
				for(int j=0; j<oneText.size(); j++)
				{
					bwTrainText.write(String.valueOf(oneText.get(j))+ " ");
					
				}
				bwTrainText.newLine();
				bwTrainLabel.write(String.valueOf(labelArr.get(i)));
				bwTrainLabel.newLine();
			}else
			{
				for(int j=0; j<oneText.size(); j++)
				{
					bwTestText.write(String.valueOf(oneText.get(j))+ " ");
					
				}
				bwTestText.newLine();
				bwTestLabel.write(String.valueOf(labelArr.get(i)));
				bwTestLabel.newLine();
			}
		}
		
		bwTrainText.close();
		fwTrainText.close();
		bwTrainLabel.close();
		fwTrainLabel.close();
		bwTestText.close();
		bwTestLabel.close();
		bwWI.close();
		fwTestText.close();
		fwTestLabel.close();
		fwWI.close();
	}
	
	
	
	
	public static void main(String []args)
	{
		//String path = "C:/Users/qjp/Workspaces/MyEclipse Professional 2014/TopicModel/src/shortLongText/";
		//TextCluster tc = new TextCluster();
		//tc.readFile();
		try
		{
			TrainTest en = new TrainTest();
			
			en.loadTrainTest();
			
			//en.printText();
			en.validText();
			en.printTFIDF();
			//en.getValidText();
			
			//en.printText();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
