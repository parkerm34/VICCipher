import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;



public class DecryptVIC extends VICOperations{

	private static int place;
	private static long dateAndID;
	private static long extendedDateAndID;
	private static String permutation = "";
	private static String permutationTwo = "";
	private static long extendedPermutation;
	private static ArrayList<String> encoder;
	private static char[] messageArray;
	private static String decodedMessage = "";
	private static char firstSpace;
	private static char secondSpace;
	private static char[] fullMessage = new char[25];
	
	public static int     ID_LEN          = 5;   // # of chars in agent ID
    public static int     DATE_LEN        = 6;   // # of chars in date
    public static int     PHRASE_LEN      = 10;  // # letters of phrase to use
    public static int     ANAGRAM_LEN     = 10;  // # of chars in anagram
    public static int     ANAGRAM_LETTERS = 8;   // # of letters in anagram

    public static char    SPACE           = (char)32; // space is ASCII 32
    public static boolean DEBUG           = false;    // toggle debug prints
    
    public static void main(String[] args)
    {
    	VICData vic = readVICData(args[0]);
    	
		place = Integer.parseInt(vic.date)%10;

		vic.agentID = vic.encodedMessage.substring(place, place+5);
		vic.message = vic.encodedMessage.substring(0, place) + vic.encodedMessage.substring(place+5, vic.encodedMessage.length());
    	
		System.out.println(vic.agentID);
		System.out.println(vic.date);
		
		dateAndID = noCarryAddition(Long.parseLong(vic.agentID), Long.parseLong(vic.date)/10);
		
		System.out.println(dateAndID);
		
		extendedDateAndID = chainAddition(dateAndID, 10);

		System.out.println(extendedDateAndID);

		permutation = digitPermutation(vic.phrase);

		System.out.println(permutation);

		extendedPermutation = noCarryAddition(extendedDateAndID, Long.parseLong(permutation));

		System.out.println(extendedPermutation);

		permutationTwo = digitPermutation(Long.toString(extendedPermutation));

		System.out.println(permutationTwo);

		encoder = straddlingCheckerboard(permutationTwo, vic.anagram);

		messageArray = vic.message.toCharArray();
		
		System.out.println();
		System.out.println(messageArray);
		System.out.println(encoder);
		
		
		firstSpace = getFirstSpace();
		secondSpace = getSecondSpace();
		
		int y = 0;
		
		for(int x = 0; x < messageArray.length; x++)
		{
			if((messageArray[x] != firstSpace) && (messageArray[x] != secondSpace))
			{
				decodedMessage += messageArray[x];
				fullMessage[y] += (char) (encoder.indexOf(decodedMessage) + 65);
//				System.out.println((encoder.indexOf(decodedMessage) + 65));
//				System.out.println(decodedMessage);
			}
			else
			{
				decodedMessage += messageArray[x];
				decodedMessage += messageArray[x+1];
				fullMessage[y] += (char) (encoder.indexOf(decodedMessage) + 65);
				x++;
			}
			System.out.print(fullMessage[y]);
			y++;
			decodedMessage = "";
		}
    }
    
    
    public static VICData readVICData (String pathName)
    {
        VICData vic = new VICData(); // Object to hold the VIC input data
        Scanner inFile = null;       // Scanner file object reference

        try {
              inFile = new Scanner(new File(pathName));
        } catch (Exception e) {
              System.out.println("File does not exist: " + pathName + "!\n");
              System.exit(1);
        }

                // Read and sanity-check agent ID.  Needs to be ID_LEN long
                // and numeric.

        if (inFile.hasNext()) {
              vic.date = inFile.nextLine();
        } else {
              System.out.println("ERROR:  Date not found!\n");
              System.exit(1);
        }

        if (vic.date.length() != DATE_LEN) {
              System.out.printf("ERROR:  Date length is %d, must be %d!\n",
                                vic.date.length(),DATE_LEN);
              System.exit(1);
        }

        try {
            long dateValue = Long.parseLong(vic.date);
        } catch (NumberFormatException e) {
              System.out.println("Date `" + vic.date 
                               + "contains non-numeric characters!\n");
              System.exit(1);
        }

                // Read and sanity-check phrase.  After removing non-letters,
                // at least PHRASE_LEN letters must remain.

        if (inFile.hasNext()) {
              vic.phraseOriginal = inFile.nextLine();
              StringBuffer sb = new StringBuffer(vic.phraseOriginal);
              for (int i = 0; i < sb.length(); i++) {
                  if (!Character.isLetter(sb.charAt(i))) {
                      sb.deleteCharAt(i);
                      i--;  // Don't advance to next index o.w. we miss a char
                  }
              }
              vic.phrase = sb.toString().toUpperCase();
              if (vic.phrase.length() >= PHRASE_LEN) {
                  vic.phrase = vic.phrase.substring(0,PHRASE_LEN);
              }
        } else {
              System.out.println("ERROR:  Phrase not found!\n");
              System.exit(1);
        }

        if (vic.phrase.length() != PHRASE_LEN) {
              System.out.printf("ERROR:  Phrase contains %d letter(s), "
                              + "must have at least %d!\n",
                                vic.phrase.length(),PHRASE_LEN);
              System.exit(1);
        }

                // Read and sanity-check anagram.  Must be ANAGRAM_LEN long,
                // and contain ANAGRAM_LETTERS letters and the rest spaces.

        if (inFile.hasNext()) {
              vic.anagram = inFile.nextLine().toUpperCase();
        } else {
              System.out.println("ERROR:  Anagram not found!\n");
              System.exit(1);
        }

        if (vic.anagram.length() != ANAGRAM_LEN) {
            System.out.printf("ERROR:  Anagram length is %d, must be %d!\n",
                              vic.anagram.length(),ANAGRAM_LEN);
            System.exit(1);
        }

        for (int i = 0; i < vic.anagram.length(); i++) {
            if (    !Character.isLetter(vic.anagram.charAt(i))
                 && vic.anagram.charAt(i) != SPACE             ) {
                System.out.printf("ERROR:  Anagram contains character `%c'.\n",
                                  vic.anagram.charAt(i) );
                System.exit(1);
            }
        }

        int numLetters = 0;
        for (int i = 0; i < vic.anagram.length(); i++) {
            if (Character.isLetter(vic.anagram.charAt(i))) {
                numLetters++;
            }
        }
        if (numLetters != ANAGRAM_LETTERS) {
            System.out.printf("ERROR:  Anagram contains %d letters, "
                            + "should have %d plus %d spaces.\n",
                              numLetters, ANAGRAM_LETTERS,
                              ANAGRAM_LEN - ANAGRAM_LETTERS);
            System.exit(1);
        }

                // Read and sanity-check message.  After removing non-letters
                // and capitalizing, at least one letter must remain.

        if (inFile.hasNext()) {
              vic.encodedMessage = inFile.nextLine();
              try {
                  long messageValue = Long.parseLong(vic.encodedMessage);
              } catch (NumberFormatException e) {
                    System.out.println("Encoded Message `" + vic.encodedMessage 
                                     + "contains non-numeric characters!\n");
                    System.exit(1);
              }

              
        } else {
              System.out.println("ERROR:  Encoded Message not found!\n");
              System.exit(1);
        }


        if (DEBUG) {
            System.out.printf("vic.agentID = %s\n",vic.agentID);
            System.out.printf("vic.date = %s\n",vic.date);
            System.out.printf("vic.phrase = %s\n",vic.phrase);
            System.out.printf("vic.anagram = %s\n",vic.anagram);
            System.out.printf("vic.message = %s\n",vic.message);
        }
        return vic;
    }
}
