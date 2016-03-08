package MultiDocument;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Map.Entry;


import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;


import kex.pattern.INSGrow;
import kex.stemmers.MartinPorterStemmer;
import kex.stopwords.Stopwords;
import kex.stopwords.StopwordsEnglish;


//Author: Qiang Jipeng
//Time: 2014-03-26
//Title: Multi-Document summarization based on Closed sequential mining


public class ClosSum_Remove {

	
	private HashMap<String, Integer> word2IdHash = new HashMap<String, Integer>(); //<word, word's id>
	private HashMap<Integer, String> id2WordHash = new HashMap<Integer, String>(); // <word's id, word>
	private HashMap<Integer, Integer> id2Isf = new HashMap<Integer, Integer>(); // <word's id,  word's isf>
	//private HashMap<Integer, Integer> id2Freq = new HashMap<Integer, Integer>(); // <word's id, word's idf>
	private MartinPorterStemmer m_Stemmer = new MartinPorterStemmer();
	private Stopwords m_EnStopwords = new StopwordsEnglish();
	private ArrayList<String> allSentences = new ArrayList<String> (); // store all the original sentences
	//private ArrayList<String> allHeads = new ArrayList<String>(); // store the head of all documents
	//private ArrayList<Integer> lenOfAllSentences = new ArrayList<Integer>(); //store the length of all the original sentences
	//private ArrayList<String> origTextArr = new ArrayList<String>();
	private int id = 0;
	//private int sumSentences = 0;  // the number of all sentences
	//private int sumDocuments = 0;  // the number of all documents
	//private StringBuffer buff = new StringBuffer();
	
	private ArrayList<ArrayList<Integer>> sentId = new ArrayList<ArrayList<Integer>>();
	//private ArrayList<ArrayList<String>> sentPOS = new ArrayList<ArrayList<String>>(); // store the word's POS for all sentences
	private HashMap<Integer, Double> id2Weight = new HashMap<Integer,Double>(); //<word's id, word's weight>
	
	private ArrayList<Double> sentIndexId = new ArrayList<Double>();
	
	private ArrayList<Integer> sentWordNum = new ArrayList<Integer>();
	//private HashMap<Integer, String> id2Tag = new HashMap<Integer, String>(); //<word's id, word's Tag>
	
	//private ArrayList<Entry<Integer,Double>> listWeight;
	private  double alpa = 0;  
	
	//private double belt=0.2 ;
	
	private double lenQ = 0.2;
	
	//private long sup =  5;
	
	private double min_sup = 2;
	
	//private int min1 = 5;
	//private int min2 = 4;
	
	
	public ClosSum_Remove(double min_sup)
	{	
		this.min_sup = min_sup;
	}
	
	
	
	public ClosSum_Remove(double alpa, double lenQ)
	{
		this.alpa = alpa;
		this.lenQ = lenQ;
	}
	

	public String getWordForID(int i)
	{
		 return id2WordHash.get(i);
	}
	public boolean isEnglishStopwords(String word)
	{
		return m_EnStopwords.isStopword(word);
	}
	
	//reading text from a file name, and return the result
	public String readTextFromFile(String fileName)
	{
		StringBuffer txtStr = new StringBuffer();
		try 
		{
			File txt = new File(fileName);
			InputStreamReader is = new InputStreamReader(new FileInputStream(txt),"UTF-8");
			int c;
			while ((c = is.read()) != -1) 
			{
				txtStr.append((char)c);
			}
			is.close();
		
		} catch (Exception e) 
		{
			System.err.println("Can't find document.");
		}
		return txtStr.toString();

	}
	
	public boolean isLetterNum(char c)
	{
		if(Character.isLetter(c))
			return true;
		if(Character.isDigit(c))
			return true;
		return false;
	}
	
	public String valueFromList(ArrayList<Integer> list)
	{
		String res = "";
		for(int i=0; i<list.size(); i++)
		{
			res += list.get(i) + " ";
		}
		return res;
	}
	
	
	//read file from a directory, and then preprocess the file for each file.
	public void extractSentenceFromDir(String filePath, MaxentTagger tagger) throws Exception
	{
	
		if (filePath==null)
		{
			System.out.println("you have not specify the file name.");
			return ;
		}
		//word2IdHash = new HashMap();
		//id2WordHash = new HashMap();
		
		File srcFile = new File(filePath);		
		String []fileList = srcFile.list();
		int num = 0;
		for(int i=0; i<fileList.length; i++)
		{
			String path = filePath + "/" + fileList[i];
			
			//String text = readTextFromFile(path);
			//System.out.println(text);
			//System.out.println("path=" + path);
			
			String buff = "";
			//ArrayList<Integer> idIndexList = new ArrayList<Integer>();
			int sentIndex = 0;
			//System.out.println("---------------------------------------------");
			 List<List<HasWord>> sentences = MaxentTagger.tokenizeText(new BufferedReader(new FileReader(path)));
			 for (List<HasWord> sentence : sentences)
			 {
			      ArrayList<TaggedWord> tSentence = tagger.tagSentence(sentence);
			     // System.out.println(Sentence.listToString(tSentence, true));
			    // idIndexList.add(sentIndex++);
			      ArrayList<Integer> idList = new ArrayList<Integer>();
			     // ArrayList<String> POSList = new ArrayList<String>();
			     // ArrayList<Integer>  idRepList= new ArrayList<Integer>();
			    // int sentBytes = 0;
			      for(int j=0; j<tSentence.size(); j++)
			      {
			    	  String word = tSentence.get(j).value();
			    	 // if ( !m_EnStopwords.isStopword(word))
			    	  //{
			    	  
			    		  String token = m_Stemmer.stemString(word);
			    		  if(isLetterNum(token.charAt(0))  && token.length()>=2 )
				    	  {
				    		 
					    		 //System.out.print(tSentence.get(j).value() + "/" + tSentence.get(j).tag() + " ");
			    			  if ( !m_EnStopwords.isStopword(token)) 
			    			  {
			    				  num++;
								  if (word2IdHash.get(token)==null)
								  {
									  //addList(id,idList,tSentence.get(j).tag(),POSList);
									 idList.add(id);
									 // idRepList.add(id);
									   word2IdHash.put(token, id);
									   id2WordHash.put(id, token);
									   id2Isf.put(id, 1);
									   
									   id++;
								    } else
								    {
								    	int wid=(Integer)word2IdHash.get(token);
								    	//idRepList.add(wid);
								    	if(!idList.contains(wid))
								    	{
								    		//addList(wid,idList,tSentence.get(j).tag(),POSList);
								    		idList.add(wid);
								    		if(!id2Isf.containsKey(wid))
								    			id2Isf.put(id, 1);
								    		else
								    			id2Isf.put(wid, id2Isf.get(wid)+1);
								    	}
								    	//else
								    		//idList.add(wid);
								    	//else
								    		//idList.add(wid); //ÔÊÐí
								    }
								 
					    	  
			    			  }
			    	  }
			    	
			    	 //sentBytes += tSentence.get(j).value().getBytes().length;
			    	  
			    	 
			      }
			     // System.out.println();
			      
			      sentWordNum.add(tSentence.size());
			    // System.out.print(tSentence.size()+ " ");
			    //if(wordNum<=8)
			    	 // System.out.println(wordNum + " " + Sentence.listToString(tSentence, true));
			      allSentences.add(Sentence.listToString(tSentence, true));
			      tSentence.clear();
			      sentId.add(idList);
			     // sentPOS.add(POSList);
			      double ddd = (double)(sentences.size()-(sentIndex++))/sentences.size();
			      sentIndexId.add(ddd);
			      
			      Collections.sort(idList);
			     // System.out.println(idList.toString());
			     // sort(idList,POSList);
			      
			      buff += valueFromList(idList) ;
			      //idList.clear();
			    //  Collections.sort(idRepList);
			     // System.out.println(idList.toString());
			     // System.out.println(idRepList.toString());
			    //  sentId.add(idRepList);
			     // idList.clear();
			      buff += "-1 ";
			     // System.out.println(buff);
			      buff += Character.toString('\n');
			     
			  }
	
			 //buff.append(-2);
			
			 HashMap<ArrayList, ArrayList> patHash = getClos(buff, sentences.size());
			// System.out.println(sentences.size());
			 computWeight(patHash,sentences.size());
			 sentences.clear();
			 patHash.clear();
			 //System.out.println(idIndexList.toString());
			// sentIndexId.add(idIndexList);
			 
		}
		//System.out.println(sentIndexId.toString());	
	}
	
	
	
	public void clear()
	{
		word2IdHash.clear();
		id2WordHash.clear();
		id2Isf.clear();
		//id2Freq.clear();
		allSentences.clear();
		sentId.clear();
		id2Weight.clear();
		//m_Stemmer.
	}
	
	private int fun(double d) {
		//System.out.println(d);
		int b = (int) d;
		if(d - 0.5 >= b){
		   b++;
		   return b;
		}
		else
			return b;
	}

	
	public void computWeight( HashMap<ArrayList, ArrayList> patHash, int numOfSent)
	{
		//double maxWeight = getMaxWeight(patHash,numOfSent);
		//System.out.println("maxWeight:" + numOfSent);
		
		List<Map.Entry<ArrayList, ArrayList>> entries = new ArrayList<Entry<ArrayList, ArrayList>>(patHash.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<ArrayList, ArrayList>>() {

			@Override
			public int compare(Entry<ArrayList, ArrayList> o1,
					Entry<ArrayList, ArrayList> o2) {
				// TODO Auto-generated method stub
				return o2.getKey().size()-o1.getKey().size();
			}
             
         });
		
        SentSup_Remove []ss = new SentSup_Remove[numOfSent];
        
        for(int i=0; i<ss.length; i++)
       	 ss[i] = new SentSup_Remove(10000);
        
        
        for(int i=0; i<entries.size(); i++)
        {
       	 	Entry<ArrayList, ArrayList> e = entries.get(i);
       	 
       	 //if(e.getKey().size()<2)
       		// continue;
       	 
	       	for(int ii=0; ii<e.getValue().size(); ii++)
	       	{
	       		ss[(Integer) e.getValue().get(ii)].add(e.getKey(), e.getValue().size(), numOfSent);
	       	}
       	 
       		
       	//System.out.println(e.getKey().toString() + "->"+ e.getValue());
        }
        
        entries.clear();
        
        for(int i=0; i<ss.length; i++)
        {
        	for(int j=0; j<ss[i].idArrWei.length; j++)
        	{
        		if(ss[i].idArrWei[j]>0)
        		{
        			if(id2Weight.containsKey(j))
    				{
    					id2Weight.put(j,id2Weight.get(j)+ss[i].idArrWei[j]);
    					//id2Freq.put(id, id2Freq.get(id)+sup);
    				}
    				else
    				{
    					//id2Freq.put(id, sup);
    					id2Weight.put(j, ss[i].idArrWei[j]);
    				}
        		}
        		
        	}
        }
        
	}
	
	
	HashMap<ArrayList,ArrayList> getClos(String buff, int numOfSent) throws Exception 
	{
		String writeName = "testdocs/en/train/multiDocu.txt";
	     BufferedWriter writer = new BufferedWriter(new FileWriter(new File(writeName)));   
	     writer.write(buff);
		 writer.close();
		
		INSGrow ins = new INSGrow();
		ins.input(writeName,min_sup/numOfSent);
		
       ins.search();
		
       HashMap<ArrayList, ArrayList> patSentIndexHash = ins.getPatSentIndexHash();
		
		return patSentIndexHash;
		
		/*Iterator iter = patHash.entrySet().iterator();
		while (iter.hasNext()) 
		{
			Map.Entry entry = (Map.Entry) iter.next();
			ArrayList<Integer> it = (ArrayList)entry.getKey();
			for(int ii=0; ii<it.size(); ii++)
				System.out.print(id2WordHash.get(it.get(ii))+" ");
			System.out.println("->"+entry.getValue());
		}*/
		//return patHash;
	}
	
	
	public void calcuSum()
	{
		//double maxWei = getMaxWeight();
		//System.out.println(maxWei);
		//System.out.println(id2Weight.size());
		Iterator iter = id2Weight.entrySet().iterator();
		while (iter.hasNext()) 
		{
			Map.Entry entry = (Map.Entry) iter.next();
			int id = Integer.parseInt(entry.getKey().toString());
			//System.out.println(entry.getKey()+"->" + entry.getValue() + ":" + id2Isf.get(entry.getKey())+" "+ id2Freq.get(entry.getKey()) + " "+ id2Weight.get(entry.getKey()));
			//id2Weight.put(id,Double.parseDouble(entry.getValue().toString())*id2Isf.get(id)*id2Freq.get(id)/sentId.size());
			//id2Weight.put(id,(double)id2Isf.get(id)*id2Freq.get(id)/sentId.size());
			//id2Weight.put(id,Double.parseDouble(entry.getValue().toString())*id2Freq.get(id)*id2Isf.get(id)/sentId.size());
			id2Weight.put(id,Double.parseDouble(entry.getValue().toString())*id2Isf.get(id)/sentId.size());
			
			//System.out.println(Double.parseDouble(entry.getValue().toString())*id2Isf.get(id)/sentId.size());
			//System.out.println("id: "+ id + "->" + id2Weight.get(id));
		}
		
//		listWeight = new ArrayList<Entry<Integer,Double>>(id2Weight.entrySet());     
//		Collections.sort(listWeight, new Comparator<Map.Entry<Integer,Double>>() 
//		{
//			public int compare(Map.Entry<Integer,Double> o1, Map.Entry<Integer,Double> o2) 
//			{
//				if ((o2.getValue() - o1.getValue())>0)  
//					return 1;  
//				else if((o2.getValue() - o1.getValue())==0)  
//					return 0;  
//				else   
//					 return -1;  
//
//			}     
//		});   
//       id2Weight.clear();
//       //System.out.println(listWeight);
//       while(listWeight.size()>200)
//       {
//    	  listWeight.remove(200);
//       }
		//for(int i=0; i<listWeight.size(); i++)
		//{
			//listWeight.g
		//}
		//listWeight.
		//System.out.println("******************************");
		//System.out.println(listWeight);
	}
	
//	public double containsKey(int id)
//	{
//		for(int i=0; i<listWeight.size(); i++)
//		{
//			Entry<Integer,Double> entry = listWeight.get(i);
//			if(entry.getKey()==id)
//			{
//				
//				return entry.getValue();
//			}
//		}
//		return 0;
//	}
//	
//	public void setKey(int id)
//	{
//		for(int i=0; i<listWeight.size(); i++)
//		{
//			Entry<Integer,Double> entry = listWeight.get(i);
//			if(entry.getKey()==id)
//			{
//				entry.setValue(entry.getValue()*belt);
//				//return true;
//			}
//		}
//		//return false;
//	}
	
	public String computSummary()
	{
		calcuSum();
		
		String summary = "";
		
		ArrayList<Integer> sumId = new ArrayList<Integer>();
		int len = 0;
		
		while(len<=700)
		{
			double score[] = new double[sentId.size()];
			
			for(int i=0; i<sentId.size(); i++)
			{
				double res = 0;
				//int preId = -1;
				//double preRes = 0;
				int usefulWord = 0;
				for(int j=0; j<sentId.get(i).size(); j++)
				{
					int id = sentId.get(i).get(j);
					//double pum = 0;
//					double value = containsKey(id);
//					if(value!=0)
//					{
//						//System.out.println(value);
//						res += value;
//						usefulWord ++;
//					}
					if(id2Weight.containsKey(id) )
					{
						res += id2Weight.get(id);
						usefulWord ++;  	
					}
					
				}
				double wordWeight = 1;
				//if(lenQ!=0)
				wordWeight = (double)usefulWord/sentWordNum.get(i) + lenQ;
				//wordWeight = (double)sentId.get(i).size()/sentWordNum.get(i);
				//System.out.println(sentId.get(i).size() + "/"+ sentWordNum.get(i) + ": "+ wordWeight);
				
				score[i] = res*sentIndexId.get(i)*wordWeight;
				//score[i] = res;
				//score[i] = res*sentIndexId.get(i);
				//score[i] = res*wordWeight;
				//else
					//score[i] = res*200/(allSentences.get(i).getBytes().length+lenQ);
				//score[i] = res*sentIndexId.get(i);
			}
			
			int index = getMax(score,sumId);
			//if(sumId.contains(index))
				//continue;
			//System.out.println(index);
			sumId.add(index);
			len += allSentences.get(index).getBytes().length;
			//System.out.println(listWeight);
			for(int i=0; i<sentId.get(index).size(); i++)
			{
				int id = sentId.get(index).get(i);
				//System.out.println(id2Weight.get(id));
				if(id2Weight.containsKey(id))
					id2Weight.put(id, id2Weight.get(id)*alpa);
				//setKey(id);
				
			}
			
		}
		
		for(int i=0; i<sumId.size(); i++)
		{
			summary += allSentences.get(sumId.get(i));
			//System.out.println("the id number :"+ sumId.get(i) + "->"+ allSentences.get(sumId.get(i)));
		}
		
		sumId.clear();
		return summary;
		
	}
	
	
	
	
	
	
	public void test()
	{
		//System.out.println("id->word:isf freq weight");
		Iterator iter = id2WordHash.entrySet().iterator();
		while (iter.hasNext()) 
		{
			Map.Entry entry = (Map.Entry) iter.next();
			System.out.println(entry.getKey()+"->" + entry.getValue() + ":" + id2Isf.get(entry.getKey())+" " + " "+ id2Weight.get(entry.getKey()));
		}
		
		//System.out.println(allSentences.size() + " = "+ sentId.size());
		
		Iterator iter2 = id2Weight.entrySet().iterator();
		while (iter2.hasNext()) 
		{
			Map.Entry entry = (Map.Entry) iter2.next();
			//System.out.println(entry.getKey()+"->" + entry.getValue());
		}
		
		
		//if(sentIndexId.size() == sentId.size())
			//System.out.println("sentIndexId.size == sentId.size");
	}
	
	public String clm(String dirName, MaxentTagger tagger)
	{
		try
		{
		
			extractSentenceFromDir(dirName, tagger); 
			//test();
			String summary = computSummary();
			//test();
			return summary;
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	public int getMax(double []score,ArrayList<Integer> sumId)
	{
		double max = 0;
		int index = 0;
		
		for(int i=0; i<score.length; i++)
		{
			if(sumId.contains(i))
				continue;
			if(score[i]>max )
			{
				max = score[i];
				index = i;
			}
		}
		return index;
	}
	
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//String dirName = "F:/Êý¾Ý¼¯/DUC2002/DUC2002_Summarization_Documents/docs.with.sentence.breaks/";
		MaxentTagger tagger = new MaxentTagger("E:/UMASS/stanford-postagger-2014-01-04/models/wsj-0-18-bidirectional-nodistsim.tagger");
		
		ClosSum_Remove ts = new ClosSum_Remove(0.3,2);
		//clm.test();
		String sum = ts.clm("E:/UMASS/TextMining/DUC2004/d30001",tagger);
		
		System.out.println(sum);
		System.out.println(ts.allSentences.size());
		//clm.mainFun(dirName, "F:/program/TextMining/experimentResult/DUC2002");
		
		
	}

}
