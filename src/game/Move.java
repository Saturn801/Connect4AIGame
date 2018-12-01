/*
 * 	Samuel James Bryan - 14701935
 */

package game;

import player.Player;

public class Move {
	int row;
	int col;
	Player player;
	int playerNum;
	char token;
	
	public Move(int c, int r, Player p, int pNum, char t){
		col = c;
		row = r;
		player = p;
		playerNum = pNum;
		token = t;
	}
	
	public int getRow(){
		return row;
	}
	
	public int getCol(){
		return col;
	}
	
	public Player getPlayer(){
		return player;
	}
	
	public int getPlayerNum(){
		return playerNum;
	}
	
	public char getToken(){
		return token;
	}
	
	public String toString(){
		String output = "Last Move Made:";
		output += "\nCol: " + (getCol()+1) + "   Row: " + (getRow()+1);
		return output;
	}
}
