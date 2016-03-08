package MultiDocument;

import java.util.ArrayList;

public class SentSup_Remove {
	
	
	
	//public int idArr [] = new int [maxId];
	public int idArrSup [];
	
	public double idArrWei [];
	
	public SentSup_Remove(int idNum)
	{
		idArrSup = new int[idNum];
		idArrWei = new double[idNum];
		
	}
	
	public void add(ArrayList arr, int sup, int num)
	{
		//if(arr.size()<2)
			//return;
		for(int i=0; i<arr.size(); i++)
		{
			int id = (Integer) arr.get(i);
			int value = idArrSup[id];
			
			if(sup>value)
			{
				idArrSup[id] = sup;
				
				idArrWei[id]  += (double)(sup-value)*arr.size();
				//idArrWei[id]  += (sup-value);
			}
			
			
		}
	}
	
	public double subSum()
	{
		double res = 0;
		
		for(int i=0; i<idArrWei.length; i++)
			res += idArrWei[i];
		
		return res;
	}
	
	public void initi(int select[], double alpa)
	{
		for(int i=0; i<select.length; i++)
		{
			//idArrSup[i] =0;
			
			if(select[i]>0)
				idArrWei[i] *= alpa;
		
		}
	}

}
