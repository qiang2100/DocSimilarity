package MultiDocument;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;


import kex.pattern.INSGrow;
import kex.stemmers.MartinPorterStemmer;
import kex.stopwords.Stopwords;
import kex.stopwords.StopwordsEnglish;


//Author: Qiang Jipeng
//Time: 2014-03-26
//Title: Multi-Document summarization based on Closed sequential mining


public class CLM {

	
	private HashMap<String, Integer> word2IdHash = new HashMap<String, Integer>(); //<word, word's id>
	private HashMap<Integer, String> id2WordHash = new HashMap<Integer, String>(); // <word's id, word>
	private HashMap<Integer, Integer> id2Isf = new HashMap<Integer, Integer>(); // <word's id,  word's isf>
	private HashMap<Integer, Integer> id2Idf = new HashMap<Integer, Integer>(); // <word's id, word's idf>
	private MartinPorterStemmer m_Stemmer = new MartinPorterStemmer();
	private Stopwords m_EnStopwords = new StopwordsEnglish();
	private ArrayList<String> allSentences = new ArrayList<String> (); // store all the original sentences
	//private ArrayList<String> allHeads = new ArrayList<String>(); // store the head of all documents
	private ArrayList<Integer> lenOfAllSentences = new ArrayList<Integer>(); //store the length of all the original sentences
	private ArrayList<String> origTextArr = new ArrayList<String>();
	private int id = 0;
	private int sumSentences = 0;  // the number of all sentences
	private int sumDocuments = 0;  // the number of all documents
	private StringBuffer buff = new StringBuffer();
	
	private ArrayList<ArrayList<Integer>> sentId = new ArrayList<ArrayList<Integer>>();
	private HashMap<Integer, Double> id2Weight = new HashMap<Integer,Double>(); //<word's id, word's wight>
	//private MaxentTagger tagger = new MaxentTagger("F:/数据集/stanford-postagger-2014-01-04/models/wsj-0-18-bidirectional-nodistsim.tagger");
	//private HashMap<Integer, String> id2Tag = new HashMap<Integer, String>(); //<word's id, word's Tag>
	
	public CLM()
	{	
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
	
	public String extraHead(String ori)
	{
		String str[] = ori.split("[><]");
		//System.out.println();
		return str[2];
		
	}
	
	public String extraSen(String ori)
	{
		//System.out.println(ori);
		String str[] = ori.split("[><]");
		//System.out.println(str[1]);
		String subStr[] = str[1].split("[\"]");
		//System.out.println(subStr[5]);
		lenOfAllSentences.add(Integer.parseInt(subStr[5]));
		return str[2];
		
	}
	
	public void splitText(String text, Set<Integer> diffDocSet)
	{
		String[] sentences = text.split("\r|\n");
		boolean isHead = false;
		boolean isText = false;
		int countHead = 0;
		for(int j=0; j<sentences.length; j++)
		{
			
			if(sentences[j].equals("<HEAD>") || sentences[j].equals("<HL>") || sentences[j].equals("<HEADLINE>"))
			{
				isHead = true;
				countHead++;
				continue;
			}
			else if(sentences[j].equals("</HEAD>") || sentences[j].equals("</HL>") || sentences[j].equals("</HEADLINE>"))
			{
				isHead = false;
				continue;
			}else if(sentences[j].equals("<TEXT>"))
			{
				isText = true;
				continue;
			}else if(sentences[j].equals("</TEXT>"))
			{
				isText = false;
				break;
			}
			if(isHead && (countHead==1) && sentences[j].length()>4)
			{
				
				//String sen = extraHead(sentences[j]);
				//System.out.println(sentences[j]);
				//System.out.println(sen);
				//allHeads.add(sen);
				//buff.append(process(sen,diffDocSet,false));
			}
			
			if(isText && sentences[j].length()>20)
			{
				String sen = extraSen(sentences[j]);
				//System.out.println(sen);
				allSentences.add(sen);
				buff.append(process(sen,diffDocSet,true));
			}
			
		}
		
	}
	
	//read file from a directory, and then preprocess the file for each file.
	public void extractSentenceFromDir(String filePath)
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
		for(int i=0; i<fileList.length; i++)
		{
			File readfile = new File(filePath + "\\" + fileList[i]);
			if(!readfile.isDirectory())
			{
				//System.out.println("path=" + readfile.getPath());
				
				Set<Integer> diffDocSet = new HashSet<Integer>();
				String text = readTextFromFile(readfile.getPath());
				//System.out.println(text);
				//Pattern ptn = Pattern.compile("\\s+");
				splitText(text,diffDocSet);
				diffDocSet.clear();
				//buff.append(-2);
				//System.out.println();
			 }
		}
		sumSentences = allSentences.size();// + allHeads.size();
		sumDocuments = fileList.length;
		//System.out.println("the number of sentences :" + allSentences.size());
		//System.out.println("the number of sentences :" + lenOfAllSentences.size());
		//System.out.println(allHeads.size());
	}
	
	public StringBuffer process(String sentence, Set<Integer> diffDocSet, boolean isSen)
	{
		//System.out.println(sentence);
		SentencePreProcess spp = new SentencePreProcess();
		String text = spp.tokenize(sentence);
	   // String tagged = tagger.tagString(text); 
		//System.out.println(tagged);
	   // String []tag = tagged.split("[ _]");
	    //tagged = null;
		StringTokenizer tok = new StringTokenizer(text);
		int num = 0;
		StringBuffer res = new StringBuffer();
	
		Set<Integer> diffId = new HashSet<Integer>();
		
		ArrayList<Integer> sId = new ArrayList<Integer>();
		
		//int tagIndex = 0;
		while (tok.hasMoreTokens()) 
		{
			String token = tok.nextToken();
		    origTextArr.add(token);
	    	token=token.toLowerCase();
		    token = m_Stemmer.stemString(token);
		    
		    if (!m_EnStopwords.isStopword(token))
		    {

//			    stemmedArr.add(token);
		    	//System.out.println(token + " "+ tag[tagIndex*2+1]);
		    	num++;
			    if (word2IdHash.get(token)==null)
			    {
			    	res.append(id + " ");
				    word2IdHash.put(token, id);
				    id2WordHash.put(id, token);
				    sId.add(id);
				    diffId.add(id);
				    diffDocSet.add(id);
				    id2Idf.put(id, 1);
			    	id2Isf.put(id, 1);
				   
			    	//id2Tag.put(id, tag[tagIndex*2+1]);
				    id++;
			    } else
			    {
			    	int wid=(Integer)word2IdHash.get(token);
			    	
			    	res.append(wid + " ");
			    	sId.add(wid);
			    	if(!diffId.contains(wid))
			    	{
			    		diffId.add(wid);
				    	id2Isf.put(wid, id2Isf.get(wid)+1);
			    	}
			    	
			    	if(!diffDocSet.contains(wid))
			    	{
			    		diffDocSet.add(wid);
				    	id2Idf.put(wid, id2Idf.get(wid)+1);
			    	}
			    	
			    }
			  
		    }
		    //tagIndex++;
	   }
		diffId.clear();
		if(isSen)
			sentId.add(sId);
		if (num >=1) res.append(-1 + " \n");
		
		//System.out.println(res);
		return res;
	}
	
//	public String sentenceProcess()
//	{
//		StringBuffer buff = new StringBuffer();
//		
//		for(int i=0; i<allHeads.size(); i++)
//		{
//			buff.append(process(allHeads.get(i)));
//		}
//		for(int i=0; i<allSentences.size(); i++)
//		{
//			buff.append(process(allSentences.get(i)));
//		}
//		buff.append(-2);
//		return buff.toString();
//		
//	}
//	
	public String clm(String dirName)
	{
		try
		{
			//String dirName = "F:/数据集/DUC2002/DUC2002_Summarization_Documents/docs.with.sentence.breaks/d061j";
			//String dirName = "F:/program/TextMining/testData";
			//System.out.println(dirName);
			
			String writeName = "testdocs/en/train/multiDocu.txt";
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(writeName)));   
			
			extractSentenceFromDir(dirName); 
			//test();
			//String str = sentenceProcess();
			
			//System.out.println(str);
			writer.write(buff.toString());
			writer.close();
			
			INSGrow ins = new INSGrow();
			ins.input(writeName,sumDocuments/3);
			
//			ins.build_next_index();
			
			Date start = new Date();
			//System.out.println("Starting searching...");
			ins.search();
			
			HashMap<ArrayList, Integer> patHash = ins.getPatHash();
			
			/*Iterator iter = patHash.entrySet().iterator();
			while (iter.hasNext()) 
			{
				Map.Entry entry = (Map.Entry) iter.next();
				ArrayList<Integer> it = (ArrayList)entry.getKey();
				for(int i=0; i<it.size(); i++)
					System.out.print(it.get(i)+" ");
				System.out.println("->"+entry.getValue());
			}*/
			
			computWeight(patHash);
			
			/*Iterator iter = id2Weight.entrySet().iterator();
			while (iter.hasNext()) 
			{
				Map.Entry entry = (Map.Entry) iter.next();
			
				System.out.println(id2WordHash.get(entry.getKey())+"->"+entry.getValue());
			}*/
			//System.out.println(sentId.size());
			String summary = computSummary();
			//System.out.println(summary.length());
			Date end = new Date();
			
			return summary;
			//for(int i=0; i<100; i++)
				 //System.out.print(getWordForID(i) + " ");
			
			//System.out.println("the size of all words :" + origTextArr.size());
			//System.out.println("the size of diff words :" + word2IdHash.size());
			
			//System.out.println(sentId.size());
			//System.out.println(allSentences.size());
			//System.out.println(lenOfAllSentences.size());
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public String computSummary()
	{
		String summary = "";
		
		ArrayList<Integer> sumId = new ArrayList<Integer>();
		int len = 0;
		
		while(len<=200)
		{
			double score[] = new double[allSentences.size()];
			for(int i=0; i<sentId.size(); i++)
			{
				if(sumId.contains(i))
					continue;
				double res = 0;
				for(int j=0; j<sentId.get(i).size(); j++)
				{
					if(id2Weight.containsKey(sentId.get(i).get(j)))
						res += id2Weight.get(sentId.get(i).get(j));
				}
				score[i] = res/lenOfAllSentences.get(i);
				
			}
			
			int index = getMax(score);
			
			sumId.add(index);
			len += lenOfAllSentences.get(index);
			
			for(int i=0; i<sentId.get(index).size(); i++)
			{
				int id = sentId.get(index).get(i);
				if(id2Weight.containsKey(id))
					id2Weight.put(id, id2Weight.get(id)*0.15);
			}
			//break;
		}
		
		for(int i=0; i<sumId.size(); i++)
		{
			summary += allSentences.get(sumId.get(i));
			//System.out.println("the id number :"+ sumId.get(i) + "->"+ allSentences.get(sumId.get(i)));
		}
		
		
		return summary;
	}
	
	
	public int getMax(double []score)
	{
		double max = 0;
		int index = 0;
		
		for(int i=0; i<score.length; i++)
		{
			if(score[i]>max)
			{
				max = score[i];
				index = i;
			}
		}
		return index;
	}
	
	public void computWeight(HashMap<ArrayList, Integer> patHash)
	{
		Iterator iter = patHash.entrySet().iterator();
		while (iter.hasNext()) 
		{
			
			Map.Entry entry = (Map.Entry) iter.next();
			ArrayList<Integer> it = (ArrayList)entry.getKey();
			
			if(it.size()>=1)
			{
				int sup = Integer.parseInt(entry.getValue().toString());
				double weight = sup*it.size();
				
				for(int i=0; i<it.size(); i++)
				{
					int id = it.get(i);
					
					//if(id == 1091)
						//System.out.println("find it");
					if(id2Weight.containsKey(id))
						id2Weight.put(id,id2Weight.get(id)+weight);
					else
						id2Weight.put(id, weight);
				}
			}
		}
		
		Iterator iter2 = id2Weight.entrySet().iterator();
		while (iter2.hasNext()) 
		{
			Map.Entry entry = (Map.Entry) iter2.next();
			int id = Integer.parseInt(entry.getKey().toString());
			double weight = Double.parseDouble(entry.getValue().toString());
			//System.out.println(entry.getKey());
			//System.out.println( id);
			id2Weight.put(id, weight*id2Isf.get(id)*id2Idf.get(id)/(sumDocuments*sumSentences));
			//ArrayList<Integer> it = (ArrayList)entry.getKey();
		}
	}
	
	public void test()
	{
		//String text = "100,000 people";
		//String str = process(text).toString();
		//System.out.println(str);
		
		System.out.println("the number of all sentences :" + sumSentences);
		System.out.println("the number of all texts :" + sumDocuments);
		
		
		System.out.println("id->word:isf idf");
		Iterator iter = id2WordHash.entrySet().iterator();
		while (iter.hasNext()) 
		{
			Map.Entry entry = (Map.Entry) iter.next();
			System.out.println(entry.getKey()+"->" + entry.getValue() + ":" + id2Isf.get(entry.getKey())+" "+ id2Idf.get(entry.getKey()));
		}
		//System.out.println();
		
//		for(int i=0; i<sentId.size(); i++)
//		{
//			for(int j=0; j<sentId.get(i).size(); j++)
//			{
//				System.out.print(sentId.get(i).get(j) + " ");
//			}
//			System.out.println();
//		}
	}
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//String dirName = "F:/数据集/DUC2002/DUC2002_Summarization_Documents/docs.with.sentence.breaks/";
		
		CLM clm = new CLM();
		//clm.test();
		clm.clm("F:/数据集/DUC2002/DUC2002_Summarization_Documents/docs.with.sentence.breaks/d061j");
		
		//clm.mainFun(dirName, "F:/program/TextMining/experimentResult/DUC2002");
		
		
	}

}
