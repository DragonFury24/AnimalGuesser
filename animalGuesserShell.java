import java.io.*;
import java.util.*;
//The Animal Guessing Program:	d oberle, 2006
//This game will attempt to guess an animal that the user is thinking of by asking yes/no questions.
//If the program does not know the animal, it will add it to its data base, making it a little smarter for the next user.
//Implement an array as a heap.  Each index has a parent at (index/2), a left child as (index*2) and a right child at index*2+1.
//The index path following a "no" response will go to the left child (left subtree).  
//The index path following a "yes" response will go to the right child (right subtree).

public class animalGuesserShell {
    //pre:  "fileName" is the name of a real file containing lines of text
    //post: returns the number of lines in fileName O(n)
    public static int getFileSize(String fileName) throws IOException {
        Scanner input = new Scanner(new FileReader(fileName));
        int size = 0;
        while (input.hasNextLine())                //while there is another line in the file
        {
            size++;                                        //add to the size
            input.nextLine();                            //go to the next line in the file
        }
        input.close();                                    //always close the files when you are done
        return size;
    }

    //pre:  "fileName" is the name of a real file containing lines of text - the first line intended to be unused
    //post:returns a String array of all the elements in <filename>.txt, with index 0 unused (heap) O(n)
    public static String[] readFile(String fileName) throws IOException {
        int size = getFileSize(fileName);        //holds the # of elements in the file
        String[] list = new String[size];        //a heap will not use index 0;
        Scanner input = new Scanner(new FileReader(fileName));
        int i = 0;                                            //index for placement in the array
        String line;
        ArrayList<String> questions = new ArrayList<String>();

        while (input.hasNextLine())                //while there is another line in the file
        {
            line = input.nextLine();                    //read in the next Line in the file and store it in line
            if (isNumber(line)) {
                for (i = 0; i < Integer.parseInt(line); i++) {
                    questions.add("null");
                }
                continue;
            }
            questions.add(line);                             //add the line into the array
            //advance the index of the array
        }
        list = questions.toArray(new String[questions.size()]);
        int numNulls = 0;
        for (i = list.length - 1, numNulls = 0; i > 0; i--) {
            if (list[i] == null || list[i].equals("null")) {
                numNulls++;
            } else break;
        }
        list = Arrays.copyOf(list, list.length - numNulls);
        input.close();
        return list;
    }

    private static boolean isNumber(String num) {
        try {
            Integer.parseInt(num);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    //pre:
    //post:displays all of the elements of the array words O(n)
    public static void showArray(String[] words) {
        for (int i = 0; i < words.length; i++)
            System.out.println(words[i] + " ");
        System.out.println();
        System.out.println("Size of array:" + words.length);
    }

    //Post: puts all the elements in the array into <filename>.txt,
    //      with one element per line O(n)
    public static void writeToFile(String[] array, String filename) throws IOException {
        System.setOut(new PrintStream(new FileOutputStream(filename)));
        for (int i = 0; i < array.length; i++)
            System.out.println(array[i]);
    }

    //pre: let>='A' && let <='Z'  OR    let>='a' && let<='z'
    //post:returns true if let is a vowel O(1)
    public static boolean isVowel(char let) {
        return (let == 'a' || let == 'e' || let == 'i' || let == 'o' || let == 'u' || let == 'A' || let == 'E' || let == 'I' || let == 'O' || let == 'U');
    }

    //post: returns true if a user prompt is N, No, NO, n, nO or no O(1)
    public static boolean isNo(String ans) {
        return (ans.toLowerCase().equals("no") || ans.toLowerCase().equals("n"));
    }

    //post: returns true if a user prompt is y, Y, Yes, yes, YES, yES, or yeS O(1)
    public static boolean isYes(String ans) {
        return (ans.toLowerCase().equals("yes") || ans.toLowerCase().equals("y"));
    }

    public static void main(String argv[]) throws IOException {
        Scanner keyboard = new Scanner(System.in);
        String input;
        String[] questionStrings = readFile("animal.txt");
        boolean correctGuess = false;
        int i = 1;
        int numItems = questionStrings.length;

        //traverse through tree of questions and animal guesses
        while (i <= numItems) {
            //if null then children don't exist and program cannot continue guessing
            if (questionStrings[i] == null || questionStrings[i].equals("null")) {
                break;
            }
            System.out.println(questionStrings[i]);
            input = keyboard.nextLine();
            //if yes and children don't exist, then animal has been correctly guessed, stop asking
            if (isYes(input) && !hasChild(questionStrings, i * 2 + 1)) {
                correctGuess = true;
                break;
            }
            //if no and children don't exist, then program can no longer guess, stop asking
            if (isNo(input) && !hasChild(questionStrings, i * 2)) {
                break;
            }
            if (isNo(input)) {
                i = i * 2; //no answers move to left child of tree
            } else if (isYes(input)) {
                i = i * 2 + 1; //yes answers move to the right child of tree
            }
        }

        String newAnimal = "";
        String newQuestion = "";
        if (correctGuess) {
            System.out.println("I have correctly guessed your animal!");
        } else {
            //asks for input of a new animal and question
            System.out.println("Oh no! I can't guess what your animal is!");
            System.out.println("Can you help me out?");
            System.out.println("What is your animal");
            //receive input for @newAnimal
            newAnimal = keyboard.nextLine();
            System.out.println("What characteristics does your animal have that the previous one doesn't.");
            //receive input for new characteristic of animal
            newQuestion = keyboard.nextLine();
        }

        if (!correctGuess) {
            String oldAnimal = questionStrings[i];
            //increase length of @questionStrings array if adding children causes OutOfBounds
            if (i * 2 + 1 > questionStrings.length) {
                questionStrings = Arrays.copyOf(questionStrings, i * 2 + 2);
            }
            //add a new question as a new left child of the last asked question
            questionStrings[i] = "Is it " + newQuestion + "?";
            //adds the @newAnimal as a right child of the newly added question
            questionStrings[i * 2 + 1] = "Is it " + (isVowel(newAnimal.charAt(0)) ? "an " : "a ") + newAnimal + "?";

            questionStrings[i * 2] = oldAnimal;
        }

        questionStrings = removeNulls(questionStrings);

        writeToFile(questionStrings, "animal.txt");
    }

    private static boolean hasChild(String[] s, int index) {
        return index < s.length && s[index] != null && !s[index].equals("0") && !s[index].equals("null");
    }

    private static String[] removeNulls(String[] strings) {
        int numNulls = 0;
        int j = 0, i = 0;
        String[] temp = new String[strings.length];

        /* traverse through @questionStrings and copy to @questionsToFile. If while traversing,
        a null is reached, count the number of nulls until a non-null element is reached and
        put the number of nulls into @questionsToFile rather than copying every null
         */
        for (i = 0, j = 0; i < strings.length; i++) {
            if (strings[i] == null || strings[i].equals("null") || strings[i].equals("")) {
                numNulls++;
                continue;
            }
            if (numNulls > 0) {
                temp[j] = Integer.toString(numNulls);
                numNulls = 0;
                j++;
            }
            temp[j] = strings[i];
            j++;
        }

        for (i = temp.length - 1, numNulls = 0; i > 0; i--) {
            if (temp[i] == null || temp[i].equals("null")) {
                numNulls++;
            } else
                break;
        }
        temp = Arrays.copyOf(temp, temp.length - numNulls);
        return temp;
    }

}