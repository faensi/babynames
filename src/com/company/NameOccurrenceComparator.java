package com.company;

import java.util.ArrayList;
import java.util.Comparator;

public class NameOccurrenceComparator implements Comparator<ArrayList<String>>
{
	@Override public int compare(ArrayList<String> al1, ArrayList<String> al2)
	{
		if (Integer.valueOf(al1.get(2)).compareTo(Integer.valueOf(al2.get(2))) == -1)
		{
			return -1;
		}
		else if (Integer.valueOf(al1.get(2)).compareTo(Integer.valueOf(al2.get(2))) == 1)
		{
			return 1;
		}
					/*else if(true)
					{       //vllt noch eine weitere abfrage auf namen(name1.equals(name2)) falls NamensListen mit selben Occurrences vorkommen, war bei mir schon der Fall
						return 1;
					}*/
		else
		{
			return -1;
		}
	}
}