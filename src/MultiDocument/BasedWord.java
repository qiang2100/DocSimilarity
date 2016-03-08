package MultiDocument;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class BasedWord {

	MaxentTagger tagger = new MaxentTagger("E:/UMASS/stanford-postagger-2014-01-04/models/wsj-0-18-bidirectional-nodistsim.tagger");
	String inputDir = "E:/UMASS/TextMining/DUC2004/";
	String outputDir = "E:/UMASS/TextMining/experimentResult/DUC2004";
	
	public void repet() throws Exception 
	{
		for(double i=0.2; i<=0.8; i = i+0.1)
		{
			for(double j=0; j<0.8; j += 0.2)
				mainFun(i,j);
		}
	}
	
	public void repetLen() throws Exception 
	{
		for(int i=2; i<20; i++)
		{
			mainFun(i);
		}
	}
	
	
	public void repetSup() throws Exception 
	{
		for(long i=2; i<=10; i++)
		{
			mainFun(i);
		}
	}
	
	public void repetRe() throws Exception 
	{
		
		for(double j=0.1; j<=0.5; j=j+0.1)
		{
			for(int i=2; i<16; i += 2)
				mainFun(i,j);
		}
	}
	
	public void repetMin() throws Exception 
	{
		for(int i=10; i>2; i--)
			for(int j=i; j>=2; j--)
			{
				mainFun(i,j);
			}
	}
	
	public void mainFun(int min1, int min2) throws Exception 
	{
		
		if (inputDir==null)
		{
			System.out.println("you have not specify the file name.");
			return ;
		}
		
		File srcFile = new File(inputDir);
		String []dirList = srcFile.list();
		
		for(int i=0; i<dirList.length; i++)
		{
			String path = inputDir + dirList[i];
			File twoDir = new File(path);
			
			ClosSum clm = new ClosSum(min1,min2);
			//clm.test();
			//System.out.println(path);
			System.out.println(dirList[i]);
			String sum = clm.clm(path,tagger);
			clm.clear();
		    clm = null;
			String newName = dirList[i]+".M.100.T.min1_"+min1+"min2_"+min2 +".spl";
			String writeRoot = "experimentResult\\DUC2004"+"\\"+dirList[i]+ "\\peers\\";
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
			writer.write(sum);
			writer.close();
			//System.out.println(sum);
			
			//new Thread().sleep(1000);
		}
		//System.out.println("the number of sentences :" + allSentences.size());
		//System.out.println("the number of sentences :" + lenOfAllSentences.size());
	}
	
	
	
	
	
	
	
	public void mainFun(double alpa, double lenQ) throws Exception 
	{
		
		if (inputDir==null)
		{
			System.out.println("you have not specify the file name.");
			return ;
		}
		
		File srcFile = new File(inputDir);
		String []dirList = srcFile.list();
		
		for(int i=0; i<dirList.length; i++)
		{
			String path = inputDir + dirList[i];
			File twoDir = new File(path);
			
			ClosSum_Remove clm = new ClosSum_Remove(alpa,lenQ);
			//clm.test();
			//System.out.println(path);
			System.out.println(dirList[i]);
			String sum = clm.clm(path,tagger);
			clm.clear();
		    clm = null;
			String newName = dirList[i]+".M.100.T.TagSum_alpa"+(int)(alpa*10)+"lenQ"+(int)(lenQ*10) +".spl";
			String writeRoot = "D:\\myperl\\ROUGE-1.5.5\\RELEASE-1.5.5\\DUC2004"+"\\"+dirList[i]+ "\\peers\\";
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
			writer.write(sum);
			writer.close();
			//System.out.println(sum);
			
			//new Thread().sleep(1000);
		}
		//System.out.println("the number of sentences :" + allSentences.size());
		//System.out.println("the number of sentences :" + lenOfAllSentences.size());
	}
	
	public void mainFun(int lenQ) throws Exception 
	{
		
		if (inputDir==null)
		{
			System.out.println("you have not specify the file name.");
			return ;
		}
		
		File srcFile = new File(inputDir);
		String []dirList = srcFile.list();
		
		for(int i=0; i<dirList.length; i++)
		{
			String path = inputDir + dirList[i];
			File twoDir = new File(path);
			
			ClosSum clm = new ClosSum(lenQ);
			//clm.test();
			//System.out.println(path);
			System.out.println(dirList[i]);
			String sum = clm.clm(path,tagger);
			clm.clear();
		    clm = null;
			String newName = dirList[i]+".M.100.T.TagSum"+lenQ +".spl";
			String writeRoot = "experimentResult\\DUC2004"+"\\"+dirList[i]+ "\\peers\\";
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
			writer.write(sum);
			writer.close();
			//System.out.println(sum);
			
			//new Thread().sleep(1000);
		}
		//System.out.println("the number of sentences :" + allSentences.size());
		//System.out.println("the number of sentences :" + lenOfAllSentences.size());
	}
	
	public void mainFun(long sup) throws Exception 
	{
		
		if (inputDir==null)
		{
			System.out.println("you have not specify the file name.");
			return ;
		}
		
		File srcFile = new File(inputDir);
		String []dirList = srcFile.list();
		
		for(int i=0; i<dirList.length; i++)
		{
			String path = inputDir + dirList[i];
			File twoDir = new File(path);
			
			ClosSum clm = new ClosSum(sup);
			//clm.test();
			//System.out.println(path);
			System.out.println(dirList[i]);
			String sum = clm.clm(path,tagger);
			clm.clear();
		    clm = null;
			String newName = dirList[i]+".M.100.T.TagSum_Sup"+sup +".spl";
			String writeRoot = "experimentResult\\DUC2004"+"\\"+dirList[i]+ "\\peers\\";
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
			writer.write(sum);
			writer.close();
			//System.out.println(sum);
			
			//new Thread().sleep(1000);
		}
		//System.out.println("the number of sentences :" + allSentences.size());
		//System.out.println("the number of sentences :" + lenOfAllSentences.size());
	}
	
	public void mainFun(double belt) throws Exception 
	{
		
		if (inputDir==null)
		{
			System.out.println("you have not specify the file name.");
			return ;
		}
		
		File srcFile = new File(inputDir);
		String []dirList = srcFile.list();
		
		for(int i=0; i<dirList.length; i++)
		{
			String path = inputDir + dirList[i];
			File twoDir = new File(path);
			
			ClosSum clm = new ClosSum(belt);
			//clm.test();
			//System.out.println(path);
			System.out.println(dirList[i]);
			String sum = clm.clm(path,tagger);
			clm.clear();
		    clm = null;
			String newName = dirList[i]+".M.100.T.TagSum"+(int)(belt*100) +".spl";
			String writeRoot = "experimentResult\\DUC2004"+"\\"+dirList[i]+ "\\peers\\";
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
			writer.write(sum);
			writer.close();
			//System.out.println(sum);
			
			//new Thread().sleep(1000);
		}
		//System.out.println("the number of sentences :" + allSentences.size());
		//System.out.println("the number of sentences :" + lenOfAllSentences.size());
	}
	
	public static void main(String[] args) {
		
		
		String dirName = "E:/UMASS/TextMining/DUC2004/";
		
		BasedWord rd = new BasedWord();
		
		try
		{
			rd.repet();
			//rd.repetLen();
			//rd.repetRe();
			//rd.repetSup();
			//rd.mainFun(12);
			//rd.repetMin();
			//rd.repetPOS();
			//rd.mainFun(0.5,2);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
}
