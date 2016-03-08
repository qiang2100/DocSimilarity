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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

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


public class TagSum {

	
	private HashMap<String, Integer> word2IdHash = new HashMap<String, Integer>(); //<word, word's id>
	private HashMap<Integer, String> id2WordHash = new HashMap<Integer, String>(); // <word's id, word>
	private HashMap<Integer, Integer> id2Isf = new HashMap<Integer, Integer>(); // <word's id,  word's isf>
	private HashMap<Integer, Integer> id2Freq = new HashMap<Integer, Integer>(); // <word's id, word's idf>
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
	private HashMap<Integer, Double> id2Weight = new HashMap<Integer,Double>(); //<word's id, word's wight>
	
	private ArrayList<Double> sentIndexId = new ArrayList<Double>();
	//private HashMap<Integer, String> id2Tag = new HashMap<Integer, String>(); //<word's id, word's Tag>
	
	private  double alpa = 0;  
	
	private double belt=0.3 ;
	
	private int lenQ = 1;
	
	public TagSum(double belt)
	{	
		this.belt = belt;
	}
	
	public TagSum(int lenQ)
	{
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
			//System.out.println("path=" + readfile.getPath());
			
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
			      
			     // ArrayList<Integer>  idRepList= new ArrayList<Integer>();
			      for(int j=0; j<tSentence.size(); j++)
			      {
			    	  String token = m_Stemmer.stemString(tSentence.get(j).value());
			    	  if ( isLetterNum(token.charAt(0)) && token.length()>=2 && !m_EnStopwords.isStopword(token))
			    	  {
			    		  //System.out.print(token + " ");
			    		  num++;
						  if (word2IdHash.get(token)==null)
						  {
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
						    		idList.add(wid);
						    		if(!id2Isf.containsKey(wid))
						    			id2Isf.put(id, 1);
						    		else
						    			id2Isf.put(wid, id2Isf.get(wid)+1);
						    	}
						    	//else
						    		//idList.add(wid); //允许
						    }
						 
			    	  }
			    	 
			      }
			      allSentences.add(Sentence.listToString(tSentence, true));
			      tSentence.clear();
			      sentId.add(idList);
			      double ddd = (double)(sentences.size()-(sentIndex++))/sentences.size()+ alpa;
			      sentIndexId.add(ddd);
			      Collections.sort(idList);
			    
			      
			      buff += valueFromList(idList) ;
			      //idList.clear();
			    //  Collections.sort(idRepList);
			     // System.out.println(idList.toString());
			     // System.out.println(idRepList.toString());
			    //  sentId.add(idRepList);
			     // idList.clear();
			      buff += "-1 ";
			      buff += Character.toString('\n');
			     
			  }
	
			 //buff.append(-2);
			
			 HashMap<ArrayList, Integer> patHash = getClos(buff);
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
		id2Freq.clear();
		allSentences.clear();
		sentId.clear();
		id2Weight.clear();
		//m_Stemmer.
	}
	
	public void computWeight( HashMap<ArrayList, Integer> patHash, int numOfSent)
	{
		Iterator iter = patHash.entrySet().iterator();
		while (iter.hasNext()) 
		{
			
			Map.Entry entry = (Map.Entry) iter.next();
			ArrayList<Integer> it = (ArrayList)entry.getKey();
			//if(it.size()>1)
			//{
				int sup = Integer.parseInt(entry.getValue().toString());
				double weight = (double)sup*it.size()/numOfSent;
					
				for(int i=0; i<it.size(); i++)
				{
					int id = it.get(i);
						
						//if(id == 1091)
							//System.out.println("find it");
					if(id2Weight.containsKey(id))
					{
						id2Weight.put(id,id2Weight.get(id)+weight);
						id2Freq.put(id, id2Freq.get(id)+sup);
					}
					else
					{
						id2Freq.put(id, sup);
						id2Weight.put(id, weight);
					}
				}
			//}
			
		}
	}
	
	
	HashMap<ArrayList,Integer> getClos(String buff) throws Exception 
	{
		String writeName = "testdocs/en/train/multiDocu.txt";
	     BufferedWriter writer = new BufferedWriter(new FileWriter(new File(writeName)));   
	     writer.write(buff);
		 writer.close();
		
		INSGrow ins = new INSGrow();
		ins.input(writeName,3);
		
       ins.search();
		
		HashMap<ArrayList, Integer> patHash = ins.getPatHash();
		
		/*Iterator iter = patHash.entrySet().iterator();
		while (iter.hasNext()) 
		{
			Map.Entry entry = (Map.Entry) iter.next();
			ArrayList<Integer> it = (ArrayList)entry.getKey();
			for(int ii=0; ii<it.size(); ii++)
				System.out.print(id2WordHash.get(it.get(ii))+" ");
			System.out.println("->"+entry.getValue());
		}*/
		return patHash;
	}
	
	
	public void calcuSum()
	{
		Iterator iter = id2Weight.entrySet().iterator();
		while (iter.hasNext()) 
		{
			Map.Entry entry = (Map.Entry) iter.next();
			int id = Integer.parseInt(entry.getKey().toString());
			//System.out.println(entry.getKey()+"->" + entry.getValue() + ":" + id2Isf.get(entry.getKey())+" "+ id2Freq.get(entry.getKey()) + " "+ id2Weight.get(entry.getKey()));
			//id2Weight.put(id,Double.parseDouble(entry.getValue().toString())*id2Isf.get(id)*id2Freq.get(id)/sentId.size());
			//id2Weight.put(id,(double)id2Isf.get(id)*id2Freq.get(id)/sentId.size());
			id2Weight.put(id,Double.parseDouble(entry.getValue().toString())*id2Isf.get(id)/sentId.size());
			//id2Weight.put(id,Double.parseDouble(entry.getValue().toString())*id2Freq.get(id)/sentId.size());
		}
	}
	
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
				
				for(int j=0; j<sentId.get(i).size(); j++)
				{
					int id = sentId.get(i).get(j);
					//double pum = 0;
					if(id2Weight.containsKey(id))
					{
						//if(preId == id)
							//pum = preRes*0.3;
						//else
						    res += id2Weight.get(id);
						    
						//id2Weight.put(id, id2Weight.get(id)*0.15);
					}
					//preId = id;
					//preRes = pum;
					//res += pum;
				}
				//if(lenQ==0)
					score[i] = res*sentIndexId.get(i);
				//else
					//score[i] = res*200/(allSentences.get(i).getBytes().length+lenQ);
				//score[i] = res*sentIndexId.get(i);
			}
			
			int index = getMax(score,sumId);
			//if(sumId.contains(index))
				//continue;
			System.out.println(index);
			sumId.add(index);
			len += allSentences.get(index).getBytes().length;
			
			for(int i=0; i<sentId.get(index).size(); i++)
			{
				int id = sentId.get(index).get(i);
				if(id2Weight.containsKey(id))
					id2Weight.put(id, id2Weight.get(id)*belt);
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
		
		//String dirName = "F:/数据集/DUC2002/DUC2002_Summarization_Documents/docs.with.sentence.breaks/";
		MaxentTagger tagger = new MaxentTagger("E:/Qiang/stanford-postagger-2014-01-04/models/wsj-0-18-bidirectional-nodistsim.tagger");
		TagSum ts = new TagSum(0.15);
		//clm.test();
		String sum = ts.clm("E:/Qiang/TextMining/DUC2004/d30002",tagger);
		
		System.out.println(sum);
		//clm.mainFun(dirName, "F:/program/TextMining/experimentResult/DUC2002");
		
		
	}

}
