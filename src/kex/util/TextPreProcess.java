package kex.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import weka.core.FastVector;

import com.blogever.RssReader.*;

import kex.pattern.INSGrow;
import kex.stemmers.SremovalStemmer;
import kex.stemmers.Stemmer;
import kex.stemmers.MartinPorterStemmer;
import kex.stopwords.Stopwords;
import kex.stopwords.StopwordsEnglish;
import kex.stopwords.StopwordsChinese;

public class TextPreProcess{
	
	private String  m_dirName = "testdocs/en/train";
	private boolean m_DisallowInternalPeriods = true;
	private int m_MaxPhraseLength =1;
	private MartinPorterStemmer m_Stemmer = new MartinPorterStemmer();
	private Stopwords m_EnStopwords = new StopwordsEnglish();
	private Stopwords m_ChStopwords = new StopwordsChinese();
	private int m_MinPhraseLength = 1;
	private int m_MinNumOccur = 3;
	private String fileName="testdocs/en/train/test.txt";
	private HashMap word2IdHash = new HashMap();
	private HashMap<Integer, String> id2WordHash = new HashMap();
	
	private ArrayList origTextArr = new ArrayList();
	private ArrayList stemmedArr = new ArrayList();
	private ArrayList idTextArr = new ArrayList();
	
	public TextPreProcess()
	{
		
	}
	
	public TextPreProcess(String str)
	
	{
		fileName = str;
	}
	
	public void setFileName(String str)
	{
		fileName = str;
	}
	
	public String getWordForID(int i)
	{
		 return id2WordHash.get(i);
	}
	public boolean isEnglishStopwords(String word)
	{
		return m_EnStopwords.isStopword(word);
	}
	
	public boolean isChineseStopwords(String word)
	{
		return m_ChStopwords.isStopword(word);
	}
	
	public String tokenize (String str) {
		StringBuffer resultStr = new StringBuffer();
		int j = 0;
		boolean phraseStart = true;
		boolean seenNewLine = false;
		boolean haveSeenHyphen = false;
		boolean haveSeenSlash = false;
		while (j < str.length()) {
		  boolean isWord = false;
		  boolean potNumber = false;
		  int startj = j;
		  while (j < str.length()) {
		    char ch = str.charAt(j);
		    if (Character.isLetterOrDigit(ch)) {
		      potNumber = true;
		      isWord = true;
		      //aly: allowing digits as words
		      /*if (Character.isLetter(ch)) {
			isWord = true;
			}
		      */
		      j++;
		    } else if ((!m_DisallowInternalPeriods && (ch == '.')) ||
			       (ch == '@') ||
			       (ch == '_') ||
			       (ch == '&') ||
			       (ch == '/') ||
			       (ch == '-')) {
		      if ((j > 0) && (j  + 1 < str.length()) &&
			  Character.isLetterOrDigit(str.charAt(j - 1)) &&
			  Character.isLetterOrDigit(str.charAt(j + 1))) {
			j++;
		      } else {
			break;
		      }
		    } else if (ch == '\'') {
		      if ((j > 0) &&
			  Character.isLetterOrDigit(str.charAt(j - 1))) {
			j++;
		      } else {
			break;
		      }
		    } else {
		      break;
		    }
		  }
		  if (isWord == true) {
		    if (!phraseStart) {
		      if (haveSeenHyphen) {
			resultStr.append('-');
		      } else if (haveSeenSlash) {
			resultStr.append('/');
		      } else {
			resultStr.append(' ');
		      }
		    }
		    resultStr.append(str.substring(startj, j));
		    if (j == str.length()) {
		      break;
		    }
		    phraseStart = false;
		    seenNewLine = false;
		    haveSeenHyphen = false;
		    haveSeenSlash = false;
		    if (Character.isWhitespace(str.charAt(j))) {
		      if (str.charAt(j) == '\n') {
			seenNewLine = true;
		      } 
		    } else if (str.charAt(j) == '-') {
		      haveSeenHyphen = true;
		    } else if (str.charAt(j) == '/') {
		      haveSeenSlash = true;
		    } else {
		      phraseStart = true;
		      resultStr.append('\n');
		    }
		    j++;
		  } else if (j == str.length()) {
		    break;
		  } else if (str.charAt(j) == '\n') {
		    if (seenNewLine) {
		      if (phraseStart == false) {
			resultStr.append('\n');
			phraseStart = true;
		      }
		    } else if (potNumber) {
		      if (phraseStart == false) {
			phraseStart = true;
			resultStr.append('\n');
		      }
		    }
		    seenNewLine = true;
		    j++;
		  } else if (Character.isWhitespace(str.charAt(j))) {
		    if (potNumber) {
		      if (phraseStart == false) {
			phraseStart = true;
			resultStr.append('\n');
		      }
		    }
		    j++;
		  } else {
		    if (phraseStart == false) {
		      resultStr.append('\n');
		      phraseStart = true;
		    }
		    j++;
		  }
		}
		return resultStr.toString();
    } 
	
	private String getPhrases(String str)
	{
		
		
		String[] buffer = new String[m_MaxPhraseLength];
		HashMap hash = new HashMap();
		
		// Extracting strings of a predefined length from "str":
		
		StringBuffer buff = new StringBuffer();
		StringTokenizer tok = new StringTokenizer(str, "\n");
		int pos = 1; 
		int sentenceId=1;
		
		while (tok.hasMoreTokens())
		{
			String phrase = tok.nextToken();
			int numSeen = 0;
			StringTokenizer wordTok = new StringTokenizer(phrase, " ");
			while (wordTok.hasMoreTokens()) 
			{
				String word = wordTok.nextToken();
				
				// Store word in buffer
				for (int i = 0; i < m_MaxPhraseLength - 1; i++) 
				{
					buffer[i] = buffer[i + 1];
				}
				buffer[m_MaxPhraseLength - 1] = word;
				
				// How many are buffered?
				numSeen++;
				if (numSeen > m_MaxPhraseLength)
				{
					numSeen = m_MaxPhraseLength;
				}
				
				// Don't consider phrases that end with a stop word
				if (m_EnStopwords.isStopword(buffer[m_MaxPhraseLength - 1])) 
				{
					pos++;
					continue;
				}	
				
				// Loop through buffer and add phrases to hashtable
				StringBuffer phraseBuffer = new StringBuffer();
				for (int i = 1; i <= numSeen; i++) 
				{
					if (i > 1) 
					{
						phraseBuffer.insert(0, ' ');
					}
					phraseBuffer.insert(0, buffer[m_MaxPhraseLength - i]);
					
					// Don't consider phrases that begin with a stop word
					if ((i > 1) && 
							(m_EnStopwords.isStopword(buffer[m_MaxPhraseLength - i]))) 
					{
						continue;
					}
					
					// Final restriction:
					// Only consider phrases with minimum length
					if (i >= m_MinPhraseLength) 
					{
						
						// orig = each detected phase in its original spelling  
						String orig = phraseBuffer.toString();

						String pseudo = orig.toLowerCase();
						pseudo = m_Stemmer.stemString(pseudo);
						
    					if (pseudo != null) 
    					{
							
							buff.append(pseudo + "; ");
						} 
					}
				}
				pos++;
			}
			buff.append("\n");
			sentenceId++;
			
		}
		/**
		Iterator phrases = hash.keySet().iterator();
		
		while (phrases.hasNext())
		{
			String phrase = (String)phrases.next();
			FastVector info = (FastVector)hash.get(phrase);
			
			// Occurring less than m_MinNumOccur? //m_MinNumOccur			
			if (((Counter)((FastVector)info).elementAt(1)).value() < 2) 
			{
				phrases.remove();
				continue;
			}
		}
		**/
		return buff.toString();
	}
	
	public String pseudoPhrase(String str) {
		//System.err.print(str + "\t");
		String[] pseudophrase;
		String[] words;
		String str_nostop;
		String stemmed;
		
		
		str = str.toLowerCase();
		
		// This is often the case with Mesh Terms,
		// where a term is accompanied by another specifying term
		// e.g. Monocytes/*immunology/microbiology
		// we ignore everything after the "/" symbol.
		if (str.matches(".+?/.+?")) {
			String[] elements = str.split("/");		
			str = elements[0];
		}	
		
		// removes scop notes in brackets
		// should be replaced with a cleaner solution
		if (str.matches(".+?\\(.+?")) {
			String[] elements = str.split("\\(");		
			str = elements[0];			
		}	
		if (str.matches(".+?\\'.+?")) {
			String[] elements = str.split("\\'");		
			str = elements[1];			
		}	
		
		
		// Remove some non-alphanumeric characters
		
		// str = str.replace('/', ' ');
		str = str.replace('-', ' ');
		str = str.replace('&', ' ');
		
		
		str = str.replaceAll("\\*", "");
		str = str.replaceAll("\\, "," ");
		str = str.replaceAll("\\. "," ");
		str = str.replaceAll("\\:","");
		
		
		str = str.trim();
		
		// Stem string
		words = str.split(" ");
		str_nostop = "";
		for (int i = 0; i < words.length; i++) {
			if (!m_EnStopwords.isStopword(words[i])) {
				if (str_nostop.equals("")) {
					str_nostop = words[i];
				} else {
					str_nostop = str_nostop + " " + words[i];
				}
			}
		}
		stemmed = m_Stemmer.stemString(str_nostop);
		
		//System.err.println(stemmed + "\t" + str_nostop + "\t"+ str);
		pseudophrase = sort(stemmed.split(" "));
		// System.err.println(join(pseudophrase));
		return join(pseudophrase);
	}
	
	/** 
	 * Joins an array of strings to a single string.
	 */
	private static String join(String[] str) {
		String result = "";
		for(int i = 0; i < str.length; i++) {
			if (result != "") {
				result = result + " " + str[i];
			} else {
				result = str[i];
			}
		}
		return result;
	}	
	
	/**
	 * Sorts an array of Strings into alphabetic order
	 *
	 */
	public static String[] sort (String [] a)    {
		
		// rename firstAt to reflect new role in alphabetic sorting
		int i, j, firstAt;
		
		for (i = 0 ; i < a.length - 1 ; i++) {
			firstAt = i;
			for (j = i + 1 ; j < a.length ; j++) {
				// modify to preserve ordering of a String that starts with
				// upper case preceding the otherwise identical String that
				// has only lower case letters
				if (a [j].toUpperCase ().compareTo (a [firstAt].toUpperCase ()) < 0) {
					// reset firstAt
					firstAt = j;
				}
				// if identical when converted to all same case
				if (a [j].toUpperCase ().compareTo (a [firstAt].toUpperCase ()) == 0) {
					// but a[j] precedes when not converted
					if (a [j].compareTo (a [firstAt]) < 0) {
						// reset firstAt
						firstAt = j;
					}
				}
			}
			if (firstAt != i) {
				swap (i, firstAt, a);
			}
		}
		return a;
	} // end method selectionSort
	
	/** 
	 * overloaded swap method: exchange 2 locations in an array of Strings.
	 */
	public static void swap (int loc1, int loc2, String [] a) {
		String temp = a [loc1];
		a [loc1] = a [loc2];
		a [loc2] = temp;
	} // end swap
	
	public String chineseTokenize(String str)
	{
		StringBuffer resultStr = new StringBuffer();
		int j = 0;
		boolean phraseStart = true;
		boolean seenNewLine = false;
		boolean haveSeenHyphen = false;
		boolean haveSeenSlash = false;
		while (j < str.length()) {
		  boolean isWord = false;
		  boolean potNumber = false;
		  int startj = j;
		  while (j < str.length()) {
		    char ch = str.charAt(j);
		    if ((Character.isLetterOrDigit(ch))||(ch>=0x4E00&&ch<=0x9FA5)) {
		      potNumber = true;
		      isWord = true;
		      j++;
		    } else {
		      break;
		    }
		  } // end while (j<str.length())
		  if (isWord == true) {
		    if (!phraseStart) {
			resultStr.append(' ');
		    }
		    resultStr.append(str.substring(startj, j));
		    if (j == str.length()) {
		      break;
		    }
		    phraseStart = false;
		    seenNewLine = false;
		    if (Character.isWhitespace(str.charAt(j))) {
		      if (str.charAt(j) == '\n') {
			seenNewLine = true;
		      } 
		    } else {
		      phraseStart = true;
		      resultStr.append('\n');
		      resultStr.append(str.substring(j, j+1));
		      resultStr.append('\n');
		    }
		    j++;
		  } else if (j == str.length()) {
		    break;
		  } else if (str.charAt(j) == '\n') {
		      if (phraseStart == false) {
			resultStr.append('\n');
			phraseStart = true;
					      }
		    j++;  
		  } else {
		    if (phraseStart == false) {
		      resultStr.append('\n');
		      resultStr.append(str.substring(j, j+1));
		      resultStr.append('\n');
		      phraseStart = true;
		    }
		    j++;
		  }
		}
		return resultStr.toString();
	}
	
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
	
	public void process()
	{
		if (fileName==null)
		{
			System.out.println("you have not specify the file name.");
			return;
		}
		String text = readTextFromFile(fileName);
		text = tokenize(text);
		int id=0;
		StringTokenizer tok = new StringTokenizer(text);
		while (tok.hasMoreTokens()) 
		{
			String token = tok.nextToken();
		    origTextArr.add(token);
		    if (Character.isLetter(token.charAt(0)))
		    {
		    	token=token.toLowerCase();
			    token = m_Stemmer.stemString(token);
			    stemmedArr.add(token);
			    if (word2IdHash.get(token)==null)
			    {
			    	idTextArr.add(id);
				    word2IdHash.put(token, id);
				    id2WordHash.put(id, token);
				    id++;
				  
			    } else
			    {
			    	int wid=(Integer)word2IdHash.get(token);
				    idTextArr.add(wid);
			    }
			  
		    }
		    else
		    {
		    	idTextArr.add(-1);
		    	stemmedArr.add(token);
		    }
	   }
	}
	
	public String chineseProcess()
	{
		if (fileName==null)
		{
			System.out.println("you have not specify the file name.");
			return null;
		}
		word2IdHash = new HashMap();
		id2WordHash = new HashMap();
		String text = readTextFromFile(fileName);
		StringBuffer buff = new StringBuffer();
		String[] textArr = text.split("[.。?？!！\r\n]");
		int id=0;
		for (int i=0;i<textArr.length;i++)
		{
			String sentence = textArr[i];
			text = SplitCaller.split(sentence,0,0);
			StringTokenizer tok = new StringTokenizer(text);
			while (tok.hasMoreTokens()) 
			{
				String token = tok.nextToken();
			    origTextArr.add(token);
			    if (token.length()>1 && !m_ChStopwords.isStopword(token)&& !isNumber(token))
			    {
				    if (word2IdHash.get(token)==null)
				    {
				    	buff.append(id + " ");
					    word2IdHash.put(token, id);
					    id2WordHash.put(id, token);
					    id++;
					  
				    } else
				    {
				    	int wid=(Integer)word2IdHash.get(token);
					    buff.append(wid + " ");
				    }
				  
			    }

		    }
			if (sentence.length()>1) buff.append(-1 + " \n");
			
		}
		buff.append(-2);

		return buff.toString();
		
		
	}
	
	public HashMap getWord2IdHash()
	{
		return word2IdHash;
	}
	
	public HashMap getId2WordHash()
	{
		return id2WordHash;
	}
	
	public String englishProcess()
	{
		if (fileName==null)
		{
			System.out.println("you have not specify the file name.");
			return null;
		}
		word2IdHash = new HashMap();
		id2WordHash = new HashMap();
		String text = readTextFromFile(fileName);
		StringBuffer buff = new StringBuffer();
		String[] textArr = text.split("[.?!]");
		int id=0;
		for (int i=0;i<textArr.length;i++)
		{
			String sentence = textArr[i];
			System.out.println(sentence);
			text = tokenize(sentence);
			StringTokenizer tok = new StringTokenizer(text);
			int num = 0;
			while (tok.hasMoreTokens()) 
			{
				String token = tok.nextToken();
			    origTextArr.add(token);
		    	token=token.toLowerCase();
			    token = m_Stemmer.stemString(token);
			    if (Character.isLetter(token.charAt(0)) && token.length()>=2 && !m_EnStopwords.isStopword(token))
			    {

//				    stemmedArr.add(token);
			    	num++;
				    if (word2IdHash.get(token)==null)
				    {
				    	buff.append(id + " ");
					    word2IdHash.put(token, id);
					    id2WordHash.put(id, token);
					    id++;
					  
				    } else
				    {
				    	int wid=(Integer)word2IdHash.get(token);
				    	buff.append(wid + " ");
				    }
				  
			    }
		   }
			if (num >=1) buff.append(-1 + " \n");
			
		}
		buff.append(-2);

		return buff.toString();
		
		
	}
	
	
	/**
	 * 判断是否是数字
	 * @param phrase
	 * @return
	 */
	private boolean isNumber(String phrase){
		
		boolean is=true;
	    for (int j = 0; j < phrase.length(); j++) {
		    if (!Character.isDigit(phrase.charAt(j))) {
		      is = false;
		      break;
		    }
	    }
		return is;
	}
	
	public String getIdHashStr()
	{
		StringBuffer buff = new StringBuffer();
		Iterator iter = id2WordHash.keySet().iterator();
		while (iter.hasNext())
		{
			int id = (Integer)iter.next();
			String token = (String)id2WordHash.get(id);
			buff.append(id + " " + token + "\n");
		}
		return buff.toString();
	}
	
	public String englishStrProcess(String text)
	{
		String[] buffer = new String[m_MaxPhraseLength];
		word2IdHash = new HashMap();
		id2WordHash = new HashMap();
		StringBuffer buff = new StringBuffer();
		String str = tokenize(text);
		StringTokenizer tok = new StringTokenizer(str, "\n");
		int pos = 1; 
		int id=0;
		
		while (tok.hasMoreTokens())
		{
			int num=0;
			String phrase = tok.nextToken();
			int numSeen = 0;
			StringTokenizer wordTok = new StringTokenizer(phrase, " ");
			/**
			if (wordTok.countTokens()<=1)
			{
				continue;
			}
			**/

			while (wordTok.hasMoreTokens()) 
			{
				String word = wordTok.nextToken();
				
				// Store word in buffer
				for (int i = 0; i < m_MaxPhraseLength - 1; i++) 
				{
					buffer[i] = buffer[i + 1];
				}
				buffer[m_MaxPhraseLength - 1] = word;
				
				// How many are buffered?
				numSeen++;
				if (numSeen > m_MaxPhraseLength)
				{
					numSeen = m_MaxPhraseLength;
				}
				
				// Don't consider phrases that end with a stop word
				if (m_EnStopwords.isStopword(buffer[m_MaxPhraseLength - 1])) 
				{
					pos++;
					continue;
				}	
				
				// Loop through buffer and add phrases to hashtable
				StringBuffer phraseBuffer = new StringBuffer();
				for (int i = 1; i <= numSeen; i++) 
				{
					if (i > 1) 
					{
						phraseBuffer.insert(0, ' ');
					}
					phraseBuffer.insert(0, buffer[m_MaxPhraseLength - i]);
					
					// Don't consider phrases that begin with a stop word
					if ((i > 1) && 
							(m_EnStopwords.isStopword(buffer[m_MaxPhraseLength - i]))) 
					{
						continue;
					}
					
					// Final restriction:
					// Only consider phrases with minimum length
					if (i >= m_MinPhraseLength) 
					{
						
						// orig = each detected phase in its original spelling  
						String orig = phraseBuffer.toString();

						String pseudo = orig.toLowerCase();
//						String pseudo = pseudoPhrase(orig);
						pseudo = m_Stemmer.stemString(pseudo);
						
    					if (pseudo != null) 
    					{
    						num++;
    					    if (word2IdHash.get(pseudo)==null)
    					    	
    					    {
    					    	buff.append(id + " ");
    						    word2IdHash.put(pseudo, id);
    						    id2WordHash.put(id, pseudo);
    						    id++;
    						  
    					    } else
    					    {
    					    	int wid=(Integer)word2IdHash.get(pseudo);
    					    	buff.append(wid + " ");
    					    }
							
						} 
					}
				}
			}
			
			/**
			String sentence = tok.nextToken();
			StringTokenizer wordTok = new StringTokenizer(sentence, " ");
			while (wordTok.hasMoreTokens())
			{
				String token = wordTok.nextToken();
				token=token.toLowerCase();
			    token = m_Stemmer.stemString(token);
			    if (Character.isLetter(token.charAt(0)) && token.length()>=2 && !m_EnStopwords.isStopword(token))
			    {
			    	num++;
				    if (word2IdHash.get(token)==null)
				    	
				    {
				    	buff.append(id + " ");
					    word2IdHash.put(token, id);
					    id2WordHash.put(id, token);
					    id++;
					  
				    } else
				    {
				    	int wid=(Integer)word2IdHash.get(token);
				    	buff.append(wid + " ");
				    }
			    }
				
			}
			**/
	    	
			if (num >=1) buff.append(-1 + " \n");
			
		}

		return buff.toString();
		
	}
	
	public void chineseStrProcess(String text)
	{
		text = SplitCaller.split(text,0,0);
		int id=0;
		StringTokenizer tok = new StringTokenizer(text);
		while (tok.hasMoreTokens()) 
		{
			String token = tok.nextToken();
		    origTextArr.add(token);
		    if ((token.charAt(0)>=0x4E00&&token.charAt(0)<=0x9FA5) 
		    		&& (!m_ChStopwords.isStopword(token))&& (token.length()>=m_MinPhraseLength))
		    {
			    if (word2IdHash.get(token)==null)
			    {
			    	idTextArr.add(id);
				    word2IdHash.put(token, id);
				    id2WordHash.put(id, token);
				    id++;
				  
			    } else
			    {
			    	int wid=(Integer)word2IdHash.get(token);
				    idTextArr.add(wid);
			    }
			  
		    }
		    else
		    {
		    	idTextArr.add(-1);
		    	stemmedArr.add(token);
		    }
	   }
		
	}
	
	public ArrayList getOrigTextArr()
	{
		return origTextArr;
	}
	
	public ArrayList getIdTextArr()
	{
		return idTextArr;
	}
	
	public ArrayList getStemmedArr()
	{
		return stemmedArr;
	}
	
	
	public Hashtable collectStems() throws Exception {
		
		Hashtable stems = new Hashtable();
		
		try {
			File dir = new File(m_dirName);
			String[] files = dir.list();
			for (int i = 0; i < files.length; i++) {
				if (files[i].endsWith(".txt.final")) {
					String stem = files[i].substring(0, files[i].length() - 10);
					if (!stems.containsKey(stem)) {
						stems.put(stem, new Double(0));
					}
				}
			}
		} catch (Exception e) {
			throw new Exception("Problem opening directory " + m_dirName);
		}
		return stems;
	}
	
	private int getPatternFeature(HashMap hash, String fileName)
	{
		try 
		{
			BufferedReader reader = new BufferedReader(new FileReader(fileName + ".pat"));
			String aLine = reader.readLine();
			BufferedReader reader2 = new BufferedReader(new FileReader(fileName + ".hash"));
			String aLine2 = reader2.readLine();
			HashMap id2WordHash = new HashMap();
			HashMap word2IdHash = new HashMap();
			HashMap hash1 = new HashMap();

			while (aLine2!=null) // 读取词语和Id间的映射信息
			{
				StringTokenizer idTok = new StringTokenizer(aLine2);
				int id = Integer.parseInt(idTok.nextToken());
				String word = idTok.nextToken();
				id2WordHash.put(id, word);
				word2IdHash.put(word, id);
				aLine2 = reader2.readLine();
			}
			reader2.close();
			
			aLine=reader.readLine();
			HashMap hashPat = new HashMap();
			while (aLine!=null)
			{
				String[] patInfoArr = aLine.split("[()]");
				ArrayList pat = new ArrayList();
				ArrayList info = new ArrayList();
				StringTokenizer tok = new StringTokenizer(patInfoArr[1]);
				while (tok.hasMoreTokens())
				{
					int item = Integer.parseInt(tok.nextToken());
					pat.add(item);
				}
				tok = new StringTokenizer(patInfoArr[2]);
				while (tok.hasMoreTokens())
				{
					int pos = Integer.parseInt(tok.nextToken());
					info.add(pos);
					
				}
				hashPat.put(pat, info);
				if (pat.size()==1)
				{
					int wId = (Integer)pat.get(0); 
//					String phrase = (String)id2WordHash.get(wId);
					int sup = (Integer)info.get(0);
					int pos = (Integer)info.get(1);
				
					FastVector vec = new FastVector(3);
					// Update hashtable with all the info
					vec.addElement(new Counter(pos + 1)); //0，首次出现位置
					vec.addElement(new Counter(sup)); //词频
					vec.addElement(new Counter(0)); // 闭合模式数目
					hash1.put(wId, vec);
				}
				aLine = reader.readLine();
			}
			reader.close();
			Iterator it = hashPat.keySet().iterator();
			while (it.hasNext())
			{
				ArrayList pat = (ArrayList)it.next();
				ArrayList info = (ArrayList)hashPat.get(pat);
				int sup = (Integer)info.get(0);
				int pos = (Integer)info.get(1);
				for (int i=0;i<pat.size();i++)
				{
					int id = (Integer)pat.get(i);
					String item = (String)id2WordHash.get(id);
					if (i==0) System.out.print("(");
					System.out.print(item);
					if (i<pat.size()-1) System.out.print(" ");
					else System.out.print(")");
					FastVector vec = (FastVector)hash1.get(id);
					if (vec==null)
					{
						vec = new FastVector(3);
						// Update hashtable with all the info
						vec.addElement(new Counter(pos)); //0，首次出现位置
						vec.addElement(new Counter(sup)); //词频
						vec.addElement(new Counter(1)); // 闭合模式数目
					}
					else
					{
						Counter c = (Counter)vec.elementAt(2);
						c.increment();
					}
					
				}
				System.out.println(" " + info.get(0));
				
			}
			it = hash1.keySet().iterator();
			while (it.hasNext())
			{
				int wid = (Integer)it.next();
				String phrase = (String)id2WordHash.get(wid);
				FastVector vec = (FastVector)hash1.get(wid);
				hash.put(phrase, vec);
//				System.out.println(wid + " " + phrase + " " + ((Counter)vec.elementAt(0)).value() 
//						+ " " + ((Counter)vec.elementAt(1)).value() 
//						+ " " + ((Counter)vec.elementAt(2)).value());
			}
			
		}
		catch(FileNotFoundException e)
		{
			System.err.println("Error: .pat not found in org.test directory");
			System.exit(-1);   //exit
		}
		catch(IOException e)
		{
			System.err.println("Error: problem in stream IO");
			System.exit(-1);
		}
		return hash.size();
	}	
	
	public void englishProcessDir(Hashtable stems)
	{
		Enumeration elem = stems.keys();
		try
		{
			while (elem.hasMoreElements())
			{
				String str = (String)elem.nextElement();
			    setFileName(m_dirName + "/" + str + ".txt.final");
			    String resultText = englishProcess();
			    String resultIdText = getIdHashStr();
			    String resultFile = m_dirName + "/" + str + ".data";
			    String resultIdFile = m_dirName + "/" + str + ".hash";
			    PrintWriter writer = new PrintWriter(new FileWriter(resultFile));
			    writer.print(resultText);
			    writer.close();
			    writer = new PrintWriter(new FileWriter(resultIdFile));
			    writer.print(resultIdText);
			    writer.close();
			    }
		}
		catch(IOException e)
		{
			System.err.println("Error: problem in stream IO");
			System.exit(-1);
		}
	}
	
	public void chineseProcessDir(Hashtable stems)
	{
		Enumeration elem = stems.keys();
		try
		{
			while (elem.hasMoreElements())
			{
				String str = (String)elem.nextElement();
			    setFileName(m_dirName + "/" + str + ".txt");
			    String resultText = chineseProcess();
			    String resultIdText = getIdHashStr();
			    String resultFile = m_dirName + "/" + str + ".data";
			    String resultIdFile = m_dirName + "/" + str + ".hash";
			    PrintWriter writer = new PrintWriter(new FileWriter(resultFile));
			    writer.print(resultText);
			    writer.close();
			    writer = new PrintWriter(new FileWriter(resultIdFile));
			    writer.print(resultIdText);
			    writer.close();
			    }
		}
		catch(IOException e)
		{
			System.err.println("Error: problem in stream IO");
			System.exit(-1);
		}
	}
	
private int getEnglishPhrases(HashMap hash, String str) {
		
	//FileOutputStream out = new FileOutputStream("candidates_kea41.txt");		
	//PrintWriter printer = new PrintWriter(new OutputStreamWriter(out)); 
	
	// hash = table to store all the information about phrases extracted from "str"
	// str  = the content of the document, separated by newlines in sentences
	
	String[] buffer = new String[m_MaxPhraseLength];
	
	// Extracting strings of a predefined length from "str":
	
	str=tokenize(str);
	StringTokenizer tok = new StringTokenizer(str, "\n");
	int pos = 1; 
	
	while (tok.hasMoreTokens()) 
	{
		String phrase = tok.nextToken();
		int numSeen = 0;
		StringTokenizer wordTok = new StringTokenizer(phrase, " ");
		while (wordTok.hasMoreTokens())
		{
			String word = wordTok.nextToken();
			
			// Store word in buffer
			for (int i = 0; i < m_MaxPhraseLength - 1; i++) 
			{
				buffer[i] = buffer[i + 1];
			}
			buffer[m_MaxPhraseLength - 1] = word;
			
			// How many are buffered?
			numSeen++;
			if (numSeen > m_MaxPhraseLength) 
			{
				numSeen = m_MaxPhraseLength;
			}
			
			// Don't consider phrases that end with a stop word
			if (m_EnStopwords.isStopword(buffer[m_MaxPhraseLength - 1]))
			{
				pos++;
				continue;
			}	
			
			// Loop through buffer and add phrases to hashtable
			StringBuffer phraseBuffer = new StringBuffer();
			for (int i = 1; i <= numSeen; i++) 
			{
				if (i > 1) {
					phraseBuffer.insert(0, ' ');
				}
				phraseBuffer.insert(0, buffer[m_MaxPhraseLength - i]);
				
				// Don't consider phrases that begin with a stop word
				if ((i > 1) && 
						(m_EnStopwords.isStopword(buffer[m_MaxPhraseLength - i])))
				{
					continue;
				}
				
				// Final restriction:
				// Only consider phrases with minimum length
				if (i >= m_MinPhraseLength ) 
				{
					
					// orig = each detected phase in its original spelling  
					String orig = phraseBuffer.toString();

					// Create internal representation:
					// either a stemmed version or a pseudo phrase: 
					
					

					String id;
//					if (m_vocabulary.equals("none")) {
					String pseudo = pseudoPhrase(orig);
					id = pseudo;
//					} else {
//						Match against the Vocabulary		
//						id = (String)m_Vocabulary.getID(orig);
//					}
					
				//	 System.out.println(orig + "\t" + pseudo + " \t " + id);
					
					if (id != null && !isNumber(id)&& !m_EnStopwords.isStopword(id)) 
					{
						
						// if Vocabulary is used, derive the correct spelling
						// of the descriptor, else use one of the spellings as in the document
//						if (!m_vocabulary.equals("none")) {
//							orig = m_Vocabulary.getOrig(id);
//						}

						// Get the vector of the current phrase from the hash table.
						// If it was already extracted from "str", the values will be
						// updated in next steps, if not a new vector will be created.
						
						System.out.println(id);
						FastVector vec = (FastVector)hash.get(id);
						
						if (vec == null) 
						{
							
							// Specifying the size of the vector
							// According to additional selected features:
							
							vec = new FastVector(3);
							
							// Update hashtable with all the info
							vec.addElement(new Counter(pos + 1 - i)); //0
							vec.addElement(new Counter()); //1
							vec.addElement(new Counter()); //2
							hash.put(id, vec);
						} else 
						{
							
							// If the phrase already was identified,
							// update its values in the old vector
							
							// Update number of occurrences
							((Counter)((FastVector)vec).elementAt(1)).increment();
						}
							
					} 
				}
			}
			pos++;
		}
	}

		// Replace secondary hashtables with most commonly occurring
		// version of each phrase (canonical) form. Delete all words
		// that are proper nouns.
		Iterator phrases = hash.keySet().iterator();
		
		while (phrases.hasNext()) {
			String phrase = (String)phrases.next();
			FastVector info = (FastVector)hash.get(phrase);
			
			// Occurring less than m_MinNumOccur? //m_MinNumOccur			
			if (((Counter)((FastVector)info).elementAt(1)).value() < m_MinNumOccur) {
				phrases.remove();
				continue;
			}
		}
		return pos;
	}
	
	public static void main(String[] args) throws Exception
	{
		
		
		String fileName = "testdocs/en/train/test.txt";
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File("testdocs/en/train/test_postProcess.txt")));     
		
		TextPreProcess tpp = new TextPreProcess();
		//String str= tpp.readTextFromFile(fileName);
		String str = tpp.englishProcess();
		HashMap hash =new HashMap();
//		tpp.getEnglishPhrases(hash, str);
//		System.out.println(hash.size());
		//str = tpp.englishStrProcess(str);
		System.out.println(str);
		writer.write(str);
		writer.close();
		for(int i=0; i<100; i++)
		 System.out.print(tpp.getWordForID(i) + " ");
		HashMap id2WordHash = tpp.getId2WordHash();
//		str = tpp.tokenize(str);
//		str = tpp.getPhrases(str);
//		System.out.println(str);

		/*INSGrow tpm = new INSGrow();
		tpm.inputStr(str, 3);
		tpm.search();
		HashMap patHash = tpm.getPatHash();
		
		Iterator it = patHash.keySet().iterator();
		while (it.hasNext())
		{
			ArrayList pat = (ArrayList)it.next();
			for (int i=0;i<pat.size();i++)
			{
				int id = (Integer)pat.get(i);
				String word = (String)id2WordHash.get(id);
				if (i<pat.size()-1) System.out.print(word+", ");
				else System.out.println(word+ " : " +patHash.get(pat));
			}

		}
		
		System.out.println(patHash.size());*/
		
		
//		System.out.println(hash.size());

//		Hashtable stems = tpp.collectStems();
//		tpp.englishProcessDir(stems);
//		HashMap hash = new HashMap();
//		tpp.getPatternFeature(hash, "testdocs/ch/train/163-009");
	
	}
			


}
