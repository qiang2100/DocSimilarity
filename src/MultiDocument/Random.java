package MultiDocument;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;

//Date: 2014-03-28
//using DUC2002 data set,  random select sentences from a summarization, and output.


public class Random {
	
	
	
	public String extraSen(String ori,ArrayList<Integer> lenOfAllSentences)
	{
		String str[] = ori.split("[><]");
		//System.out.println(str[1]);
		String subStr[] = str[1].split("[\"]");
		//System.out.println(subStr[5]);
		lenOfAllSentences.add(Integer.parseInt(subStr[5]));
		return str[2];
		
	}
	
	public void splitText(String text,ArrayList<String> allSentences, ArrayList<Integer> lenOfAllSentences)
	{
		String[] sentences = text.split("\n");
	
		boolean isText = false;
		
		for(int j=0; j<sentences.length; j++)
		{
			
			if(sentences[j].equals("<TEXT>"))
			{
				isText = true;
				continue;
			}else if(sentences[j].equals("</TEXT>"))
			{
				isText = false;
				break;
			}
			
			if(isText && sentences[j].length()>20)
			{
				String sen = extraSen(sentences[j],lenOfAllSentences);
				//System.out.println(sen);
				allSentences.add(sen);
			}
			
		}
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
	
	public void filtSen2(String path, File twoDir, ArrayList<String> allSentences, ArrayList<Integer> lenOfAllSentences)
	{
		String []twoDirList = twoDir.list();
		
		//System.out.println(dirList[i]);
		
		for(int j=0; j<twoDirList.length; j++)
		{
			File readFile = new File(path + twoDirList[j]);
			
			//System.out.println(twoDirList[j]);
			if(!readFile.isDirectory())
			{
				//System.out.println("path=" + readfile.getPath());
				String text = readTextFromFile(readFile.getPath());
				//System.out.println(text);
				//Pattern ptn = Pattern.compile("\\s+");
				splitText(text,allSentences, lenOfAllSentences);
		
				//System.out.println();
			 }
			//cur =  allSentences.size() - sum;
			//sum = allSentences.size();
			//System.out.println("the current file contians " + cur + " sentences.");
			//System.out.println("*************************************");
		}
	}
	
	public void filtSen(String inputDir) throws Exception
	{
		if (inputDir==null)
		{
			System.out.println("you have not specify the file name.");
			return ;
		}
		
		File srcFile = new File(inputDir);		
		String []dirList = srcFile.list();
		
		//int sum = 0;
		//int cur = 0;
		
		//BufferedWriter writer = new BufferedWriter(new FileWriter(new File("experimentResult")));  
		
		
		for(int i=0; i<dirList.length; i++)
		{
			File twoDir = new File(inputDir + "\\" + dirList[i]);
			
			ArrayList<String> allSentences = new ArrayList<String> (); // store all the original sentences
			ArrayList<Integer> lenOfAllSentences = new ArrayList<Integer>(); //store the length of all the original sentences
			
			
			String path = inputDir + "\\" + dirList[i]+"\\";
			
			//System.out.println(dirList[i]);
			filtSen2(path, twoDir, allSentences, lenOfAllSentences);
			
			String newName = dirList[i]+"_random_"+".spl";
			String writeRoot = "experimentResult\\DUC2002"+"\\"+dirList[i]+ "\\peers\\";
			File writeFile = new File(writeRoot);		
			if(!writeFile.exists())
			{		 
				writeFile.mkdirs();		
			} 
			
			File newFile = new File(writeFile,newName);
			if(!newFile.exists())
			{
				newFile.createNewFile();
			}
			BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));  
			
			int wordNum = 0; 
			//Random random = new Random();
			HashSet<Integer> hs = new HashSet<Integer>();
			while(wordNum<=200)
			{
				int num = (int)(Math.random()*allSentences.size());
				if(!hs.contains(num))
				{
					hs.add(num);
					writer.write(allSentences.get(num));
					//System.out.println(allSentences.get(num));
					wordNum += lenOfAllSentences.get(num);
				}
			}
			//System.out.println(allSentences.size());
			//writer.write("111");
			writer.close();
			
			allSentences.clear();
			lenOfAllSentences.clear();
			//System.out.println("----------------------------------------");
		}
	}
	
	
	//inputDir: random select sentences from the input test directory , and then form a summarization.
	//outputDir: the summarizations from random program are stored into the output directory.
	public void mainFun(String inputDir,String outputDir)
	{
		try{
		filtSen(inputDir);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		//System.out.println("the number of sentences :" + allSentences.size());
		//System.out.println("the number of sentences :" + lenOfAllSentences.size());
	}
	
	public static void main(String[] args) {
		
		
			String dirName = "F:/Êý¾Ý¼¯/DUC2002/DUC2002_Summarization_Documents/docs.with.sentence.breaks/";
			
			
			
			Random rd = new Random();
			
			rd.mainFun(dirName, "");
			
		
		
		
	}

}
