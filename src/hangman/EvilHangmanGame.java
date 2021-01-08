package hangman;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EvilHangmanGame implements IEvilHangmanGame{
    private Map <String,Set<String>> setMap;
    private Set <String> wholeSet;
    private SortedSet <Character> guessedChars;
    private StringBuilder gameWord;
    private int numGuesses;
    private int length;
    public EvilHangmanGame() {
        this.setMap = new TreeMap<String, Set<String>>();
        this.wholeSet = new HashSet<String>();
        this.guessedChars = new TreeSet<Character>();
        this.gameWord = new StringBuilder();
        this.numGuesses = 0;
        this.length = 0;
    }
    @Override
    public void startGame(File dictionary, int wordLength) throws IOException, EmptyDictionaryException {
        //First lets clear everything from beforehand
        setMap.clear();
        wholeSet.clear();
        length = wordLength;
        Scanner sc = new Scanner (dictionary);
        for (int i = 0;i < wordLength;i++){
            gameWord.append('-');
        }
        //Now lets read in the dictionary file
        String next;
        while (sc.hasNext()) {
            next = sc.next();
            //Want to populate the set with only words of the correct length
            if (next.length() == length) {
                wholeSet.add(next);
            }
        }
        //when no words in the dictionary file match the length - throw an exception
        if (wholeSet.isEmpty())
            throw new EmptyDictionaryException();
    }

    @Override
    public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
        int ascVal = guess;
        if (ascVal < 65 || ascVal > 122) {
            throw new GuessAlreadyMadeException();
        }
        guess=Character.toLowerCase(guess);
        if (guessedChars.contains(guess)){
            throw new GuessAlreadyMadeException();
        }
        StringBuilder wordKey = new StringBuilder();
        Iterator <String> it = wholeSet.iterator();
        String key = "";
        guessedChars.add(guess);
        setMap.clear();
        while(it.hasNext()) {
            String currWord = it.next();
            for (int i = 0; i < currWord.length(); i++) {
                if (currWord.charAt(i)==guess){
                    wordKey.append(guess);
                }
                else {
                    wordKey.append('-');
                }
            }
            if (!setMap.containsKey(wordKey.toString())) {
                setMap.put(wordKey.toString(), new TreeSet<String>());
            }
            setMap.get(wordKey.toString()).add(currWord);
            wordKey = new StringBuilder();
        }
        key = getKey(guess);
        wholeSet.clear();
        this.wholeSet = setMap.get(key);
        updateGameWord(key);
        if (key.contains(""+guess)){
            int countToReturn = 0;
            for (int i = 0; i < key.length();i++){
                if (key.charAt(i)==guess){
                    countToReturn++;
                }
            }
            System.out.println("Yes, there is "+countToReturn+" "+guess);
        }
        else{
            System.out.println("Sorry, there are no "+guess+"'s");
            this.numGuesses--;
        }
        return setMap.get(key);
    }
    public String getKey(char guess) {
        int maxSize = 0;
        String maxKey = "";
        for (String key:setMap.keySet()) {
            if (setMap.get(key).size()>maxSize) {
                maxSize = setMap.get(key).size();
                maxKey = key;
            }
            if (setMap.get(key).size()==maxSize) {
                //handle if they're equal here
                keyTie(maxKey,key,guess);
            }
        }
        return maxKey;
    }
    public String keyTie (String key1, String key2, char guess) {
        String maxKey = key1;
        String key = key2;
        //1. Choose the group in which the letter does not appear at all.
        if (!(key.contains("" + guess))) {
            return key;
        }
        if (!(maxKey.contains("" + guess))) {
            return maxKey;
        }
        //2. If each group has the guessed letter, choose the one with the fewest letters.
        else if (compareCharCount(maxKey, key, guess) != null) {
            return compareCharCount(maxKey, key, guess);
        }
        //3. If this still has not resolved the issue, choose the one with the rightmost guessed letter.
        else {
            String maxKeyHold = maxKey;
            String keyHold = key;
            //4. If there is still  more than one group, choose the one with the next rightmost letter.
            // Repeat this step (step 4) until a group is chosen.
            for (int i = length-1; i >= 0; i--) {
                if (maxKey.charAt(i) == guess && key.charAt(i) == guess) {
                }
                else if (maxKey.charAt(i) == guess) {
                    return maxKey;
                }
                else {
                    return key;
                }
            }
            /*
            while(maxKey.lastIndexOf(guess)==key.lastIndexOf(guess)){
                int i = maxKey.lastIndexOf(guess);
                maxKey=maxKey.substring(0,i);
                key=key.substring(0,i);
            }
            if (maxKey.lastIndexOf(guess) > key.lastIndexOf(guess)){
                return maxKeyHold;
            }
            else {
                return keyHold;
            }
             */
        }
        return null;
    }
    public String compareCharCount(String key1, String key2,char guess) {
        int count1 = 0;
        int count2 = 0;

        for (int i = 0; i < key1.length();i++){
            if (key1.charAt(i)==guess) {
                count1++;
            }
        }
        for (int i = 0; i < key2.length();i++) {
            if (key2.charAt(i)==guess) {
                count2++;
            }
        }
        if (count1==count2) {
            return null;
        }
        else if (count1<count2) {
            return key1;
        }
        else {
            return key2;
        }
    }
    public void setNumGuesses (int numGuesses) {
        if (numGuesses<1){
            throw new NumberFormatException();
        }
        this.numGuesses = numGuesses;
    }
    public int getNumGuesses () {
        return numGuesses;
    }
    public StringBuilder getGameWord() {
        return gameWord;
    }
    public void setWordLength(int wordLength) {
        if (wordLength<2){
            throw new NumberFormatException();
        }
        this.length=wordLength;
    }
    public void updateGameWord(String key) {
        for (int i = 0; i < key.length();i++) {
            if (key.charAt(i)!='-'){
                gameWord.setCharAt(i,key.charAt(i));
            }
        }
    }
    public String getFinalWord() {
        if (numGuesses > 0) {
            return gameWord.toString();
        }
        else{
            return wholeSet.stream().findAny().get();
        }
    }
    @Override
    public SortedSet<Character> getGuessedLetters() {
        return guessedChars;
    }
}
