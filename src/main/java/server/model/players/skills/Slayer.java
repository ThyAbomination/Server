package server.model.players.skills;

import server.Server;
import server.model.players.Client;
import server.util.Misc;

/**
 * Slayer.java
 * 
 * @author Sanity
 * 
 **/

public class Slayer {

	private Client c;

	public Slayer(Client c) {
		this.c = c;
	}

	public static int[] lowTasks = { 1265, 4688, 4686, 1584 };
	public static int[] lowReqs = { 1, 1, 1, 1 };
	public static int[] medTasks = { 7641, 7793, 1643, 1618, 7801, 1624, 7805, 4695,
			4698, 1956, 4677, 55, 6106, 1154, 1156 };
	public static int[] medReqs = { 5, 35, 45, 50, 60, 65, 70, 1, 1, 1, 1, 1, 1, 1, 1 };
	public static int[] highTasks = { 1610, 7643, 1613, 1615, 7798, 7800, 2783, 7797,
			7799, 2452, 4705, 6103, 7795, 1590, 53, 1591, 5362, 1592, 5363, 9463, 10773, 1157 };
	public int[] highReqs = { 75, 75, 80, 85, 85, 85, 90, 90, 95, 1, 1, 1, 1,
			1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
	public static int[] eliteTasks = {2883,2882,2881,3200,50,1158,5666,6247,6222,6260,6203,8133,10773};

	public void eliteTask(){
		if(c.combatLevel == 126){
			giveTask(4);
		} else {
			c.sendMessage("You only have a combat level of "+c.combatLevel+", you need 126.");
		}
	}
	
	public void giveTask() {
		if (c.combatLevel < 50) {
			giveTask(1);
		} else if (c.combatLevel >= 50 && c.combatLevel <= 90) {
			giveTask(2);
		} else if (c.combatLevel > 90 && c.combatLevel <= 126) {
			giveTask(3);
		} else
			giveTask(2);
	}

	public void giveTask2() {
		for (int j = 0; j < lowTasks.length; j++) {
			if (lowTasks[j] == c.slayerTask) {
				c.sendMessage("You already have an easy task... to kill "
						+ c.taskAmount + " "
						+ Server.npcHandler.getNpcListName(c.slayerTask) + "s.");
				return;
			}
		}
		giveTask(1);
	}
	
	public void giveEliteTask() {
		if(c.combatLevel == 126){
			giveTask(4);
		int given = 0;
		int random = 0;
		random = (int) (Math.random() * (eliteTasks.length - 1));
		given = eliteTasks[random];
		} else {
			c.sendMessage("You only have a combat level of "+c.combatLevel+", you need 126.");
		}
	}

	public void giveTask(int taskLevel) {
		int given = 0;
		int random = 0;
		if (taskLevel == 1) {
			random = (int) (Math.random() * (lowTasks.length - 1));
			given = lowTasks[random];
		} else if (taskLevel == 2) {
			random = (int) (Math.random() * (medTasks.length - 1));
			given = medTasks[random];
		} else if (taskLevel == 3) {
			random = (int) (Math.random() * (highTasks.length - 1));
			given = highTasks[random];
		} else if (taskLevel == 4){
			random = (int) (Math.random() * (eliteTasks.length - 1));
			given = eliteTasks[random];
		}
		if (!canDoTask(taskLevel, random)) {
			giveTask(taskLevel);
			return;
		}
		c.slayerTask = given;
		c.taskAmount = Misc.random(15) + 15;
		c.sendMessage("You have been assigned to kill " + c.taskAmount + " "
				+ Server.npcHandler.getNpcListName(given)
				+ "s as a slayer task.");
	}

	public boolean canDoTask(int taskLevel, int random) {
		if (taskLevel == 1) {
			return c.playerLevel[c.playerSlayer] >= lowReqs[random];
		} else if (taskLevel == 2) {
			return c.playerLevel[c.playerSlayer] >= medReqs[random];
		} else if (taskLevel == 3) {
			return c.playerLevel[c.playerSlayer] >= highReqs[random];
		} else if (taskLevel == 4 && c.combatLevel == 126){
			return true;
		}
		return false;
	}
	
	public void resetTask() {
		c.taskAmount = -1;
		c.slayerTask = -1;
	}

	public boolean easyTask(int npcType) {
		for(int i : lowTasks)
			if(i == npcType)
				return true;
		return false;
	}

	public boolean medTask(int npcType) {
		for(int i : medTasks)
			if(i == npcType)
				return true;
		return false;
	}

	public boolean hardTask(int npcType) {
		for(int i : highTasks)
			if(i == npcType)
				return true;
		return false;
	}

	public boolean eliteTask(int npcType) {
		for(int i : eliteTasks)
			if(i == npcType)
				return true;
		return false;
	}
}