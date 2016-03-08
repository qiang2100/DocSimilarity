package ProprocessDoc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import kex.pattern.INSGrow;

public class TextRepresentationPattern {

	int wordNum;
	
	int dataNum;
	
	ArrayList<ClassClosePatt> allPatt= new ArrayList<ClassClosePatt>();
	
	ArrayList<Integer> classBegin = new ArrayList<Integer>();
	double [][]textRepres;
	
	int []textPatLen;
	
	DecimalFormat df = new DecimalFormat("0.0000");
	
	String newsName = "20News/news20";
	
	public void generatePattern(int sup, String dataPath)
	{
		try
		{
			File folder = new File(dataPath);
			File[] listOfFiles = folder.listFiles();
		
			int ind = 0;
			for (File file : listOfFiles) 
			{
				INSGrow ins = new INSGrow();
				ins.input(file.getAbsolutePath(),sup);
				
		       ins.search();
		       System.out.println(ind + " total Pattern: " + ins.tot);
		       ClassClosePatt cla = new ClassClosePatt();
		       cla.patSentIndexHash = ins.getPatSentIndexHash();
		       allPatt.add(cla);
		       ind++;
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
       //patSentIndexHash = ins.getPatSentIndexHash();
	}
	
	public void getWordAndDataNum(String statPath, String labelPath)
	{
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(statPath));
			//System.out.println(subFile.getAbsolutePath());
			String line = br.readLine();
		
			String word[] = line.split(" ");
			wordNum = Integer.parseInt(word[1]);
			
			line = br.readLine();
			word = line.split(" ");
			dataNum = Integer.parseInt(word[1]);
			
			br.close();
			
			BufferedReader br2 = new BufferedReader(new FileReader(labelPath));
			
			line = br2.readLine();
			
			String cur = "";
			int ind = 0;
			while(line!=null)
			{
				if(!line.equals(cur))
				{
					classBegin.add(ind);
					cur = line;
					System.out.println(ind);
				}
				ind++;
				line = br2.readLine();
			}
			br2.close();
		
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	public void calTextRepres()
	{
		textRepres = new double[dataNum][wordNum];
		textPatLen =  new int[dataNum];
		//Iterator iter = patSentIndexHash.entrySet().iterator();
		
		for(int i=0; i<allPatt.size(); i++)
		{
			HashMap<ArrayList, ArrayList> patMap = allPatt.get(i).patSentIndexHash;
			int docInd = classBegin.get(i);
			
			Iterator iter = patMap.entrySet().iterator();
			
			while (iter.hasNext()) 
			{
				Map.Entry entry = (Map.Entry) iter.next();
				
				//ArrayList<Integer> it = (ArrayList)entry.getKey();
				ArrayList<Integer> wordId = (ArrayList)entry.getKey();
				//if(wordId.size()<  2)
					//if(sup<min1)
					//	continue;
				
				ArrayList<Integer> value = (ArrayList)entry.getValue();
				
				Set<Integer> hs = new HashSet<>();
				hs.addAll(value);
				value.clear();
				value.addAll(hs);
				
				int sup = value.size();
				
				//if(sup<2)
					//continue;
				
				for(int ii=0; ii<sup; ii++)
				{
					int textId = value.get(ii);
					
					for(int jj=0; jj<wordId.size(); jj++)
					{
						textRepres[docInd + textId][wordId.get(jj)] += sup;
						textPatLen[docInd + textId] += sup;
					}
					//System.out.print(value.get(ii)+" ");
				}
				
				hs = new HashSet<>();
				hs.addAll(wordId);
				wordId.clear();
				wordId.addAll(hs);
				//System.out.println("");
			}
			
		}
		
		for(int i=0; i<textRepres.length; i++)
		{
			int patLen = textPatLen[i];
			
			for(int j=0; j<textRepres[i].length; j++)
			{
				if(textRepres[i][j]<0.1)
				{
					continue;
				}
				else
				{
					textRepres[i][j] /= patLen;
					//bwText.write(df.format(val) + " ");
				}
			}
		}
	}
	
	public double contain(double []text, ArrayList<Integer> wordArr)
	{
		double offering=0;
		
		for(int i=0; i<wordArr.size(); i++)
		{
			offering +=  text[wordArr.get(i)];
		}
		
		return offering;
	}
	
	public void ipEvolving()
	{
		for(int i=0; i<allPatt.size(); i++)
		{
			System.out.println("the " + i + " evolving");
			int docInd = classBegin.get(i);
			
			int docEnd = dataNum;
			if(i!=allPatt.size()-1)
				docEnd = classBegin.get(i+1);
			
			for(int j=0; (j!=i) && (j<allPatt.size()); j++)
			{
				HashMap<ArrayList, ArrayList> patMap = allPatt.get(j).patSentIndexHash;
	
				
				Iterator iter = patMap.entrySet().iterator();
				
				while (iter.hasNext()) 
				{
					Map.Entry entry = (Map.Entry) iter.next();
					
					//ArrayList<Integer> it = (ArrayList)entry.getKey();
					ArrayList<Integer> wordId = (ArrayList)entry.getKey();
					//if(wordId.size()<  2)
						//if(sup<min1)
						//	continue;
						
					for(int textInd = docInd; textInd<docEnd; textInd++)
					{
						double offerning = contain(textRepres[textInd],wordId);
						
						if(offerning == 0)
							continue;
						
						///int sup = ((ArrayList)entry.getValue()).size();
						
						double base = 1-offerning;
						
						double alpha =  2;
						offerning *= (1-1./alpha);
						
						for(int t=0; t<wordNum; t++)
						{
							if(wordId.contains(t))
							{
								textRepres[textInd][t] *= 1.0/alpha;
							}else
							{
								if (textRepres[textInd][t]!=0)
									textRepres[textInd][t] *= (1+ offerning/base);
							}
						}
					}
				}
			}
		}
	}
	
	public void printTextRepres()
	{
		try{
			FileWriter fwText = new FileWriter("C:/Users/jipeng/Desktop/Qiang/KNN/" + newsName + "TextPatternRemove.txt");
			BufferedWriter bwText = new BufferedWriter(fwText);
			
			for(int i=0; i<textRepres.length; i++)
			{
				//int patLen = textPatLen[i];
				
				for(int j=0; j<textRepres[i].length; j++)
				{
					if(textRepres[i][j]==0)
					{
						bwText.write("0 ");
					}
					else
					{
						//double val = ((double)textRepres[i][j])/patLen;
						//bwText.write(df.format(val) + " ");
						bwText.write(String.valueOf(textRepres[i][j]) + " ");
					}
				}
				bwText.newLine();
			}
			bwText.close();
			fwText.close();
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void mainFun()
	{
		String statName = "C:/Users/jipeng/Desktop/Qiang/KNN/" + newsName + "Stat.txt";
		String labelName = "C:/Users/jipeng/Desktop/Qiang/KNN/" + newsName + "Label.txt";
		String fileName = "C:/Users/jipeng/Desktop/Qiang/KNN/20News/cata/";
		getWordAndDataNum(statName, labelName);
		generatePattern(5,fileName);
		
		calTextRepres();
		
		ipEvolving();
		printTextRepres();
		
	}
	
	public static void main(String[] args) {
		TextRepresentationPattern trp = new TextRepresentationPattern();
		trp.mainFun();
	}
}
