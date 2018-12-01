/*
 * 	Samuel James Bryan - 14701935
 */

package player;

import java.util.Scanner;

public class HumanPlayer implements Player{
	public String makeMove(Scanner scanner){
		String move = "";
		System.out.println("In which column do you wish to insert your token? ");		
		move = scanner.nextLine();
		return move;
	}
}