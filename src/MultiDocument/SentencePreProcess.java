package MultiDocument;

import kex.util.TextPreProcess;

public class SentencePreProcess {
	
	public String tokenize (String str) {
		StringBuffer resultStr = new StringBuffer();
		int j = 0;
		str = str.replaceAll("\''","\"");
		while (j < str.length()) {
		 
		  int startj = j;
		  boolean isDigit = false;
		  boolean bigDigit = false;
		 
		  while (j < str.length()) {
		    char ch = str.charAt(j);
		    if(Character.isDigit(ch))
		    	isDigit = true;
		    if (Character.isLetterOrDigit(ch)) {
		      j++;
		    } else if( (ch == ',') && isDigit)
		    {
		    	if((j<str.length()-1) && Character.isDigit(str.charAt(j+1)))
		    		bigDigit = true;
		    	else
		    		break;
		    	j++;
		    		
		    }else if(ch == '.')
		    {
		    	if((j<str.length()-1) && Character.isLetterOrDigit(str.charAt(j+1)))
		    		j++;
		    	else
		    		break;
		    	
		    } else if(ch == '\'')
		    		j++;
		    else
		    	break;
		  }
		  String pun = str.substring(startj, j);
		  if(bigDigit)
		  {
			  String sub[] = pun.split(",");
			  pun = "";
			  for(int i=0; i<sub.length; i++)
				  pun += sub[i];
		  }
		 
		  resultStr.append(pun);
		  resultStr.append(' ');
		  if (j == str.length()) {
		      break;
		   }
		    j++;
		}
		return resultStr.toString();
    }
	
	public static void main(String args[])
	{
		String text = "Gilbert's movement";
		SentencePreProcess spp = new SentencePreProcess();
		String str = spp.tokenize(text);
		System.out.println(str);
		
		TextPreProcess tpp = new TextPreProcess();
		String str2 = tpp.tokenize(text);
		System.out.println(str2);
	}

}
