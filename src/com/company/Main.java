package com.company;

import java.io.*;
import java.util.*;

public class Main
{

	//Java Version
	public static int[][] levenshtein(String firstWord, String secondWord)
	{
		double zeit = System.nanoTime();

		// lower case everything
		firstWord = firstWord.toLowerCase();
		secondWord = secondWord.toLowerCase();

		// store length
		int firstLength = firstWord.length();
		int secondLength = secondWord.length();

		// matrix to store differences
		int[][] distance = new int[firstLength + 1][secondLength + 1];

		for (int i = 1; i <= firstLength; i++)
		{
			distance[i][0] = i;
		}

		for (int j = 1; j <= secondLength; j++)
		{
			distance[0][j] = j;
		}

		for (int j = 1; j <= secondLength; j++)
		{
			for (int i = 1; i <= firstLength; i++)
			{
				if (firstWord.charAt(i - 1) == secondWord.charAt(j - 1))
				{
					distance[i][j] = distance[i - 1][j - 1];
				}
				else
				{
					distance[i][j] = Math
						.min(distance[i - 1][j] + 1, Math.min(distance[i][j - 1] + 1, distance[i - 1][j - 1] + 1));
				}
			}
		}

		zeit = System.nanoTime() - zeit;
		System.out.println("Time spend first LevAlgo: " + zeit);

		return distance;
	}

	//Python Version
	/*
	public static int[][] levenshteinDistanceMatrix(String string1, String string2)
	{
		double zeit = System.nanoTime();

		int[][] distances = new int[string1.length() + 1][string2.length() + 1];
		int lenStr1 = string1.length();
		int lenStr2 = string2.length();

		//0-te column und spalte mit 0-n/m intWerten initialisieren, um effektiv dyn. Prog. anzuwenden

		for (int i = 0; i < lenStr1 + 1; i++)
		{
			distances[i][0] = i;
		}

		for (int i = 0; i < lenStr2 + 1; i++)
		{
			distances[0][i] = i;
		}

		char[] str1 = string1.toCharArray();
		char[] str2 = string2.toCharArray();

		//printMatrix(distances, string1, string2);

		//Calculate Distances between all prefixes
		for (int i = 1; i < lenStr1 + 1; i++)
		{
			for (int j = 1; j < lenStr2 + 1; j++)
			{
				int a = Integer.MIN_VALUE;
				int b = Integer.MIN_VALUE;
				int c = Integer.MIN_VALUE;
				if (str1[i - 1] == str2[j - 1])
					distances[i][j] = distances[i - 1][j - 1];
				else
				{
					a = distances[i][j - 1];
					b = distances[i - 1][j];
					c = distances[i - 1][j - 1];
				}
				if (a != Integer.MIN_VALUE && b != Integer.MIN_VALUE && c != Integer.MIN_VALUE)
				{
					if (a <= b && a <= c)
					{
						distances[i][j] = a + 1;
					}
					else if (b <= a && b <= c)
					{
						distances[i][j] = b + 1;
					}
					else
					{
						distances[i][j] = c + 1;
					}
				}
			}
		}
		System.out.println();
		//printMatrix(distances,string1,string2);
		//Value in the bottom-right corner is result: distances[lenStr1][lenStr2]

		zeit = System.nanoTime() - zeit;
		//System.out.println("Time spend second LevAlgo: "+zeit);

		return distances;
	}*/
    /*
	public static void printMatrix(int[][] distanceMatrix, String string1, String string2)
	{
		char[] str1 = string1.toCharArray();
		String str2 = string2.replaceAll("(?<=.)(?!$)", " ");
		for (int i = 0; i < distanceMatrix.length; i++)
		{
			if (i == 0)
			{
				System.out.println("    " + str2);
				System.out.print("  ");
			}
			else
			{
				System.out.print(str1[i - 1] + " ");
			}
			for (int j = 0; j < distanceMatrix[0].length; j++)
			{
				System.out.print(distanceMatrix[i][j] + " ");
			}
			System.out.println();
		}
	}
*/
	//Optimization: contains, j<k, check half of the word for similarity,
	// not really using the score for similarity yet
	// (if it would be used, subtract wordlength diff, to compare similarity just on amount of same characterLength),
	// just the matrixErrors till a point

	public static void main(String[] args)
	{

		double timestop = System.nanoTime();
		//read from files and execute levenshtein on each files names, combine similar names under one entry
		//when writing it into new year file

		//Name,Geschlecht,Anzahl

		ArrayList<ArrayList<String>> readEntries = new ArrayList<>();

		String readLine = "";
		String[] readLineSeparated;

		//int tillYear = 2020;
		int tillYear = 1881;        //Nur für Testing und kürzere Rechenzeit

		for (int i = 1880, n = 0; i < tillYear; i++, n++)
		{
			try (BufferedReader br = new BufferedReader(new FileReader("lib/names_data/yob" + i + ".txt")))
			//try(BufferedReader br = new BufferedReader(new FileReader("Testing.txt")))

			{
				while ((readLine = br.readLine()) != null)
				{
					//separate Line
					readLineSeparated = readLine.split(",");
					//System.out.println("Durchlauf: " + i + " " + Arrays.toString(readLineSeparated));
					readEntries.add(new ArrayList<>(Arrays.asList(readLineSeparated)));
					//System.out.println("Amount of names: " + readEntries.size());
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

			//Levenshtein execute and writeback into new files M/F
			int amountOfNamesInFile = readEntries.size();

			Set<ArrayList<String>> treeM = new TreeSet<>(new NameOccurrenceComparator());
			Set<ArrayList<String>> treeF = new TreeSet<>(new NameOccurrenceComparator());

			String sameGender = "";
			int lengthWord1;
			int lengthWord2;

			//variante 2 der Ähnlichkeit weit unten im Algo, eher schlechter
			//int lengthWordDifference = 0;
			//int levenshteinDistance = Integer.MAX_VALUE;

			String similarNames = "";
			String name1 = "";
			String name2 = "";
			int sumAmountSimilar = 0;

			boolean wordIsContained;
			int diffTillXCharacters = 0;
			int amountCharsToCompare = 0;

			boolean[] alreadyUsedNames = new boolean[amountOfNamesInFile];
			boolean boolName1CanBeUsed;
			boolean boolName2CanBeUsed;

			//Name,Geschlecht,Anzahl
			try (BufferedWriter bwM = new BufferedWriter(new FileWriter("updatedNames/yobM" + i + ".txt"));
				BufferedWriter bwF = new BufferedWriter(new FileWriter("updatedNames/yobF" + i + ".txt")))
			{
				int j = 0;
				//bisher durch: && j<965 eingeschränkt, später entfernen
				for (; j < amountOfNamesInFile /* && j<965 */; j++)
				{   //alle Namen
					//Resets für Variablen hier machen, um mögliche Fehlerfälle durch alte Werte zu verhindern
					boolName1CanBeUsed = false;         //reset boolean

					sumAmountSimilar = Integer.valueOf(readEntries.get(j).get(2));

					//
					if(!similarNames.isEmpty())if (readEntries.get(j).get(0).charAt(0) != similarNames.charAt(0))
					{
						similarNames = readEntries.get(j).get(0);   //put name which gets compared into simNames
						name1 = similarNames;

						System.out.println("_________________________\nSimilarNames at start: " + similarNames);
					}

					for (int k = j + 1; k < amountOfNamesInFile; k++)
					{    //verglichen mit allen anderen Namen
						//Resets für Variablen hier machen, um mögliche Fehlerfälle durch alte Werte zu verhindern
						wordIsContained = false;        //reset boolean
						//
						name2 = readEntries.get(k).get(0);

						sameGender = readEntries.get(j).get(1);

						if (!alreadyUsedNames[k])
						{
							boolName2CanBeUsed = true;      //isn't used atm -> maybe remove
						}

						//Falls das Wort, zu dem wir ähnliche Wörter suchen, schon benutzt wurde und wir nicht in dem Durchgang (j) sind, wo wir den Wert erst true gesetzt haben
						// -> überspringen wir komplette Prüfungen
						if (!alreadyUsedNames[j] || boolName1CanBeUsed)
						//boolName1CanBeUsed ist notwendig, um nach einmaligem anhängen eines Namens, noch weitere anzuhängen,
						// nach dem Durchlauf des j Wortes(hier werden jeweils andere Namen angehängt, vor dem Write) -> nächstes Wort ist boolName1CanBeUsed wieder false(reset)
						{
							boolName1CanBeUsed = true;

							//compare Gender to only execute Levenshtein for same Gender
							if (sameGender.equals(readEntries.get(k).get(1)))
							{    //dont compare entries that were already compared in a prev iteration again

								if (name1.contains(name2) || name2.contains(
									name1))   //one word is part of the other -> they are similar -> fast way to say if we can write them in one line
								{
									wordIsContained = true;
								}
								else
								{
									lengthWord1 = readEntries.get(j).get(0).length();
									lengthWord2 = readEntries.get(k).get(0).length();

									//get Matrix of Levensthein back to check for similarity in the first x chars

									//second Algo   //worse
									//int[][] levenshteinMat = levenshteinDistanceMatrix(readEntries.get(j).get(0),readEntries.get(k).get(0));

									//firstAlgo     //better
									int[][] levenshteinMat = levenshtein(readEntries.get(j).get(0),
										readEntries.get(k).get(0));

									int lenSmallerWord = Math
										.min(lengthWord1, lengthWord2);  //get length of the smallest word out of both

									//check half of the smallest words characters in the matrix, minimum 2,3,4  siehe 3 Vorschläge unten
									diffTillXCharacters = 0;       //reset
									amountCharsToCompare = (lenSmallerWord + 1) / 2;
									if (amountCharsToCompare < 5)
									{
										//5 ->(9+1)/2= 5 ist nicht mehr im if
										// if für Wörter der Länge 8 -> überprüfe alle 8
										//change it to 4 ->(7+1)/2= 4 ist nicht mehr im if
										// if für Wörter der Länge 6 -> überprüfe alle 6
										//change it to 3 ->(5+1)/2= 3 ist nicht mehr im if
										// if für Wörter der Länge 4 -> überprüfe alle 4
										amountCharsToCompare = lenSmallerWord;       //minimum 2 char long word -> compare it complete if it's just <= 4
									}
									//weitere Bedingung? gestaffelte Ähnlichkeitsvergleiche, da sonst Differenz zwischen Wort der Länge 4 -> wird ganz verglichen, Wort der Länge 5 -> vergleiche nur 3 buchstaben
									//else if (amountCharsToCompare <)

									//version 1     Preferred
									for (int l = 0; l < amountCharsToCompare; l++)
									{    //+1 to get the bigger chunk out of an uneven length word
										if (levenshteinMat[l + 1][l + 1] != diffTillXCharacters)
										{
											diffTillXCharacters++;
										}
									}

									//version 2     solely levenshteinDistance without just looking at part of the chars of the smaller word

									//lengthWordDifference = Math.abs(lengthWord1 - lengthWord2);       //calculate difference to subtract from score
									//levenshteinDistance = levenshteinMat[levenshteinMat.length - 1][levenshteinMat[0].length - 1];
									//diffTillTestEnd = levenshteinDistance - lengthWordDifference;     //maybe the lengthWordDifference is now needless, see above code

								}

								//condition to call the words equal or almost equal,    1 difference in character or 0 or percentage error?

								//System.out.println("Comparing diffOfPartialWord: " + diffTillTestEnd + " AmountOfCharsToCompare/5: "+ amountCharsToCompare/5);
								if ((diffTillXCharacters <= (amountCharsToCompare / 5) && !alreadyUsedNames[k]) || (
									wordIsContained && !alreadyUsedNames[k])) //20% Fehler sind erlaubt
								{
									System.out.println(
										"no more " + (j + 1) + ":" + readEntries.get(j) + ",no more " + (k + 1) + ":"
											+ readEntries.get(k));
									alreadyUsedNames[k] = true;
									alreadyUsedNames[j] = true;
									similarNames += "," + readEntries.get(k).get(0);    //build simNames up
									System.out.println("SimilarNames: " + similarNames);
									sumAmountSimilar += Integer.valueOf(readEntries.get(k)
										.get(2)); //add the similar words occurrences to our words occurrences
								}
								else if (!alreadyUsedNames[j])
								{
									System.out.println("no more" + (j + 1) + ":" + readEntries.get(j));
									alreadyUsedNames[j] = true;   //to mark it as used, because it will be written
								}
							}
						}
					}
					//if the last one wasn't used before it wouldn't be written otherwise
					if (j == amountOfNamesInFile - 1 && !alreadyUsedNames[amountOfNamesInFile - 1])
					{
						similarNames = readEntries.get(amountOfNamesInFile - 1).get(0);
						sumAmountSimilar = Integer.valueOf(readEntries.get(amountOfNamesInFile - 1).get(2));
						sameGender = readEntries.get(amountOfNamesInFile - 1).get(1);
						alreadyUsedNames[amountOfNamesInFile - 1] = true;
						boolName1CanBeUsed = true;
					}

					//Change comparator compare zu inverse oder laufe unten For-Loop inverse ab beim write, um desc order zu bekommen
					if (boolName1CanBeUsed)
					{
						if (sameGender.equals("F"))
						{
							System.out.println("Adding a female match: " + similarNames);
							treeF.add(new ArrayList<String>(
								Arrays.asList(similarNames, sameGender, String.valueOf(sumAmountSimilar))));

						}
						else if (sameGender.equals("M"))
						{
							System.out.println("Adding a male match: " + similarNames);
							treeM.add(new ArrayList<String>(
								Arrays.asList(similarNames, sameGender, String.valueOf(sumAmountSimilar))));

						}
						else
						{
							System.out.println("The Gender is unknown! " + sameGender);
						}
					}
				}
				//flatten treeset and write it into files
				System.out.println("Länge des TreesetsF: " + treeF.size());
				ArrayList[] transform = new ArrayList[treeF.size()];
				treeF.toArray(transform);

				System.out.println("Länge des TreesetsM: " + treeM.size());
				ArrayList[] transform2 = new ArrayList[treeM.size()];
				treeM.toArray(transform2);

				for (ArrayList<String> a : transform)
				{
					System.out.println("\n" + a);
					bwF.write(a.get(0) + "," + a.get(1) + "," + a.get(2));
					bwF.newLine();
				}

				for (ArrayList<String> a : transform2)
				{
					System.out.println("\n" + a);
					bwM.write(a.get(0) + "," + a.get(1) + "," + a.get(2));
					bwM.newLine();
				}

			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		timestop = System.nanoTime() - timestop;
		System.out.println("Taken time: " + timestop);

	}

	//Maybe leave it out, because it can't be used to solely exclude cases, see comment example below
	//if ("ab".substring(0,1) == "a".substring(0,1))   //both start with the same character -> first condition for possible name similarity, starting from the front (mögliche Fehlerfälle: Marie, Rose-Marie)

	//catches the mentioned case above
    /*    if ("ab".contains("a") || "a".contains("ab"))   //one word is part of the other -> they are similar -> fast way to say if we can write them in one line
    {

    }
        else
    {

    }
    //There are still some duplicates listed double, maybe a boolean[] to mark already used words, so they dont get used again
    */

}
