package MultiDocument;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kex.pattern.INSGrow;
import kex.stemmers.MartinPorterStemmer;
import kex.stopwords.Stopwords;
import kex.stopwords.StopwordsEnglish;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class FrePatt {
	
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
	
	
	
	public boolean isLetterNum(char c)
	{
		if(Character.isLetter(c))
			return true;
		if(Character.isDigit(c))
			return true;
		return false;
	}
	
	public String valueFromListForClos(ArrayList<Integer> list)
	{
		String res = "";
		for(int i=0; i<list.size(); i++)
		{
			res += list.get(i) + " -1 ";
		}
		return res;
	}
	
	public String valueFromListForFre(ArrayList<Integer> list)
	{
		String res = "";
		for(int i=0; i<list.size(); i++)
		{
			res += list.get(i) + " ";
		}
		return res;
	}
	
	
	public String extractSentence(String fileName, MaxentTagger tagger) throws Exception
	{ 
		int num = 0;
		String buff = "";
		
		List<List<HasWord>> sentences = MaxentTagger.tokenizeText(new BufferedReader(new FileReader(fileName)));
		for (List<HasWord> sentence : sentences)
		{
		      ArrayList<TaggedWord> tSentence = tagger.tagSentence(sentence);
		    
		      ArrayList<Integer> idList = new ArrayList<Integer>();
		    
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
		      }
		     Collections.sort(idList);
			   
			 buff += valueFromListForFre(idList) ;
		     buff += "-1 ";
			     // System.out.println(buff);
			 buff += Character.toString('\n');      
		}
		return buff;
	}
	
	
	public void writeForPatternMining(String fileName, MaxentTagger tagger) throws Exception
	{ 
		int num = 0;
		String buff = "";
		
		List<List<HasWord>> sentences = MaxentTagger.tokenizeText(new BufferedReader(new FileReader(fileName)));
		for (List<HasWord> sentence : sentences)
		{
		      ArrayList<TaggedWord> tSentence = tagger.tagSentence(sentence);
		    
		      ArrayList<Integer> idList = new ArrayList<Integer>();
		    
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
		    				 // num++;
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
		      }
		     Collections.sort(idList);
			   
			 buff += valueFromListForClos(idList) ;
		     buff += "-2 ";
			     // System.out.println(buff);
			 buff += Character.toString('\n');      
		}
		
		String writeName = "E:/UMASS/sequential pattern mining/data/multi.txt";
	     BufferedWriter writer = new BufferedWriter(new FileWriter(new File(writeName)));   
	     writer.write(buff);
		 writer.close();
		 
	}
	
	
	public void getClos(String buff, double support) throws Exception 
	{
		String writeName = "testdocs/en/train/multi.txt";
	     BufferedWriter writer = new BufferedWriter(new FileWriter(new File(writeName)));   
	     writer.write(buff);
		 writer.close();
		
		 long begin=0,end=0,time=0;
		 Date mydate=new Date();
			begin=mydate.getTime();

		 
		INSGrow ins = new INSGrow();
		ins.input(writeName,support);
		
       ins.search();
		
       Date mydate2=new Date();
		end=mydate2.getTime();
	
		time += end-begin;
		

		
		HashMap<ArrayList, ArrayList> patHash = ins.getPatSentIndexHash();
		
		Iterator iter = patHash.entrySet().iterator();
		int numOfClosePattern = 0;
		
		while (iter.hasNext()) 
		{
			Map.Entry entry = (Map.Entry) iter.next();
			ArrayList<Integer> it = (ArrayList)entry.getKey();
			for(int ii=0; ii<it.size(); ii++)
				System.out.print(id2WordHash.get(it.get(ii))+" ");
			System.out.println("->"+entry.getValue().toString());
			
			numOfClosePattern++;
		}
		System.out.println("the time is " + time);
		System.out.println("the number of closed pattern is " + numOfClosePattern);
	}
	
	
	public void process(String fileName, MaxentTagger tagger)
	{
		try
		{
			String buff = extractSentence(fileName, tagger);
			
			System.out.println(buff);
			
			getClos(buff,0.1);
			
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
	}
	
	public void computeTimeClose()
	{
		String writeName = "E:/UMASS/sequential pattern mining/data/multi_ME.txt";
		
		long begin=0,end=0,time=0;
		 //Date mydate=new Date();
			//begin=mydate.getTime();
		double maxMemory = 0;
		 
		INSGrow ins = new INSGrow();
		
		double currentMemory = (Runtime.getRuntime().totalMemory() -  Runtime.getRuntime().freeMemory())
				/ 1024d / 1024d;
		if (currentMemory > maxMemory) {
			maxMemory = currentMemory;
		}
		ins.input(writeName,0.01);
		begin = System.currentTimeMillis();
         ins.search();
		
         end = System.currentTimeMillis();
         
         currentMemory = (Runtime.getRuntime().totalMemory() -  Runtime.getRuntime().freeMemory())
 				/ 1024d / 1024d;
 		if (currentMemory > maxMemory) {
 			maxMemory = currentMemory;
 		}
     // Date mydate2=new Date();
		//end=mydate2.getTime();
      	
		time += end-begin;
		System.out.println("the time is " + time);
		HashMap<ArrayList, Integer> patHash = ins.getPatHash();
        System.out.println("the closed pattern is "+ patHash.size());
        
        System.out.println("the memory is " + maxMemory);
	}
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//String dirName = "F:/Êý¾Ý¼¯/DUC2002/DUC2002_Summarization_Documents/docs.with.sentence.breaks/";
		//MaxentTagger tagger = new MaxentTagger("E:/UMASS/stanford-postagger-2014-01-04/models/wsj-0-18-bidirectional-nodistsim.tagger");
		
		FrePatt fp = new FrePatt();
		//fp.process("clostext.txt",tagger);
		
		try
		{
			//fp.writeForPatternMining("clostext.txt", tagger);
			fp.computeTimeClose();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		//clm.mainFun(dirName, "F:/program/TextMining/experimentResult/DUC2002");
		
		
	}
	

}
