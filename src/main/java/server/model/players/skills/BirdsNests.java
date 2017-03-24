package server.model.players.skills;

import server.util.Misc;
import server.model.players.Client;
import server.model.players.PlayerHandler;

	/**
	 * @author Optimum
	 * I do not give permission to 
	 * release this anywhere else
	 */

public class BirdsNests {
	
	/**
	 * Ints.
	 */
	
	public static final int nest = 5074;
	public static final int row = 2572;
	public static final int[] RING_REWARDS = {1635, 1637, 1639, 1641, 1643, 2572};
	public static final int EMPTY = 5075;
	public static final int AMOUNT = 1;
	
	public static boolean isNest(final int itemId) {
			if(nest == itemId) {
			    return true;
			}
		return false;
	}
	
	/**
	 * Generates the random drop and creates a ground item
	 * where the player is standing
	 */
	
	public static void dropNest(Client c){
			if (Misc.random(40) < 4) {
					c.getItems().addItem(5074, 1);
			}
	}
	
	/**
	 * 
	 * Searches the nest.
	 * 
	 */
	
	public static final void searchNest(Client c, int itemId){
		if(Misc.random(150) <= 1){
			c.getItems().addItem(row, 1);
			PlayerHandler.yell("@red@[News]: "+Misc.formatPlayerName(c.playerName)+" has just recieved a Ring of wealth from a birds nest!");
		} else {
			ringNest(c, itemId);
			c.getItems().deleteItem(itemId, AMOUNT);
			c.getItems().addItem(EMPTY, AMOUNT);
		}
	}
	
	/**
	 * 
	 * Determines what loot you get
	 *  from ring bird nests
	 *  
	 */
	public static final void ringNest(Client c, int itemId){
		if(itemId == 5074){
			int random = Misc.random(1000);
				if(random >= 0 && random <= 340){
					c.getItems().addItem(RING_REWARDS[0], AMOUNT);
				}else if (random >= 341 && random <= 600){
					c.getItems().addItem(RING_REWARDS[1], AMOUNT);
				}else if (random >= 601 && random <= 850){
					c.getItems().addItem(RING_REWARDS[2], AMOUNT);
				}else if (random >= 851 && random <= 900){
					c.getItems().addItem(RING_REWARDS[3], AMOUNT);
				}else if (random >= 901 && random <= 960){
					c.getItems().addItem(RING_REWARDS[4], AMOUNT);
				}else if (random >= 961 && random <= 1000){
					c.getItems().addItem(RING_REWARDS[7], AMOUNT);
				}
			}
		}
}