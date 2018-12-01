/*
 * 	Samuel James Bryan - 14701935
 */

package game;

import java.util.Scanner;


import player.AIPlayer;
import player.HumanPlayer;
import player.Player;
import test.Test;
	
public class Game {
	private int numCols;
	private int numRows;
	private int[] heights;
	private char[][] grid;
	private char[][] copyGrid;
	private Player player1;
	private Player player2;
	private Move lastMove;
	private int numFreeCells = 0;
	private int numMoves = 0;
	private String invalidCell1;
	private String invalidCell2;
	private int winner;
	
	// Direct input by system, need to check variables.
	public Game(int[] heightOfColumns, String invalid1, String invalid2){
		numCols = heightOfColumns.length;
		if(numCols < 6 || numCols > 11){
			System.err.println("Number of columns should be between 6 and 11.");
			System.exit(0);
		}
		if(!checkValidRowHeights(heightOfColumns)){
			System.err.println("Height of rows should be between 5 and 12 for all rows.");
			System.exit(0);
		}
		numRows = getMaxHeight(heightOfColumns);
		heights = heightOfColumns;
		initializeGrid();
		if(!checkValidString(invalid1) || !checkValidString(invalid2)
				|| invalid1.equals(invalid2)){
			System.err.println("Invalid locations. Should be of the form 'A3', with" +
					" the letter in uppercase and both letter and number in the ranges" +
					" of the grid." +
					"\nBoth strings should not be the same.");
			System.exit(0);
		}
		setInvalidLocations(invalid1, invalid2);
		invalidCell1 = invalid1;
		invalidCell2 = invalid2;
	}
	
	// Input by user, variables already checked.
	public Game(int[] heightOfColumns){
		numCols = heightOfColumns.length;
		numRows = getMaxHeight(heightOfColumns);
		heights = heightOfColumns;
		initializeGrid();
	}
	
	public Game() {
	}
	
	public void resetGame(){
		numFreeCells = 0;
		numMoves = 0;
		initializeGrid();
		setInvalidLocations(invalidCell1, invalidCell2);	
	}
	
	public void setupPlayers(Boolean isHuman, int[] efforts){
		if(!checkEfforts(isHuman, efforts))
			System.err.println("Efforts are invalid. Should be appropriate" +
					" number and at least 1.");
		if(isHuman){
			player1 = new HumanPlayer();
			player2 = new AIPlayer(this, 2, efforts[0]);
		}		
		else{
			player1 = new AIPlayer(this, 1, efforts[0]);
			player2 = new AIPlayer(this, 2, efforts[1]);
		}
	}
	
	public Boolean checkEfforts(Boolean isHuman, int[] efforts){
		if(efforts.length < 1)
			return false;
		if(isHuman){
			if(efforts.length!=1 || efforts[0] < 1)
				return false;
		}
		else{
			if(efforts.length!=2 || efforts[0] < 1 || efforts[1] < 1)
				return false;
		}
		return true;
	}

	public void playGame(int firstPlayer, Boolean printGrid, Scanner scan){
		Player currPlayer;
		int currPlayerNum;
		char currPlayerToken;
		if(firstPlayer==1){
			currPlayer = player1;
			currPlayerNum = 1;
			currPlayerToken = 'X';
		}			
		else{
			currPlayer = player2;
			currPlayerNum = 2;
			currPlayerToken = 'O';
		}				
		do{
			String input; 
			Boolean valid;
			char move;
			if(printGrid){
				System.out.println(printGrid());
				System.out.println("Player " + currPlayerNum + "'s turn: ");
			}		
			do{
				input = currPlayer.makeMove(scan);
				valid = isValidMove(grid, input);
				if(!valid)
					System.out.println("Invalid input. Please enter one " +
							"capital letter for a valid column.");
			}while(!valid);
			move = input.charAt(0);
			makeMove(grid, move, currPlayerToken, currPlayer, currPlayerNum);
			resetCopy();
			numFreeCells--;
			numMoves++;
			if(currPlayer == player1){
				currPlayer = player2;
				currPlayerNum = 2;
				currPlayerToken = 'O';
			}				
			else{
				currPlayer = player1;
				currPlayerNum = 1;
				currPlayerToken = 'X';
			}
		}while(!gameOver(grid, numFreeCells));
		if(printGrid){
			System.out.println(printGrid());
		}		
		System.out.println("\nGAME OVER!");
		if(isDraw(grid, numFreeCells)){
			System.out.println("Game is a draw.");
			winner = 0;
		}			
		else{
			System.out.println("Winner is: Player " + lastMove.getPlayerNum() + "!");
			winner = lastMove.getPlayerNum();
		}
			
	}
	
	public int getNumCols(){
		return numCols;
	}
	
	public char[][] getGridCopy(){
		return copyGrid;
	}
	
	public char[][] getGrid(){
		return grid;
	}
	
	public int getRemainingMoves(){
		return numFreeCells;
	}
	
	public int getNumMovesMade(){
		return numMoves;
	}
	
	public int getWinner(){
		return winner;
	}
	
	public void resetCopy(){
		for(int i = 0; i < numCols; i++){
			for(int j = 0; j < numRows; j++){
				copyGrid[i][j] = grid[i][j];
			}
		}
	}
	
	public Boolean gameOver(char[][] cells, int moves){
		if(isDraw(cells, moves) || isConnectFour(cells))
			return true;
		return false;
	}
	
	public Boolean isDraw(char[][] cells, int moves){
		if(isConnectFour(cells) || moves!=0)
			return false;
		return true;
	}
	
	public Boolean isConnectFour(char[][] cells){
		int row = lastMove.getRow(), col = lastMove.getCol();
		int lowerLeftDiagRow = row, lowerLeftDiagCol = col,
		    lowerRightDiagRow = row, lowerRightDiagCol = col;
		int currentSequence = 0;
		char token = lastMove.getToken();
		// Horizontal Connect
		for(int i=0; i<numCols; i++){
			if(cells[i][row] == token){
				currentSequence++;
				if(currentSequence==4)
					return true;
			}
			else
				currentSequence = 0;
		}
		currentSequence = 0;
		// Vertical Connect
		for(int j=0; j<numRows; j++){
			if(cells[col][j] == token){
				currentSequence++;
				if(currentSequence==4)
					return true;
			}
			else
				currentSequence = 0;
		}
		// Find lower cell of left diagonal
		while(lowerLeftDiagRow!=0 && lowerLeftDiagCol!=0){
			lowerLeftDiagRow--;
			lowerLeftDiagCol--;
		}
		// Find lower cell of right diagonal
		while(lowerRightDiagRow!=0 && lowerRightDiagCol!=(numCols-1)){
			lowerRightDiagRow--;
			lowerRightDiagCol++;
		}
		currentSequence = 0;
		// Left Diagonal Connect
		int i = lowerLeftDiagCol, j = lowerLeftDiagRow;
		while(i<numCols && j<numRows){
			if(cells[i][j] == token){
				currentSequence++;
				if(currentSequence==4)
					return true;
			}
			else
				currentSequence = 0;
			i++;
			j++;
		}
		currentSequence = 0;
		// Right Diagonal Connect
		i = lowerRightDiagCol; 
		j = lowerRightDiagRow;
		while(i>=0 && j<numRows){
			if(cells[i][j] == token){
				currentSequence++;
				if(currentSequence==4)
					return true;
			}
			else
				currentSequence = 0;
			i--;
			j++;
		}
		return false;
	}
	
	public void makeMove(char[][] cells, char letter, char token,
			Player player, int playerNum){
		int column = letter - 65;
		for(int i = 0; i < numRows; i++){
			if(cells[column][i] == ' '){
				cells[column][i] = token;
				lastMove = new Move(column, i, player, playerNum, token);
				return;
			}
			else if(cells[column][i] == '*'){
				cells[column][i] = Character.toLowerCase(token);
				lastMove = new Move(column, i, player, playerNum,
						Character.toLowerCase(token));
				return;
			}
		}
	}
	
	public void undoLastMove(char[][] cells){
		int col = lastMove.getCol(), row = lastMove.getRow();
		if(cells[col][row] == 'x' || cells[col][row] == 'o')
			cells[col][row] = '*';
		else
			cells[col][row] = ' ';
	}
	
	public Boolean isValidMove(char[][] cells, String move){
		if(move.length()!=1)
			return false;
		char letter = move.charAt(0);
		// Check character is within column limit.
		if(letter < 65 || letter > (65+numCols-1))
			return false;
		int column = letter - 65;
		for(int i = 0; i < numRows; i++){
			if(cells[column][i] == ' ' || cells[column][i] == '*'){
				return true;
			}
		}
		return false;
	}
	
	public void setInvalidLocations(String invalid1, String invalid2){
		invalidCell1 = invalid1;
		invalidCell2 = invalid2;
		char letter1 = invalid1.charAt(0), letter2 = invalid2.charAt(0);
		int column1 = letter1 - 65, column2 = letter2 - 65;
		int row1 = Integer.parseInt(invalid1.substring(1)), 
			row2 = Integer.parseInt(invalid2.substring(1));
		grid[column1][row1-1] = '*';
		grid[column2][row2-1] = '*';
		copyGrid[column1][row1-1] = '*';
		copyGrid[column2][row2-1] = '*';
	}
	
	private Boolean checkValidRowHeights(int[] heightOfColumns){
		for(int i = 0; i < heightOfColumns.length; i++){
			if(heightOfColumns[i] < 5 || heightOfColumns[i] > 12)
				return false;
		}
		return true;
	}
	
	public Boolean checkValidString(String string){
		Test test = new Test();
		if(string.length()!=2 && string.length()!=3)
			return false;
		char letter = string.charAt(0);
		// Check character is within column limit.
		if(letter < 65 || letter > (65+numCols-1)){
			return false;
		}
		int column = letter - 65;
		// Check number is within row limit.
		String number = string.substring(1);
		if(!test.isInteger(number))
			return false;
		int num = Integer.parseInt(number);
		if(num < 1 || num > numRows){
			return false;
		}
		// Check row in column is valid.
		if(grid[column][num-1] == '#'){
			return false;
		}
		return true;
	}
	
	private void initializeGrid(){
		grid = new char[numCols][numRows];
		copyGrid = new char[numCols][numRows];
		for(int i = 0; i < numCols; i++){
			for(int j = 0; j < numRows; j++){
				if(j < heights[i]){
					grid[i][j] = ' ';
					copyGrid[i][j] = ' ';
					numFreeCells++;
				}				
				else{
					grid[i][j] = '#';
					copyGrid[i][j] = '#';
				}							
			}
		}
	}
	
	public String printGrid(){
		String output = "";
		int currRow = numRows-1;
		while(currRow >= 0){
			output += "\n" + duplicateString(numCols, "  _____ ");
			output += "\n" + duplicateString(numCols, " |     |") + "\n";
			for(int i = 0; i < numCols; i++){
				output += " |  " + grid[i][currRow] + "  |";
			}
			output += " " + (currRow+1);
			output += "\n" + duplicateString(numCols, " |_____|");
			currRow--;
		}
		output += "\n";
		for(int i = 0; i < numCols; i++){
			char column = (char) (65+i);
			output += "    " + column + "   ";
		}
		return output;
	}
	
	private String duplicateString(int num, String string){
		String newString = "";
		for(int i = 0; i < num; i++){
			newString += string;
		}
		return newString;
	}
	
	private int getMaxHeight(int[] heightOfColumns){
		int max = heightOfColumns[0];
		for(int i=1; i<heightOfColumns.length; i++){
			if(heightOfColumns[i] > max)
				max = heightOfColumns[i];
		}
		return max;
	}
}
