import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

class VICData {
    public String agentID;
    public String date;
    public String phrase;
    public String phraseOriginal;
    public String anagram;
    public String message;
    public String messageOriginal;
    public String encodedMessage;
} // class VICData

public class EncryptVIC extends VICOperations {
		
	private static long dateAndID;
	private static long extendedDateAndID;
	private static String permutation = "";
	private static String permutationTwo = "";
	private static long extendedPermutation;
	private static ArrayList<String> encoder;
	private static String encodedMessage = "";
	private static char[] messageArray;
	private static int place;
	private static String first = "";
	private static String second = "";
	
	public static int     ID_LEN          = 5;   // # of chars in agent ID
    public static int     DATE_LEN        = 6;   // # of chars in date
    public static int     PHRASE_LEN      = 10;  // # letters of phrase to use
    public static int     ANAGRAM_LEN     = 10;  // # of chars in anagram
    public static int     ANAGRAM_LETTERS = 8;   // # of letters in anagram

    public static char    SPACE           = (char)32; // space is ASCII 32
    public static boolean DEBUG           = false;    // toggle debug prints
	
	public static void main(String[] args) throws FileNotFoundException
	{
		VICData vic = readVICData(args[0]);
		
		dateAndID = noCarryAddition(Long.parseLong(vic.agentID), Long.parseLong(vic.date.substring(0, 5)));
		
		extendedDateAndID = chainAddition(dateAndID, 10);
		
		permutation = digitPermutation(vic.phrase);
		
		extendedPermutation = noCarryAddition(extendedDateAndID, Long.parseLong(permutation));
		
		permutationTwo = digitPermutation(Long.toString(extendedPermutation));
		
		encoder = straddlingCheckerboard(permutationTwo, vic.anagram);
		
		messageArray = vic.message.toCharArray();
		
		for(int x = 0; x < messageArray.length; x++)
		{
			encodedMessage += encoder.get(messageArray[x] - 65);
		}
		
		place = Integer.parseInt(vic.date)%10;
		
		first = encodedMessage.substring(0, place);
		second = encodedMessage.substring(place, encodedMessage.length());
		
		
		encodedMessage = first + vic.agentID + second;
		
		System.out.println();
		System.out.println();
		
		System.out.println(encodedMessage);
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
              vic.agentID = inFile.nextLine();
        } else {
              System.out.println("ERROR:  Agent ID not found!\n");
              System.exit(1);
        }

        if (vic.agentID.length() != ID_LEN) {
              System.out.printf("ERROR:  Agent ID length is %d, must be %d!\n",
                                vic.agentID.length(),ID_LEN);
              System.exit(1);
        }

        try {
            long idValue = Long.parseLong(vic.agentID);
        } catch (NumberFormatException e) {
              System.out.println("Agent ID `" + vic.agentID 
                               + "contains non-numeric characters!\n");
              System.exit(1);
        }

                // Read and sanity-check date.  Needs to be DATE_LEN long
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
              vic.messageOriginal = inFile.nextLine();
              StringBuffer sb = new StringBuffer(vic.messageOriginal);
              for (int i = 0; i < sb.length(); i++) {
                  if (!Character.isLetter(sb.charAt(i))) {
                      sb.deleteCharAt(i);
                      i--;  // Don't advance to next index o.w. we miss a char
                  }
              }
              vic.message = sb.toString().toUpperCase();
        } else {
              System.out.println("ERROR:  Message not found!\n");
              System.exit(1);
        }

        if (vic.message.length() == 0) {
              System.out.printf("ERROR:  Message contains no letters!\n");
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
