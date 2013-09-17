import java.util.ArrayList;

/*
 * Parker Mathewson
 * NetID: Parkerm34
 * 
 * 9/13/13
 * 
 * VICOperations.java
 * This class is the backbone of the Encrypt and Decrypt VIC classes.
 * This has all of the calculations for the coding process known as the
 * VIC Cipher. 
 * 
 * Methods
 * =======
 * Public
 * ------
 * static long noCarryAddition(long first, long second)
 * static long chainAddition(long initial, int length)
 * static String digitPermutation(String message)
 * static ArrayList<String> straddlingCheckerboard(String permutation, String anagram) 
 */
public class VICOperations {
	
	private static char firstSpace;
	private static char secondSpace;
	
	/*
	 * Method: noCarryAddition(long first, long second)
	 * 
	 * Purpose: To create the first of four functions used in the VIC encoder
	 * functions, encrypt and decrypt.
	 * This is done by adding together the numbers like normal, but only keeping
	 * the ones place digit in each place for the two numbers.
	 * 
	 * Pre-Condition: The long variables passed in, are in fact Long, and in bounds
	 * 
	 * Post-Condition: Two numbers are added using no carry addition
	 * 
	 * Parameters: first -- the first of two longs that are to be added by noCarryAddition
	 * 				second -- the second of two longs that are to be added by noCarryAddition
	 * 
	 * Returns: A new long that was calculated using noCarryAddition
	 */
	public static long noCarryAddition (long first, long second)
	{
		long tempFirst = first;
		long tempSecond = second;
		long tempValue = 0;
		
		int count = 0;
		
		while(tempFirst > 0)
		{
			tempValue += (((tempFirst%10) + (tempSecond%10))%10)*(Math.pow(10, count));
			tempFirst = tempFirst/10;
			tempSecond = tempSecond/10;
			count++;
		}
		if(tempSecond > 0)
			tempValue += tempSecond*(Math.pow(10,  count));
		
		return tempValue;
	}
	
	/*
	 * Method: chainAddition(long initial, int length)
	 * 
	 * Purpose: Using chainAddition, you can extend any number to a specified length
	 * 			by taking in a requested length, stored in the variable length.
	 * 			This is done by adding the indices of the long numbers, and adding the
	 * 			two with noCarryAddition style and placing them on the end.
	 * 
	 * Pre-Condition: both variables are of correct type.
	 * 
	 * Post-Condition: a long is passed back
	 * 
	 * Returns: a Long number that has been either cut at/extended to the correct
	 * 			requested length.
	 */
	public static long chainAddition (long initial, int length)
	{
		long[] singleValues = new long[length];
		long tempInitial = initial;
		int countOne = 0;
		int countTwo = 0;
		int carryAdditionHelper = 0;
		
		
		while(tempInitial/10 > 0)
		{
			tempInitial = tempInitial/10;
			countOne++;
		}
		
		if(countOne >= length)
			return (long) (initial/(Math.pow(10,  countOne - length + 1)));

		else
		{
			for(countTwo = 0; countTwo <= countOne; countTwo++)
				singleValues[countTwo] = (long) ((long)initial/(Math.pow(10, countOne-countTwo))%10);
		
			for(; countTwo < length; countTwo++)
			{
				singleValues[countTwo] = noCarryAddition(singleValues[carryAdditionHelper], singleValues[carryAdditionHelper + 1]);
				carryAdditionHelper++;
			}
		
			tempInitial = 0;
		
			for(countOne = 0; countOne < length; countOne++)
				tempInitial += singleValues[countOne] * (Math.pow(10,  length - (countOne+1)));
		
			return tempInitial;
		}
	}
	
	/*
	 * Method: digitPermutation(String message)
	 * 
	 * Purpose: To count how many times letters are used in order, or
	 * 			permutating the letters in a String. This is an indexed
	 * 			counting, starting at 0, and creates a 10 character
	 * 			permutation to be returned.
	 * 
	 * Pre-Condition: a String must be passed in, containing any amount letters	
	 * 					no spaces, case
	 * 
	 * Post-Condition: a String is passed back.
	 * 
	 * Returns: a permutated String, counting how many times each letter is
	 * 			used in a given String of integers, is returned.
	 */
	public static String digitPermutation (String message)
	{
		if(message.length() < 10)
			return null;
		else
		{
			message = message.toUpperCase();
			message = message.substring(0, 10);
			char[] permutation = message.toCharArray();
			char first = permutation[0];
			int[] newPerm = new int[10];
			int count = 0;
			String numbers = "";
			
			while(count < 10)
			{
				first = permutation[0];
				for(int x = 0; x < permutation.length; x++)
				{
					if(first > permutation[x])
						first = permutation[x];
				}
			
				for(int x = 0; x < permutation.length; x++)
				{
					if(permutation[x] == first)
					{
						newPerm[x] = count;
						count++;
						permutation[x] = '~';
					}
				}
			}
			for(int x = 0; x < 10; x++)
				numbers += newPerm[x];
			
			return numbers;
		}
	}
	
	
	/*
	 * Method: ArrayList<String> straddlingCheckerboard(String permutation, String anagram)
	 * 
	 * Purpose: 
	 */
	public static ArrayList<String> straddlingCheckerboard (String permutation, String anagram)
	{
		anagram = anagram.toUpperCase();
		
		char[] alphabet = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
				'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
				'W', 'X', 'Y', 'Z'};
		
		char[] tenArray = new char[10];
		char[] eightArray = new char[8];
		
		char[] permArray = permutation.toCharArray();
		char[] anaArray = anagram.toCharArray();
		char firstSpace = '~';
		char secondSpace = '~';
		
		ArrayList<String> checkerboard = new ArrayList<String>(26);
		String addThis = new String();

		int count = 0;
		int index = 0;
		int arrayCount = 0;
		
		for(int x = 0; x < 26; x++)
			checkerboard.add(x, "");
			
		
		if(anagram.length() < 10)
			return null;
		
		System.out.print(" ");
		for(int x = 0; x < permArray.length; x++)
			System.out.print(" " + permArray[x]);
		
		System.out.println();
		
		System.out.print(" ");
		for(int x = 0; x < anaArray.length; x++)
		{
			if(anaArray[x] == ' ')
			{
				if(firstSpace == '~')
				{
					firstSpace = permArray[x];
					setFirstSpace(firstSpace);
					count++;
				}
				else
				{
					secondSpace = permArray[x];
					setSecondSpace(secondSpace);
					count++;
				}
			}
			System.out.print(" " + anaArray[x]);
			if(anaArray[x] != ' ')
			{
				addThis += permArray[x];
				index = anaArray[x]-65;
				alphabet[index] = '~';
				checkerboard.remove(index);
				checkerboard.add(index, addThis);
			}
			addThis = "";
		}
		if(count < 2)
			return null;
		
		
		for(int x = 0; x < 26; x++)
		{
			if(alphabet[x] != '~' && arrayCount < 10)
			{
				addThis += (char)firstSpace;
				addThis += (char)permArray[arrayCount];
				tenArray[arrayCount] = alphabet[x];
				checkerboard.remove(x);
				checkerboard.add(x, addThis);
				arrayCount++;
				alphabet[x] = '~';
			}
			if(alphabet[x] != '~' && arrayCount > 9)
			{
				addThis += (char)secondSpace;
				addThis += (char)permArray[arrayCount-10];
				eightArray[arrayCount - 10] = alphabet[x];
				checkerboard.remove(x);
				checkerboard.add(x, addThis);
				arrayCount++;
				alphabet[x] = '~';
			}
			addThis = "";
		}
		
		System.out.println();
		System.out.print(firstSpace);
		for(int x = 0; x < 10; x++)
			System.out.print(" " + tenArray[x]);
		System.out.println();
		System.out.print(secondSpace);
		for(int x = 0; x < 8; x++)
			System.out.print(" " + eightArray[x]);
		
		return checkerboard;
	}
	
	public static void setFirstSpace(char first)
	{
		firstSpace = first;
	}
	
	public static void setSecondSpace(char second)
	{
		secondSpace = second;
	}
	
	public static char getFirstSpace()
	{
		return firstSpace;
	}
	
	public static char getSecondSpace()
	{
		return secondSpace;
	}
}
