package hangman;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class EvilHangman {

    public static void main(String[] args) throws IOException, EmptyDictionaryException, GuessAlreadyMadeException {

        File dictionaryFile = new File(args[0]);
        int wordLength = Integer.parseInt(args[1]);
        int numGuesses = Integer.parseInt(args[2]);
        Scanner userIn = new Scanner(System.in);
        String userGuess;
        //Declare a game object
        EvilHangmanGame game = new EvilHangmanGame();
        try {
            game.setNumGuesses(numGuesses);
            game.setWordLength(wordLength);
        }
        catch (NumberFormatException ex) {
            System.out.println("Number of guesses or word length is incorrect. Enter an appropriate number of guesses");
            return;
        }
        try {
            game.startGame(dictionaryFile, wordLength);
        }
        catch (IOException ex){
            System.out.print("File not found. Please enter appropriate command line arguments");
            return;
        }
        catch(EmptyDictionaryException ex) {
            System.out.println("The dictionary was empty or had no words that matched the input");
            return;
        }
        while(game.getNumGuesses()>0) {
            if (!game.getGameWord().toString().contains(""+'-')){
                System.out.println("You Win!");
                break;
            }
            System.out.println("You have "+game.getNumGuesses()+" guesses left");
            System.out.printf("Used letters: ");
            for (char a:game.getGuessedLetters()) {
                System.out.printf(a+" ");
            }
            System.out.println();
            System.out.println("Word: "+game.getGameWord());
            System.out.println("Enter guess: ");
            userGuess = userIn.next();
            try {
                if (userGuess.length()!=1) {
                    throw new GuessAlreadyMadeException();
                }
                game.makeGuess(userGuess.charAt(0));
            }
            catch(GuessAlreadyMadeException ex) {
                System.out.println("Letter was already guessed, please make another guess");
            }
        }
        System.out.print("The word was: "+game.getFinalWord());
    }
}
