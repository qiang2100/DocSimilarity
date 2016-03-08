package MultiDocument;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

// extract human summarization from DUC2002 which only require its length equal 200
public class DUC2002_Model {

	
	public String readTextFromFile(String fileName)
	{
		StringBuffer txtStr = new StringBuffer();
		String text="";
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
			
			String[] sentences = txtStr.toString().split("[><]");
			
			
			text = sentences[2].replaceAll("\r|\n", "");; 
			System.out.println(text);
		
		
		} catch (Exception e) 
		{
			System.err.println("Can't find document.");
		}
		return text;

	}
	
	
	public void mainFun(String inputDir, String outputDir) throws Exception
	{
		if (inputDir==null)
		{
			System.out.println("you have not specify the file name.");
			return ;
		}
		
		File srcFile = new File(inputDir);		
		String []dirList = srcFile.list();
	
		//String writeRoot = "experimentResult\\DUC2002";
		
		
		int flag = 1;
		for(int i=0; i<dirList.length; i++)
		{
			File twoDir = new File(inputDir + "\\" + dirList[i]);
			
			String []twoDirList = twoDir.list();
			
			//System.out.println(dirList[i]);
			
			for(int j=0; j<twoDirList.length; j++)
			{
				if(twoDirList[j].equals("200"))
				{
					//System.out.println(twoDirList[j]);
					File readFile = new File(inputDir + "\\" + dirList[i]+ "\\"+twoDirList[j]);
					
					String text = readTextFromFile(readFile.getPath());
					
					String newName = dirList[i].substring(0,dirList[i].length()-1)+"_model_" + flag+"_.spl";
					
					if(flag == 1)
						flag = 2;
					else
						flag = 1;
					File writeFile = new File(outputDir+"\\"+dirList[i].substring(0,dirList[i].length()-1)+"\\"+"models\\");		
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
					writer.write(text);
					writer.close();
				}
			}
		}
	}
	
	
	public static void main(String[] args) {
		
		
		try
		{
			String dirName = "F:/Ъ§ОнМЏ/DUC2002/summaries";
			
			
			
			DUC2002_Model duc = new DUC2002_Model();
			
			duc.mainFun(dirName, "F:/program/TextMining/experimentResult/DUC2002");
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		
	
	
	
}
}
