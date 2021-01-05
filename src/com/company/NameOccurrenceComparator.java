package com.company;

import java.util.ArrayList;
import java.util.Comparator;

public class NameOccurrenceComparator implements Comparator<ArrayList<String>>
{
	@Override public int compare(ArrayList<String> al1, ArrayList<String> al2)
	{
		if (Integer.valueOf(al1.get(2)).compareTo(Integer.valueOf(al2.get(2))) == -1)
		{
			return 1;
		}
		else if (Integer.valueOf(al1.get(2)).compareTo(Integer.valueOf(al2.get(2))) == 1)
		{
			return -1;
		}
		else if (al1.get(0).equals(al2.get(0))){
			// can't be the case because of no duplicate names in the source file
			// but since it is "compare", we want to include it
			return 0;
		}
		else
		{
			return al1.get(0).substring(0,1).compareTo(al2.get(0).substring(0,1));
		}

	}
}