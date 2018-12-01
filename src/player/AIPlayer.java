/*
 * 	Samuel James Bryan - 14701935
 */

package player;

import game.Game;

import java.util.Random;
import java.util.Scanner;
	
public class AIPlayer implements Player{
	Game game;
	AIPlayer otherPlayer;
	char token;
	int playerNum;
	int effort;
	
	public AIPlayer(Game g, int n, int e){
		game = g;
		playerNum = n;
		effort = e;
		if(n==1){
			token = 'X';
			otherPlayer = new AIPlayer('O', 2);
		}		
		else{
			token = 'O';
			otherPlayer = new AIPlayer('X', 1);
		}			
	}
	
	public AIPlayer(char t, int n){
		token = t;
		playerNum = n;
	}
	
	public String makeMove(Scanner scanner){
		char column = winningMove(this);
		String move;
		
		if(column == 0){
			float[] scores = new float[game.getNumCols()];
			int[] timesPicked = new int[game.getNumCols()];
			column = 'A';
			// Initialize array
			for(int i = 0; i < game.getNumCols(); i++){
				timesPicked[i] = 0;
				if(game.isValidMove(game.getGrid(), Character.toString((char) (column+i))))
					scores[i] = 1;
				else
					scores[i] = -1;
			}
			// Simulate games
			for(int i = 0; i < effort; i++){
				char firstMove = generateRandomMove(game.getGridCopy());
				int columnPicked = firstMove - 65;
				scores[columnPicked] += simulateGame(firstMove);
				timesPicked[columnPicked]++;
				game.resetCopy();
			}
			// Calculate average value of columns
			for(int i = 0; i < game.getNumCols(); i++){
				scores[i] = scores[i] / timesPicked[i];
			}
			int best = chooseBestMove(scores);
			move = Character.toString((char) (column+best));
		}
		else
			move = Character.toString(column);
		
		return move;
	}
	
	private int simulateGame(char firstMove){
		AIPlayer currentPlayer = this;
		game.makeMove(game.getGridCopy(), firstMove, token, this, playerNum);
		int movesMade = 1;
		char move;
		
		while(!game.gameOver(game.getGridCopy(), game.getRemainingMoves()-movesMade)){
			if(currentPlayer == this)
				currentPlayer = otherPlayer;
			else
				currentPlayer = this;
			char column = winningMove(currentPlayer);
			if(column == 0){
				move = generateRandomMove(game.getGridCopy());
			}
			else				
				move = column;
			game.makeMove(game.getGridCopy(), move, currentPlayer.getToken(),
					currentPlayer, currentPlayer.getPlayerNum());
			movesMade++;
		}
		
		if(game.isDraw(game.getGridCopy(), game.getRemainingMoves()-movesMade))
			return 1;
		else{
			if(currentPlayer == this)
				return 2;
			else
				return 0;
		}
	}
	
	private char winningMove(AIPlayer player){
		char move = 'A';
		for(int i = 0; i < game.getNumCols(); i++){
			if(game.isValidMove(game.getGridCopy(), Character.toString((char) (move+i)))){
				game.makeMove(game.getGridCopy(), (char) (move+i), player.getToken(),
						player, player.getPlayerNum());
				if(game.isConnectFour(game.getGridCopy())){
					game.undoLastMove(game.getGridCopy());
					return (char) (move+i);
				}
				game.undoLastMove(game.getGridCopy());
			}
		}		
		return 0;
	}
	
	private int chooseBestMove(float[] scores){
		float max = scores[0];
		int maxIndex = 0;
		for(int i=1; i<scores.length; i++){
			if(scores[i] > max){
				max = scores[i];
				maxIndex = i;
			}				
		}
		return maxIndex;
	}
	
	private int generateRandomValue(int min, int max){		
		Random rand = new Random();
		int num = rand.nextInt((max+1)-min) + min;
		return num;
	}
	
	private char generateRandomMove(char[][] cells){
		int random;
		char move;
		char column = 'A';
		do{
			random = generateRandomValue(0, game.getNumCols()-1);
		}while(!game.isValidMove(cells, Character.toString((char) (column+random))));
		move = (char) (column+random);
		return move;
	}
	
	public char getToken(){
		return token;
	}
	
	public int getPlayerNum(){
		return playerNum;
	}
}
