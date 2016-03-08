package ProprocessDoc;

import java.io.BufferedReader;
import java.io.FileReader;

public class VerifyCode {

	public void verifyOne()
	{
		try
		{
			String path = "C:/Users/jipeng/Desktop/Qiang/KNN/OHSUMED/OHSUMEDTextPatternRemove.txt";
			BufferedReader br = new BufferedReader(new FileReader(path));
			
			
			String line = br.readLine();
			
			
			while(line!=null)
			{
				String fre[] = line.split(" ");
				
				double total = 0;
				
				for(int i=0; i<fre.length; i++)
				{
					total += Double.parseDouble(fre[i]);
				}
				System.out.println(total);
				line = br.readLine();
			}
			br.close();
		
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		VerifyCode vc = new VerifyCode();
		vc.verifyOne();
	}

}
