/*
 * 	Samuel James Bryan - 14701935
 */

package test;

import game.Game;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Test {	
	public void runHardCodedGame(){
		Scanner scanner = new Scanner(System.in);
		int[] heights = {7, 6, 6, 5, 7, 6, 7, 5, 7};
		int[] efforts = {2000, 50};
		Game testGame = new Game(heights, "D3", "G2");
		testGame.setupPlayers(false, efforts);
		testGame.playGame(1, true, scanner);
		scanner.close();
	}
	
	public void runUserInputGame(){
		Game testGame = new Game();
		Scanner scanner = new Scanner(System.in);
		int firstPlayer = 0;
		Boolean isHumanPlayer;
		int[] efforts;
		
		testGame = setUpGame(scanner);	
		// Determine players.
		System.out.println("Enter Y if you want Human vs Computer." +
				"\nOr N if you want Computer vs Computer.");
		isHumanPlayer = getPlayersChoice(scanner);	
		// Get player efforts.
		if(isHumanPlayer){
			efforts = new int[1];
			System.out.println("Enter effort of AI player: ");
			efforts[0] = getIntInRange(1, 2000, scanner);
		}
		else{
			efforts = new int[2];
			System.out.println("Enter effort of first AI player: ");
			efforts[0] = getIntInRange(1, 2000, scanner);
			System.out.println("Enter effort of second AI player: ");
			efforts[1] = getIntInRange(1, 2000, scanner);
		}
		testGame.setupPlayers(isHumanPlayer, efforts);
		// Get starting order.
		System.out.println("Would you like player 1 or 2 to go first? ");
		firstPlayer = getIntInRange(1, 2, scanner);
		
		testGame.playGame(firstPlayer, true, scanner);
		scanner.close();
	}
	
	private int getIntInRange(int min, int max, Scanner scanner){
		Boolean check = false;
		String input;
		int numColumns = 0;
		do{
			input = scanner.nextLine();
			if(!isInteger(input)){
				System.out.println("Invalid Input. Please enter an integer between "
						+ min + " and " + max + ".");
				continue;
			}				
			numColumns = Integer.parseInt(input);
			if(numColumns < min || numColumns > max){
				System.out.println("Invalid Input. Please enter an integer between "
						+ min + " and " + max + ".");
				continue;
			}
			check = true;
		}while(!check);
		return numColumns;
	}
	
	private String getInvalidLocationString(Game game, Scanner scanner){
		String input;
		Boolean check = false;
		do{
			input = scanner.nextLine();
			if(!game.checkValidString(input)){
				System.out.println("Invalid Input. Please enter a location of the form A3." +
						"\nWith a valid column & row.");
				continue;
			}
			check = true;
		}while(!check);
		return input;
	}
	
	private Boolean getPlayersChoice(Scanner scanner){
		String input;
		Boolean check = false;
		do{
			input = scanner.nextLine();
			if(!input.equals("Y") && !input.equals("N")){
				System.out.println("Invalid Input. Please enter Y or N.");
				continue;
			}
			check = true;
		}while(!check);
		if(input.equals("Y"))
			return true;
		return false;
	}
	
	public Boolean isInteger(String input){
		for(int i=0; i<input.length(); i++){
			char character = input.charAt(i);
			if(character < 48 || character > 57)
				return false;
		}		
		return true;
	}
	
	// Set up game grid (NOT PLAYERS)
	private Game setUpGame(Scanner scanner){
		Game testGame = new Game();
		int numColumns = 0;
		String invalid1, invalid2;

		// Get number of columns.
		System.out.println("How many columns would you wish to play with? ");
		numColumns = getIntInRange(6, 11, scanner);		
		// Get height of columns.
		int[] heights = new int[numColumns];
		for(int i=0; i<heights.length; i++){
			System.out.println("Enter height for column " + (i+1) + ": ");
			heights[i] = getIntInRange(5, 12, scanner);
		}
		// Initialize grid.
		testGame = new Game(heights);
		// Get invalid location cells.
		System.out.println("Enter first invalid location: ");
		invalid1 = getInvalidLocationString(testGame, scanner);
		System.out.println("Enter second invalid location: ");
		invalid2 = getInvalidLocationString(testGame, scanner);
		// Set invalid location cells.
		testGame.setInvalidLocations(invalid1, invalid2);
		
		return testGame;
	}
	
	public void testResetGame(){
		Scanner scanner = new Scanner(System.in);
		int[] heights = {7, 6, 6, 5, 7, 6, 7, 5, 7};
		int[] efforts = {50, 2000};
		Game testGame = new Game(heights, "D3", "G2");
		testGame.setupPlayers(false, efforts);
		testGame.playGame(1, false, scanner);
		testGame.resetGame();
		int[] efforts2 = {2000, 50};
		testGame.setupPlayers(false, efforts2);
		testGame.playGame(1, false, scanner);
		scanner.close();
	}
	
	public void runAllTests(int numAIGames){
		PrintWriter out = null;
		try {
			out = new PrintWriter("tests.txt");
		} catch (FileNotFoundException e) {
			System.err.println("Cannot write to file.");
			System.exit(0);
		}
		Scanner scanner = new Scanner(System.in);
		Game[] gameList = new Game[4];
		int[] efforts = {80, 150, 500, 2000};
		for(int i=0; i<gameList.length; i++){
			System.out.println("\nPLEASE ENTER VARIABLES FOR GRID " + (i+1) + ": ");
			gameList[i] = setUpGame(scanner);
		}
		for(int i=0; i<gameList.length; i++){
			out.println();
			out.println("GAME SET-UP Nº " + i + ": ");	
			for(int j=0; j<efforts.length; j++){
				// 4 games Human VS Computer (Always human first)
				int[] gameEfforts = {efforts[j]};
				gameList[i].setupPlayers(true, gameEfforts);
				int outcome;
				int numMoves;
				gameList[i].playGame(1, true, scanner);
				outcome = gameList[i].getWinner();
				numMoves = gameList[i].getNumMovesMade();
				gameList[i].resetGame();
				// PRINT RESULTS
				out.println("Human VS Computer (Effort " + efforts[j] + ")");
				out.println("Winner:   Player " + outcome);
				out.println("Number of Moves made: " + numMoves);
			}
			for(int j=0; j<efforts.length; j++){
				for(int k=0; k<efforts.length; k++){
					// 16 combinations Computer VS Computer
					int[] gameEfforts = {efforts[j], efforts[k]};
					gameList[i].setupPlayers(false, gameEfforts);
					int[] outcomes = {0,0,0};
					int averageMoves = 0;
					for(int l=0; l<numAIGames; l++){
						// X number of games per each
						// Two for starting player order
						
						// First player starts first.
						gameList[i].playGame(1, false, scanner);
						if(gameList[i].getWinner()==0)
							outcomes[0]++;
						else if(gameList[i].getWinner()==1)
							outcomes[1]++;
						else
							outcomes[2]++;
						averageMoves += gameList[i].getNumMovesMade();
						gameList[i].resetGame();
						// Second player starts first.
						gameList[i].playGame(2, false, scanner);
						if(gameList[i].getWinner()==0)
							outcomes[0]++;
						else if(gameList[i].getWinner()==1)
							outcomes[1]++;
						else
							outcomes[2]++;
						averageMoves += gameList[i].getNumMovesMade();
						gameList[i].resetGame();
					}
					averageMoves = averageMoves / (numAIGames*2);
					// PRINT RESULTS
					out.println("Computer (Effort " + efforts[j] + ") VS Computer (Effort "
					+ efforts[k] + ")");
					out.println("Draws: " + outcomes[0]);
					out.println("Player 1 Wins: " + outcomes[1]);
					out.println("Player 2 Wins: " + outcomes[2]);
					out.println("Average Number of Moves made: " + averageMoves);
				}
			}		
		}
		scanner.close();
		out.close();	
	}
	
	public static void main(String[] args) {
		Test test = new Test();
		//test.runUserInputGame();
		//test.runHardCodedGame();
		//test.testResetGame();
		test.runAllTests(25);
	}
}
